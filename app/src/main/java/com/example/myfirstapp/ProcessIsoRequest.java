package com.example.myfirstapp;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by adarsh on 03/01/17.
 */

public class ProcessIsoRequest extends AsyncTask<Void, Void, Byte[]> {
    private String Module = this.getClass().getSimpleName();
    public Byte[] doInBackground(Void... somevoid)
    {
        byte[] Bitmap = new byte[16];
        boolean isSecBitmapReq = false;
        byte[] isoMessageData = new byte[512];
        ManageBitmap manageBitmap = new ManageBitmap();
        DataFormatterUtil dataformatterutil = new DataFormatterUtil();
        CardData cardData = new CardData();
        TerminalData terminalData = new TerminalData();


        int tcpIpLenPos = 0;
        int usedBytes = 0;

        byte[] TPDU = terminalData.getTPDU();
        Log.d( Module, "TPDU " + new String( dataformatterutil.BCDToChar( TPDU ) ) );

        byte[] MsgType = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex( String.valueOf( terminalData.getMsgtype() ) ), 4 );
        Log.d( Module, "MsgType " + new String( dataformatterutil.BCDToChar( MsgType ) ) );

        byte[] buffer;

        terminalData.setPcode("0");
        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex( terminalData.getPcode() ), 6 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy(buffer, 0, isoMessageData, usedBytes, buffer.length);
        manageBitmap.setBitOn( Bitmap, 3 );
        usedBytes += buffer.length;

        buffer = terminalData.getAmountAuthorized();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy(buffer, 0, isoMessageData, usedBytes, buffer.length);
        manageBitmap.setBitOn( Bitmap, 4 );
        usedBytes += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex( terminalData.getTrace() ), 6 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy(buffer, 0, isoMessageData, usedBytes, buffer.length);
        manageBitmap.setBitOn( Bitmap, 11 );
        usedBytes += buffer.length;

        //buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex( "0071" ), 4 );
        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex( "0051" ), 4 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy(buffer, 0, isoMessageData, usedBytes, buffer.length);
        manageBitmap.setBitOn( Bitmap, 22 );
        usedBytes += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex( "0026" ), 4 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy(buffer, 0, isoMessageData, usedBytes, buffer.length);
        manageBitmap.setBitOn( Bitmap, 24 );
        usedBytes += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex( "00" ), 2 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy(buffer, 0, isoMessageData, usedBytes, buffer.length);
        manageBitmap.setBitOn( Bitmap, 25 );
        usedBytes += buffer.length;

        buffer = cardData.getTrack2();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        byte[] bufferLen = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex( String.valueOf( new String( dataformatterutil.BCDToChar( buffer )).indexOf('F') ) ), 2 );
        //byte[] bufferLen = dataformatterutil.hexToBCD( dataformatterutil.intToHex( buffer.length), 2 );
        System.arraycopy(bufferLen, 0, isoMessageData, usedBytes, 1);
        usedBytes++;
        System.arraycopy(buffer, 0, isoMessageData, usedBytes, buffer.length);
        manageBitmap.setBitOn( Bitmap, 35 );
        usedBytes += buffer.length;

        buffer = dataformatterutil.charToByte( "91110001".toCharArray() );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy(buffer, 0, isoMessageData, usedBytes, buffer.length);
        manageBitmap.setBitOn( Bitmap, 41 );
        usedBytes += buffer.length;

        buffer = dataformatterutil.charToByte( "100010331      ".toCharArray() );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
            System.arraycopy(buffer, 0, isoMessageData, usedBytes, buffer.length);
        manageBitmap.setBitOn( Bitmap, 42 );
        usedBytes += buffer.length;

        byte[] emvData = new byte[512];
        int emvDataLen = 2;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9F0206"), 6 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = terminalData.getAmountAuthorized();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9F0306"), 6 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = terminalData.getAmountOther();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9F06"), 4 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = terminalData.getTermApplicationId();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        buffer = dataformatterutil.hexToBCD( dataformatterutil.intToHex( buffer.length) , 2);
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = terminalData.getTermApplicationId();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("8202"), 4 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = cardData.getAIP();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9F3602"), 6 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = cardData.getATC();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9F2608"), 6 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = cardData.getCryptogram();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9F2701"), 6 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer[0] = cardData.getCID();
        System.arraycopy( buffer, 0, emvData, emvDataLen, 1 );
        emvDataLen ++;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9F1A020634"), 10 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9505"), 4 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = terminalData.getTVR();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9A03"), 4 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = terminalData.getTermTxndate();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9C0100"), 6 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("5F2A020634"), 10 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9F3704"), 6 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = terminalData.getUnPredictableNumber();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        buffer = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex("9F10"), 4 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = cardData.getIssuerApplicationData();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        buffer = dataformatterutil.hexToBCD( dataformatterutil.intToHex( buffer.length) , 2);
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;
        buffer = cardData.getIssuerApplicationData();
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( buffer ) ) );
        System.arraycopy( buffer, 0, emvData, emvDataLen, buffer.length );
        emvDataLen += buffer.length;

        Log.d(Module, "Length " + ( emvDataLen-2 ));
        byte[] emvDataLenBCD = dataformatterutil.hexToBCD( dataformatterutil.asciiToHex( Integer.toString( emvDataLen-2) ), 4 );
        Log.d( Module, "" + new String( dataformatterutil.BCDToChar( emvDataLenBCD ) ) );
        System.arraycopy( emvDataLenBCD, 0, emvData, 0, 2 );
        Log.d(Module, "EMV Tags " + new String( dataformatterutil.BCDToChar( emvData ) ) );

        System.arraycopy( emvData, 0, isoMessageData, usedBytes, emvDataLen);
        usedBytes += emvDataLen;
        manageBitmap.setBitOn( Bitmap, 55 );

        byte[] isoMessage = new byte[ 2 + TPDU.length + MsgType.length + ( isSecBitmapReq?16:8 ) + usedBytes];
        byte[] isoMessageLenBCD = dataformatterutil.hexToBCD( dataformatterutil.intToHex( MsgType.length + TPDU.length + ( isSecBitmapReq?16:8 ) + usedBytes ), 4 );
        int isoMsg = 0;
        System.arraycopy( isoMessageLenBCD, 0, isoMessage, isoMsg, 2 );
        isoMsg += 2;
        System.arraycopy( TPDU, 0, isoMessage, isoMsg, TPDU.length );
        isoMsg += TPDU.length;
        System.arraycopy( MsgType, 0, isoMessage, isoMsg, 2 );
        isoMsg += MsgType.length;
        System.arraycopy( Bitmap, 0, isoMessage, isoMsg, ( isSecBitmapReq?16:8 ) );
        isoMsg += ( isSecBitmapReq?16:8 );
        System.arraycopy( isoMessageData, 0, isoMessage, isoMsg, usedBytes );

        Log.d(Module, "Final message " + new String( dataformatterutil.BCDToChar( isoMessage ) ) );
        return dataformatterutil.byteToByte( isoMessage );
    }
}