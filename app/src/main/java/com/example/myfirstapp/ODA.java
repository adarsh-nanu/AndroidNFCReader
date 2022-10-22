package com.example.myfirstapp;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.example.myfirstapp.genericUtil.BitManager;
import com.example.myfirstapp.genericUtil.DateFormatter;
import com.example.myfirstapp.genericUtil.DoRSA;
import com.example.myfirstapp.genericUtil.XMLParser;

//import static com.example.myfirstapp.READRECORD.ICCPublicKeyExponent;
//import static com.example.myfirstapp.READRECORD.IssuerPublicKeyExponent;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;

/**
 * Created by adarsh on 28/01/17.
 */
//public class ODA
public class ODA extends AsyncTask<Void, Void, Boolean>
{
    static boolean SDASupported = false;
    static boolean DDASupported = false;
    static boolean CDASupported = false;
    static boolean SDADAvailable = false;
    private String Module = this.getClass().getSimpleName();

    public Boolean doInBackground(Void... v)
    {
        TapCard tapCard = new TapCard();
        TerminalData terminalData = new TerminalData();
        //XMLParser capkList = new XMLParser();
        DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        byte[] IssuerPublicKeyModulus = new byte[0];
        CardData cardData = new CardData();

        terminalData.dumpCAPKs();
        String IPKM = terminalData.getCAPK( terminalData.getRID(), new String( dataFormatterUtil.BCDToChar( cardData.getCAPKIndex() ) ) );
        Log.d(Module, "Issuer Public Key Modulus " +  IPKM );
        IssuerPublicKeyModulus = dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex( IPKM ), 0 );
        Log.d(Module, "Issuer Public Key Modulus " +  dataFormatterUtil.BCDToChar( IssuerPublicKeyModulus ) );
        byte[] IssuerPublicKeyCertificate = cardData.getIssuerPublicKeyCertificate();
        Log.d("ODA", "Issuer Public Key Certificate " + new String(dataFormatterUtil.BCDToChar(IssuerPublicKeyCertificate)));
        if( new String( dataFormatterUtil.BCDToChar( IssuerPublicKeyCertificate ) ) == null )
        {
            terminalData.setTVR(1, 8);
            return false;
        }

        byte[] IssuerPublicKeyExponent = cardData.getIssuerPublicKeyExponent();
        Log.d("ODA", "IssuerPublicKeyExponent " + new String( dataFormatterUtil.BCDToChar( IssuerPublicKeyExponent ) ) );
        if( new String( dataFormatterUtil.BCDToChar( IssuerPublicKeyExponent ) ) == null )
        {
            terminalData.setTVR(1, 8);
            return false;
        }

        byte[] PublicKeyCertificateContents = DoRSA.performRSA( IssuerPublicKeyCertificate, IssuerPublicKeyExponent, IssuerPublicKeyModulus);
        Log.d("ODA", "Public Key Certificate Contents " + new String(dataFormatterUtil.BCDToChar(PublicKeyCertificateContents)));
        int PublicKeyCertificateContentsLength = 0;
        int HashDataLength = 0;

