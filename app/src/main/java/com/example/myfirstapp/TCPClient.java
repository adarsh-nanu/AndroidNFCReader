package com.example.myfirstapp;
import java.net.*;
import java.io.*;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;
import android.util.Log;
/**
 * Created by adarsh on 02/01/17.
 */

public class TCPClient
{
	Socket clientSocket;
	DataInputStream is;
	DataOutputStream os;
	byte[] outputData = new byte[0];
	DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
	private void resetOutData()
	{
		outputData = new byte[0];
	}

	public void updateOutData( byte[] data ) {
		outputData = new byte[data.length];
		System.arraycopy(data, 0, outputData, 0, data.length);
		Log.d("TCPClient", "Out data length " + outputData.length );
	}

	public TCPClient( String IP, int Port, int timeOut ) throws Exception
	{
		try
		{
			clientSocket = new Socket( IP, Port );
		}
		catch( Exception E )
		{
			System.out.println( "Exception socket: " + E );
			return;
		}
		Log.d( "TCPClient", "Socket Created");
		try
		{
			is = new DataInputStream( clientSocket.getInputStream() );
			clientSocket.setSoTimeout( timeOut );
		}
		catch( Exception E )
		{
			System.out.println( "Exception InputStream : " + E );
			return;
		}
		Log.d( "TCPClient", "Input stream created" );
		try
		{
			os = new DataOutputStream( clientSocket.getOutputStream() );
		}
		catch( Exception E )
		{
			System.out.println( "Exception OutputStream : " + E );
			return;
		}
		Log.d( "TCPClient", "Output Stream created" );
	}

	public void sendData()
	{
		try
		{
			os.write( outputData, 0, outputData.length );
			System.out.println( "Sent[" + outputData.length + "] bytes" );
			os.flush();
		}
		catch( Exception E )
		{
			System.out.println( "Exception sendData : " + E );
		}
		//System.out.println( "waiting for response " );
	}

	public byte[] receiveData()
	{

		try
		{
			byte[] tcpHeaderLength = new byte[2];
			System.out.println( "Waiting for response " );
			while( is.read( tcpHeaderLength, 0, 2 ) != -1)
			{
				Log.d("TCP", "msg length " + new String( dataFormatterUtil.BCDToChar( tcpHeaderLength ) ) );
				int dataLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( tcpHeaderLength) );
				Log.d("TCP", "msg length " + dataLength );
				byte[] isoData = new byte[dataLength];
				while( is.read( isoData, 0, dataLength ) != -1) {
					Log.d("TCP", "msg " + new String( dataFormatterUtil.BCDToChar( isoData ) ) );
					return isoData;
				}
				break;
			}
		}
		catch( Exception E )
		{
			System.out.println( "Exception receiveData : " + E );
			return new byte[1];
		}
		return new byte[1];
	}
}