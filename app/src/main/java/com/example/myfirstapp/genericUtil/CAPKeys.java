package com.example.myfirstapp.genericUtil;

import android.util.Log;

/**
 * Created by adarsh on 03/03/17.
 */

public class CAPKeys {
    public static int DPAS = 0;
    public static int VISA = 1;
    public String RID = null;
    public String Index = null;
    public String Key = null;
    public int PSType = 0;
    String Module = this.getClass().getSimpleName();
    public CAPKeys(String rid, String index, String key, int pstype) {
        RID = rid;
        Index = index;
        Key = key;
        PSType = pstype;
    }

    public void tostring() {
        Log.d( Module, "RID " + RID );
        Log.d( Module, "Index " + Index );
        Log.d( Module, "Key " + Key );
        Log.d( Module, "Payment Scheme Type " + PSType );
    }

    public String getRID()
    {
        return RID;
    }

    public String getKey()
    {
        return Key;
    }

    public String getIndex()
    {
        return Index;
    }

    public int getPSType()
    {
        return PSType;
    }
}
