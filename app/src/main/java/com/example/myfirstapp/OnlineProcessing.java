package com.example.myfirstapp;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by adarsh on 24/02/17.
 */

public class OnlineProcessing extends AsyncTask<Byte[], Void, Boolean>
{
    TCPClient tcpClient = null;
    DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
    String Module = this.getClass().getSimpleName();

    public void init( String IP, int Port, int TimeOut)
    {
        try {
            //tcpClient = new TCPClient("192.168.212.177", 4001, 10000);
            tcpClient = new TCPClient( IP, Port, TimeOut );
        }
        catch ( Exception E)
        {
            Log.d( Module, "Exception in socket/stream initialization" );
            return;
        }
    }

    public Boolean doInBackground( Byte[]... outData )
    {
        String IP = new String( dataFormatterUtil.byteToChar( dataFormatterUtil.ByteTobyte( outData[0] ) ) );
        int Port = Integer.parseInt( new String( dataFormatterUtil.byteToChar( dataFormatterUtil.ByteTobyte( outData[1] ) ) ) );
        int Timeout = Integer.parseInt( new String( dataFormatterUtil.byteToChar( dataFormatterUtil.ByteTobyte( outData[2] ) ) ) );
        Log.d( Module, "Connect " + IP + " : " + Port + " timeout " + Timeout);
        init( IP, Port, Timeout );
        tcpClient.updateOutData( dataFormatterUtil.ByteTobyte( outData[3] ) );
        tcpClient.sendData();
        byte[] isoResponse = tcpClient.receiveData();
        ProcessIsoResponse processIsoResponse = new ProcessIsoResponse();
        processIsoResponse.parseISOMessage( isoResponse );
        return new Boolean( true );
    }

}
