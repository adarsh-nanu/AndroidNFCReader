package com.example.myfirstapp.genericUtil;

import android.util.Log;

import com.example.myfirstapp.DataFormatterUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.security.Key;

/**
 * Created by adarsh on 07/02/17.
 */


public class TDes {
    String KEY;
    DataFormatterUtil dfu = null;
    Cipher cipher = null;
    byte[] keyBCD;
    byte[] ePinBlock;
    byte[] PinBlock;
    Key key;
    String Module = this.getClass().getSimpleName();
    public TDes(String key, byte[] pinBlock) {
        dfu = new DataFormatterUtil();
        KEY = key;
        System.arraycopy(pinBlock, 0, PinBlock, 0, pinBlock.length);
    }

    public byte[] encrypt() {
        keyBCD = dfu.hexToBCD(dfu.asciiToHex(KEY), KEY.length());
        for (int i = 0; i < keyBCD.length; i++)
            System.out.println("KEY - " + String.format("%02X", keyBCD[i]));
        try {
            cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        } catch (Exception E) {
            System.out.println("Cipher.getInstance Exception : " + E);
        }
        key = new SecretKeySpec(keyBCD, "DESede");
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (Exception E) {
            System.out.println("Cipher.init Exception : " + E);
        }

        //PinBlock = dfu.hexToBCD( dfu.asciiToHex( PinBlock ), PinBlock.length );

        try {
            ePinBlock = cipher.doFinal(PinBlock);
        } catch (Exception E) {
            System.out.println("Cipher dofinal Exception : " + E);
        }
        //for( int i = 0; i < ePinBlock.length; i++ )
        //    System.out.println("ePINBlock - " +  String.format("%02X", ePinBlock[i] ) );
        Log.d("TDes", "Encrypted PIN Block " + dfu.BCDToChar(ePinBlock));
        return ePinBlock;
    }
}