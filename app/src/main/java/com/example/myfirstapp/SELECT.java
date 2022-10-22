package com.example.myfirstapp;

import android.nfc.tech.IsoDep;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;

import static com.example.myfirstapp.TapCard.RID;

/**
 * Created by adarsh on 08/01/17.
 */

public class SELECT
{
    public String cardAID = null;
    public final int classByte = 0;
    public final int insByte = 1;
    public final int p1 = 2;
    public final int p2 = 3;
    public final int lc = 4;
    public int CommandStart = 5;
    public static byte[] PDOL;
    String Module = this.getClass().getSimpleName();

    public byte[] SELECTProcessing(IsoDep isoDep, byte[] AID ) throws Exception
    {
        TapCard tapCard = new TapCard();
        DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        //byte[] command = dataFormatterUtil.trimByteArray( Command );
        //String rid = new String( dataFormatterUtil.BCDToChar( Command ) ).substring(1, 10);
        //RID = dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex( rid ), 10 );
        TerminalData terminalData = new TerminalData();

        if( new String( dataFormatterUtil.byteToChar(AID)).equals("2PAY.SYS.DDF01") )
            tapCard.updateScreenList( "SELECT " + new String( dataFormatterUtil.makeReadable( AID ) ) );
        else {
            tapCard.updateScreenList("SELECT " + new String(dataFormatterUtil.BCDToChar(AID)));
            terminalData.setRID( new String( dataFormatterUtil.BCDToChar( AID ) ).substring(0, 10 ) );
            terminalData.setTermApplicationId( AID );
        }
        //for( int i=0; i<command.length; i++ )
        //    System.out.println("command " + String.format( "%02X", command[i] ) );
        byte[] commandLengthBCD = dataFormatterUtil.hexToBCD( dataFormatterUtil.intToHex( AID.length ), 2);
        //System.out.println("commandLengthHex " + String.format( "%02X", commandLengthBCD[0] ) );
        String commandLengthHex = dataFormatterUtil.BCDToHex( commandLengthBCD );
        //System.out.println("Command Length Hex: " + commandLengthHex );
        int commandLength = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(commandLengthBCD));
        //System.out.println("Command Length: " + commandLength );

