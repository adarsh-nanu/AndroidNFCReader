package com.example.myfirstapp.genericUtil;

/**
 * Created by adarsh on 19/01/17.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter
{
    SimpleDateFormat format;
    Date today;
    public DateFormatter( String format )
    {
        this.format = new SimpleDateFormat( format );
        today = new Date();
    }

    public String getFormattedDate()
    {
        return format.format( today );
    }
}