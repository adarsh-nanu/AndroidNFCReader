package com.example.myfirstapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class AskPaymentMethod extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public AskToTapCard askToTapCard;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.askpaymentmethod);

        Intent intent = getIntent();
        String message = "QR. " + intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);
        setContentView(R.layout.askpaymentmethod);
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d("AskPaymentmethod", "onPause");
    /*    try {
            this.unregisterReceiver(askToTapCard);
            Log.d("AskPaymentmethod", "unregisterReceiver");
        }
        catch (Exception E)
        {
            Log.d("Exception:AskPaymentMet", E.toString() );
        }
    */
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d("AskPaymentmethod", "onStop");
    /*    try {
            this.unregisterReceiver(askToTapCard);
            Log.d("AskPaymentmethod", "unregisterReceiver");
        }
        catch (Exception E)
        {
            Log.d("Exception:AskPaymentMet", E.toString() );
        }
        */
    }

    public void getcardtap(View view)
    {
        Log.d( "getcardtap", " enter");
        Intent intent = new Intent(this, TapCard.class);
        startActivity(intent);
        Log.d( "getcardtap", " exit");
    }
}