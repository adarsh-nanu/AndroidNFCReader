package com.example.myfirstapp;

import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import com.example.myfirstapp.genericUtil.*;

/**
 * Created by adarsh on 13/01/17.
 */

public class GETProcessingOptions extends AsyncTask<IsoDep, Void, Boolean>{
    private byte[] SignedDynamicAppData = new byte[0];
    static byte[] PDOL = new byte[0];
    static int PDOLDataLength = 0;
    static byte[] TransactionData = new byte[0];
    static int TransactionDataLength = 0;
    String Module = this.getClass().getSimpleName();

    public Boolean doInBackground( IsoDep... isoDep)
    {
        DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        TerminalData terminalData = new TerminalData();
        byte[] TTQ = new byte[1];
        byte[] AmountAuthorized = new byte[1];
        byte[] OtherAmount = new byte[1];
        byte[] TerminalCountryCode = new byte[1];
        byte[] TransactionDate = new byte[1];
        byte[] TransactionCurrencyCode = new byte[1];
        byte[] TransactionType = new byte[1];
        byte[] UnPredictableNumber = new byte[0];
        byte[] AFL = new byte[0];
        int TTQLength = 0;
        int AmountAuthorizedLength = 0;
        int OtherAmountLength = 0;
        int TerminalCountryCodeLength = 0;
        int TransactionDateLength = 0;
        int UnPredictableNumberLength = 0;
        int TransactionCurrencyCodeLength = 0;
        int TransactionTypeLength = 0;
        boolean PDOLAvailable = false;
        PDOL = new byte[256];
        PDOLDataLength = 0;
        TerminalData terminalData1 = new TerminalData();
        //TapCard tapCard = new TapCard();
        int PSType = terminalData.getPSType( terminalData.getRID() );
        Log.d( Module, "Payment Scheme Type " + PSType );


        for( int i=0; i<SELECT.PDOL.length; i++)
        {
            PDOLAvailable = true;
            if( SELECT.PDOL[i] == (byte) 0x9F )
            {
                i++;
                if( SELECT.PDOL[i] == (byte) 0x66 )
                {
                    i++;
                    Log.d(Module, "Terminal Transaction Qualifier ");
                    byte[] TTQLenHex = new byte[1];
                    TTQLenHex[0] = SELECT.PDOL[i];
                    TTQLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( TTQLenHex) );
                    Log.d( Module, "TTQLength " + TTQLength );
                    TTQ = new byte[TTQLength];
                    BitManager bitManager = new BitManager();

                    TTQ[0] = bitManager.setBiton( TTQ[0], 6 );
                    Log.d(Module, "TTQ - EMV Mode supported");
                    TTQ[0] = bitManager.setBiton( TTQ[0], 3 );
                    Log.d(Module, "TTQ - Online PIN supported");
                    if( ( Double.parseDouble( new String( dataFormatterUtil.BCDToChar( terminalData.getAmountAuthorized() ) ) ) * .01 ) > 50 )
                    {
                        TTQ[1] = bitManager.setBiton( TTQ[1], 7 );
                        Log.d(Module, "TTQ - CVM Required");
                        TTQ[1] = bitManager.setBiton(TTQ[1], 7);
                        Log.d(Module, "TTQ - ARQC Required");
                        terminalData.setTVR( 4, 8 );
                    }
                    TTQ[2] = bitManager.setBiton( TTQ[2], 8 );
                    Log.d(Module, "TTQ - Issuer Update Processing Supported");
                    ///////////////testing
                    TTQ = dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex("B6000000"), 0 );
                    //////////////////////
                    Log.d(Module, "TTQ " + new String( dataFormatterUtil.BCDToChar( TTQ ) ) );
                    System.arraycopy(TTQ, 0, PDOL, PDOLDataLength, TTQ.length );
                    PDOLDataLength +=TTQ.length;
                    terminalData.setTTQ( TTQ );
                }
                else if( SELECT.PDOL[i] == 0x02)
                {
                    i++;
                    //Log.d( Module, "Amount Authorized" );
                    byte[] AmountAuthorizedLenHex = new byte[1];
                    AmountAuthorizedLenHex[0] = SELECT.PDOL[i];
                    AmountAuthorizedLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex(AmountAuthorizedLenHex) );
                    //Log.d( Module, "Amount Authorized Length " + AmountAuthorizedLength );
                    //AmountAuthorized = new byte[AmountAuthorizedLength];
                    AmountAuthorized = terminalData.getAmountAuthorized();
                    Log.d( Module, "Amount Authorized 9F02 " + new String( dataFormatterUtil.BCDToChar( AmountAuthorized ) ) );
                    System.arraycopy(AmountAuthorized, 0, PDOL, PDOLDataLength, AmountAuthorized.length );
                    PDOLDataLength+=AmountAuthorized.length;
                 }
                else if( SELECT.PDOL[i] == 0x03)
                {
                    i++;
                    //Log.d( Module, "Other Amount" );
                    byte[] OtherAmountLenHex = new byte[1];
                    OtherAmountLenHex[0] = SELECT.PDOL[i];
                    OtherAmountLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( OtherAmountLenHex) );
                    //Log.d( Module, "Other Amount Length " + OtherAmountLength );
                    OtherAmount = new byte[ OtherAmountLength ];
                    OtherAmount = dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex( "000000000000" ), 12 );
                    Log.d( Module, "Other Amount 9F03 " + new String( dataFormatterUtil.BCDToChar( OtherAmount ) ) );
                    System.arraycopy(OtherAmount, 0, PDOL, PDOLDataLength, OtherAmount.length );
                    PDOLDataLength += OtherAmount.length;
                    terminalData.setAmountOther( OtherAmount );
                }
                else if( SELECT.PDOL[i] == 0x1A)
                {
                    i++;
                    //Log.d( Module, "Terminal Country Code" );
                    byte[] TerminalCountryCodeLenHex = new byte[1];
                    TerminalCountryCodeLenHex[0] = SELECT.PDOL[i];
                    TerminalCountryCodeLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( TerminalCountryCodeLenHex) );
                    //Log.d( Module, "Terminal Country Code Length " + TerminalCountryCodeLength );
                    TerminalCountryCode = new byte[ TerminalCountryCodeLength ];
                    TerminalCountryCode = dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex( "0634" ), 4 );
                    Log.d( Module, "Terminal Country Code 9F1A " + new String( dataFormatterUtil.BCDToChar( TerminalCountryCode ) ) );
                    System.arraycopy(TerminalCountryCode, 0, PDOL, PDOLDataLength, TerminalCountryCode.length );
                    PDOLDataLength += TerminalCountryCode.length;
                }
                else if( SELECT.PDOL[i] == 0x37)
                {
                    i++;
                    //Log.d( Module, "Unpredictable Number" );
                    byte[] UnPredictableNumberLenHex = new byte[1];
                    UnPredictableNumberLenHex[0] = SELECT.PDOL[i];
                    UnPredictableNumberLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( UnPredictableNumberLenHex) );
                    //Log.d( Module, "Unpredictable Number Length " + UnPredictableNumberLength );
                    UnPredictableNumber = new byte[ UnPredictableNumberLength ];
                    RandomNumGenerator randomNumGenerator = new RandomNumGenerator();
                    UnPredictableNumber = dataFormatterUtil.hexToBCD( randomNumGenerator.getRandomNumber(), 8 );
                    Log.d( Module, "Unpredictable Number " + new String( dataFormatterUtil.BCDToChar(UnPredictableNumber) ) );
                    System.arraycopy(UnPredictableNumber, 0, PDOL, PDOLDataLength, UnPredictableNumber.length );
                    PDOLDataLength += UnPredictableNumber.length;
                    terminalData.setUnPredictableNumber( UnPredictableNumber );
                 }
            }
            else if( SELECT.PDOL[i] == 0x5F && SELECT.PDOL[i+1] == 0x2A )
            {
                i+=2;
                //Log.d( Module, "Transaction Currency Code" );
                byte[] TransactionCurrencyCodeLenHex = new byte[1];
                TransactionCurrencyCodeLenHex[0] = SELECT.PDOL[i];
                TransactionCurrencyCodeLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( TransactionCurrencyCodeLenHex) );
                //Log.d( Module, "Transaction Currency Code Length " + TransactionCurrencyCodeLength );
                //TransactionCurrencyCode = new byte[ TransactionCurrencyCodeLength ];
                TransactionCurrencyCode = dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex( "0634" ), 4 );
                Log.d( Module, "Transaction Currency Code 5F2A " + new String( dataFormatterUtil.BCDToChar( TransactionCurrencyCode ) ) );
                System.arraycopy(TransactionCurrencyCode, 0, PDOL, PDOLDataLength, TransactionCurrencyCode.length );
                PDOLDataLength+=TransactionCurrencyCode.length;
            }
            else if( SELECT.PDOL[i] == (byte) 0x9A)
            {
                i++;
                //Log.d( Module, "Transaction Date" );
                byte[] TransactionDateLenHex = new byte[1];
                TransactionDateLenHex[0] = SELECT.PDOL[i];
                TransactionDateLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( TransactionDateLenHex) );
                //Log.d( Module, "Transaction Date Length " + TransactionDateLength );
                //TransactionDate = new byte[ TransactionDateLength ];
                DateFormatter dateFormatter = new DateFormatter( "yyMMdd" );
                TransactionDate = dataFormatterUtil.hexToBCD( dateFormatter.getFormattedDate(), 6 );
                Log.d(Module, "Transaction Date 9C " + new String( dataFormatterUtil.BCDToChar( TransactionDate ) ) );
                System.arraycopy(TransactionDate, 0, PDOL, PDOLDataLength, TransactionDate.length );
                PDOLDataLength += TransactionDate.length;
                terminalData.setTermTxndate( TransactionDate );
            }
            else if( SELECT.PDOL[i] == (byte) 0x9C)
            {
                i++;
                //Log.d( Module, "Transaction Type" );
                byte[] TransactionTypeLenHex = new byte[1];
                TransactionTypeLenHex[0] = SELECT.PDOL[i];
                TransactionTypeLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( TransactionTypeLenHex) );
                //Log.d( Module, "Transaction Type Length " + TransactionTypeLength );
                //TransactionType = new byte[ TransactionTypeLength ];
                TransactionType = dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex( "00" ), 2 );
                Log.d( Module, "Transaction Type 9C " + new String( dataFormatterUtil.BCDToChar( TransactionType ) ) );
                System.arraycopy(TransactionType, 0, PDOL, PDOLDataLength, TransactionType.length );
                PDOLDataLength += TransactionType.length;
            }
        }

        Log.d(Module, "PDOL Data Length " + PDOLDataLength );
        int commandLength = 5;
        commandLength += 2;

        if( PDOLAvailable == true )
            commandLength += PDOLDataLength;
        byte[] command = new byte[commandLength+1];
        command[0] = (byte)0x80;
        command[1] = (byte)0xA8;
        command[2] = 0x00;
        command[3] = 0x00;
        int pos = 4;
        if( PDOLAvailable == true )
        {
            //if( PSType == 2 )
            if( true )
            {
                byte[] commandLengthBCD = dataFormatterUtil.hexToBCD( dataFormatterUtil.intToHex( PDOLDataLength+2 ), 0 );
                command[pos++] = commandLengthBCD[0];
                command[pos++] = (byte) 0x83;
                byte[] PDOLLengthBCD = dataFormatterUtil.hexToBCD(dataFormatterUtil.intToHex( PDOLDataLength ), 0);
                command[pos++] = PDOLLengthBCD[0];
            }
            else
            {
                byte[] commandLengthBCD = dataFormatterUtil.hexToBCD( dataFormatterUtil.intToHex( PDOLDataLength ), 0 );
                command[pos++] = commandLengthBCD[0];
            }

            System.arraycopy(PDOL, 0, command, pos, PDOLDataLength);
        }
        byte[] RAPDU = new byte[0];
        try
        {
            Log.d( Module, "CAPDU " + new String( dataFormatterUtil.BCDToChar(command) ) );
            RAPDU = isoDep[0].transceive( command );
            Log.d( Module, "RAPDU " + new String( dataFormatterUtil.BCDToChar(RAPDU) ) );
        }
        catch (Exception E)
        {
            Log.d(Module, "Exception Transeive()");
            return false;
        }

        CardData cardData = new CardData();
        TransactionData = new byte[512];
        TransactionDataLength = 0;
        byte[] swisw2 = new byte[2];

        swisw2[0] = RAPDU[RAPDU.length-2];
        swisw2[1] = RAPDU[RAPDU.length-1];
        if( swisw2[0] == (byte)0x90 && swisw2[1] == 0x00 )
            Log.d(Module, "Success response");
        else
        {
            Log.d(Module, "Fail response");
            return false;
        }
        ODA.SDADAvailable = false;
        for(int i=0; i<RAPDU.length; i++)
        {
            if( RAPDU[i] == 0x77 )
            {
                Log.d(Module, "Response message template Format 2 - 77");
                i++;
                BitManager bitManager = new BitManager();
                byte[] RespMsgTempForm2LenBCD = new byte[1];
                if( !bitManager.checkIsBitOn( RAPDU[i], 8) )
                    RespMsgTempForm2LenBCD[0] = RAPDU[i];
                else
                    RespMsgTempForm2LenBCD[0] = RAPDU[++i];
                i++;
                int RespMsgTempForm2Length = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( RespMsgTempForm2LenBCD ) );
                Log.d(Module, "Template 77 Length " + new String( dataFormatterUtil.BCDToChar( RespMsgTempForm2LenBCD) ) );
                //i++;
                byte[] RespMsgTempForm2 = new byte[ RespMsgTempForm2Length];
                System.arraycopy(RAPDU, i, RespMsgTempForm2, 0, RespMsgTempForm2Length);
                i+=RespMsgTempForm2Length-1;

                for( int k=0; k<RespMsgTempForm2Length; k++ )
                {
                    byte[] emvTag = new byte[2];
                    emvTag[0] = RespMsgTempForm2[k];
                    if( k+1 < RespMsgTempForm2Length )
                        emvTag[1] = RespMsgTempForm2[k+1];
                    if( emvTag[0] == (byte)0x9F && emvTag[1] == 0x4B )
                    {
                        k+=2;
                        //Log.d(Module, "Signed Dynamic Applicaton Data 9F4B");
                        byte[] SignedDynAppDataLenBCD = new byte[1];
                        SignedDynAppDataLenBCD[0] = RespMsgTempForm2[k];
                        int SignedDynAppDataLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( SignedDynAppDataLenBCD ) );
                        k++;
                        SignedDynamicAppData = new byte[ SignedDynAppDataLength];
                        System.arraycopy( RespMsgTempForm2, k, SignedDynamicAppData, 0, SignedDynAppDataLength );
                        k+=SignedDynAppDataLength-1;
                        Log.d( Module, "Signed Dynamic Application Data 9F4B " + new String( dataFormatterUtil.BCDToChar( SignedDynamicAppData ) ) );
                        cardData.setSDAD( SignedDynamicAppData );
                        ODA.SDADAvailable = true;
                    }
                    else if( emvTag[0] == (byte)0x9F && emvTag[1] == 0x36 )
                    {
                        k+=2;
                        //Log.d(Module, "ATC 9F36");
                        byte[] AppTxnCounterLenBCD = new byte[1];
                        AppTxnCounterLenBCD[0] = RespMsgTempForm2[k];
                        int AppTxnCounterLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( AppTxnCounterLenBCD ) );
                        k++;
                        byte[] AppTxnCounter = new byte[ AppTxnCounterLength];
                        System.arraycopy( RespMsgTempForm2, k, AppTxnCounter, 0, AppTxnCounterLength );
                        k+= AppTxnCounterLength-1;
                        System.arraycopy( emvTag, 0, TransactionData, TransactionDataLength, 2 );
                        TransactionDataLength += 2;
                        TransactionData[TransactionDataLength++] = AppTxnCounterLenBCD[0];
                        System.arraycopy( AppTxnCounter, 0, TransactionData, TransactionDataLength, AppTxnCounter.length);
                        TransactionDataLength += AppTxnCounter.length;
                        Log.d( Module, "Application Transaction Counter 9F36 " + new String( dataFormatterUtil.BCDToChar( AppTxnCounter )) );
                        cardData.setATC( AppTxnCounter );
                    }
                    else if( emvTag[0] == (byte)0x9F && emvTag[1] == 0x10 )
                    {
                        k+=2;
                        //Log.d(Module, "Issuer Application Data 9F10");
                        byte[] IssAppDataLenBCD = new byte[1];
                        IssAppDataLenBCD[0] = RespMsgTempForm2[k];
                        int IssAppDataLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( IssAppDataLenBCD ) );
                        k++;
                        byte[] IssAppData = new byte[ IssAppDataLength];
                        System.arraycopy( RespMsgTempForm2, k, IssAppData, 0, IssAppDataLength);
                        k += IssAppDataLength-1;
                        System.arraycopy( emvTag, 0, TransactionData, TransactionDataLength, 2 );
                        TransactionDataLength += 2;
                        TransactionData[TransactionDataLength++] = IssAppDataLenBCD[0];
                        System.arraycopy( IssAppData, 0, TransactionData, TransactionDataLength, IssAppData.length );
                        TransactionDataLength += IssAppData.length;
                        Log.d( Module, "Issuer Application Data 9F10 " + new String( dataFormatterUtil.BCDToChar( IssAppData ) ) );
                        cardData.setIssuerApplicationData( IssAppData );
                    }
                    else if( emvTag[0] == (byte)0x9F && emvTag[1] == 0x26 )
                    {
                        k+=2;
                        //Log.d(Module, "Cryptogram 9F26");
                        byte[] CryptgramLenBCD = new byte[1];
                        CryptgramLenBCD[0] = RespMsgTempForm2[k];
                        int CryptgramLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( CryptgramLenBCD ) );
                        k++;
                        byte[] Cryptgram = new byte[ CryptgramLength];
                        System.arraycopy( RespMsgTempForm2, k, Cryptgram, 0, CryptgramLength);
                        k += CryptgramLength-1;
                        System.arraycopy( emvTag, 0, TransactionData, TransactionDataLength, 2 );
                        TransactionDataLength += 2;
                        TransactionData[TransactionDataLength++] = CryptgramLenBCD[0];
                        System.arraycopy( Cryptgram, 0, TransactionData, TransactionDataLength, Cryptgram.length );
                        TransactionDataLength += Cryptgram.length;
                        Log.d( Module, "Cryptgram 9F26 " + new String( dataFormatterUtil.BCDToChar( Cryptgram ) ) );
                        cardData.setCryptogram( Cryptgram );
                    }
                    else if( emvTag[0] == (byte)0x9F && emvTag[1] == 0x27 )
                    {
                        k+=2;
                        //Log.d(Module, "CID 9F27");
                        byte[] CryptoInfoDataLenBCD = new byte[1];
                        CryptoInfoDataLenBCD[0] = RespMsgTempForm2[k];
                        int CryptoInfoDataLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( CryptoInfoDataLenBCD ) );
                        k++;
                        //BitManager bitManager = new BitManager();
                        byte CryptoInfoData = RespMsgTempForm2[k];
                        cardData.setCID( CryptoInfoData );
                        if( bitManager.checkIsBitOn( CryptoInfoData, 8) && !bitManager.checkIsBitOn( CryptoInfoData, 7 ) )
                        {
                            Log.d( Module, "CID 9F27 ARQC");
                            //tapCard.updateScreenList("ARQC");
                            TapCard.GoOnline = true;
                        }
                        else if ( !bitManager.checkIsBitOn( CryptoInfoData, 8 ) && bitManager.checkIsBitOn( CryptoInfoData, 7) )
                        {
                            Log.d( Module, "CID 9F27 TC");
                            //tapCard.updateScreenList("TC");
                        }
                        else if( !bitManager.checkIsBitOn( CryptoInfoData, 8 ) && !bitManager.checkIsBitOn( CryptoInfoData, 7) )
                        {
                            Log.d( Module,  "CID 9F27 AAC" );
                            TapCard.DeclineTransaction = true;
                            //tapCard.updateScreenList("AAC");
                        }
                        else if( bitManager.checkIsBitOn( CryptoInfoData, 8) && bitManager.checkIsBitOn( CryptoInfoData, 7) )
                            Log.d( Module, "CID 9F27 Reserved for future use");
                        System.arraycopy( emvTag, 0, TransactionData, TransactionDataLength , 2 );
                        TransactionDataLength += 2;
                        TransactionData[TransactionDataLength++] = CryptoInfoDataLenBCD[0];
                        TransactionData[TransactionDataLength++] = CryptoInfoData;
                        Log.d( Module, "CID 9F27" + new String( dataFormatterUtil.BCDToChar( CryptoInfoData) ) );
                    }
                    else if( emvTag[0] == (byte)0x9F && emvTag[1] == 0x71 )
                    {
                        k+=2;
                        //Log.d(Module, "Card Processing Requirements 9F71");
                        byte[] CardProcessingReqLenBCD = new byte[1];
                        CardProcessingReqLenBCD[0] = RespMsgTempForm2[k];
                        int CardProcessingReqLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( CardProcessingReqLenBCD ) );
                        //Log.d(Module, "Card Processing Requirements Length " + CardProcessingReqLength );
                        k++;
                        byte[] CardProcessingReq = new byte[ CardProcessingReqLength];
                        System.arraycopy( RespMsgTempForm2, k, CardProcessingReq, 0, CardProcessingReqLength );
                        k+=CardProcessingReqLength;
                        for( int j=0; j<CardProcessingReqLength; j++ )
                        {
                            switch( j )
                            {
                                case 0:
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 8 ) ) {
                                        Log.d(Module, "CPR Online PIN Required");
                                        TapCard.OnlinePinRequired = true;
                                        //tapCard.updateScreenList("Online PIN Required");
                                    }
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 7 ) )
                                        Log.d( Module, "CPR Signature Required" );
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 6 ) )
                                        Log.d( Module, "CPR PID Limit Reached - Loyalty transaction approved" );
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 5 ) )
                                        Log.d( Module, "CPR Consumer device CVM Performed" );
                                    break;
                                case 1:
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 8 ) )
                                        Log.d( Module, "CPR Switch other interface if unable to process online");
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 7 ) )
                                        Log.d( Module, "CPR Process online if CDA failed" );
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 6 ) )
                                        Log.d( Module, "CPR Decline/switch other interface if CDA failed" );
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 5 ) )
                                        Log.d( Module, "CPR Issuer Update Processing supported" );
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 4 ) )
                                        Log.d( Module, "CPR Process online if card expired" );
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 3 ) )
                                        Log.d( Module, "CPR Decline if card expired" );
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 2 ) )
                                        Log.d( Module, "CPR CVM Fallback to Signature allowed" );
                                    if( bitManager.checkIsBitOn( CardProcessingReq[j], 1 ) )
                                        Log.d( Module, "CPR CVM Fallback to No CVM allowed" );
                                    break;
                            }
                        }
                        //k--;
                        System.arraycopy( emvTag, 0, TransactionData, TransactionDataLength, 2 );
                        TransactionDataLength += 2;
                        TransactionData[TransactionDataLength++] = CardProcessingReqLenBCD[0];
                        System.arraycopy( CardProcessingReq, 0, TransactionData, TransactionDataLength, CardProcessingReq.length );
                        TransactionDataLength+= CardProcessingReq.length;
                        cardData.setCPR( CardProcessingReq );
                    }
                    else if( emvTag[0] == (byte)0x9F && emvTag[1] == 0x6C )
                    {
                        k+=2;
                        //Log.d(Module, "Card Transaction Qualifier 9F6C");
                        byte[] CardTxnQualifierLenBCD = new byte[1];
                        CardTxnQualifierLenBCD[0] = RespMsgTempForm2[k];
                        int CardTxnQualifierLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( CardTxnQualifierLenBCD ) );
                        k++;
                        byte[] CardTxnQualifier = new byte[ CardTxnQualifierLength];
                        System.arraycopy( RespMsgTempForm2, k, CardTxnQualifier, 0, CardTxnQualifierLength);
                        k += CardTxnQualifierLength-1;
                        System.arraycopy( emvTag, 0, TransactionData, TransactionDataLength, 2 );
                        TransactionDataLength += 2;
                        TransactionData[TransactionDataLength++] = CardTxnQualifierLenBCD[0];
                        System.arraycopy( CardTxnQualifier, 0, TransactionData, TransactionDataLength, CardTxnQualifier.length );
                        TransactionDataLength += CardTxnQualifier.length;
                        Log.d( Module, "Card Transaction Qualifier 9F6C " + new String( dataFormatterUtil.BCDToChar( CardTxnQualifier ) ) );
                        //cardData.setCryptogram( CardTxnQualifier );
                    }
                    else if (emvTag[0] == (byte) 0x5F && emvTag[1] == (byte) 0x34) {
                        //Log.d(Module, "Application PAN Sequence number 5F34");
                        k += 2;
                        byte[] AppPanSeqBCD = new byte[1];
                        AppPanSeqBCD[0] = RespMsgTempForm2[k];
                        int AppPanSeqLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(AppPanSeqBCD));
                        k++;
                        byte[] AppPanSeq = new byte[AppPanSeqLength];
                        System.arraycopy(RespMsgTempForm2, k, AppPanSeq, 0, AppPanSeqLength);
                        //System.out.println( new String( dataFormatterUtil.BCDToChar(AppPanSeq)));
                        Log.d(Module, "Application PAN Sequence number 5F34 " + new String(dataFormatterUtil.BCDToChar(AppPanSeq)));
                        k += AppPanSeqLength - 1;
                    }
                    else if (emvTag[0] == (byte) 0x57) {
                        //Log.d(Module, "Track 2 57");
                        k += 1;
                        byte[] Track2BCD = new byte[1];
                        Track2BCD[0] = RespMsgTempForm2[k];
                        int Track2Length = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(Track2BCD));
                        k++;
                        byte[] Track2 = new byte[Track2Length];
                        System.arraycopy(RespMsgTempForm2, k, Track2, 0, Track2Length);
                        System.out.println( Track2Length );
                        Log.d(Module, "Track 2 57 " + new String(dataFormatterUtil.BCDToChar(Track2)));
                        k += Track2Length - 1;
                        cardData.setTrack2(Track2);
                    }
                    else if( emvTag[0] == (byte)0x82 )
                    {
                        k+=1;
                        //Log.d(Module, "Application Interchange Profile 82");
                        byte[] AIPLenBCD = new byte[1];
                        AIPLenBCD[0] = RespMsgTempForm2[k];
                        int AIPLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( AIPLenBCD ) );
                        k++;
                        byte[] AIP = new byte[ AIPLength];
                        //BitManager bitManager = new BitManager();
                        System.arraycopy( RespMsgTempForm2, k, AIP, 0, AIPLength );
                        k += AIPLength-1;
                        if( !bitManager.checkIsBitOn( AIP[0], 8 ) )
                            Log.d( Module, "AIP 8 RFU" );
                        if( !bitManager.checkIsBitOn( AIP[0], 7 ) )
                            Log.d( Module, "AIP 7 SDA Not supported" );
                        else
                        {
                            Log.d( Module, "AIP SDA Supported ");
                            ODA.SDASupported = true;
                            //tapCard.updateScreenList("SDA Supported");
                        }
                        if( bitManager.checkIsBitOn( AIP[0], 6 ) )
                            Log.d( Module, "AIP 6 DDA supported (for C3-compliant card application only)" );
                        else
                        {
                            Log.d( Module, "AIP DDA Supported ");
                            ODA.DDASupported = true;
                            //tapCard.updateScreenList("DDA Supported");
                        }
                        if( bitManager.checkIsBitOn( AIP[0], 5 ) )
                            Log.d( Module, "AIP 5 Cardholder verification is supported" );
                        if( bitManager.checkIsBitOn( AIP[0], 4 ) )
                            Log.d( Module, "AIP 4 Terminal Risk Management not to be performed" );
                        if( bitManager.checkIsBitOn( AIP[0], 3 ) )
                            Log.d( Module, "AIP 3 External authenticate supported" );
                        else
                            Log.d( Module, "AIP 3 External authenticate is not supported" );
                        if( bitManager.checkIsBitOn( AIP[0], 1 ) ) {
                            Log.d(Module, "AIP 1 CDA Supported");
                            ODA.CDASupported = true;
                            //tapCard.updateScreenList("CDA Supported");
                        }
                        else
                            Log.d( Module, "AIP 1 CDA not supported" );
                        System.arraycopy( emvTag, 0, TransactionData, TransactionDataLength, 1 );
                        TransactionDataLength++;
                        TransactionData[TransactionDataLength++] = AIPLenBCD[0];
                        System.arraycopy( AIP, 0, TransactionData, TransactionDataLength, AIP.length);
                        TransactionDataLength += AIP.length;
                        Log.d( Module, "AIP 82 " + new String( dataFormatterUtil.BCDToChar( AIP ) ) );
                        cardData.setAIP( AIP );
                    }
                    else if( emvTag[0] == (byte)0x94 )
                    {
                        k+=1;
                        //Log.d(Module, "Application File Locator 94");
                        byte[] AFLLenBCD = new byte[1];
                        AFLLenBCD[0] = RespMsgTempForm2[k];
                        int AFLLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( AFLLenBCD ) );
                        k++;
                        AFL = new byte[ AFLLength];
                        System.arraycopy(RespMsgTempForm2, k, AFL, 0, AFLLength);
                        k+= AFLLength-1;
                        System.arraycopy( emvTag, 0, TransactionData, TransactionDataLength, 1 );
                        TransactionDataLength += 1;
                        TransactionData[TransactionDataLength++] = AFLLenBCD[0];
                        System.arraycopy( AFL, 0, TransactionData, TransactionDataLength, AFL.length);
                        TransactionDataLength += AFL.length;
                        Log.d( Module, "AFL 94 " + new String( dataFormatterUtil.BCDToChar( AFL )) );
                        cardData.setAFL( AFL );
                    }
                }
            }
        }
        return true;
    }
}