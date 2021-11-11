package com.aix.mpagents.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    public static String formatDate(Date date){
        SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy");
        return spf.format(date);
    }
}
