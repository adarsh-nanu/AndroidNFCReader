package com.example.myfirstapp;

import android.os.AsyncTask;
import android.util.Log;

import com.example.myfirstapp.genericUtil.BitManager;
import com.example.myfirstapp.genericUtil.CAPKeys;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by adarsh on 17/02/17.
 */

public class TerminalData {
    private static byte[] TVR = new byte[5];
    private static byte[] TTQ = new byte[4];
    private static byte[] UnPredictableNumber = new byte[4];
    private static int    Msgtype;
    private static byte[] TPDU = new byte[0];
    private static String Pcode;
    private static byte[] AmountAuthorized = new byte[0];
    private static byte[] AmountOther = new byte[0];
    private static String Trace;
    private static byte[] TermTxndate = new byte[0];
    private static byte[] TermApplicationId = new byte[0];
    private static byte[] TerminalDefaultActionCode = new byte[0];
    private static byte[] TerminalOnlineActionCode = new byte[0];
    private static byte[] TerminalDeclineActionCode = new byte[0];
    private static int    responseCode=0;
    private static byte[] CSU = new byte[0];
    private static byte[] Cryptogram = new byte[0];
    private static List<CAPKeys> capKeysList = new LinkedList<CAPKeys>();
    private static String RID = null;
    String Module = this.getClass().getSimpleName();

    public void clearTerminalData()
    {
        TVR = new byte[5];
        TTQ = new byte[4];
        UnPredictableNumber = new byte[4];
        TPDU = new byte[0];
        AmountAuthorized = new byte[0];
        AmountOther = new byte[0];
        TermTxndate = new byte[0];
        TermApplicationId = new byte[0];
        TerminalDefaultActionCode = new byte[0];
        TerminalOnlineActionCode = new byte[0];
        TerminalDeclineActionCode = new byte[0];
        CSU = new byte[0];
        Cryptogram = new byte[0];
        clearCAPKs();
    }

    public byte[] getCryptogram()
    {
        return Cryptogram;
    }
    public String getRID()
    {
        return RID;
    }

    public void setRID( String rid )
    {
        RID = rid;
        Log.d( Module, "RID Set " + RID );
    }

    public void dumpCAPKs( )
    {
        Log.d( Module, "dump ca keys");
        for( CAPKeys capKeys:capKeysList)
        {
            capKeys.tostring();
        };
    }

    public void clearCAPKs()
    {
        capKeysList.clear();
    }

    public int getPSType( String rid )
    {
        Log.d(Module, "GET PS Type of " + rid );
        for( CAPKeys capKeys:capKeysList)
        {
            capKeys.tostring();
            if( rid.equals( capKeys.getRID() ) )
            {
                return capKeys.getPSType();
            }
        }
        return 0;
    }

    public String getCAPK( String rid, String index )
    {
        Log.d(Module, "GET CAPK of " + rid + " and " + index );
        for( CAPKeys capKeys:capKeysList)
        {
            capKeys.tostring();
            if( rid.equals( capKeys.getRID() ) && index.equals( capKeys.getIndex() ) )
            {
                return capKeys.getKey();
            }
        }
        return null;
    }

    public void addCAPKToList( CAPKeys capKeys )
    {
        capKeysList.add( capKeys );
    }

    public void setCryptogram( byte[] cryptogram )
    {
        Cryptogram = new byte[cryptogram.length];
        System.arraycopy( cryptogram, 0, Cryptogram, 0, cryptogram.length );
    }

    public byte[] getCSU()
    {
        return CSU;
    }

    public void setCSU( byte[] csu )
    {
        CSU = new byte[csu.length];
        System.arraycopy( csu, 0, CSU, 0, csu.length );
    }

    public int getIssuerResponseCode()
    {
        return responseCode;
    }

    public void setIssuerResponseCode( int respcode )
    {
        responseCode = respcode;
    }

    public byte[] getTerminalDefaultActionCode()
    {
        return TerminalDefaultActionCode;
    }

    public void setTerminalDefaultActionCode( byte[] actionCode )
    {
        TerminalDefaultActionCode = new byte[5];
        System.arraycopy( actionCode, 0, TerminalDefaultActionCode, 0, 5 );
    }

    public byte[] getTerminalOnlineActionCode()
    {
        return TerminalOnlineActionCode;
    }

    public void setTerminalOnlineActionCode( byte[] actionCode )
    {
        TerminalOnlineActionCode = new byte[5];
        System.arraycopy( actionCode, 0, TerminalOnlineActionCode, 0, 5 );
    }

    public byte[] getTerminalDeclineActionCode()
    {
        return TerminalDeclineActionCode;
    }

    public void setTerminalDeclineActionCode( byte[] actionCode )
    {
        TerminalDeclineActionCode = new byte[5];
        System.arraycopy( actionCode, 0, TerminalDeclineActionCode, 0, 5 );
    }


    public byte[] getTermApplicationId()
    {
        return TermApplicationId;
    }

    public void setTermApplicationId( byte[] aid )
    {
        TermApplicationId = new byte[aid.length];
        System.arraycopy( aid, 0, TermApplicationId, 0, aid.length );
    }

    public byte[] getTermTxndate()
    {
        return TermTxndate;
    }

    public void setTermTxndate( byte[] date )
    {
        TermTxndate = new byte[ date.length];
        System.arraycopy( date, 0, TermTxndate, 0, date.length );
    }

    public byte[] getAmountOther()
    {
        return AmountOther;
    }

    public void setAmountOther( byte[] amount )
    {
        AmountOther = new byte[amount.length];
        System.arraycopy( amount, 0, AmountOther, 0, amount.length );
    }

    public byte[] getAmountAuthorized()
    {
        return AmountAuthorized;
    }

    public void setAmountAuthorized( byte[] amount )
    {
        AmountAuthorized = new byte[amount.length];
        System.arraycopy( amount, 0, AmountAuthorized, 0, amount.length );
    }

    public void setTrace( String trace )
    {
        Trace = trace;
    }

    public String getTrace()
    {
        return Trace;
    }

    public void setPcode( String pcode )
    {
        Pcode = pcode;
    }

    public String getPcode()
    {
        return Pcode;
    }

    public void setMsgtype( int msgtype)
    {
        Msgtype = msgtype;
    }

    public int getMsgtype()
    {
        return Msgtype;
    }

    public void setTPDU( byte[] tpdu )
    {
        TPDU = new byte[tpdu.length];
        System.arraycopy( tpdu, 0, TPDU, 0, tpdu.length );
    }

    public byte[] getTPDU()
    {
        return TPDU;
    }

    public byte[] getTVR()
    {
        return TVR;
    }

    public void setTVR(int Byte, int Bit )
    {
        BitManager bitManager = new BitManager();
        TVR[Byte-1] |= bitManager.setBiton( TVR[Byte-1], Bit );
    }

    public byte[] getTTQ()
    {
        return TTQ;
    }

    public void setTTQ( byte[] ttq )
    {
        TTQ = new byte[ttq.length];
        System.arraycopy( ttq, 0, TTQ, 0, 4 );
    }

    public byte[] getUnPredictableNumber()
    {
        return UnPredictableNumber;
    }

    public void setUnPredictableNumber( byte[] unPredictableNumber )
    {
        UnPredictableNumber = new byte[unPredictableNumber.length];
        System.arraycopy( unPredictableNumber, 0, UnPredictableNumber, 0, 4 );
    }
}
