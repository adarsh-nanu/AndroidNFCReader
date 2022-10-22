package com.example.myfirstapp.genericUtil;
import android.util.Log;

import java.math.BigInteger;
/**
 * Created by adarsh on 27/01/17.
 */

public class DoRSA {
    public static byte[] performRSA(byte[] dataBytes, byte[] expBytes, byte[] modBytes) {

        int inBytesLength = dataBytes.length;
        Log.d( "DoRSA", "Cert Length" + dataBytes.length );
        Log.d( "DoRSA", "Exp Length" + expBytes.length );
        Log.d( "DoRSA", "Mod Length" + modBytes.length );

        if (expBytes[0] >= (byte) 0x80) {
            //Prepend 0x00 to modulus
            byte[] tmp = new byte[expBytes.length + 1];
            tmp[0] = (byte) 0x00;
            System.arraycopy(expBytes, 0, tmp, 1, expBytes.length);
            expBytes = tmp;
        }

        if (modBytes[0] >= (byte) 0x80) {
            //Prepend 0x00 to modulus
            byte[] tmp = new byte[modBytes.length + 1];
            tmp[0] = (byte) 0x00;
            System.arraycopy(modBytes, 0, tmp, 1, modBytes.length);
            modBytes = tmp;
        }

        if (dataBytes[0] >= (byte) 0x80) {
            //Prepend 0x00 to signed data to avoid that the most significant bit is interpreted as the "signed" bit
            byte[] tmp = new byte[dataBytes.length + 1];
            tmp[0] = (byte) 0x00;
            System.arraycopy(dataBytes, 0, tmp, 1, dataBytes.length);
            dataBytes = tmp;
        }

        BigInteger exp = new BigInteger(expBytes);
        BigInteger mod = new BigInteger(modBytes);
        BigInteger data = new BigInteger(dataBytes);

        byte[] result = data.modPow(exp, mod).toByteArray();

        if (result.length == (inBytesLength+1) && result[0] == (byte)0x00) {
            //Remove 0x00 from beginning of array
            byte[] tmp = new byte[inBytesLength];
            System.arraycopy(result, 1, tmp, 0, inBytesLength);
            result = tmp;
        }
        return result;
    }
}