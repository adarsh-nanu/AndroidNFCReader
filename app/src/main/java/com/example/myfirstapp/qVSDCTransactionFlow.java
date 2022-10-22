package com.example.myfirstapp;

import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static com.example.myfirstapp.TapCard.ApproveOffline;
import static com.example.myfirstapp.TapCard.DeclineTransaction;
import static com.example.myfirstapp.TapCard.GoOnline;

/**
 * Created by adarsh on 08/03/17.
 */

public class qVSDCTransactionFlow extends AppCompatActivity{
    private IsoDep isoDep = null;
    private String Module = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qvsdctransactionflow);
        Log.d(Module, "onCreate");
        TapCard tapCard = new TapCard();
        isoDep = tapCard.getIsoDep();
        try {
            process();
        }
        catch (Exception E)
        {
            Log.d(Module, "Exception from qVSDCTransactionFlow " + E);
            return;
        }
    }

    public qVSDCTransactionFlow()
    {}

    /*
    public qVSDCTransactionFlow()
    {
        isoDep = isodep;
    }
    */

    void process() throws Exception
    {
        DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        TapCard tapCard = new TapCard();
        TerminalData terminalData = new TerminalData();
        GETProcessingOptions getProcessingOptions = new GETProcessingOptions();
        try {
            boolean result = getProcessingOptions.execute(isoDep).get();
            if (result == false)
            {
                Log.d("tapcard", "GET PO Failed");
                tapCard.updateScreenList("GET PO Failed ");
                return;
            }
        }
        catch(Exception E )
        {
            tapCard.updateScreenList("Exception GETPO thread" + E );
            return;
        }

        READRECORD readrecord = new READRECORD();
        try
        {
            boolean result = readrecord.execute( isoDep ).get();
            if( result == false ) {
                Log.d("tapcard", "GET READ Failed");
                tapCard.updateScreenList("READ Failed ");
                return;
            }
        }
        catch (Exception E)
        {
            tapCard.updateScreenList("Exception READ thread" + E );
            return;
        }

        tapCard.updateScreenList( "You can remove your card now");

        if( DeclineTransaction == true )
        {
            tapCard.updateScreenList( "Card Decline");
            return;
        }

        terminalData.setTVR(1, 8);
        if( ODA.SDADAvailable ) {
            ODA oda = new ODA();
            try {
                if (oda.execute().get() == false) {
                    tapCard.updateScreenList("ODA Fail");
                }
            } catch (Exception E) {
                tapCard.updateScreenList("Exception ODA thread" + E);
                return;
            }
        }
        else
            terminalData.setTVR(1, 8);

        ProcessingRestrictions processingRestrictions = new ProcessingRestrictions();
        processingRestrictions.processRestrictions();
        tapCard.updateScreenList( "Processing Restrictions Complete");
        Log.d(Module, "Processing Restrictions Complete");


        //tapCard.updateScreenList( "Start Terminal Action Analysis");
        //Log.d(Module, "Start Terminal Action Analysis");
        terminalData.setTerminalOnlineActionCode( dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex( "A060008000" ), 0 ) );
        //Log.d(Module, "Terminal Action Analysis Complete");


        Log.d(Module, "Start Terminal Risk Management");
        TerminalRiskManagement terminalRiskManagement = new TerminalRiskManagement();
        terminalRiskManagement.execute();
        Log.d(Module, "Terminal Risk Management complete");

        //tapCard.updateScreenList( "Terminal Action Analysis  Complete" );
        Log.d( Module, "Decline " + TapCard.DeclineTransaction + " Online " + TapCard.GoOnline + " Offline " + TapCard.ApproveOffline );

        if( DeclineTransaction )
        {
            tapCard.updateScreenList("Decline offline" );
            Log.d( Module, "Decline offline" );
            return;
        }
        else if( GoOnline )
        {
            tapCard.updateScreenList("Go Online" );
            Log.d(Module, "Go Online" );
            PINPad pinPad = new PINPad();
            //Bundle savedInstanceState = new Bundle();
            //super.onCreate(savedInstanceState);
            pinPad.show( getFragmentManager().beginTransaction(), "Enter PIN");
            try {
                terminalData.setTrace( "1" );
                Byte[] isoRequest = new ProcessIsoRequest().execute().get();
                Byte[] IP = dataFormatterUtil.byteToByte( dataFormatterUtil.charToByte( "192.168.212.177".toCharArray() ) );
                Byte[] Port = dataFormatterUtil.byteToByte( dataFormatterUtil.charToByte( String.valueOf( 4001).toCharArray() ) );
                Byte[] TimeOut = dataFormatterUtil.byteToByte( dataFormatterUtil.charToByte( String.valueOf( 10000 ).toCharArray() ) );
                new OnlineProcessing().execute( IP, Port, TimeOut, isoRequest );
            }
            catch ( Exception E)
            {
                Log.d(Module, "iso message prep/send failed " + E);
                tapCard.updateScreenList("iso message prep/send failed " + E);
            }

        }
        else if( ApproveOffline )
        {
            tapCard.updateScreenList("Approved Offline" );
            Log.d(Module, "Approved Offline" );
        }
    }
}