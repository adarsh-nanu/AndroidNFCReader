package com.example.myfirstapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.app.PendingIntent;
import android.content.IntentFilter.MalformedMimeTypeException;

import com.example.myfirstapp.genericUtil.XMLParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public NfcAdapter nfcAdapter;
    public PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    static String acq_amount;
    Double Amount = 0.00;
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    String Module = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(Module, "onCreate");
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        DBManager dbManager = new DBManager(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = dbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("trace", 123);
        //sqLiteDatabase.insert("TraceSeq", null, contentValues);

        SQLiteDatabase readabledb = dbManager.getReadableDatabase();
        String[] output = {"trace"};
        Cursor cursor = readabledb.query("TraceSeq", output, null, null, null, null, null);
        List rows = new ArrayList<>();
        while (cursor.moveToNext()) {
            int traceno = cursor.getInt(cursor.getColumnIndexOrThrow("trace"));
            Log.d(Module, "Trace Number : " + traceno);
        }

        XMLParser capkList = new XMLParser();
        //DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        //byte[] IssuerPublicKeyModulus = new byte[0];

        try {
            InputStream in = (InputStream) getApplicationContext().getResources().openRawResource(R.raw.capks);
            TerminalData terminalData = new TerminalData();
            terminalData.clearCAPKs();
            capkList.parserInit(in);
            capkList.initCAPKs();
            //terminalData.dumpCAPKs();
        } catch (Exception E) {
            Log.d("main", "Exception read CAPK" + E);
            //TapCard.screenData.add("Exception read CAPK" + E);
            return;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("main", "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("main", "onResume");
    }

    public boolean isAmountZero()
    {
        Double amt0 = Amount * 100;
        String amt1 = String.format("%012.0f", amt0).toString();
        if( ( Amount = Double.parseDouble(String.format("%012.0f", amt0).toString()) * .01 ) == 0 )
            return true;
        else
            return false;
    }

    public void updateAmount(Integer newEntry) {
        Double amt0 = Amount * 100;
        String amt1 = String.format("%012.0f", amt0).toString().concat(newEntry.toString());

        Amount = Double.parseDouble(amt1) * .01;
        EditText editText = (EditText) findViewById(R.id.edit_message);
        editText.setText(String.format("%12.2f", Amount));
    }

    public void clearLastDigit(View view) {
        Double amt0 = Amount * 100;
        String amt1 = String.valueOf(amt0.longValue());
        String amt2 = amt1.substring(0, amt1.length() - 1);
        Double amt3 = Double.parseDouble(amt2.isEmpty() ? "0" : amt2);
        Amount = amt3 * .01;
        EditText editText = (EditText) findViewById(R.id.edit_message);
        editText.setText(String.format("%12.2f", Amount));
    }

    public void resetAmount(View view) {
        Amount = 0.00;
        EditText editText = (EditText) findViewById(R.id.edit_message);
        editText.setText(String.format("%12.2f", Amount));
    }

    public void input0(View view) {
        updateAmount(0);
    }

    public void input1(View view) {
        updateAmount(1);
    }

    public void input2(View view) {
        updateAmount(2);
    }

    public void input3(View view) {
        updateAmount(3);
    }

    public void input4(View view) {
        updateAmount(4);
    }

    public void input5(View view) {
        updateAmount(5);
    }

    public void input6(View view) {
        updateAmount(6);
    }

    public void input7(View view) {
        updateAmount(7);
    }

    public void input8(View view) {
        updateAmount(8);
    }

    public void input9(View view) {
        updateAmount(9);
    }

    public void sendMessage(View view) {
        if( isAmountZero() )
            return;
        Intent intent = new Intent(this, AskPaymentMethod.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        acq_amount = message;
        Double Amt = Double.parseDouble(message) * 100;
        TerminalData terminalData = new TerminalData();
        CardData cardData = new CardData();
        terminalData.clearTerminalData();
        cardData.clearCardData();
        terminalData.setAmountAuthorized(new DataFormatterUtil().hexToBCD(new DataFormatterUtil().asciiToHex(String.format("%012.0f", Double.parseDouble(message) * 100)), 12));
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}