        byte[] CAPDU = new byte[ 5 + commandLength];
        //System.out.println( "CAPDU Buffer length " + CAPDU.length );
        CAPDU[classByte] = 0x00;
        CAPDU[insByte] = (byte) 0xA4;
        CAPDU[p1] = 0x04;
        CAPDU[p2] = 0x00;
        CAPDU[lc] = commandLengthBCD[0];
        byte[] fail_sw1sw2 = dataFormatterUtil.hexToBCD( dataFormatterUtil.asciiToHex("6F00"), 4 );
        CommandStart = 5;
        for( int i=0; i<commandLength; i++)
            CAPDU[CommandStart++] = AID[i];
        //System.arraycopy(command, 0, CAPDU, 0, commandLength);
        byte[] RAPDU;
        try
        {
            /*
            System.out.println("CAPDU ");
            for( int i=0; i<CAPDU.length; i++)
                System.out.print(String.format("%02X", CAPDU[i]) );
            System.out.println("");
            */
            Log.d("SELECT", "CAPDU " + new String( dataFormatterUtil.BCDToChar(CAPDU) ) );
            RAPDU = isoDep.transceive(CAPDU);
        }
        catch( Exception E)
        {
            Log.d("SELECT", "Exception "+E);
            return fail_sw1sw2;
        }
        /*
        System.out.print("RAPDU ");
        for( int i=0; i<RAPDU.length; i++)
            System.out.print(String.format("%02X", RAPDU[i]));
        System.out.println("");
        */
        Log.d("SELECT", "RPADU " + new String( dataFormatterUtil.BCDToChar( RAPDU )) );
        return RAPDU;
    }

    public boolean selectResponseProcessing( byte[] selectResponse ) throws Exception
    {
        DataFormatterUtil dataFormatterUtil = new DataFormatterUtil();
        byte[] sw1sw2 = new byte[2];
        sw1sw2[0] = selectResponse[selectResponse.length-2];
        sw1sw2[1] = selectResponse[selectResponse.length-1];
        //System.out.println("SW : " + String.format( "%02X%02X", sw1sw2[0], sw1sw2[1]) );
        Log.d( "SELECT", "SWI SW2 " + new String( dataFormatterUtil.BCDToChar( sw1sw2) ) );
        if( ( sw1sw2[0] == (byte)0x90 ) && ( sw1sw2[1] == 0x00 ) )
            Log.d("SELECT", "SUCCESS");
        else if( ( sw1sw2[0] == (byte)0x62 ) && ( sw1sw2[0] == 0x63 ) )
            Log.d("SELECT", "Warning");
        else {
            Log.d("SELECT", "Error");
            return false;
        }

        int pos = 0;
        CardData cardData = new CardData();
        byte[] FCIData = new byte[256];
        if( selectResponse[pos++] == (byte)0x6F)
        {
            Log.d("SELECT", "FCI Template 6F");
            byte[] FCITemplateLen = new byte[1];
            FCITemplateLen[0] = selectResponse[pos++];
            int FCITemplateLength = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( FCITemplateLen ) );
            Log.d("SELECT", "FCI Length "+FCITemplateLength );
            FCIData = new byte[FCITemplateLength];
            //for( int i=0; i<FCITemplateLength; i++ )
            //    FCIData[i] = selectResponse[pos++];
            System.arraycopy(selectResponse, pos, FCIData, 0, FCITemplateLength);
            //pos += FCITemplateLength;
            pos = 0;
            if( FCIData[pos++] == (byte)0x84)
            {
                //Log.d("SELECT", "DF Name 84");
                byte[] dfNameLen = new byte[1];
                dfNameLen[0] = FCIData[pos++];
                int dfNameLenInt = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( dfNameLen ) );
                //Log.d( "SELECT", "DF Length " + dfNameLenInt);
                byte[] dfName = new byte[ dfNameLenInt];
                //for( int i=0; i < dfNameLenInt; i++ )
                //    dfName[i] = FCIData[pos++];
                System.arraycopy( FCIData, pos, dfName, 0, dfNameLenInt );
                pos+=dfNameLenInt;
                char[] dfNameChar = dataFormatterUtil.byteToChar( dfName );
                //System.out.println("DF NAme : " + new String( dfNameChar) );
                Log.d( "SELECT", "DF Name 84 " + new String( dfNameChar) );
                Log.d( "SELECT", "DF Name 84 " + new String( dataFormatterUtil.BCDToChar( dfName) ) );
                RID = new String( dataFormatterUtil.BCDToChar( dfName ) ).substring(0, 10);
            }
            byte[] FCIPData = new byte[256];
            if( FCIData[pos++] == (byte)0xA5)
            {
                Log.d("SELECT", "FCI Proprietary Template A5");
                byte[] FCIPLen = new byte[1];
                FCIPLen[0] = FCIData[pos++];
                int FCIPLenInt = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( FCIPLen ) );
                //Log.d( "SELECT", "FCI-P Length " + FCIPLenInt);
                FCIPData = new byte[FCIPLenInt];
                for( int i=0; i<FCIPLenInt; i++)
                    FCIPData[i] = FCIData[pos++];
                pos=0;
                int id = 0;
                byte[] emvTag = new byte[2];
                for( int i=0; i < FCIPLenInt; i++)
                {
                    //Log.d("SELECT", "i=" + i);
                    //System.out.println( "FCI Dis data BCD " + String.format("%02X", FCIPData[i] ));
                    emvTag[0] = FCIPData[i];
                    if( (i+1) < FCIPLenInt )
                        emvTag[1] = FCIPData[i + 1];

                    if (emvTag[0] == (byte) 0xBF && emvTag[1] == (byte) 0x0C)
                    {
                        Log.d("SELECT", "FCI Issuer Discretionary Data BFOC");
                        i += 2;
                        byte[] FCIIDDLen = new byte[1];
                        FCIIDDLen[0] = FCIPData[i];
                        //System.out.println( "FCI Dis data len hex " + String.format("%02X", FCIIDDLen[0] ));
                        //Log.d("SELECT", "FCI Dis data len Hex " + new String( dataFormatterUtil.BCDToChar( FCIIDDLen )) );
                        int FCIIDDLenInt = dataFormatterUtil.hexToInt( dataFormatterUtil.BCDToHex( FCIIDDLen ) );
                        //Log.d( "SELECT", "FCI Discretionary Data Length " + FCIIDDLenInt );
                        i+=1;
                        byte[] FCIDiscData = new byte[FCIIDDLenInt];
                        for( int m=0; m<FCIIDDLenInt; m++ )
                            FCIDiscData[m] = FCIPData[i++]; //7:21 PM
                        i--;

                        //for( int o=0; o<FCIIDDLenInt; o++ )
                        //    System.out.println( "FCI Dis data " + o + " [ " + String.format( "%02X", FCIDiscData[o]));

                        for( int n=0; n<FCIIDDLenInt; n++)
                        {
                            //System.out.println(" n = " + n );
                            emvTag[0] = FCIDiscData[n];
                            if( ( n+1 ) < FCIIDDLenInt)
                                emvTag[1] = FCIDiscData[n + 1];
                            if( emvTag[0] == (byte) 0x61 )
                            {
                                n += 1;
                                Log.d("SELECT", "Application Template 61");
                                byte[] ApplTempLen = new byte[1];
                                ApplTempLen[0] = FCIDiscData[n];
                                //System.out.println("FCI Dis data len hex " + String.format("%02X", ApplTempLen[0]));
                                int ApplTempLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(ApplTempLen));
                                //Log.d("SELECT", "Application Template Length " + ApplTempLenInt);
                                n++;
                                //System.out.println(" n(2) = " + n + " [ " + String.format( "%02x", FCIDiscData[n] ) );
                                byte ApplicationTemplate[] = new byte[ApplTempLenInt];
                                for (int k = 0; k < ApplTempLenInt; k++) {
                                    //System.out.println(" n(2) = " + (n+k) + " [ " + String.format( "%02x", FCIDiscData[n+k] ) );
                                    ApplicationTemplate[k] = FCIDiscData[n++];
                                }
                                n--;
                                //System.out.println("i before template : " + i);
                                for (int k = 0; k < ApplTempLenInt; k++)
                                {
                                    //Log.d( "SELECT", "k = " + k );
                                    emvTag[0] = ApplicationTemplate[k];
                                    if( ( k+1 ) <ApplTempLenInt)
                                        emvTag[1] = ApplicationTemplate[k + 1];
                                    //System.out.println("EMV 0[" + String.format("%02x %02x", emvTag[0], emvTag[1]) + "]");
                                    //Log.d( "SELECT", "Tag " + new String( dataFormatterUtil.BCDToChar(emvTag) ) );
                                    if (emvTag[0] == (byte) 0x9F && emvTag[1] == (byte) 0x2A) {
                                        //Log.d("SELECT", "Kernel Identifier 9F2A");
                                        k += 2;
                                        byte[] KerIdLen = new byte[1];
                                        KerIdLen[0] = ApplicationTemplate[k];
                                        int KerIdLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(KerIdLen));
                                        //Log.d("SELECT", "Kernal Id Length " + KerIdLenInt);
                                        k++;
                                        byte[] KernalIdentifier = new byte[KerIdLenInt];
                                        //for (int l = 0; l < KerIdLenInt; l++)
                                        //    KernalIdentifier[l] = ApplicationTemplate[k++];
                                        System.arraycopy( ApplicationTemplate, k, KernalIdentifier, 0, KerIdLenInt );
                                        Log.d("SELECT", "Kernal Identifier 9F2A " + new String( dataFormatterUtil.BCDToChar(KernalIdentifier) ) );
                                        //k--;
                                        k += ( KerIdLenInt -1 );
                                    } else if (emvTag[0] == (byte) 0x4F) {
                                        //Log.d("SELECT", "ADF Name 4F");
                                        k++;
                                        byte[] ADFNameLen = new byte[1];
                                        ADFNameLen[0] = ApplicationTemplate[k];
                                        int ADFNameLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex(ADFNameLen));
                                        //Log.d("SELECT", "ADF Name Length " + ADFNameLenInt);
                                        k++;
                                        byte[] ADFName = new byte[ADFNameLenInt];
                                        //for (int l = 0; l < ADFNameLenInt; l++)
                                        //    ADFName[l] = ApplicationTemplate[k++];
                                        System.arraycopy( ApplicationTemplate, k, ADFName, 0, ADFNameLenInt);
                                        //buildCandidateList(ADFName, id++);
                                        Log.d("SELECT", "ADF Name 4F " + new String( dataFormatterUtil.BCDToChar(ADFName) ) );
                                        CardAIDList cardAIDList = new CardAIDList( new String( dataFormatterUtil.BCDToChar(ADFName) ) );
                                        cardData.addCardAIDToList( cardAIDList );
                                        //k--;
                                        k+= ( ADFNameLenInt -1 );
                                    }
                                    else if( emvTag[0] == (byte)0x50 )
                                    {
                                        //Log.d( "SELECT", "Application Label 50");
                                        k++;
                                        byte[] AppLabelLen = new byte[1];
                                        AppLabelLen[0] = ApplicationTemplate[k];
                                        int AppLabelLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex( AppLabelLen ) );
                                        //Log.d("SELECT", "Application Label Length " + AppLabelLenInt);
                                        k++;
                                        byte[] ApplicationLabel = new byte[AppLabelLenInt];
                                        //for( int l=0; l<AppLabelLenInt; l++)
                                        //    ApplicationLabel[l] = ApplicationTemplate[k++];
                                        System.arraycopy( ApplicationTemplate, k, ApplicationLabel, 0, AppLabelLenInt );
                                        Log.d("SELECT", "Application Label 50 " + new String( dataFormatterUtil.byteToChar( ApplicationLabel ) ) );
                                        k += (AppLabelLenInt -1 );
                                        //k--;
                                    }
                                    else if( emvTag[0] == (byte)0x87 )
                                    {
                                        //Log.d( "SELECT", "Application Priority Indicator 87");
                                        k++;
                                        byte[] AppPrioIndLen = new byte[1];
                                        AppPrioIndLen[0] = ApplicationTemplate[k];
                                        int AppPrioIndLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex( AppPrioIndLen ) );
                                        //Log.d("SELECT", "Application Priorty Indicator Length " + AppPrioIndLenInt );
                                        k++;
                                        byte[] ApplPrioIndicator = new byte[AppPrioIndLenInt];
                                        //for( int l=0; l<AppPrioIndLenInt; l++)
                                        //    ApplPrioIndicator[l] = ApplicationTemplate[k++];
                                        System.arraycopy( ApplicationTemplate, k, ApplPrioIndicator, 0, AppPrioIndLenInt );
                                        Log.d("SELECT", "Application Priority Indicator 87 " + new String( dataFormatterUtil.BCDToChar( ApplPrioIndicator ) ) );
                                        //k--;
                                        k+= (AppPrioIndLenInt-1);
                                    }
                                }
                            }
                            else if( emvTag[0] == (byte) 0x9F && emvTag[1] == (byte) 0x4D )
                            {
                                //Log.d("SELECT", "Log Entry 9F4D");
                                n+=2;
                                byte[] LogEntryLen = new byte[1];
                                LogEntryLen[0] = FCIDiscData[n];
                                int LogEntryLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex( LogEntryLen ) );
                                //Log.d("SELECT", "Log Entry length 9F4D " + LogEntryLenInt);
                                n++;
                                byte[] LogEntry = new byte[LogEntryLenInt];
                                //for( int l=0; l<LogEntryLenInt; l++ )
                                //    LogEntry[l] = FCIDiscData[n++];
                                System.arraycopy(FCIDiscData, n, LogEntry, 0, LogEntryLenInt);
                                Log.d("SELECT", "Log Entry 9F4D " + new String( dataFormatterUtil.BCDToChar( LogEntry ) ) );
                                //n--;
                                n+=(LogEntryLenInt-1);
                            }
                        }
                    }
                    else if( emvTag[0] == (byte)0x50 )
                    {
                        //Log.d( "SELECT", "Application Label 50");
                        i++;
                        byte[] AppLabelLen = new byte[1];
                        AppLabelLen[0] = FCIPData[i];
                        int AppLabelLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex( AppLabelLen ) );
                        //Log.d("SELECT", "Application Label Length " + AppLabelLenInt);
                        i++;
                        byte[] ApplicationLabel = new byte[AppLabelLenInt];
                        //for( int l=0; l<AppLabelLenInt; l++)
                        //    ApplicationLabel[l] = FCIPData[i++];
                        System.arraycopy( FCIPData, i, ApplicationLabel, 0, AppLabelLenInt );
                        Log.d("SELECT", "Application Label 50 " + new String( dataFormatterUtil.byteToChar( ApplicationLabel ) ) );
                        //i--;
                        i+= ( AppLabelLenInt -1);
                    }
                    else if( emvTag[0] == (byte)0x87 )
                    {
                        //Log.d( "SELECT", "Application Priority Indicator 87");
                        i++;
                        byte[] AppPrioIndLen = new byte[1];
                        AppPrioIndLen[0] = FCIPData[i];
                        int AppPrioIndLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex( AppPrioIndLen ) );
                        //Log.d("SELECT", "Application Priorty Indicator Length " + AppPrioIndLenInt );
                        i++;
                        byte[] ApplPrioIndicator = new byte[AppPrioIndLenInt];
                        //for( int l=0; l<AppPrioIndLenInt; l++)
                        //    ApplPrioIndicator[l] = FCIPData[i++];
                        System.arraycopy( FCIPData, i, ApplPrioIndicator, 0, AppPrioIndLenInt );
                        Log.d("SELECT", "Application Priority Indicator 87 " + new String( dataFormatterUtil.BCDToChar( ApplPrioIndicator ) ) );
                        //i--;
                        i += (AppPrioIndLenInt-1);
                    }
                    if( emvTag[0] == (byte) 0x9F && emvTag[1] == (byte) 0x38 )
                    {
                        //Log.d("SELECT", "PDOL 9F38");
                        i+=2;
                        byte[] PDOLLen = new byte[1];
                        PDOLLen[0] = FCIPData[i];
                        int PDOLLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex( PDOLLen ) );
                        //Log.d("SELECT", "PDOL Length " + PDOLLenInt);
                        i++;
                        PDOL = new byte[PDOLLenInt];
                        //for( int l=0; l<PDOLLenInt; l++ )
                        //    PDOL[l] = FCIPData[i++];
                        System.arraycopy( FCIPData, i, PDOL, 0, PDOLLenInt );
                        Log.d( "SELECT", "PDOL 9F38 " + new String( dataFormatterUtil.BCDToChar( PDOL ) ) );
                        //i--;
                        i += (PDOLLenInt );
                    }
                    else if( emvTag[0] == (byte) 0x5F && emvTag[1] == (byte) 0x2D )
                    {
                        //Log.d("SELECT", "Language Preference 5F2D");
                        i+=2;
                        byte[] LangPrefLen = new byte[1];
                        LangPrefLen[0] = FCIPData[i];
                        int LangPrefLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex( LangPrefLen ) );
                        //Log.d("SELECT", "Language Preference " + LangPrefLenInt);
                        i++;
                        byte[] LangPref = new byte[LangPrefLenInt];
                        //for( int l=0; l<LangPrefLenInt; l++ )
                        //    LangPref[l] = FCIPData[i++];
                        System.arraycopy(FCIPData, i, LangPref, 0, LangPrefLenInt );
                        Log.d("SELECT", "LangPref 5F2D" + new String( dataFormatterUtil.byteToChar( LangPref ) ) );
                        //i--;
                        i += (LangPrefLenInt-1);
                    }
                    else if( emvTag[0] == (byte) 0x9F && emvTag[1] == (byte) 0x12 )
                    {
                        //Log.d("SELECT", "Application Preferred Name 9F12");
                        i+=2;
                        byte[] AppPrefNameLen = new byte[1];
                        AppPrefNameLen[0] = FCIPData[i];
                        int AppPrefNameLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex( AppPrefNameLen ) );
                        //Log.d("SELECT", "Application Preferred Name " + AppPrefNameLenInt);
                        i++;
                        byte[] AppPrefName = new byte[AppPrefNameLenInt];
                        //for( int l=0; l<AppPrefNameLenInt; l++ )
                        //    AppPrefName[l] = FCIPData[i++];
                        System.arraycopy(FCIPData, i, AppPrefName, 0, AppPrefNameLenInt);
                        Log.d("SELECT", "Application Preferred Name 9F12 " + new String( dataFormatterUtil.byteToChar( AppPrefName ) ) );
                        //i--;
                        i+= (AppPrefNameLenInt-1);
                    }
                    else if( emvTag[0] == (byte) 0x9F && emvTag[1] == (byte) 0x11 )
                    {
                        //Log.d( "SELECT", "Issuer Code Table Index 9F11" );
                        i+=2;
                        byte[] IssCodeTblIdxLen = new byte[1];
                        IssCodeTblIdxLen[0] = FCIPData[i];
                        int IssCodeTblIdxLenInt = dataFormatterUtil.hexToInt(dataFormatterUtil.BCDToHex( IssCodeTblIdxLen ) );
                        //Log.d("SELECT", "Issuer Code Table index Length " + IssCodeTblIdxLenInt);
                        i++;
                        byte[] IssCodeTblIdx = new byte[IssCodeTblIdxLenInt];
                        //for( int l=0; l<IssCodeTblIdxLenInt; l++ )
                        //    IssCodeTblIdx[l] = FCIPData[i++];
                        System.arraycopy(FCIPData, i, IssCodeTblIdx, 0, IssCodeTblIdxLenInt);
                        Log.d("SELECT", "Issuer Code Table Index 9F11" + new String( dataFormatterUtil.BCDToChar( IssCodeTblIdx ) ) );
                        //i--;
                        i += (IssCodeTblIdxLenInt-1);
                    }
                }
            }
        }
        return true;
    }
}