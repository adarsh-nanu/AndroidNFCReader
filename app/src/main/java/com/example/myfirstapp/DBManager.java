package com.example.myfirstapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by adarsh on 28/02/17.
 */

public class DBManager extends SQLiteOpenHelper {

    private static final int Database_Version = 1;
    private static final String Database_Name = "Transit";
    private String Create_Transit_Table = "create table transit( msgtype INTEGER, pan PAN, amount REAL, trace INTEGER, local_date INTEGER, local_time INTEGER, track2 TEXT, emvdata_req BLOB, emvdata_resp BLOB )";
    private String Create_Approved_Txn_Table = "create table approved( msgtype INTEGER, pan PAN, amount REAL, trace INTEGER, local_date INTEGER, local_time INTEGER, track2 TEXT, responsecode INTEGER, authnum TEXT, emvdata_req BLOB, emvdata_resp BLOB )";
    private String Create_TraceSeq_Table = "create table TraceSeq( trace INTEGER )";
    private String Drop_Transit_Table = "drop table if exists transit";
    private String Drop_Approved_Txn_Table = "drop table if exists approved";
    String Module = this.getClass().getSimpleName();

    public DBManager(Context context) {
        super(context, Database_Name, null, Database_Version);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL( Create_Transit_Table );
        sqLiteDatabase.execSQL( Create_Approved_Txn_Table );
        sqLiteDatabase.execSQL( Create_TraceSeq_Table );
        Log.d(Module, "Database Created");
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL( Drop_Transit_Table );
        sqLiteDatabase.execSQL( Drop_Approved_Txn_Table );
        Log.d(Module, "Database upgraded" );
        sqLiteDatabase.execSQL( Create_Transit_Table );
        sqLiteDatabase.execSQL( Create_Approved_Txn_Table );
    }

    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        Log.d(Module, "Database downgraded");
    }
}