package com.example.myfirstapp;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by adarsh on 17/02/17.
 */

public class CardData {
    //private static byte[] DFName = new byte[0];
    private static byte[] ATC = new byte[0];
    private static byte[] IssuerApplicationData = new byte[0];
    private static byte[] Track2 = new byte[0];
    private static byte   CID;
    private static byte[] CPR = new byte[2];
    private static byte[] AFL = new byte[0];
    private static byte[] SDAD = new byte[0];
    private static byte[] IssuerPublicKeyCertificate = new byte[0];
    private static byte   CAPKIndex;
    private static byte[] IssuerPublicKeyRemainder = new byte[0];
    private static byte[] IssuerPAN = new byte[0];
    private static byte[] ICCPublicKeyCertificate = new byte[0];
    private static byte[] ICCPublicKeyRemainder = new byte[0];
    private static byte[] ICCPublicKeyExponent = new byte[0];
    private static byte[] IssuerPublicKeyExponent = new byte[0];
    private static byte[] AIP = new byte[0];
    private static byte[] Cryptogram = new byte[0];
    private static boolean isDpas = false;
    private static List<CardAIDList> cardAIDLists = new LinkedList<CardAIDList>();
    private static byte[] CDOL1 = new byte[0];
    private static byte[] CDOL2 = new byte[0];
    private static byte[] IACDefault = new byte[5];
    private static byte[] IACDenial = new byte[5];
    private static byte[] IACOnline = new byte[5];
    private static byte[] Track1Discretionary = new byte[0];
    private static byte[] CardholderName = new byte[0];
    private static byte[] CVMList = new byte[0];
    private static byte[] SSAD = new byte[0];
    private static byte[] SDATagList = new byte[0];
    private static byte[] CardAuthRelatedData= new byte[0];
    private static byte[] ServiceCode = new byte[2];

    public void clearCardData()
    {
        ATC = new byte[0];
        IssuerApplicationData = new byte[0];
        Track2 = new byte[0];
        CPR = new byte[2];
        AFL = new byte[0];
        SDAD = new byte[0];
        IssuerPublicKeyCertificate = new byte[0];
        IssuerPublicKeyRemainder = new byte[0];
        IssuerPAN = new byte[0];
        ICCPublicKeyCertificate = new byte[0];
        ICCPublicKeyRemainder = new byte[0];
        ICCPublicKeyExponent = new byte[0];
        IssuerPublicKeyExponent = new byte[0];
        AIP = new byte[0];
        Cryptogram = new byte[0];
        isDpas = false;
        CID = 0x00;
        CAPKIndex = 0x00;
        clearCardAIDList();
        CDOL1 = new byte[0];
        CDOL2 = new byte[0];
        IACDefault = new byte[5];
        IACDenial = new byte[5];
        IACOnline = new byte[5];
        Track1Discretionary = new byte[0];
        CardholderName = new byte[0];
        CVMList = new byte[0];
        SSAD = new byte[0];
        SDATagList = new byte[0];
        CardAuthRelatedData = new byte[0];
        ServiceCode = new byte[2];
    }

    public byte[] getServiceCode()
    {
        return ServiceCode;
    }

    public void setServiceCode( byte[] serviceCode )
    {
        System.arraycopy( serviceCode, 0, ServiceCode, 0, serviceCode.length );
    }

    public byte[] getCardAuthRelatedData()
    {
        return CardAuthRelatedData;
    }

    public void setCardAuthRelatedData( byte[] cardAuthRelatedData )
    {
        CardAuthRelatedData = new byte[cardAuthRelatedData.length];
        System.arraycopy( cardAuthRelatedData, 0, CardAuthRelatedData, 0, cardAuthRelatedData.length );
    }

    public byte[] getSDATagList()
    {
        return SDATagList;
    }

    public void setSDATagList( byte[] sdaTagList )
    {
        SDATagList = new byte[sdaTagList.length];
        System.arraycopy( sdaTagList, 0, SDATagList, 0, sdaTagList.length );
    }

    public byte[] getSSAD()
    {
        return SSAD;
    }

    public void setSSAD( byte[] ssad )
    {
        SSAD = new byte[ssad.length];
        System.arraycopy( ssad, 0, SSAD, 0, ssad.length );
    }

    public byte[] getCVMList()
    {
        return CVMList;
    }

    public void setCVMList( byte[] cvmlist )
    {
        CVMList = new byte[cvmlist.length];
        System.arraycopy( cvmlist, 0, CVMList, 0, cvmlist.length );
    }

    public byte[] getCardholderName()
    {
        return CardholderName;
    }

    public void setCardholderName( byte[] cardholderName )
    {
        CardholderName = new byte[cardholderName.length];
        System.arraycopy( cardholderName, 0, CardholderName, 0, cardholderName.length );
    }

    public byte[] getTrack1Discretionary()
    {
        return Track1Discretionary;
    }

    public void setTrack1Discretionary( byte[] track1disc )
    {
        Track1Discretionary = new byte[track1disc.length];
        System.arraycopy( track1disc, 0, Track1Discretionary, 0, track1disc.length );
    }

    public byte[] getIACCDefault()
    {
        return IACDefault;
    }

    public void setIACCDefault( byte[] iac)
    {
        System.arraycopy( iac, 0, IACDefault, 0, 5 );
    }

    public byte[] getIACCDenial()
    {
        return IACDenial;
    }

    public void setIACCDenial( byte[] iac)
    {
        System.arraycopy( iac, 0, IACDenial, 0, 5 );
    }


    public byte[] getIACCOnline()
    {
        return IACOnline;
    }

    public void setIACCOnline( byte[] iac)
    {
        System.arraycopy( iac, 0, IACOnline, 0, 5 );
    }

