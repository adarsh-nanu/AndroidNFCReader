package com.example.myfirstapp;
import java.util.*;
import java.text.*;

/**
 * Created by adarsh on 02/01/17.
 */

public class IsoRequest
{
	private String TPDU;
	private int msgType;
	private byte[] primaryBitmap;
	private byte[] secondaryBitmap;
	private String Pan;
	private long Pcode;
	private double acqAmount;
	private long gmt_date;
	private long gmt_time;
	private long trace;
	private long local_date;
	private long local_time;
	private int exp_date;
	private int Mcc;
	private int posEntryCode;
	private int posConditionCode;
	private String track2;
	private String termId;
	private String merchantId;
	public int acqCurrencyCode;
	public String EMVData;
	private double batchAmount;
	private long batchNumber;
	private static int usedBytes;
	public void setTPDU( String TPDU )
	{
		this.TPDU = TPDU;
	}

	public void setMsgType( int msgType )
	{
		this.msgType = msgType;
	}
	public void setPan( String Pan )
	{
		this.Pan = Pan;
	}
	public void setPcode( long Pcode )
	{
		this.Pcode = Pcode;
	}
	public void setAcqAmount( double acqAmount )
	{
		this.acqAmount = acqAmount;
	}
	public void setTrace()
	{
		trace = 1;
	}
	public void setExpiryDate( int exp_date )
	{
		this.exp_date  = exp_date;
	}
	public void setMcc( int Mcc )
	{
		this.Mcc = Mcc;
	}
	public void setPosEntryCode( int posEntryCode )
	{
		this.posEntryCode = posEntryCode;
	}

	public void setPosConditionCode( int  posConditionCode )
	{
		this.posConditionCode = posConditionCode;
	}

	public void setTrack2( String track2 )
	{
		this.track2 = track2;
	}
	public void setTermid( String termId )
	{
		this.termId = termId;
	}
	public void setMerchantId( String merchantId )
	{
		this.merchantId = merchantId;
	}
	public void setAcqCurrencyCode( int acqCurrencyCode )
	{
		this.acqCurrencyCode = acqCurrencyCode;
	}
	public void setPINData()
	{
	}
	public void setEMVData( String EMVdata )
	{
		this.EMVData = EMVData;
	}

	public void setBatchAmount( double batchAmount )
	{
		this.batchAmount = batchAmount;
	}

	public void setBatchNumber( long batchNumber )
	{
		this.batchNumber = batchNumber;
	}

	public int getMsgType( )
	{
		System.out.println( "msgType : " + msgType );
		return msgType;
	}

	public String getTPDU( )
	{
		System.out.println( "TPDU : " + TPDU );
		return TPDU;
	}

	public byte[] getPrimaryBitmap()
	{
		return primaryBitmap;
	}

	public String getPan( )
	{
		System.out.println( "Pan : " + Pan );
		return Pan;
	}

	public long getPcode( )
	{
		System.out.println( "Pcode : " + Pcode);
		return Pcode;
	}

	public double getAcqAmount( )
	{
		System.out.println( "acqAmount : " + acqAmount );
		return acqAmount;
	}
	public long getGmtDate()
	{
		System.out.println( "gmt_date : " + gmt_date );
		return gmt_date;
	}
	public long getGmtTime()
	{
		System.out.println( "gmt_time: " + gmt_time );
		return gmt_time;
	}
	public long getTrace()
	{
		System.out.println( "trace : " + trace );
		return trace;
	}
	public long getLocalDate()
	{
		System.out.println( "local_date : " + local_date );
		return local_date;
	}
	public long getLocalTime()
	{
		System.out.println( "local_time : " + local_time );
		return local_time;
	}
	public int getExpiryDate()
	{
		System.out.println( "exp_date: " + exp_date );
		return exp_date;
	}
	public int getMcc( )
	{
		System.out.println( "Mcc: " + Mcc );
		return Mcc;
	}
	public int getPosEntryCode( )
	{
		System.out.println( "posEntryCode : " + posEntryCode );
		return posEntryCode;
	}

	public int getPosConditionCode( )
	{
		System.out.println( "posConditionCode : " + posConditionCode );
		return posConditionCode;
	}

	public String getTrack2( )
	{
		//System.out.println( " : " + );
		return track2;
	}
	public String getTermid( )
	{
		System.out.println( "termId : " + termId );
		return termId;
	}
	public String getMerchantId( )
	{
		System.out.println( "merchantId : " + merchantId );
		return merchantId;
	}
	public int getAcqCurrencyCode( )
	{
		System.out.println( "acqCurrencyCode : " + acqCurrencyCode );
		return acqCurrencyCode;
	}
	public void getPINData()
	{
		//System.out.println( " : " + );
	}
	public String getEMVData( )
	{
		//System.out.println( " : " + );
		return EMVData;
	}

	public double getBatchAmount( )
	{
		return batchAmount;
	}

	public long getBatchNumber( )
	{
		return batchNumber;
	}

	public IsoRequest()
	{
		primaryBitmap = new byte[8];
		//secondaryBitmap = new byte[8];
		Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat( "MMdd" );
		local_date = Long.parseLong( sdf.format( currentDate ) );
		//System.out.println( "local_date : " + local_date );
		sdf.applyPattern( "hhmmss" );
		local_time = Long.parseLong( sdf.format( currentDate ) );
		//System.out.println( "local_time : " + local_time );
		usedBytes = 0;
	}
}