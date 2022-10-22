package com.example.myfirstapp.genericUtil;

import android.util.Log;

import com.example.myfirstapp.DataFormatterUtil;

/**
 * Created by adarsh on 07/02/17.
 */

public class ANSIPin{
    public byte[] PINBlock = new byte[8];
    DataFormatterUtil dfu = null;
    byte[] Pan = new byte[0];
    byte[] Pin = new byte[0];
    String Module = this.getClass().getSimpleName();
    public ANSIPin() {
        dfu = new DataFormatterUtil();
    }

    public byte[] format0(String pan, String pin) {
        Log.d( Module, "Pan [" + pan + "] PIN [ " + pin + " ]");
        char[] panLast12 = "0000000000000000".toCharArray();

        System.arraycopy(pan.substring(pan.length() - 13).toCharArray(), 0, panLast12, 4, 12);
        byte[] panLast12BCD = dfu.hexToBCD(dfu.asciiToHex(new String(panLast12)), 16);
        for (int i = 0; i < panLast12BCD.length; i++)
            System.out.println("panlast12 " + String.format("[%02X]", panLast12BCD[i]));

        char[] PINLength = ("00".substring(0, 2 - String.valueOf(pin.length()).length()) + String.valueOf(pin.length())).toCharArray();
        for (int i = 0; i < PINLength.length; i++)
            System.out.println("PINLength " + String.format("[%c]", PINLength[i]));

        char[] LLPin = new char[16];
        System.arraycopy(PINLength, 0, LLPin, 0, 2);
        System.arraycopy(pin.toCharArray(), 0, LLPin, 2, pin.length());
        for (int i = 2 + pin.length(); i < LLPin.length; i++)
            LLPin[i] = 15;
        for (int i = 0; i < LLPin.length; i++)
            System.out.println("LLPin " + String.format("[%c]", LLPin[i]));

        byte[] LLPINFF = dfu.hexToBCD(dfu.asciiToHex(new String(LLPin)), 16);
        for (int i = 0; i < LLPINFF.length; i++)
            System.out.println("LLPINFF " + String.format("[%02X]", LLPINFF[i]));

        byte[] clearPINBlock = new byte[8];
        for (int i = 0; i < 8; i++) {
            clearPINBlock[i] = (byte) (panLast12BCD[i] ^ LLPINFF[i]);
            System.out.println("clearPINBlock " + String.format("[%02X]", clearPINBlock[i]));
        }
        return clearPINBlock;
    }
}