    public byte[] getCDOL1()
    {
        return CDOL1;
    }

    public void setCDOL1( byte[] cdol)
    {
        CDOL1 = new byte[cdol.length];
        System.arraycopy( cdol, 0, CDOL1, 0, cdol.length );
    }

    public byte[] getCDOL2()
    {
        return CDOL2;
    }

    public void setCDOL2( byte[] cdol)
    {
        CDOL2 = new byte[cdol.length];
        System.arraycopy( cdol, 0, CDOL2, 0, cdol.length );
    }

    public void clearCardAIDList()
    {
        cardAIDLists.clear();
    }

    public void addCardAIDToList( CardAIDList cardAIDList )
    {
        cardAIDLists.add( cardAIDList );
    }

    public List<CardAIDList> getCardAIDLists()
    {
        return cardAIDLists;
    }

    public void setCryptogram(byte[] cryptogram )
    {
        Cryptogram = new byte[cryptogram.length];
        System.arraycopy( cryptogram, 0, Cryptogram, 0, cryptogram.length );
    }

    public byte[] getCryptogram()
    {
        return Cryptogram;
    }

    public void setAIP(byte[] aip )
    {
        AIP = new byte[aip.length];
        System.arraycopy( aip, 0, AIP, 0, aip.length );
    }

    public byte[] getAIP()
    {
        return AIP;
    }


    public void setIssuerPublicKeyExponent(byte[] exponent )
    {
        IssuerPublicKeyExponent = new byte[exponent.length];
        System.arraycopy( exponent, 0, IssuerPublicKeyExponent, 0, exponent.length );
    }

    public byte[] getIssuerPublicKeyExponent()
    {
        return IssuerPublicKeyExponent;
    }

    public void setICCPublicKeyExponent(byte[] exponent )
    {
        ICCPublicKeyExponent = new byte[exponent.length];
        System.arraycopy( exponent, 0, ICCPublicKeyExponent, 0, exponent.length );
    }

    public byte[] getICCPublicKeyExponent()
    {
        return ICCPublicKeyExponent;
    }


    public void setICCPublicKeyRemainder(byte[] remainder )
    {
        ICCPublicKeyRemainder = new byte[remainder.length];
        System.arraycopy(remainder, 0, ICCPublicKeyRemainder, 0, remainder.length );
    }

    public byte[] getIccPublicKeyRemainder()
    {
        return ICCPublicKeyRemainder;
    }

    public void setICCPublicKeyCertificate(byte[] Certificate )
    {
        ICCPublicKeyCertificate = new byte[Certificate.length];
        System.arraycopy(Certificate, 0, ICCPublicKeyCertificate, 0, Certificate.length );
    }

    public byte[] getICCPublicKeyCertificate()
    {
        return ICCPublicKeyCertificate;
    }


    public void setIssuerPAN(byte[] pan )
    {
        IssuerPAN = new byte[pan.length];
        System.arraycopy(pan, 0, IssuerPAN, 0, pan.length );
    }

    public byte[] getIssuerPAN()
    {
        return IssuerPAN;
    }


    public void setIssuerPublicKeyRemainder(byte[] remainder )
    {
        IssuerPublicKeyRemainder = new byte[remainder.length];
        System.arraycopy(remainder, 0, IssuerPublicKeyRemainder, 0, remainder.length );
    }

    public byte[] getIssuerPublicKeyRemainder()
    {
        return IssuerPublicKeyRemainder;
    }

    public void setCAPKIndex( byte capkIndex)
    {
        CAPKIndex = capkIndex;
    }

    public byte getCAPKIndex()
    {
        return CAPKIndex;
    }


    public void setDFName( byte[] dfname )
    {}

    public void setIssuerPublicKeyCertificate(byte[] Certificate )
    {
        IssuerPublicKeyCertificate = new byte[Certificate.length];
        System.arraycopy(Certificate, 0, IssuerPublicKeyCertificate, 0, Certificate.length );
    }

    public byte[] getIssuerPublicKeyCertificate()
    {
        return IssuerPublicKeyCertificate;
    }

    public void setATC(byte[] atc )
    {
        ATC = new byte[2];
        System.arraycopy(atc, 0, ATC, 0, 2);
    }

    public byte[] getATC()
    {
        return ATC;
    }

    public void setIssuerApplicationData( byte[] iad)
    {
        IssuerApplicationData = new byte[iad.length];
        System.arraycopy( iad, 0, IssuerApplicationData, 0, iad.length);
    }

    public byte[] getIssuerApplicationData()
    {
        return IssuerApplicationData;
    }

    public void setTrack2( byte[] track2 )
    {
        Track2 = new byte[ track2.length ];
        System.arraycopy( track2, 0, Track2, 0, track2.length );
    }

    public byte[] getTrack2()
    {
        return Track2;
    }

    public void setCID( byte cid)
    {
        CID = cid;
    }

    public byte getCID()
    {
        return CID;
    }

    public void setCPR( byte[] cpr )
    {
        CPR = new byte[ 2 ];
        System.arraycopy( cpr, 0, CPR, 0, 2 );
    }

    public byte[] getCPR()
    {
        return CPR;
    }

    public void setAFL( byte[] afl )
    {
        AFL = new byte[ afl.length ];
        System.arraycopy( afl, 0, AFL, 0, afl.length );
    }

    public byte[] getAFL()
    {
        return AFL;
    }

    public void setSDAD( byte[] sdad )
    {
        SDAD = new byte[ sdad.length ];
        System.arraycopy( sdad, 0, SDAD, 0, sdad.length );
    }

    public byte[] getSDAD()
    {
        return SDAD;
    }

}
