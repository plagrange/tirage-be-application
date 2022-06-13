package com.dictao.dtp.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpContext {

    static class Context {
        HttpServletRequest request;
        HttpServletResponse response;
        String tenantName;
        String applicationName;
        String accessId;
        String externalAccessId;
    }
    private static final ThreadLocal<Context> threadLocal = new ThreadLocal<Context>();

    public static void set(HttpServletRequest request, HttpServletResponse response,
            String tenantName, String appName, String aid, String eaid) {
        Context ctx = new Context();
        ctx.request = request;
        ctx.response = response;
        ctx.tenantName = tenantName;
        ctx.applicationName = appName;
        ctx.accessId = aid;
        ctx.externalAccessId = eaid;        
        threadLocal.set(ctx);
    }

    public static void unset() {
        threadLocal.remove();
    }

    public static HttpServletRequest getRequest() {
        return threadLocal.get().request;
    }

    public static HttpServletResponse getResponse() {
        return threadLocal.get().response;
    }
    
    public static String getTenantName(){
        return threadLocal.get().tenantName;
    }
    
    public static String getApplicationName(){
        return threadLocal.get().applicationName;
    }
    
    public static String getAccessId(){
        return threadLocal.get().accessId;
    }
    
    public static String getExternalAccessId(){
        return threadLocal.get().externalAccessId;
    }
}
