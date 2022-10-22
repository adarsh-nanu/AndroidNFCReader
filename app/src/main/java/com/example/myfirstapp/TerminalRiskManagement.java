package com.example.myfirstapp;

import android.os.AsyncTask;
import android.util.Log;

import com.example.myfirstapp.genericUtil.BitManager;

/**
 * Created by adarsh on 14/02/17.
 */

public class TerminalRiskManagement extends AsyncTask<Void, Void, Void> {
    public Void doInBackground(Void... vOid) {
        String Module = this.getClass().getSimpleName();
        TerminalData terminalData = new TerminalData();
        CardData cardData = new CardData();
        byte[] TVR = terminalData.getTVR();
        BitManager bitManager = new BitManager();
        Void nothing = null;
        //is CDA failed
        DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        Log.d(Module, "TVR " + new String(dataFormatterUtil.BCDToChar(terminalData.getTVR())));
        if (bitManager.checkIsBitOn(TVR[1 - 1], 3)) {
            Log.d(Module, "CDA is failed");
            byte[] cpr = cardData.getCPR();
            //Decline/switch other interface if CDA failed
            if (bitManager.checkIsBitOn(cpr[2 - 1], 6)) {
                Log.d(Module, "Decline since no other interface available");
                TapCard.DeclineTransaction = true;  //no other interface supported
                return nothing;
            } else {
                //Process online if CDA failed
                if (bitManager.checkIsBitOn(cpr[2 - 1], 7)) {
                    Log.d(Module, "Process Online if CDA Failed");
                    TapCard.GoOnline = true;
                    return nothing;
                } else {
                    byte[] ttq = terminalData.getTTQ();
                    //Offline-only reader
                    if (!bitManager.checkIsBitOn(ttq[1 - 1], 4)) {
                        Log.d(Module, "Process Online");
                        TapCard.GoOnline = true;
                        return nothing;
                    } else {
                        Log.d(Module, "Decline since no other interface available");
                        TapCard.DeclineTransaction = true; //no other interface supported
                        return nothing;
                    }
                }
            }
        } else if (bitManager.checkIsBitOn(TVR[2 - 1], 7)) {
            Log.d(Module, "Expired Application");
            byte[] cpr = cardData.getCPR();
            //Decline if card expired
            if (bitManager.checkIsBitOn(cpr[2 - 1], 3)) {
                Log.d(Module, "Decline");
                TapCard.DeclineTransaction = true;
                return nothing;
            } else {
                //Process online if card expired
                if (bitManager.checkIsBitOn(cpr[2 - 1], 4)) {
                    Log.d(Module, "Process Online if card expired");
                    TapCard.GoOnline = true;
                    return nothing;
                } else {
                    byte[] ttq = terminalData.getTTQ();
                    //Offline-only reader
                    if (!bitManager.checkIsBitOn(ttq[1 - 1], 4)) {
                        Log.d(Module, "Proceed Online");
                        TapCard.GoOnline = true;
                        return nothing;
                    } else {
                        Log.d(Module, "Decline Transaction");
                        TapCard.DeclineTransaction = true; //no other interface supported
                        return nothing;
                    }
                }
            }
        } else {
            byte[] ttq = terminalData.getTTQ();
            //Online Cryptogram required
            if (bitManager.checkIsBitOn(ttq[2 - 1], 8)) {
                Log.d(Module, "Online Cryptogram required");
                byte cid = cardData.getCID();
                if (!bitManager.checkIsBitOn(cid, 8) && bitManager.checkIsBitOn(cid, 7)) {
                    Log.d(Module, "TC Decline Transaction");
                    TapCard.DeclineTransaction = true;
                    return nothing;
                } else {
                    if (bitManager.checkIsBitOn(cid, 8) && !bitManager.checkIsBitOn(cid, 7)) {
                        Log.d(Module, "ARQC Proceed Online");
                        TapCard.GoOnline = true;
                        return nothing;
                    } else {
                        byte[] tvr = terminalData.getTVR();
                        if (bitManager.checkIsBitOn(tvr[1 - 1], 3)) {
                            Log.d(Module, "CDA failed - Decline Transaction");
                            TapCard.DeclineTransaction = true; //no other interface supported
                            return nothing;
                        } else {
                            Log.d(Module, "Process Online if CDA Failed");
                            TapCard.ApproveOffline = true;
                            return nothing;
                        }
                    }
                }
            }
        }
        byte[] tvr = terminalData.getTVR();
        byte[] tac_online = terminalData.getTerminalOnlineActionCode();
        for( int i=0; i<5; i++ )
            if( ( tvr[i] & tac_online[i] ) != 0x00 )
            {
                TapCard.ApproveOffline = false;
                TapCard.GoOnline = true;
                Log.d( Module, "Terminal decides to go online");
                break;
            }
        return nothing;
    }
}