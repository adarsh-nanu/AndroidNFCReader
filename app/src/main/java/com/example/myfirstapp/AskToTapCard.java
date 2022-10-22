package com.example.myfirstapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.nfc.Tag;
/**
 * Created by adarsh on 05/01/17.
 */

public class AskToTapCard extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("AskToTapCard", "NFC Detected");
    }
}
