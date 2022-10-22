package com.example.myfirstapp;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by adarsh on 28/02/17.
 */

public class SqlDataProcessor extends AsyncTask<String, Void, Boolean>
{
    String Module = this.getClass().getSimpleName();
    private Context context;
    public SqlDataProcessor( Context context )
    {
        this.context = context;
    }

    public Boolean doInBackground( String... inputData)
    {
        int option = Integer.parseInt( inputData[0] );
        DBManager dbManager = new DBManager( context );
        switch( option )
        {
            case 1:
                Log.d(Module, "Get Trace Number");
                break;
            case 2:
                Log.d(Module, "Log to Transit");
                break;
            case 3:
                Log.d(Module, "Log to Approved");
                break;
            default:
                Log.d(Module, "Invalid Option");
        }
        return true;
    }
}
