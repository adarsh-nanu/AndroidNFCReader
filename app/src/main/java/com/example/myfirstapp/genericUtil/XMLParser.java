package com.example.myfirstapp.genericUtil;

import android.util.Log;
import android.util.Xml;

import com.example.myfirstapp.CardData;
import com.example.myfirstapp.ODA;
import com.example.myfirstapp.TerminalData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adarsh on 11/02/17.
 */

public class XMLParser {
    public static final String namespace = null;
    public XmlPullParser parser = null;
    public InputStream in = null;
    String Module = this.getClass().getSimpleName();

    public void parserInit(InputStream in) throws XmlPullParserException, IOException {
        //Log.d("XMLParser", "in " + in );
        parser = Xml.newPullParser();
        //Log.d("XMLParser", "after newPullParser " );
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        //Log.d("XMLParser", "after set feature ");
        parser.setInput(in, null);
        this.in = in;
        //Log.d("XMLParser", "after set input ");
        //parser.nextTag();

    }

    public String getCAPK(String RID, String Index) throws XmlPullParserException, IOException {
        Log.d("XMLParser", "Search for " + RID + " and " + Index);
        int eventType = parser.getEventType();
        //Log.d("XMLParser", "getCAPK 1");
        CardData cardData = new CardData();
        //ODA.CAPKTable = new String[50][2][625];
        String value = new String();
        //Log.d("XML", "0 eventtype " + eventType );
        //Log.d( "XMLParsr", "start " + parser.getName() );
        while (eventType != XmlPullParser.END_DOCUMENT) {
            //Log.d( "XMLParsr", "not document end" );
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    //Log.d( "XMLParsr", "start doc  " + parser.getName() );
                    eventType = parser.next();
                    //Log.d("XML", "1 eventtype " + eventType );
                    continue;
                case XmlPullParser.START_TAG:
                    //Log.d( "XMLParsr", "start tag " + parser.getName() );
                    //Log.d("XML", "2 eventtype " + eventType );
                    String TagName = parser.getName();
                    if (TagName.equals("rid")) {
                        String rid = parser.nextText();
                        if (rid.equals(RID)) {
                            //Log.d("XML", "required rid located");
                            eventType = parser.nextTag();
                            TagName = parser.getName();
                            //Log.d("XML", "tag name - " + TagName);
                            if (TagName.equals("index")) {
                                String index = parser.nextText();
                                if (index.equals(Index)) {

                                    //Log.d("XML", "required index located");
                                    eventType = parser.nextTag();
                                    TagName = parser.getName();
                                    if (TagName.equals("key"))
                                        return parser.nextText();
                                }
                            }
                        }
                    }
                    eventType = parser.next();
                    continue;
                case XmlPullParser.END_TAG:
                    //Log.d( "XMLParsr", "end tag " + parser.getName() );
                    eventType = parser.next();
                    //Log.d("XML", "3 eventtype " + eventType );
                    continue;
                case XmlPullParser.END_DOCUMENT:
                    //Log.d( "XMLParsr", "end doc " + parser.getName() );
                    eventType = parser.next();
                    //Log.d("XML", "4 eventtype " + eventType );
                    continue;
                default:
                    eventType = parser.next();
                    //Log.d( "XMLParsr", "default" );
                    //Log.d("XML", "5 eventtype " + eventType );
                    //parser.next();
            }
        }
        return null;
    }

    public void initCAPKs() throws XmlPullParserException, IOException {
        TerminalData terminalData = new TerminalData();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    eventType = parser.next();
                    continue;
                case XmlPullParser.START_TAG:
                    String TagName = parser.getName();
                    if (TagName.equals("rid")) {
                        String rid = parser.nextText();
                        {
                            eventType = parser.nextTag();
                            TagName = parser.getName();
                            if (TagName.equals("index")) {
                                String index = parser.nextText();
                                {
                                    eventType = parser.nextTag();
                                    TagName = parser.getName();
                                    String key = null;
                                    if (TagName.equals("key"))
                                        key = parser.nextText();
                                    //Log.d(Module, key);
                                    eventType = parser.nextTag();
                                    TagName = parser.getName();
                                    if ( TagName.equals("type")) {
                                        String pstype = parser.nextText();
                                        //Log.d(Module, pstype);
                                        CAPKeys capKeys = new CAPKeys(rid, index, key, Integer.parseInt( pstype ) );
                                        terminalData.addCAPKToList( capKeys );
                                    }
                                }
                            }
                        }
                    }
                    eventType = parser.next();
                    continue;
                case XmlPullParser.END_TAG:
                    eventType = parser.next();
                    continue;
                case XmlPullParser.END_DOCUMENT:
                    eventType = parser.next();
                    continue;
                default:
                    eventType = parser.next();
            }
        }
    }
}