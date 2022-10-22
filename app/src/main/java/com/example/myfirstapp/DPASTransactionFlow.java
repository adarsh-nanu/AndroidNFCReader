package com.example.myfirstapp;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import static com.example.myfirstapp.TapCard.ApproveOffline;
import static com.example.myfirstapp.TapCard.DeclineTransaction;
import static com.example.myfirstapp.TapCard.GoOnline;

/**
 * Created by adarsh on 08/03/17.
 */

public class DPASTransactionFlow extends AppCompatActivity{
    private IsoDep isoDep = null;
    private String Module = this.getClass().getSimpleName();

    public DPASTransactionFlow(IsoDep isodep )
    {
        isoDep = isodep;
    }

    void process()
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

        tapCard.updateScreenList( "Start Terminal Action Analysis");
        terminalData.setTerminalOnlineActionCode( dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex( "2060008000" ), 0 ) );

        new TerminalRiskManagement().execute();

        tapCard.updateScreenList( "Terminal Action Analysis  Complete");
        Log.d( Module, "Decline " + DeclineTransaction + " Online " + GoOnline + " Offline " + ApproveOffline );

        if( DeclineTransaction == true )
        {
            tapCard.updateScreenList("Decline offline" );
            return;
        }
        else if( GoOnline == true )
        {
            tapCard.updateScreenList("Go Online" );
            PINPad pinPad = new PINPad();
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
                Log.d("Tapcard", "iso message prep/send failed " + E);
            }

        }
        else if( ApproveOffline == true )
        {
            tapCard.updateScreenList("Approved Offline" );
        }
        Log.d( "tapcard", "onNewIntent exit" );

    }
}