        byte RecoveredDataHeader = PublicKeyCertificateContents[PublicKeyCertificateContentsLength++];
        Log.d("ODA", "Recovered Data Header " + new String(dataFormatterUtil.BCDToChar(RecoveredDataHeader)));
        if (RecoveredDataHeader != 0x6A) {
            tapCard.updateScreenList("Invalid Certificate Header");
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );
            return false;
        }

        byte RecoveredDataTrailer = PublicKeyCertificateContents[PublicKeyCertificateContents.length-1];
        Log.d("ODA", "Recovered Data Trailer " + new String( dataFormatterUtil.BCDToChar(RecoveredDataTrailer) ) );
        if (RecoveredDataTrailer != (byte) 0xBC) {
            tapCard.updateScreenList("Invalid Certificate Trailer");
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );return false;
        }

        byte CertificateFormat = PublicKeyCertificateContents[PublicKeyCertificateContentsLength++];
        Log.d("ODA", "Certificate Type " + new String( dataFormatterUtil.BCDToChar(CertificateFormat ) ) );
        HashDataLength += 1;

        byte[] IssuerIdentifier = new byte[4];
        System.arraycopy(PublicKeyCertificateContents, PublicKeyCertificateContentsLength, IssuerIdentifier, 0, 4);
        PublicKeyCertificateContentsLength += 4;
        HashDataLength += 4;
        Log.d("OAD", "Issuer Identifier " + new String(dataFormatterUtil.BCDToHex(IssuerIdentifier)));

        byte[] CertExpiryDate = new byte[2];
        System.arraycopy(PublicKeyCertificateContents, PublicKeyCertificateContentsLength, CertExpiryDate, 0, 2);
        PublicKeyCertificateContentsLength += 2;
        HashDataLength += 2;
        Log.d("ODA", "Certificate Expiry Date " + new String(dataFormatterUtil.BCDToChar(CertExpiryDate)));

        byte[] CertSerialNo = new byte[3];
        System.arraycopy(PublicKeyCertificateContents, PublicKeyCertificateContentsLength, CertSerialNo, 0, 3);
        PublicKeyCertificateContentsLength += 3;
        HashDataLength += 3;
        Log.d("ODA", "Certificate Serial " + new String(dataFormatterUtil.BCDToChar(CertSerialNo)));

        byte HashAlgoInd = PublicKeyCertificateContents[PublicKeyCertificateContentsLength++];
        HashDataLength += 1;
        Log.d("ODA", "Hash Algorithm indicator" + new String(dataFormatterUtil.BCDToChar(HashAlgoInd)));

        byte IssuerPKAlgoInd = PublicKeyCertificateContents[PublicKeyCertificateContentsLength++];
        HashDataLength += 1;
        Log.d("ODA", "Issuer PK Algorithm indicator" + new String(dataFormatterUtil.BCDToChar(IssuerPKAlgoInd)));

        byte IssuerPKLengthBCD = PublicKeyCertificateContents[PublicKeyCertificateContentsLength++];
        int IssPKLength = dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(IssuerPKLengthBCD)));
        HashDataLength += 1;
        Log.d("ODA", "Issuer PK Length " + new String(dataFormatterUtil.BCDToChar(IssuerPKLengthBCD)));

        byte IssuerPKExpolength = PublicKeyCertificateContents[PublicKeyCertificateContentsLength++];
        HashDataLength += 1;
        Log.d("ODA", "Issuer PK Exponent Length " + new String(dataFormatterUtil.BCDToChar(IssuerPKExpolength)));
        HashDataLength += 1;   //data

        byte[] IssuerPublicKeyRemainder = cardData.getIssuerPublicKeyRemainder();
        byte[] IssuerPublicKey = new byte[IssPKLength];
        System.arraycopy(PublicKeyCertificateContents, PublicKeyCertificateContentsLength, IssuerPublicKey, 0, PublicKeyCertificateContents.length - 21 - PublicKeyCertificateContentsLength);
        System.arraycopy(IssuerPublicKeyRemainder, 0, IssuerPublicKey, (PublicKeyCertificateContents.length - 21 - PublicKeyCertificateContentsLength), IssuerPublicKeyRemainder.length);
        PublicKeyCertificateContentsLength += (PublicKeyCertificateContents.length - 21 - PublicKeyCertificateContentsLength);
        HashDataLength += IssPKLength;
        Log.d("ODA", "Issuer PK " + new String(dataFormatterUtil.BCDToChar(IssuerPublicKey)));

        byte[] HashResult = new byte[20];
        System.arraycopy(PublicKeyCertificateContents, PublicKeyCertificateContentsLength, HashResult, 0, 20);
        PublicKeyCertificateContentsLength += 20;
        Log.d("ODA", "Hash in Certificate " + new String(dataFormatterUtil.BCDToChar(HashResult)));

        byte[] HashData = new byte[HashDataLength];
        int HashDataPos = 0;
        HashData[HashDataPos++] = CertificateFormat;
        System.arraycopy(IssuerIdentifier, 0, HashData, HashDataPos, 4);
        HashDataPos += 4;
        System.arraycopy(CertExpiryDate, 0, HashData, HashDataPos, 2);
        HashDataPos += 2;
        System.arraycopy(CertSerialNo, 0, HashData, HashDataPos, 3);
        HashDataPos += 3;
        HashData[HashDataPos++] = HashAlgoInd;
        HashData[HashDataPos++] = IssuerPKAlgoInd;
        HashData[HashDataPos++] = IssuerPKLengthBCD;
        HashData[HashDataPos++] = IssuerPKExpolength;
        System.arraycopy(IssuerPublicKey, 0, HashData, HashDataPos, IssPKLength);
        HashDataPos += IssPKLength;
        System.arraycopy(IssuerPublicKeyExponent, 0, HashData, HashDataPos, IssuerPublicKeyExponent.length);
        Log.d("ODA", "Hash Preparation Data " + new String(dataFormatterUtil.BCDToChar(HashData)));

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA1");
            crypt.reset();
            crypt.update(HashData);
            String ComputedHash = new String(dataFormatterUtil.BCDToChar(crypt.digest()));
            if (ComputedHash.equals(new String(dataFormatterUtil.BCDToChar(HashResult))))
                Log.d("ODA", "Issuer Public Key Signature Hash validation Success");
            else {
                Log.d("ODA", "Issuer Public Key Signature Hash validation failed");
                tapCard.updateScreenList("Invalid Issuer Public Key Signature Hash");
                if( SDADAvailable )
                    terminalData.setTVR( 1, 3 );
                else terminalData.setTVR( 1, 4 );return false;
            }
        } catch (Exception E) {
            Log.d("ODA", "Exception Issuer Public Key Signature Hash validation" + E);
            tapCard.updateScreenList("Exception Issuer Public Key Signature Hash validation " + E );
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );return false;
        }

        byte[] ICCPublicKeyCertificate = cardData.getICCPublicKeyCertificate();
        byte[] ICCPublicKeyExponent = cardData.getICCPublicKeyExponent();

        Log.d("ODA", "ICC Public Key Certificate " + new String(dataFormatterUtil.BCDToChar( ICCPublicKeyCertificate)));
        Log.d("ODA", "ICC Public Key Exponent " + new String(dataFormatterUtil.BCDToChar( ICCPublicKeyExponent)));
        byte[] ICCPublicKeyCertificateContents = DoRSA.performRSA( ICCPublicKeyCertificate, ICCPublicKeyExponent, IssuerPublicKey);
        Log.d("ODA", "ICC Public Key Certificate Contents " + new String(dataFormatterUtil.BCDToChar(ICCPublicKeyCertificateContents)));

        int ICCPublicKeyCertificateContentsPos = 0;
        if (ICCPublicKeyCertificateContents[ICCPublicKeyCertificateContentsPos++] != 0x6A) {
            Log.d("ODA", "ICC Public certificate header invalid ");
            tapCard.updateScreenList("Invalid ICC Public certificate header" );
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );
            return false;
        }

        HashDataLength = 0;
        if (ICCPublicKeyCertificateContents[ICCPublicKeyCertificateContentsPos++] != 0x04) {
            Log.d("ODA", "Invalid ICC Certificate format");
            tapCard.updateScreenList("Invalid ICC Certificate Format" );
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );
            return false;
        }
        HashDataLength += 1;
        byte[] applicationPan = new byte[10];
        System.arraycopy(ICCPublicKeyCertificateContents, ICCPublicKeyCertificateContentsPos, applicationPan, 0, 10);
        Log.d("ODA", "Application PAN " + new String(dataFormatterUtil.BCDToChar(applicationPan)));
        ICCPublicKeyCertificateContentsPos += 10;
        HashDataLength += 10;

        byte[] ICCCertExpDate = new byte[2];
        System.arraycopy(ICCPublicKeyCertificateContents, ICCPublicKeyCertificateContentsPos, ICCCertExpDate, 0, 2);
        Log.d("ODA", "ICC Certificate Expiration Date " + new String(dataFormatterUtil.BCDToChar(ICCCertExpDate)));
        ICCPublicKeyCertificateContentsPos += 2;
        HashDataLength += 2;

        byte[] ICCCertSerNo = new byte[3];
        System.arraycopy(ICCPublicKeyCertificateContents, ICCPublicKeyCertificateContentsPos, ICCCertSerNo, 0, 3);
        Log.d("ODA", "ICC Certificate Serial Number " + new String(dataFormatterUtil.BCDToChar(ICCCertSerNo)));
        ICCPublicKeyCertificateContentsPos += 3;
        HashDataLength += 3;

        byte ICCHashAlgoInd = ICCPublicKeyCertificateContents[ICCPublicKeyCertificateContentsPos++];
        Log.d("ODA", "ICC hash Algorith Ind " + new String(dataFormatterUtil.BCDToChar(ICCHashAlgoInd)));
        HashDataLength += 1;

        byte ICCPublicKeyAlgoInd = ICCPublicKeyCertificateContents[ICCPublicKeyCertificateContentsPos++];
        Log.d("ODA", "ICC Public Key Algorithm Ind " + new String(dataFormatterUtil.BCDToChar(ICCPublicKeyAlgoInd)));
        HashDataLength += 1;

        byte ICCPublicKeylength = ICCPublicKeyCertificateContents[ICCPublicKeyCertificateContentsPos++];
        Log.d("ODA", "ICC Public Key Length " + new String(dataFormatterUtil.BCDToChar(ICCPublicKeylength)));
        HashDataLength += 1;
        HashDataLength += dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCPublicKeylength)));

        byte ICCPublicKeyExplength = ICCPublicKeyCertificateContents[ICCPublicKeyCertificateContentsPos++];
        Log.d("ODA", "ICC Public Key Exp Length " + new String(dataFormatterUtil.BCDToChar(ICCPublicKeyExplength)));
        HashDataLength += 1;
        HashDataLength += dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCPublicKeyExplength)));

        byte[] ICCPublicKeyRemainder = cardData.getIccPublicKeyRemainder();
        byte[] ICCPublicKey = new byte[dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCPublicKeylength)))];
        System.arraycopy(ICCPublicKeyCertificateContents, ICCPublicKeyCertificateContentsPos, ICCPublicKey, 0, ICCPublicKeyCertificateContents.length - ICCPublicKeyCertificateContentsPos - 20 - 1);
        Log.d("ODA", "ICC Pub Key(no rem) " + new String(dataFormatterUtil.BCDToChar(ICCPublicKey)));
        System.arraycopy( ICCPublicKeyRemainder, 0, ICCPublicKey, (ICCPublicKeyCertificateContents.length - ICCPublicKeyCertificateContentsPos - 20 - 1), ICCPublicKeyRemainder.length);
        ICCPublicKeyCertificateContentsPos += (ICCPublicKeyCertificateContents.length - ICCPublicKeyCertificateContentsPos - 20 - 1);
        Log.d("ODA", "ICC Public Key " + new String(dataFormatterUtil.BCDToChar(ICCPublicKey)));

        byte[] ICCHashData = new byte[20];
        System.arraycopy(ICCPublicKeyCertificateContents, ICCPublicKeyCertificateContentsPos, ICCHashData, 0, 20);
        Log.d("ODA", "ICC Hash Data " + new String(dataFormatterUtil.BCDToChar(ICCHashData)));
        ICCPublicKeyCertificateContentsPos += 20;
        HashDataLength += READRECORD.ODAData.length;

        if (ICCPublicKeyCertificateContents[ICCPublicKeyCertificateContentsPos] != (byte) 0xBC) {
            Log.d("ODA", "ICC Public certificate trailer invalid ");
            tapCard.updateScreenList("Invalid ICC Public certificate trailer" );
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );
            return false;
        }

        HashData = new byte[HashDataLength];
        HashDataPos = 0;
        HashData[HashDataPos++] = 0x04;
        System.arraycopy(applicationPan, 0, HashData, HashDataPos, applicationPan.length);
        HashDataPos += applicationPan.length;
        System.arraycopy(ICCCertExpDate, 0, HashData, HashDataPos, ICCCertExpDate.length);
        HashDataPos += ICCCertExpDate.length;
        System.arraycopy(ICCCertSerNo, 0, HashData, HashDataPos, ICCCertSerNo.length);
        HashDataPos += ICCCertSerNo.length;
        HashData[HashDataPos++] = ICCHashAlgoInd;
        HashData[HashDataPos++] = ICCPublicKeyAlgoInd;
        HashData[HashDataPos++] = ICCPublicKeylength;
        HashData[HashDataPos++] = ICCPublicKeyExplength;
        System.arraycopy(ICCPublicKey, 0, HashData, HashDataPos, dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCPublicKeylength))));
        HashDataPos += dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCPublicKeylength)));
        System.arraycopy( ICCPublicKeyExponent, 0, HashData, HashDataPos, dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCPublicKeyExplength))));
        HashDataPos += dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCPublicKeyExplength)));
        System.arraycopy(READRECORD.ODAData, 0, HashData, HashDataPos, READRECORD.ODAData.length);

        Log.d("ODA", "Prepared Hash data " + new String(dataFormatterUtil.BCDToChar(HashData)));

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA1");
            crypt.reset();
            crypt.update(HashData);
            String ComputedHash = new String(dataFormatterUtil.BCDToChar(crypt.digest()));
            if (ComputedHash.equals(new String(dataFormatterUtil.BCDToChar(ICCHashData))))
                Log.d("ODA", "ICC Public Key Signature Hash validation Success");
            else {
                Log.d("ODA", "ICC Public Key Signature Hash validation failed");
                tapCard.updateScreenList("ICC Public Key Signature Hash validation failed" );
                if( SDADAvailable )
                    terminalData.setTVR( 1, 3 );
                else terminalData.setTVR( 1, 4 );
                return false;
            }

        } catch (Exception E) {
            Log.d("ODA", "Exception ICC Public Key Signature Hash " + E);
            tapCard.updateScreenList("Exception ICC Public Key Signature Hash " + E );
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );
            return false;
        }

        if( !SDADAvailable)
            return true;

        byte[] SignedDynamicAppData = cardData.getSDAD();
        if ( SignedDynamicAppData.length != ICCPublicKey.length) {
            Log.d("ODA", "Signed Dynamic Data Signature and ICC Public Key length mismatch " + SignedDynamicAppData.length + " " + ICCPublicKey.length);
            tapCard.updateScreenList("DDA and ICC Public Key length mismatch " );
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );
            return false;
        }

        Log.d("ODA", "SignedDynamicAppData " + new String( dataFormatterUtil.BCDToChar( SignedDynamicAppData ) ) );
        Log.d("ODA", "ICCPublicKeyExponent " + new String( dataFormatterUtil.BCDToChar( ICCPublicKeyExponent ) ) );
        Log.d("ODA", "ICCPublicKey " + new String( dataFormatterUtil.BCDToChar( ICCPublicKey ) ) );
        byte[] SDADCertificateContents = DoRSA.performRSA( SignedDynamicAppData, ICCPublicKeyExponent, ICCPublicKey);
        Log.d("ODA", "SDAD Certificate Contents " + new String(dataFormatterUtil.BCDToChar(SDADCertificateContents)));

        int SDADCertificateContentsPos = 0;
        HashDataPos = 0;

        if (SDADCertificateContents[0] != (byte) 0x6A) {
            Log.d("ODA", "SDAD Certificate Header != 6A");
            tapCard.updateScreenList("Invalid SDAD Certificate Header " );
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );return false;
        }
        if (SDADCertificateContents[SDADCertificateContents.length - 1] != (byte) 0xBC) {
            Log.d("ODA", "SDAD Certificate Trailer != BC");
            tapCard.updateScreenList("Invalid SDAD Certificate Trailer " );
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );return false;
        }
        SDADCertificateContentsPos++;
        byte SDADCertFormat = SDADCertificateContents[SDADCertificateContentsPos++];
        Log.d("ODA", "SDAD Certificate Format " + new String(dataFormatterUtil.BCDToChar(SDADCertFormat)));
        HashDataPos++;

        byte SDADHashAlgoInd = SDADCertificateContents[SDADCertificateContentsPos++];
        Log.d("ODA", "SDAD Certificate Hash Algo Ind " + new String(dataFormatterUtil.BCDToChar(SDADHashAlgoInd)));
        HashDataPos++;

        byte ICCDynamicDataLength = SDADCertificateContents[SDADCertificateContentsPos++];
        Log.d("ODA", "ICC Dynamic Data Length " + new String(dataFormatterUtil.BCDToChar(ICCDynamicDataLength)));
        HashDataPos++;

        byte ICCDynNumLength = SDADCertificateContents[SDADCertificateContentsPos++];
        Log.d("ODA", "ICC Dynamic Number Length " + new String(dataFormatterUtil.BCDToChar(ICCDynNumLength)));
        HashDataPos++;

        byte[] ICCDynamicNumber = new byte[dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCDynNumLength)))];
        System.arraycopy(SDADCertificateContents, SDADCertificateContentsPos, ICCDynamicNumber, 0, dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCDynNumLength))));
        SDADCertificateContentsPos += dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCDynNumLength)));
        Log.d("ODA", "ICC Dynamic Number " + new String(dataFormatterUtil.BCDToChar(ICCDynamicNumber)));
        HashDataPos += dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCDynNumLength)));

        byte CryptoInfoData = SDADCertificateContents[SDADCertificateContentsPos++];
        Log.d("ODA", "Cryptogram Information Data " + new String(dataFormatterUtil.BCDToChar(CryptoInfoData)));
        HashDataPos++;

        byte[] Cryptogram = new byte[8];
        System.arraycopy(SDADCertificateContents, SDADCertificateContentsPos, Cryptogram, 0, 8);
        Log.d("ODA", "Cryptogram " + new String(dataFormatterUtil.BCDToChar(Cryptogram)));
        cardData.setCryptogram( Cryptogram );
        SDADCertificateContentsPos += 8;
        HashDataPos += 8;

        byte[] TxnDataHash = new byte[20];
        System.arraycopy(SDADCertificateContents, SDADCertificateContentsPos, TxnDataHash, 0, 20);
        Log.d("ODA", "Transaction data hash " + new String(dataFormatterUtil.BCDToChar(TxnDataHash)));
        SDADCertificateContentsPos += 20;
        HashDataPos += 20;
        try {
            byte[] TransactionData = new byte[GETProcessingOptions.PDOLDataLength + GETProcessingOptions.TransactionDataLength];
            System.arraycopy(GETProcessingOptions.PDOL, 0, TransactionData, 0, GETProcessingOptions.PDOLDataLength);
            System.arraycopy(GETProcessingOptions.TransactionData, 0, TransactionData, GETProcessingOptions.PDOLDataLength, GETProcessingOptions.TransactionDataLength);
            Log.d("ODA", "Transaction Data " + new String(dataFormatterUtil.BCDToChar(TransactionData)));
            MessageDigest crypt = MessageDigest.getInstance("SHA1");
            crypt.reset();
            crypt.update(TransactionData);
            String ComputedHash = new String(dataFormatterUtil.BCDToChar(crypt.digest()));
            if (ComputedHash.equals(new String(dataFormatterUtil.BCDToChar(TxnDataHash))))
                Log.d("ODA", "Transaction Data Hash validation Success");
            else {
                Log.d("ODA", "Transaction Data Hash validation failed " );
                tapCard.updateScreenList("Transaction Data Hash validation failed " );
                if( SDADAvailable )
                    terminalData.setTVR( 1, 3 );
                else terminalData.setTVR( 1, 4 );return false;
            }

        } catch (Exception E) {
            Log.d("ODA", "Exception Transaction Data Hash validation" + E);
            tapCard.updateScreenList("Exception Transaction Data Hash validation" + E );
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );return false;
        }

        byte[] UnPredictableNumber = terminalData.getUnPredictableNumber();
        Log.d("ODA", "Unpredictable Number " + new String( dataFormatterUtil.BCDToChar( UnPredictableNumber ) ) );
        byte[] PadString = new byte[SDADCertificateContents.length - SDADCertificateContentsPos - 20 - 1];
        System.arraycopy(SDADCertificateContents, SDADCertificateContentsPos, PadString, 0, SDADCertificateContents.length - SDADCertificateContentsPos - 20 - 1);
        Log.d("ODA", "Pad String " + new String(dataFormatterUtil.BCDToChar(PadString)));
        SDADCertificateContentsPos += PadString.length;
        HashDataPos += PadString.length;
        HashDataPos += UnPredictableNumber.length;

        byte[] SDADHash = new byte[20];
        System.arraycopy(SDADCertificateContents, SDADCertificateContentsPos, SDADHash, 0, 20);
        Log.d("ODA", "SDAD Hash " + new String(dataFormatterUtil.BCDToChar(SDADHash)));

        HashData = new byte[HashDataPos];
        HashDataPos = 0;
        HashData[HashDataPos++] = SDADCertFormat;
        HashData[HashDataPos++] = SDADHashAlgoInd;
        HashData[HashDataPos++] = ICCDynamicDataLength;
        HashData[HashDataPos++] = ICCDynNumLength;
        System.arraycopy(ICCDynamicNumber, 0, HashData, HashDataPos, dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCDynNumLength))));
        HashDataPos += dataFormatterUtil.hexToInt(new String(dataFormatterUtil.BCDToHex(ICCDynNumLength)));
        HashData[HashDataPos++] = CryptoInfoData;
        System.arraycopy(Cryptogram, 0, HashData, HashDataPos, 8);
        HashDataPos += 8;
        System.arraycopy(TxnDataHash, 0, HashData, HashDataPos, 20);
        HashDataPos += 20;
        System.arraycopy(PadString, 0, HashData, HashDataPos, PadString.length);
        HashDataPos += PadString.length;
        System.arraycopy( UnPredictableNumber, 0, HashData, HashDataPos, UnPredictableNumber.length);
        Log.d("ODA", "Hash String " + new String(dataFormatterUtil.BCDToChar(HashData)));

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA1");
            crypt.reset();
            crypt.update(HashData);
            String ComputedHash = new String(dataFormatterUtil.BCDToChar(crypt.digest()));
            if (ComputedHash.equals(new String(dataFormatterUtil.BCDToChar(SDADHash))))
                Log.d("ODA", "Signed Dynamic Application Data Signature Hash validation Success");
            else {
                Log.d("ODA", "Signed Dynamic Application Data Signature Hash validation failed");
                tapCard.updateScreenList("Signed Dynamic Application Data Signature Hash validation failed" );
                if( SDADAvailable )
                    terminalData.setTVR( 1, 3 );
                else terminalData.setTVR( 1, 4 );
                return false;
            }
        } catch (Exception E) {
            Log.d("ODA", "Exception Signed Dynamic Application Data Signature Hash validation" + E);
            tapCard.updateScreenList(" Exception Signed Dynamic Application Data Signature Hash validation " + E );
            if( SDADAvailable )
                terminalData.setTVR( 1, 3 );
            else terminalData.setTVR( 1, 4 );
            return false;
        }
        return true;
    }
}