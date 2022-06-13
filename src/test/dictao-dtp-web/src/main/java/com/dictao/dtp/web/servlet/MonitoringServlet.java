package com.dictao.dtp.web.servlet;

import com.dictao.dtp.core.transactions.ApplicationListConf;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dictao.dtp.web.config.ConfigReader;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.servlet.ServletContext;

public class MonitoringServlet extends Servlet {

    private static final long serialVersionUID = 5106447659436410864L;
    private static final Logger LOG = LoggerFactory.getLogger(MonitoringServlet.class);
    private Map<String, String> infos = new HashMap<String, String>();
    private static String STATUS_PATH = "/status";
    private static String DATE_FORMAT = "EEEE  d MMMM yyyy kk:mm:ss";
    private static String SERVER_NAME = "ServerName";
    private static String SERVER_IP = "ServerIP";
    private static String TIME = "ServerDate";
    private static String JVM_FREE_MEMORY = "JVMFreeMem";
    private static String JVM_TOTAL_MEMORY = "JVMTotalMem";
    private static String JVM_MAX_MEMORY = "JVMMaxMem";
    private static String JVM_USED_MEMORY = "JVMUsedMem";
    private static String CURRENT_WS_NUMBER = "WSNumberInProgress";
    private static String DATE_LAST_WS = "DateLastWS";
    private static String EXPORT_ERRORS = "ExportErrors";
    private static String CONFIGURATION = "Configuration";
    private static String SESSION_NUMBER = "SessionNumber";
    private static String OK = "OK";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(final HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        boolean result = checkDTP();
        String path = request.getPathInfo();

        if ((path != null) && path.equalsIgnoreCase(STATUS_PATH)) {
            Enumeration<String> accepts = request.getHeaders("Accept");
            boolean htmlOutput = true;
            while (accepts.hasMoreElements()) {
                String accept = accepts.nextElement();
                if (accept.equals("text/plain")) {
                    htmlOutput = false;
                    break;
                } else if (accept.startsWith("text/html")) {
                    break;
                }
            }
            if (htmlOutput) {
                statusHtml(request, response, result);
            } else {
                statusText(response, result);
            }
        } else {
            int status = result ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            response.setStatus(status);
        }
    }

    private void statusHtml(HttpServletRequest request, HttpServletResponse response, boolean checkDTP) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head></head>");
        out.println("<script>");
        out.println("function StartRefresh(){  setTimeout(\"AutoRefresh()\",10000); }  ");
        out.println("function AutoRefresh(){   var findCheck = document.location.href.split(\"?checked\");  var parameter = '?checked=true';");
        out.println("if(document.Form.Box.checked) {");
        out.println("if(findCheck.length == 2) document.location.href= document.location.href");
        out.println("else document.location.href= document.location.href + parameter;");
        out.println("}");
        out.println(" else { if(findCheck.length == 2) document.location.href= findCheck[0]; }");
        out.println("}  ");
        out.println("function Load() {  var findCheck = document.location.href.split(\"?checked\");  ");
        out.println(" if(findCheck.length == 2) {  document.Form.Box.checked=true; StartRefresh(); }  ");
        out.println(" }  ");
        out.println("</script>");
        out.println(" <body onLoad=\"Load()\"> ");
        out.println("<h2>Page de supervision DTP</h2>");

        ServletContext application = getServletConfig().getServletContext();
        InputStream inputStream = application.getResourceAsStream("/META-INF/MANIFEST.MF");
        Manifest mf = new Manifest(inputStream);

        out.println("<table>");
        out.println("<tr><td>Nom du serveur : </td><td>" + infos.get(SERVER_NAME) + "</td></tr>");
        out.println("<tr><td>IP du serveur : </td><td>" + infos.get(SERVER_IP) + "</td></tr>");
        out.println("<tr><td>Date du serveur : </td><td>" + infos.get(TIME) + "</td></tr>");
        out.println("</table>");

