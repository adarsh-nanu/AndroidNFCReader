package com.example.myfirstapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

//import static com.example.myfirstapp.READRECORD.IssuerPublicKeyExponent;

/**
 * Created by adarsh on 08/01/17.
 */

public class TapCard extends AppCompatActivity {
    public NfcAdapter nfcAdapter;
    static ArrayList<String> screenData = new ArrayList<String>(100);
    static ArrayAdapter<String> adapter;
    public static boolean OnlinePinRequired = false;
    public static boolean DeclineTransaction = false;
    public static boolean GoOnline = false;
    public static boolean ApproveOffline = false;
    public static String RID;
    public static String CAPK_Index;
    String Module = this.getClass().getSimpleName();
    private static IsoDep isoDep = null;
    public Context getTapCardContext()
    {
        return getApplicationContext();
    }

    public void setIsoDep(IsoDep isodep)
    {
        isoDep = isodep;
    }

    public IsoDep getIsoDep()
    {
        return isoDep;
    }

    public void TransactionLog( String statusupdate)
    {
        screenData.add(statusupdate);
        adapter = new ArrayAdapter<String>(this, R.layout.listlayout, screenData);
        ListView screenupdateList = (ListView) findViewById( R.id.statusScreen );
        screenupdateList.setAdapter( adapter);
    }

    public void updateScreenList( String statusupdate )
    {
        screenData.add( statusupdate);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tapcard);
        nfcAdapter = NfcAdapter.getDefaultAdapter( this );
        TerminalData terminalData = new TerminalData();
        DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        terminalData.setMsgtype( 200 );
        terminalData.setTPDU( dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex("600026A139"), 0 ) );
        screenData.clear();
        TransactionLog( "Waiting for Card");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(Module, "onPause");

    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(Module, "onResume");
        if (this.nfcAdapter != null)
        {
            nfcAdapter.enableForegroundDispatch(this,
                    PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0),
                    new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) },
                    new String[][] { new String[] { IsoDep.class.getName() }});
            Log.d( Module, "foreground dispatch enabled");
        }
        else Log.d( Module, "nfcAdapter is null");
    }

    @Override
    protected void onNewIntent( Intent intent) {
        byte[] sw1sw2 = new byte[2];
        super.onNewIntent(intent);
        Log.d(Module, "onNewIntent");
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        IsoDep isoDep = IsoDep.get(tagFromIntent);
        setIsoDep( isoDep );
        DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        TerminalData terminalData = new TerminalData();
        try {
            isoDep.connect();
            byte[] historicalBytes = isoDep.getHistoricalBytes();
            Log.d(Module, "historical bytes " + new String(dataFormatterUtil.BCDToChar(historicalBytes)));
        } catch (Exception E) {
            System.out.println("Exception in connect and read historical bytes" + E);
            return;
        }

        SELECT select = new SELECT();
        CardData cardData = new CardData();
        cardData.clearCardAIDList();
        TapCard tapCard = new TapCard();
        tapCard.updateScreenList( "Sending PPSE");
        byte[] command = dataFormatterUtil.stringToBytes("2PAY.SYS.DDF01");

        try {
            byte[] selectResponse = select.SELECTProcessing(isoDep, command);
            if (select.selectResponseProcessing(selectResponse) != true) {
                tapCard.updateScreenList("Failed ");
                return;
            }
        }
        catch( Exception E)
        {
            tapCard.updateScreenList("Exception in SELECT " + E);
            E.printStackTrace(System.out);
            return;
        }

        String AID = null;
        List<CardAIDList> cardAIDLists = cardData.getCardAIDLists();
        for( CardAIDList obj:cardAIDLists)
        {
            obj.tostring();
            AID = obj.getAID();
            break;
        }

        try {
            byte[] selectResponse = select.SELECTProcessing(isoDep, dataFormatterUtil.hexToBCD(dataFormatterUtil.asciiToHex(AID), 0));
            if (select.selectResponseProcessing(selectResponse) != true) {
                tapCard.updateScreenList("Failed ");
                return;
            }
        }catch (Exception E)
        {
            tapCard.updateScreenList("Exception in SELECT " + E);
            E.printStackTrace(System.out);
            return;
        }

        //DPASTransactionFlow dpastransactionFlow = new DPASTransactionFlow( isoDep );
        //dpastransactionFlow.process();

        Intent aidintent = new Intent(this, qVSDCTransactionFlow.class);
        startActivity( aidintent);
        /*
        qVSDCTransactionFlow qvSDCTransactionFlow = new qVSDCTransactionFlow(isoDep);
        try {
            qvSDCTransactionFlow.process();
        }
        catch( Exception E )
        {
            Log.d(Module, "Exception in qVSDCTransactionFlow " + E);
            E.printStackTrace(System.out);
        }
        */
        Log.d( Module, "after transaction flow" );
    }
}