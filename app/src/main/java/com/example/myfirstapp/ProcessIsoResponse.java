package com.example.myfirstapp;

import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by adarsh on 24/02/17.
 */

public class ProcessIsoResponse {
    public boolean parseISOMessage(byte[] isoMessage) {
        String Module = this.getClass().getSimpleName();
        DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        TerminalData terminalData = new TerminalData();
        int position = 0;
        byte[] TPDU = new byte[5];
        boolean issuerApproved = false;

        System.arraycopy(isoMessage, position, TPDU, 0, 5);
        Log.d("Response ", "TPDU " + new String(dataFormatterUtil.BCDToChar(TPDU)));
        position += TPDU.length;

        byte[] MsgType = new byte[2];
        System.arraycopy(isoMessage, position, MsgType, 0, 2);
        Log.d("Response ", "MsgType " + new String(dataFormatterUtil.BCDToChar(MsgType)));
        position += MsgType.length;

        byte[] PBitmap = new byte[8];
        byte[] SBitmap = new byte[16];
        int bitmapSize = 64;
        System.arraycopy(isoMessage, position, PBitmap, 0, 8);
        Log.d("Response", "Primary Bitmap " + new String(dataFormatterUtil.BCDToChar(PBitmap)));
        position += 8;
        ManageBitmap manageBitmap = new ManageBitmap();
        if (manageBitmap.checkIsBitOn(PBitmap, 1)) {
            System.arraycopy(PBitmap, 0, SBitmap, 0, 8);
            System.arraycopy(isoMessage, position, SBitmap, 8, 8);
            Log.d("Response", "Secondary Bitmap " + new String(dataFormatterUtil.BCDToChar(SBitmap)));
            position += 8;
            bitmapSize *= 2;
        }

        for (int i = 1; i <= bitmapSize; i++) {
            if (manageBitmap.checkIsBitOn((bitmapSize == 64) ? PBitmap : SBitmap, i)) {
                switch (i) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        byte[] Pcode = new byte[3];
                        System.arraycopy(isoMessage, position, Pcode, 0, 3);
                        Log.d("Response ", "Pcode " + new String(dataFormatterUtil.BCDToChar(Pcode)));
                        position += 3;
                        break;
                    case 4:
                        byte[] AmountAuthorized = new byte[6];
                        System.arraycopy(isoMessage, position, AmountAuthorized, 0, 6);
                        Log.d("Response ", "Amount Authorized " + new String(dataFormatterUtil.BCDToChar(AmountAuthorized)));
                        position += 6;
                        break;
                    case 11:
                        byte[] Trace = new byte[3];
                        System.arraycopy(isoMessage, position, Trace, 0, 3);
                        Log.d("Response ", "Trace " + new String(dataFormatterUtil.BCDToChar(Trace)));
                        position += 3;
                        break;
                    case 12:
                        byte[] Local_Time = new byte[3];
                        System.arraycopy(isoMessage, position, Local_Time, 0, 3);
                        Log.d("Response ", "Local Time " + new String(dataFormatterUtil.BCDToChar(Local_Time)));
                        position += 3;
                        break;
                    case 13:
                        byte[] Local_Date = new byte[2];
                        System.arraycopy(isoMessage, position, Local_Date, 0, 2);
                        Log.d("Response ", "Local Date " + new String(dataFormatterUtil.BCDToChar(Local_Date)));
                        position += 2;
                        break;
                    case 24:
                        byte[] bufer1 = new byte[2];
                        System.arraycopy(isoMessage, position, bufer1, 0, 2);
                        Log.d("Response ", "Buffer " + new String(dataFormatterUtil.BCDToChar(bufer1)));
                        position += 2;
                        break;
                    case 37:
                        byte[] RetrefNum = new byte[12];
                        System.arraycopy(isoMessage, position, RetrefNum, 0, 12);
                        Log.d("Response ", "Retrieval Reference Number " + new String(dataFormatterUtil.byteToChar(RetrefNum)));
                        position += 12;
                        break;
                    case 38:
                        byte[] AuthNum = new byte[6];
                        System.arraycopy(isoMessage, position, AuthNum, 0, 6);
                        Log.d("Response ", "Auth Number " + new String(dataFormatterUtil.byteToChar(AuthNum)));
                        position += 6;
                        break;
                    case 39:
                        byte[] Respcode = new byte[2];
                        System.arraycopy(isoMessage, position, Respcode, 0, 2);
                        Log.d("Response ", "Response Code " + new String(dataFormatterUtil.byteToChar(Respcode)));
                        position += 2;
                        terminalData.setIssuerResponseCode( Integer.parseInt( new String(dataFormatterUtil.byteToChar(Respcode)) ) );
                        break;
                    case 41:
                        byte[] TerminalId = new byte[8];
                        System.arraycopy(isoMessage, position, TerminalId, 0, 8);
                        Log.d("Response ", "Terminal ID " + new String(dataFormatterUtil.byteToChar(TerminalId)));
                        position += 8;
                        break;
                    case 55:
                        byte[] emvDataLen = new byte[2];
                        System.arraycopy(isoMessage, position, emvDataLen, 0, 2);
                        position += 2;
                        Log.d("Response ", "emv data len " + new String(dataFormatterUtil.BCDToChar(emvDataLen)));
                        byte[] emvData = new byte[Integer.parseInt(new String(dataFormatterUtil.BCDToChar(emvDataLen)))];
                        System.arraycopy(isoMessage, position, emvData, 0, emvData.length);
                        Log.d("Response ", "Emv Data " + new String(dataFormatterUtil.BCDToChar(emvData)));
                        position += emvData.length;
                        for (int j = 0; j < emvData.length; j++) {
                            if (emvData[j] == (byte) 0x91) {
                                Log.d(Module, "Issuer Authentication Data");
                                j++;
                                byte[] IADLenBCD = new byte[1];
                                IADLenBCD[0] = emvData[j++];
                                int IADLen = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(IADLenBCD));
                                Log.d(Module, "IAD Length " + IADLen);
                                byte[] Cryptogram = new byte[IADLen - 2];
                                System.arraycopy(emvData, j, Cryptogram, 0, IADLen - 2);
                                Log.d(Module, "Cryptogram " + new String( dataFormatterUtil.BCDToChar( Cryptogram) ) );
                                terminalData.setCryptogram( Cryptogram );
                                j += IADLen - 2;
                                byte[] CSU = new byte[2];
                                System.arraycopy(emvData, j, CSU, 0, 2);
                                Log.d(Module, "CSU " + new String( dataFormatterUtil.BCDToChar( CSU) ) );
                                terminalData.setCSU( CSU );
                                j += 2;
                            }
                        }
                }
            }
        }
        return issuerApproved;
    }
}