        out.println("<h3>Donn&eacute;es Techniques</h3>");

        out.println("<table>");
        out.println("<tr><td>M&eacute;moire libre de la JVM : </td><td>" + infos.get(JVM_FREE_MEMORY) + "</td></tr>");
        out.println("<tr><td>M&eacute;moire de la JVM : </td><td>" + infos.get(JVM_TOTAL_MEMORY) + "</td></tr>");
        out.println("<tr><td>M&eacute;moire maximale de la JVM : </td><td>" + infos.get(JVM_MAX_MEMORY) + "</td></tr>");
        out.println("<tr><td>M&eacute;moire utilisee de la JVM : </td><td>" + infos.get(JVM_USED_MEMORY) + "</td></tr>");
        out.println("</table>");

        out.println("<h3>Donn&eacute;es M&eacute;tiers</h3>");

        out.println("<table>");
        // Test DTP and the value must be on the same line (much more simple to parse)
        final String testDTP;
        if (checkDTP) {
            testDTP = "<td>true</td></tr>";
        } else {
            testDTP = "<td>false</td></tr>";
        }
        out.println("<tr><td>Test DTP : </td>" + testDTP);
        out.println("<tr><td>Implementation Version : </td><td>"
                + mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE) + " "
                + mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION) + "</td></tr>");

        out.println("<tr><td>Build By : </td><td>" + mf.getMainAttributes().getValue("BuildBy") + "</td></tr>");

        out.println("<tr><td>Build Number : </td><td>" + mf.getMainAttributes().getValue("BuildNumber") + "</td></tr>");
        out.println("<tr><td>Build Date : </td><td>" + mf.getMainAttributes().getValue("BuildDate") + "</td></tr>");


        out.println("<tr><td>Nombre de session utilisateur : </td><td>" + infos.get(SESSION_NUMBER) + "</td></tr>");
        out.println("<tr><td>Nombre de WebService : </td><td>" + infos.get(CURRENT_WS_NUMBER) + "</td></tr>");
        out.println("<tr><td>Date du dernier appel WebService : </td><td>" + infos.get(DATE_LAST_WS) + "</td></tr>");
        out.println("<tr><td>Nombre d'erreurs de génération d'exports : </td><td>" + infos.get(EXPORT_ERRORS) + "</td></tr>");

        out.println("</table>");


        out.println("<h3>Tenants/Applications</h3>");


        out.println("<table>");

        ApplicationListConf appConfList = getApplicationListConf();

        out.println("<tr><td>Tenant</td><td>Application</td><td>Active</td></tr>");

        for (String tenantId : appConfList.getTenantList()) {
            for (String applicationName : appConfList.getApplicationList(tenantId)) {
                out.println("<tr>");
                out.println("    <td>" + tenantId + "</td>");
                
                String dashboardUrl = request.getContextPath();
                dashboardUrl += getInitParameter("DashboardHome");
                dashboardUrl = String.format(dashboardUrl, tenantId, applicationName);
                
                out.println("    <td><a href=\"" + dashboardUrl +"\" target=\"_blank\">" + applicationName + "</td>");
                out.println("    <td>" + Boolean.toString(appConfList.IsApplicationConfActive(tenantId, applicationName)) + "</td>");
                out.println("</tr>");
            }
        }

        out.println("</table>");

        out.println("<p><form name=\"Form\"><input type=\"checkbox\" name=\"Box\" onclick=\"StartRefresh()\">  Rafraichissement automatique (10 secondes)</form></p>");

        out.println("</body></html>");
    }

    private void statusText(HttpServletResponse response, boolean checkDTP) throws IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("Page de supervision : DTP");

        ServletContext application = getServletConfig().getServletContext();
        InputStream inputStream = application.getResourceAsStream("/META-INF/MANIFEST.MF");
        Manifest mf = new Manifest(inputStream);

        out.println("Nom du serveur : " + infos.get(SERVER_NAME));
        out.println("IP du serveur : " + infos.get(SERVER_IP));
        out.println("Date du serveur : " + infos.get(TIME));
        out.println("Mémoire libre de la JVM : " + infos.get(JVM_FREE_MEMORY));
        out.println("Mémoire de la JVM : " + infos.get(JVM_TOTAL_MEMORY));
        out.println("Mémoire maximale de la JVM : " + infos.get(JVM_MAX_MEMORY));
        out.println("Mémoire utilisee de la JVM : " + infos.get(JVM_USED_MEMORY));

        // Test DTP and the value must be on the same line (much more simple to parse)
        final String testDTP;
        if (checkDTP) {
            testDTP = "true";
        } else {
            testDTP = "false";
        }
        out.println("Test DTP : " + testDTP);
        out.println("Implementation Version : "
                + mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE) + " "
                + mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION));

        out.println("Build By : " + mf.getMainAttributes().getValue("BuildBy"));

        out.println("Build Number : " + mf.getMainAttributes().getValue("BuildNumber"));
        out.println("Build Date : " + mf.getMainAttributes().getValue("BuildDate"));

        out.println("Nombre de session utilisateur : " + infos.get(SESSION_NUMBER));
        out.println("Nombre de WebService : " + infos.get(CURRENT_WS_NUMBER));
        out.println("Date du dernier appel WebService : " + infos.get(DATE_LAST_WS));
        out.println("Nombre d'erreurs de génération d'exports : " + infos.get(EXPORT_ERRORS));
    }

    private boolean checkDTP() {
        boolean result = true;
        infos.clear();
        try {
            //Server name
            String serverName = getServerName();
            infos.put(SERVER_NAME, serverName);

            //Time
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            Date date = new Date();
            infos.put(TIME, df.format(date));

            //JVM free memory
            long freeMem = getFreeMem();
            infos.put(JVM_FREE_MEMORY, Long.toString(freeMem));

            //JVM total memory
            long totalMem = getTotalMem();
            infos.put(JVM_TOTAL_MEMORY, Long.toString(totalMem));

            //JVM maximum memory
            long maxMem = getMaxMem();
            infos.put(JVM_MAX_MEMORY, Long.toString(maxMem));

            //JVM used memory
            long usedMem = getUsedMem();
            infos.put(JVM_USED_MEMORY, Long.toString(usedMem));

            //WS
            int ws = Monitoring.getWs();
            infos.put(CURRENT_WS_NUMBER, String.valueOf(ws));

            //Date of last WS
            String lastWs = Monitoring.getDate();
            infos.put(DATE_LAST_WS, lastWs);

            // Export errors
            int exporErrors = Monitoring.getExportErrors();
            infos.put(EXPORT_ERRORS, Integer.toString(exporErrors));

            //session Count
            String sessionCount = getSessionNumber();
            infos.put(SESSION_NUMBER, sessionCount);

            //Configuration
            if (checkConfiguration()) {
                infos.put(CONFIGURATION, OK);
            } else {
                result = false;
            }
        } catch (Exception e) {
            LOG.error(e);
            result = false;
        }

        LOG.exiting();
        return result;
    }

    public static String getSessionNumber() {
        return Integer.toString(HttpSessionExpiryListener.getCurrentSession());
    }

    public static String getServerName() {
        String serverName = null;
        try {
            serverName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOG.error(e);
        }
        return serverName;
    }

    public static String getServerIP() {
        String serverIP = null;
        try {
            serverIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOG.error(e);
        }
        return serverIP;
    }

    public static long getFreeMem() {
        return Runtime.getRuntime().freeMemory();
    }

    public static long getTotalMem() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getMaxMem() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getUsedMem() {
        return getTotalMem() - getFreeMem();
    }

    public static boolean checkConfiguration() {
        return ConfigReader.isConfUp();
    }
}
