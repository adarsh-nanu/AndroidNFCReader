package com.example.myfirstapp;

import android.util.Log;

import com.example.myfirstapp.genericUtil.DateFormatter;

/**
 * Created by adarsh on 17/02/17.
 */

public class ProcessingRestrictions {
    public void processRestrictions( )
    {
        TapCard tapCard = new TapCard();
        CardData cardData = new CardData();
        TerminalData terminalData = new TerminalData();
        byte[] track2 = cardData.getTrack2();
        DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        String Track2 = new String( dataFormatterUtil.BCDToChar( track2 ) );
        int delimiter = Track2.indexOf( 'D' );
        if( delimiter == -1 )
            delimiter = Track2.indexOf( '=' );
        if( delimiter == -1)
            terminalData.setTVR(2, 7);
        else {
            char[] Trk2ExpDate = new char[4];
            System.arraycopy(Track2.toCharArray(), delimiter + 1, Trk2ExpDate, 0, 4);
            Log.d("PROC-RESTR", "Expiry Date " + new String( Trk2ExpDate ) );
            DateFormatter dateFormatter = new DateFormatter("yyMM");
            if ( Integer.parseInt( dateFormatter.getFormattedDate() ) > Integer.parseInt( new String( Trk2ExpDate ) ) ) {
                Log.d("PROC-RESTR", "Card expired");
                terminalData.setTVR(2, 7);
                tapCard.updateScreenList("Card Expired");
            }
        }

        Log.d("PROC-RESTR", "No Exception File available" );
    }
}
