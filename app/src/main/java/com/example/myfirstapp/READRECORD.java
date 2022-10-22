package com.example.myfirstapp;

import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.util.MonthDisplayHelper;

import com.example.myfirstapp.genericUtil.BitManager;
import com.example.myfirstapp.genericUtil.DateFormatter;

import java.sql.Ref;

//import static com.example.myfirstapp.GETProcessingOptions.AFL;


/**
 * Created by adarsh on 16/01/17.
 */

public class READRECORD extends AsyncTask<IsoDep, Void, Boolean> {
    static byte[] ODAData = new byte[0];
    private String Module = this.getClass().getSimpleName();

    public Boolean doInBackground(IsoDep... isoDep) {
        //TapCard tapCard = new TapCard();
        CardData cardData = new CardData();
        TerminalData terminalData = new TerminalData();
        DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        int ODADataBufferLength = 0;
        byte[] ODADataBuffer = new byte[512];
        byte[] AFLData = cardData.getAFL();
        Log.d(Module, "AFL " + new String(dataFormatterUtil.BCDToChar(AFLData)));

        //test
        if (AFLData.length == 0)
            AFLData = dataFormatterUtil.hexToBCD(dataFormatterUtil.asciiToHex("00010400100104002001040030010400400104005001040060010400700104008001040090010400A0010400B0010400C0010400D0010400E00104004001040008010400180104002801040038010400"), 0);
        //"480104005801040068010400780104008801040098010400A8010400B8010400C8010400D8010400E8010400F8010400"), 0 );
        //AFLData = dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex("00010F0010010F0020010F0030010F0040010F0050010F0060010F0070010F0080010F0090010F00A0010F00B0010F00C0010F00D0010F00E0010F00F0010F0008010F0018010F0028010F0038010F0048010F0058010F0068010F0078010F0088010F0098010F00A8010F00B8010F00C8010F00D8010F00E8010F00F8010F00"), 0 );
        //
        for (int z = 0; z < AFLData.length; z += 4) {
            byte[] AFL = new byte[4];
            System.arraycopy(AFLData, z, AFL, 0, 4);
            Log.d(Module, "AFLunit " + new String(dataFormatterUtil.BCDToChar(AFL)));

            byte RefControlParm = (byte) (AFL[0] | 0x04);

            byte[] RecordStartBCD = new byte[1];
            RecordStartBCD[0] = AFL[1];
            String RecordStartHex = dataFormatterUtil.BCDToHex(RecordStartBCD);
            int RecordStart = dataFormatterUtil.hexToInt(RecordStartHex);
            Log.d(Module, "Rec Start " + RecordStart);

            byte[] LastRecordBCD = new byte[1];
            LastRecordBCD[0] = AFL[2];
            String LastRecordHex = dataFormatterUtil.BCDToHex(LastRecordBCD);
            int LastRecord = dataFormatterUtil.hexToInt(LastRecordHex);
            Log.d(Module, "Last Rec " + LastRecord);

            byte[] ODARecBCD = new byte[1];
            ODARecBCD[0] = AFL[3];
            String ODARecHex = dataFormatterUtil.BCDToHex(ODARecBCD);
            int ODARec = dataFormatterUtil.hexToInt(ODARecHex);
            Log.d(Module, "ODA #" + ODARec);

            byte[] RAPDU = new byte[0];
            for (int i = RecordStart; i <= LastRecord; i++) {
                byte[] P1 = new byte[1];
                P1 = dataFormatterUtil.hexToBCD(dataFormatterUtil.intToHex(i), 2);
                byte[] command = new byte[4];
                command[0] = 0x00;
                command[1] = (byte) 0xB2;
                command[2] = P1[0];
                command[3] = RefControlParm;
                try {
                    Log.d(Module, "CAPDU " + new String(dataFormatterUtil.BCDToChar(command)));
                    RAPDU = isoDep[0].transceive(command);
                } catch (Exception E) {
                    Log.d(Module, "Read Exception " + E);
                    return false;
                }
                Log.d(Module, "RAPDU " + new String(dataFormatterUtil.BCDToChar(RAPDU)));
                BitManager bitManager = new BitManager();
                //int j=0, k=0;
                for (int j = 0, k = 0; j < RAPDU.length - 2; j++) {
                    //System.out.println("\nRAPDU - " + String.format("%02X", RAPDU[j]));
                    byte RAPDUDataLength = 0x00;
                    if (j == 0 && RAPDU[0] == 0x70) {
                        Log.d(Module, "EMV Proprietary Template");
                        j++;
                        RAPDUDataLength = (byte) RAPDU[j++];
                        Log.d(Module, "Length " + new String(dataFormatterUtil.BCDToChar(RAPDUDataLength)));
                        if (new BitManager().checkIsBitOn(RAPDUDataLength, 8)) {
                            RAPDUDataLength = (byte) RAPDU[j++];
                            Log.d(Module, "Length " + new String(dataFormatterUtil.BCDToChar(RAPDUDataLength)));
                        }
                    }
                    if (i <= ODARec) {
                        if (i >= 1 && 1 <= 10) {
                            System.arraycopy(RAPDU, 2, ODADataBuffer, ODADataBufferLength, dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(RAPDUDataLength))));
                            ODADataBufferLength += dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(RAPDUDataLength)));
                        } else {
                            System.arraycopy(RAPDU, 0, ODADataBuffer, ODADataBufferLength, dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(RAPDUDataLength))));
                            ODADataBufferLength += dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(RAPDUDataLength)));
                        }
                    }
                    if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x32) {
                        //Log.d(Module, "Issuer Public Key Exponent 9F32");
                        j += 2;
                        byte[] IssPubKeyExpBCD = new byte[1];
                        IssPubKeyExpBCD[0] = RAPDU[j];
                        int IssPubKeyExpLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(IssPubKeyExpBCD));
                        j++;
                        byte[] IssuerPublicKeyExponent = new byte[IssPubKeyExpLength];
                        System.arraycopy(RAPDU, j, IssuerPublicKeyExponent, 0, IssPubKeyExpLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar( IssuerPublicKeyExponent) ) );
                        Log.d(Module, "Issuer Public Key Exponent 9F32 " + new String(dataFormatterUtil.BCDToChar(IssuerPublicKeyExponent)));
                        j += IssPubKeyExpLength - 1;
                        cardData.setIssuerPublicKeyExponent(IssuerPublicKeyExponent);
                    } else if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x47) {
                        //Log.d(Module, "ICC Public Key Exponent 9F47");
                        j += 2;
                        byte[] IccPubKeyExpBCD = new byte[1];
                        IccPubKeyExpBCD[0] = RAPDU[j];
                        int IccPubKeyExpLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(IccPubKeyExpBCD));
                        j++;
                        byte[] ICCPublicKeyExponent = new byte[IccPubKeyExpLength];
                        System.arraycopy(RAPDU, j, ICCPublicKeyExponent, 0, IccPubKeyExpLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(ICCPublicKeyExponent)));
                        Log.d(Module, "ICC Public Key Exponent 9F47 " + new String(dataFormatterUtil.BCDToChar(ICCPublicKeyExponent)));
                        j += IccPubKeyExpLength - 1;
                        cardData.setICCPublicKeyExponent(ICCPublicKeyExponent);
                    } else if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x48) {
                        //Log.d(Module, "ICC Public Key Reminder 9F48");
                        j += 2;
                        byte[] IccPubKeyRemBCD = new byte[1];
                        IccPubKeyRemBCD[0] = RAPDU[j];
                        int IccPubKeyRemLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(IccPubKeyRemBCD));
                        j++;
                        byte[] ICCPublicKeyRemainder = new byte[IccPubKeyRemLength];
                        System.arraycopy(RAPDU, j, ICCPublicKeyRemainder, 0, IccPubKeyRemLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(ICCPublicKeyRemainder)));
                        Log.d(Module, "ICC Public Key Reminder 9F48 " + new String(dataFormatterUtil.BCDToChar(ICCPublicKeyRemainder)));
                        j += IccPubKeyRemLength - 1;
                        cardData.setICCPublicKeyRemainder(ICCPublicKeyRemainder);
                    } else if (RAPDU[j] == (byte) 0x5F && RAPDU[j + 1] == (byte) 0x20) {
                        //Log.d(Module, "Application expiry date 5F24");
                        j += 2;
                        byte[] CardHolderNameLenBCD = new byte[1];
                        CardHolderNameLenBCD[0] = RAPDU[j];
                        int CardHolderNameLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(CardHolderNameLenBCD));
                        j++;
                        byte[] CardHolderName = new byte[CardHolderNameLength];
                        System.arraycopy(RAPDU, j, CardHolderName, 0, CardHolderNameLength);
                        Log.d(Module, "Card Holder Name 5F20 " + new String(dataFormatterUtil.BCDToChar(CardHolderName)));
                        j += CardHolderNameLength - 1;
                        cardData.setCardholderName(CardHolderName);
                    } else if (RAPDU[j] == (byte) 0x5F && RAPDU[j + 1] == (byte) 0x25) {
                        //Log.d(Module, "Application effective date 5F25");
                        j += 2;
                        byte[] AppEffectiveDateBCD = new byte[1];
                        AppEffectiveDateBCD[0] = RAPDU[j];
                        int AppEffectiveDateLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(AppEffectiveDateBCD));
                        j++;
                        byte[] AppEffectiveDate = new byte[AppEffectiveDateLength];
                        System.arraycopy(RAPDU, j, AppEffectiveDate, 0, AppEffectiveDateLength);
                        Log.d(Module, "Application effective date 5F25 " + new String(dataFormatterUtil.BCDToChar(AppEffectiveDate)));
                        j += AppEffectiveDateLength - 1;
                        DateFormatter dateFormatter = new DateFormatter("yyMMdd");
                        if (Integer.parseInt(dateFormatter.getFormattedDate()) < Integer.parseInt(new String(dataFormatterUtil.BCDToChar(AppEffectiveDate)))) {
                            Log.d(Module, "Application not yet effective");
                            //bitManager.setBiton(TapCard.TVR[1], 6);
                            terminalData.setTVR(2, 6);
                            //tapCard.updateScreenList("Application NOt effective");
                        }
                    } else if (RAPDU[j] == (byte) 0x5F && RAPDU[j + 1] == (byte) 0x24) {
                        //Log.d(Module, "Application expiry date 5F24");
                        j += 2;
                        byte[] AppExpiryDateBCD = new byte[1];
                        AppExpiryDateBCD[0] = RAPDU[j];
                        int AppExpiryDateLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(AppExpiryDateBCD));
                        j++;
                        byte[] AppExpiryDate = new byte[AppExpiryDateLength];
                        System.arraycopy(RAPDU, j, AppExpiryDate, 0, AppExpiryDateLength);
                        Log.d(Module, "Application Expiry Date 5F24 " + new String(dataFormatterUtil.BCDToChar(AppExpiryDate)));
                        j += AppExpiryDateLength - 1;
                        DateFormatter dateFormatter = new DateFormatter("yyMMdd");
                        if (Integer.parseInt(dateFormatter.getFormattedDate()) > Integer.parseInt(new String(dataFormatterUtil.BCDToChar(AppExpiryDate)))) {
                            Log.d(Module, "Application expired");
                            //bitManager.setBiton(TapCard.TVR[1], 7);
                            terminalData.setTVR(2, 7);
                            //tapCard.updateScreenList("Application Expired");
                        }
                    } else if (RAPDU[j] == (byte) 0x5F && RAPDU[j + 1] == (byte) 0x34) {
                        //Log.d(Module, "Application PAN Sequence number 5F34");
                        j += 2;
                        byte[] AppPanSeqBCD = new byte[1];
                        AppPanSeqBCD[0] = RAPDU[j];
                        int AppPanSeqLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(AppPanSeqBCD));
                        j++;
                        byte[] AppPanSeq = new byte[AppPanSeqLength];
                        System.arraycopy(RAPDU, j, AppPanSeq, 0, AppPanSeqLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(AppPanSeq)));
                        Log.d(Module, "Application PAN Sequence number 5F34 " + new String(dataFormatterUtil.BCDToChar(AppPanSeq)));
                        j += AppPanSeqLength - 1;
                    } else if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x08) {
                        //Log.d(Module, "Application version number 9F08");
                        j += 2;
                        byte[] AppVersionNoBCD = new byte[1];
                        AppVersionNoBCD[0] = RAPDU[j];
                        int AppVersionNoLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(AppVersionNoBCD));
                        j++;
                        byte[] AppVersionNo = new byte[AppVersionNoLength];
                        System.arraycopy(RAPDU, j, AppVersionNo, 0, AppVersionNoLength);
                        Log.d(Module, "Application Version number 9F08 " + new String(dataFormatterUtil.BCDToChar(AppVersionNo)));
                        j += AppVersionNoLength - 1;
                    } else if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x07) {
                        //Log.d(Module, "Application usage control 9F07");
                        j += 2;
                        byte[] AppUsageCtrlBCD = new byte[1];
                        AppUsageCtrlBCD[0] = RAPDU[j];
                        int AppUsageCtrlLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(AppUsageCtrlBCD));
                        j++;
                        byte[] AppUsageCtrl = new byte[AppUsageCtrlLength];
                        System.arraycopy(RAPDU, j, AppUsageCtrl, 0, AppUsageCtrlLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(AppUsageCtrl)));
                        Log.d(Module, "Application Usage Control 9F07 " + new String(dataFormatterUtil.BCDToChar(AppUsageCtrl)));
                        j += AppUsageCtrlLength - 1;
                    } else if (RAPDU[j] == (byte) 0x5F && RAPDU[j + 1] == (byte) 0x28) {
                        //Log.d(Module, "Issuer country code 5F28 ");
                        j += 2;
                        byte[] IssCntryCodeBCD = new byte[1];
                        IssCntryCodeBCD[0] = RAPDU[j];
                        int IssCntryCodeLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(IssCntryCodeBCD));
                        j++;
                        byte[] IssCntryCode = new byte[IssCntryCodeLength];
                        System.arraycopy(RAPDU, j, IssCntryCode, 0, IssCntryCodeLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(IssCntryCode)));
                        Log.d(Module, "Issuer Country Code 5F28 " + new String(dataFormatterUtil.BCDToChar(IssCntryCode)));
                        j += IssCntryCodeLength - 1;
                    } else if (RAPDU[j] == (byte) 0x5F && RAPDU[j + 1] == (byte) 0x30) {
                        //Log.d(Module, "Issuer country code 5F28 ");
                        j += 2;
                        byte[] ServiceCodeLenBCD = new byte[1];
                        ServiceCodeLenBCD[0] = RAPDU[j];
                        int ServiceCodeLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(ServiceCodeLenBCD));
                        j++;
                        byte[] ServiceCode = new byte[ServiceCodeLength];
                        System.arraycopy(RAPDU, j, ServiceCode, 0, ServiceCodeLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(IssCntryCode)));
                        Log.d(Module, "Service Code 5F30 " + new String(dataFormatterUtil.BCDToChar(ServiceCode)));
                        cardData.setServiceCode(ServiceCode);
                        j += ServiceCodeLength - 1;
                    } else if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x46) {
                        //Log.d(Module, "ICC public key certificate 9F46");
                        j += 3;
                        byte[] IccPubKeyCertBCD = new byte[1];
                        IccPubKeyCertBCD[0] = RAPDU[j];
                        int IccPubKeyCertLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(IccPubKeyCertBCD));
                        j++;
                        byte[] ICCPublicKeyCertificate = new byte[IccPubKeyCertLength];
                        System.arraycopy(RAPDU, j, ICCPublicKeyCertificate, 0, IccPubKeyCertLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(ICCPublicKeyCertificate)));
                        Log.d(Module, "ICC Public Key Certificate 9F46 " + new String(dataFormatterUtil.BCDToChar(ICCPublicKeyCertificate)));
                        j += IccPubKeyCertLength - 1;
                        cardData.setICCPublicKeyCertificate(ICCPublicKeyCertificate);
                    } else if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x0D) {
                        //Log.d(Module, "ICC public key certificate 9F46");
                        j += 2;
                        byte[] IACLenBCD = new byte[1];
                        IACLenBCD[0] = RAPDU[j];
                        int IACLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(IACLenBCD));
                        j++;
                        byte[] IAC = new byte[IACLength];
                        System.arraycopy(RAPDU, j, IAC, 0, IACLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(ICCPublicKeyCertificate)));
                        Log.d(Module, "IAC Default 9F0D " + new String(dataFormatterUtil.BCDToChar(IAC)));
                        j += IACLength - 1;
                        cardData.setIACCDefault(IAC);
                    } else if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x0E) {
                        //Log.d(Module, "ICC public key certificate 9F46");
                        j += 2;
                        byte[] IACLenBCD = new byte[1];
                        IACLenBCD[0] = RAPDU[j];
                        int IACLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(IACLenBCD));
                        j++;
                        byte[] IAC = new byte[IACLength];
                        System.arraycopy(RAPDU, j, IAC, 0, IACLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(ICCPublicKeyCertificate)));
                        Log.d(Module, "IAC Denial 9F0DE " + new String(dataFormatterUtil.BCDToChar(IAC)));
                        j += IACLength - 1;
                        cardData.setIACCDenial(IAC);
                    } else if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x0F) {
                        //Log.d(Module, "ICC public key certificate 9F46");
                        j += 2;
                        byte[] IACLenBCD = new byte[1];
                        IACLenBCD[0] = RAPDU[j];
                        int IACLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(IACLenBCD));
                        j++;
                        byte[] IAC = new byte[IACLength];
                        System.arraycopy(RAPDU, j, IAC, 0, IACLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(ICCPublicKeyCertificate)));
                        Log.d(Module, "IAC Default 9F0F " + new String(dataFormatterUtil.BCDToChar(IAC)));
                        j += IACLength - 1;
                        cardData.setIACCOnline(IAC);
                    } else if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x1F) {
                        //Log.d(Module, "ICC public key certificate 9F46");
                        j += 2;
                        byte[] Track1DiscretionaryLenBCD = new byte[1];
                        Track1DiscretionaryLenBCD[0] = RAPDU[j];
                        int Track1DiscretionaryLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(Track1DiscretionaryLenBCD));
                        j++;
                        byte[] Track1Discretionary = new byte[Track1DiscretionaryLength];
                        System.arraycopy(RAPDU, j, Track1Discretionary, 0, Track1DiscretionaryLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(ICCPublicKeyCertificate)));
                        Log.d(Module, "Track1 Discretionary 9F1F " + new String(Track1Discretionary));
                        j += Track1DiscretionaryLength - 1;
                        cardData.setTrack1Discretionary(Track1Discretionary);
                    } else if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x4A) {
                        j += 2;
                        byte[] SDATagListLenBCD = new byte[1];
                        SDATagListLenBCD[0] = RAPDU[j];
                        int SDATagListLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(SDATagListLenBCD));
                        j++;
                        byte[] SDATagList = new byte[SDATagListLength];
                        System.arraycopy(RAPDU, j, SDATagList, 0, SDATagListLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(ICCPublicKeyCertificate)));
                        Log.d(Module, "SDA Tag List 9F4A " + new String(SDATagList));
                        j += SDATagListLength - 1;
                        cardData.setSDATagList(SDATagList);
                    } else if (RAPDU[j] == (byte) 0x9F && RAPDU[j + 1] == (byte) 0x69) {
                        j += 2;
                        byte[] CardAuthRelDataLenBCD = new byte[1];
                        CardAuthRelDataLenBCD[0] = RAPDU[j];
                        int CardAuthRelDataLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(CardAuthRelDataLenBCD));
                        j++;
                        byte[] CardAuthRelData = new byte[CardAuthRelDataLength];
                        System.arraycopy(RAPDU, j, CardAuthRelData, 0, CardAuthRelDataLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(ICCPublicKeyCertificate)));
                        Log.d(Module, "Card Authentication Related Data 9F69 " + new String(dataFormatterUtil.BCDToChar( CardAuthRelData)));
                        j += CardAuthRelDataLength - 1;
                        cardData.setCardAuthRelatedData(CardAuthRelData);
                    } else if (RAPDU[j] == (byte) 0x5A) {
                        //Log.d(Module, "Issuer primary Account number 5A");
                        j += 1;
                        byte[] IssPanBCD = new byte[1];
                        IssPanBCD[0] = RAPDU[j];
                        int IssPanLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(IssPanBCD));
                        j++;
                        byte[] IssPan = new byte[IssPanLength];
                        System.arraycopy(RAPDU, j, IssPan, 0, IssPanLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(IssPan)));
                        Log.d(Module, "Issuer Primary Account Number 5A " + new String(dataFormatterUtil.BCDToChar(IssPan)));
                        j += IssPanLength - 1;
                        cardData.setIssuerPAN(IssPan);
                    } else if (RAPDU[j] == (byte) 0x92) {
                        //Log.d(Module, "Issuer public key reminder 92");
                        j += 1;
                        byte[] IssPubKeyRemBCD = new byte[1];
                        IssPubKeyRemBCD[0] = RAPDU[j];
                        int IssPubKeyRemLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(IssPubKeyRemBCD));
                        j++;
                        byte[] IssuerPublicKeyRemainder = new byte[IssPubKeyRemLength];
                        System.arraycopy(RAPDU, j, IssuerPublicKeyRemainder, 0, IssPubKeyRemLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(IssuerPublicKeyRemainder)));
                        Log.d(Module, "Issuer Public Key Remainder 92 " + new String(dataFormatterUtil.BCDToChar(IssuerPublicKeyRemainder)));
                        j += IssPubKeyRemLength - 1;
                        cardData.setIssuerPublicKeyRemainder(IssuerPublicKeyRemainder);
                    } else if (RAPDU[j] == (byte) 0x93) {
                        Log.d(Module, "Signed Static Application Data 93");
                        j++;
                        byte[] SSADLenBCD = new byte[1];
                        SSADLenBCD[0] = RAPDU[j++];
                        Log.d(Module, "1. bcd length " + new String( dataFormatterUtil.BCDToChar(SSADLenBCD)));
                        if (new BitManager().checkIsBitOn(SSADLenBCD[0], 8))
                            SSADLenBCD[0] = RAPDU[j++];
                        Log.d(Module, "2. bcd length " + new String( dataFormatterUtil.BCDToChar(SSADLenBCD)));
                        int SSADLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(SSADLenBCD));
                        Log.d(Module, "1. length " + SSADLength);
                        byte[] SSAD = new byte[SSADLength];
                        System.arraycopy(RAPDU, j, SSAD, 0, SSADLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(IssuerPublicKeyRemainder)));
                        Log.d(Module, "Signed Static Application Data 93 " + new String(dataFormatterUtil.BCDToChar(SSAD)));
                        j += SSADLength - 1;
                        cardData.setSSAD(SSAD);
                    } else if (RAPDU[j] == (byte) 0x8F) {
                        //Log.d(Module, "Certification authority public key index 8F");
                        j += 1;
                        byte[] CertAuthPubKeyIdxBCD = new byte[1];
                        CertAuthPubKeyIdxBCD[0] = RAPDU[j];
                        int CertAuthPubKeyIdxLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(CertAuthPubKeyIdxBCD));
                        j++;
                        byte[] CertAuthPubKeyIdx = new byte[CertAuthPubKeyIdxLength];
                        System.arraycopy(RAPDU, j, CertAuthPubKeyIdx, 0, CertAuthPubKeyIdxLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(CertAuthPubKeyIdx)));
                        TapCard.CAPK_Index = new String(dataFormatterUtil.BCDToChar(CertAuthPubKeyIdx));
                        Log.d(Module, "Certification Authority Public Key index 8F " + TapCard.CAPK_Index);
                        j += CertAuthPubKeyIdxLength - 1;
                        cardData.setCAPKIndex(CertAuthPubKeyIdx[0]);
                    } else if (RAPDU[j] == (byte) 0x57) {
                        //Log.d(Module, "Track 2 57");
                        j += 1;
                        byte[] Track2BCD = new byte[1];
                        Track2BCD[0] = RAPDU[j];
                        int Track2Length = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(Track2BCD));
                        j++;
                        byte[] Track2 = new byte[Track2Length];
                        System.arraycopy(RAPDU, j, Track2, 0, Track2Length);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(Track2)));
                        Log.d(Module, "Track 2 57 " + new String(dataFormatterUtil.BCDToChar(Track2)));
                        j += Track2Length - 1;
                        cardData.setTrack2(Track2);
                    } else if (RAPDU[j] == (byte) 0x90) {
                        //Log.d(Module, "Public key certificate 90");
                        j += 2;
                        byte[] PubKeyCertBCD = new byte[1];
                        PubKeyCertBCD[0] = RAPDU[j];
                        int PubKeyCertLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(PubKeyCertBCD));
                        j++;
                        byte[] IssuerPublicKeyCertificate = new byte[PubKeyCertLength];
                        System.arraycopy(RAPDU, j, IssuerPublicKeyCertificate, 0, PubKeyCertLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(IssuerPublicKeyCertificate)));
                        Log.d(Module, "Public Key Certificate 90 " + new String(dataFormatterUtil.BCDToChar(IssuerPublicKeyCertificate)));
                        j += PubKeyCertLength - 1;
                        cardData.setIssuerPublicKeyCertificate(IssuerPublicKeyCertificate);
                    } else if (RAPDU[j] == (byte) 0x8C) {
                        j += 1;
                        byte[] CDOLLenBCD = new byte[1];
                        CDOLLenBCD[0] = RAPDU[j];
                        int CDOLLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(CDOLLenBCD));
                        j++;
                        byte[] CDOL = new byte[CDOLLength];
                        System.arraycopy(RAPDU, j, CDOL, 0, CDOLLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(IssuerPublicKeyCertificate)));
                        Log.d(Module, "CDOL1 8C " + new String(dataFormatterUtil.BCDToChar(CDOL)));
                        j += CDOLLength - 1;
                        cardData.setCDOL1(CDOL);
                    } else if (RAPDU[j] == (byte) 0x8D) {
                        j += 1;
                        byte[] CDOLLenBCD = new byte[1];
                        CDOLLenBCD[0] = RAPDU[j];
                        int CDOLLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(CDOLLenBCD));
                        j++;
                        byte[] CDOL = new byte[CDOLLength];
                        System.arraycopy(RAPDU, j, CDOL, 0, CDOLLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(IssuerPublicKeyCertificate)));
                        Log.d(Module, "CDOL2 8D " + new String(dataFormatterUtil.BCDToChar(CDOL)));
                        j += CDOLLength - 1;
                        cardData.setCDOL2(CDOL);
                    } else if (RAPDU[j] == (byte) 0x8E) {
                        j += 1;
                        byte[] CVMListLenBCD = new byte[1];
                        CVMListLenBCD[0] = RAPDU[j];
                        int CVMListLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(CVMListLenBCD));
                        j++;
                        byte[] CVMList = new byte[CVMListLength];
                        System.arraycopy(RAPDU, j, CVMList, 0, CVMListLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(IssuerPublicKeyCertificate)));
                        Log.d(Module, "CVM List 8E " + new String(dataFormatterUtil.BCDToChar(CVMList)));
                        j += CVMListLength - 1;
                        cardData.setCVMList(CVMList);
                    }
                }
            }
        }
        ODAData = new byte[ODADataBufferLength];
        System.arraycopy(ODADataBuffer, 0, ODAData, 0, ODADataBufferLength);
        Log.d(Module, "ODA Data : " + new String(dataFormatterUtil.BCDToChar(ODAData)));
        return true;
    }
}