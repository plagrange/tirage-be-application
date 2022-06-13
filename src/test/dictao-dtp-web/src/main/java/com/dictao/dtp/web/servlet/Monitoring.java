package com.dictao.dtp.web.servlet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Monitoring {
    
    private static int ws = 0;
    private static DateFormat df = new SimpleDateFormat("EEEE  d MMMM yyyy kk:mm:ss");
    private static String date = "";
    private static int exportErrors = 0;

    /**
     * Start monitoring of the web service call.
     * We have to synchronize to ensure ws counter is thread safe.
     * @param message message displayed in log
     */
    synchronized public static void start() {
        ws++;
        date = df.format(new Date());
    }
    
    synchronized public static void stop() {
        ws--;
    }
    
    synchronized public static void recordExportError() {
        exportErrors++;
    }
    
    public static int getWs() {
        return ws;
    }
    
    public static int getExportErrors() {
        return exportErrors;
    }
    
    public static String getDate() {
        return date;
    }
}
