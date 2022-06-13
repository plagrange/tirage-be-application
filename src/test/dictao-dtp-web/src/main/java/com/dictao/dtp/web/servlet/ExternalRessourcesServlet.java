package com.dictao.dtp.web.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.dictao.dtp.core.transactions.ApplicationListConf;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;

public class ExternalRessourcesServlet extends DocumentServletBase {

    /* **************************** CONSTANTS *************************** */
    private static final Logger LOG = LoggerFactory.getLogger(ExternalRessourcesServlet.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + ExternalRessourcesServlet.class.getName());
   
    private static final long serialVersionUID = -3647298593522205517L;

    @Inject
    private ApplicationListConf configuration;

    /* **************************** ATTRIBUTES ************************** */
    
    /* ************************** PUBLIC METHODS ************************ */

    /* ********************* PROTECTED/PRIVATE METHODS ****************** */

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        sendError(response, HttpServletResponse.SC_NOT_IMPLEMENTED);
    }

    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
         new ServletTask(response) {

            @Override
            public void run() throws Exception {

                // Parameter extraction
                String[] params = URLDecoder.decode(request.getPathInfo(), "UTF-8").substring(1).split("/");
                if (params.length < 4) {
                    LOGU.error("Error decode url parameters: %s",
                            request.getPathInfo());
                    sendError(response, HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                String tenant = params[0];
                String applicationName = params[1];
                String company = params[2];
                String mainFolder = params[3];

                // Check if the resource is authorized 
                if (!mainFolder.equals("css") && !mainFolder.equals("images") && !mainFolder.equals("img")
                        && !mainFolder.equals("dacs") && !mainFolder.equals("blugin")) {   
                    LOGU.error( "Unauthorized access to folder '%s' occurred", mainFolder);
                    sendError(response, HttpServletResponse.SC_NOT_FOUND);
                    return;
                }


                String docFileName = params[params.length-1];
                String ressource = "";
                for(int i=3;i<params.length;i++){
                    ressource = ressource + File.separator +params[i];
                }
                
                String pathContext = configuration.getStaticResourceFolder(tenant, applicationName, company);
                if(pathContext!=null){

                    String path = pathContext + ressource;

                    if((new File(path)).exists()){
                        
                        File file = new File(path);
                        FileInputStream fin = new FileInputStream(file);
                        byte fileContent[] = new byte[(int)file.length()];
                        fin.read(fileContent);
                        long lastMod = file.lastModified();
                        InputStream input = new ByteArrayInputStream(fileContent);

                        String disposition = URL_PARAM_DISPOSITION_VALUE_INLINE;
                        response.reset();
                        ServletOutputStream output = response.getOutputStream();

                        String contentDisposition;
                        contentDisposition = disposition + ";filename=\"" + docFileName + "\"";
                        String contentType = getContentType(docFileName);
                        response.setHeader("Content-Disposition", contentDisposition);
                        response.setContentType(contentType);
                        response.setContentLength((int)file.length());
                        response.setDateHeader("Last-modified", lastMod);
                        copyAll(input, output);
                        output.flush();
                        output.close();

                    } else {
                        LOG.debug("Unable to load following resource : '%s'", path);
                        sendError(response, HttpServletResponse.SC_NOT_FOUND);
                    }

                } else {
                    sendError(response, HttpServletResponse.SC_NOT_FOUND);
                }
                
            }
        }.execute();
    }

    private String getContentType(String docFileName) {
        //Type text
        if(docFileName.endsWith(".css")){
            return "text/css";
        } else if(docFileName.endsWith(".csv")){
            return "text/csv";
        } else if(docFileName.endsWith(".html")){
            return "text/html";
        }
        //Type images
        else if(docFileName.endsWith(".png")){
            return "image/png";
        } else if(docFileName.endsWith(".gif")){
            return "image/gif";
        } else if(docFileName.endsWith(".jpg")){
            return "image/jpg";
        } else if(docFileName.endsWith(".jpeg")){
            return "image/jpeg";
        }
        //Type application
        else if(docFileName.endsWith(".pdf")){
            return "application/pdf";
        } else if(docFileName.endsWith(".js")){
            return "text/javascript";
        } else if(docFileName.endsWith(".xml")){
            return "application/xml";
        } else if(docFileName.endsWith(".zip")){
            return "application/zip";
        } else if(docFileName.endsWith(".jar")){
            return "application/java-archive";
        } else if(docFileName.endsWith(".class")){
            return "application/x-java-applet";
        }
        

        return "text/plain";
    }
}
