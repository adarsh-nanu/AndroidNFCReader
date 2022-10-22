package com.example.myfirstapp;

import android.util.Log;

/**
 * Created by adarsh on 03/03/17.
 */

public class CardAIDList {
    private String AID = null;
    String Module = this.getClass().getSimpleName();
    public CardAIDList( String aid )
    {
        AID = aid;
    }

    public String getAID()
    {
        return AID;
    }

    public void tostring()
    {
        Log.d( Module, "AID " + AID );
    }

}
