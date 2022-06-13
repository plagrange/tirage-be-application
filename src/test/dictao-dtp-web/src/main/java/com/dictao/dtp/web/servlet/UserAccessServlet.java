/*
 * (@)CelServlet.java
 * Copyright © 2010 Dictao. All rights reserved. Confidential and proprietary.
 * Creation on 5 janv. 10
 * Last Modified on 5 janv. 10
 *
 */

package com.dictao.dtp.web.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.EnvironmentException.Code;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.web.gwt.common.shared.entity.data.ClientConstants;
import com.dictao.dtp.web.servlet.mvc.AbstractController;
import com.dictao.dtp.web.servlet.mvc.Controller;
import com.dictao.dtp.web.servlet.mvc.ModelAndView;
import com.dictao.dtp.web.servlet.mvc.annotation.Path;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

/**
 * This servlet is the main entry point of a UserAccess.
 * 
 * 
 * @author VRB
 */
public final class UserAccessServlet extends HttpServlet {
    /* **************************** CONSTANTS *************************** */
    private static final long serialVersionUID = -5030683673925769323L;

    /* **************************** ATTRIBUTES ************************** */
    private BeanManager bmgr;
    
    @Inject
    private TransactionFactory factory;

    private final ConcurrentMap<Tuple, Class<Controller<? extends TransactionHandler>>> handlers = new ConcurrentHashMap<Tuple, Class<Controller<? extends TransactionHandler>>>();
    
    /* ********************* PUBLIC METHODS ****************** */

    @Override
    public void init() {
        bmgr = (BeanManager) getServletContext().getAttribute(
                "org.jboss.weld.environment.servlet."
                + BeanManager.class.getName());

        Iterator<?> iter = bmgr.getBeans(Controller.class).iterator();
        while(iter.hasNext()) {
            Bean<Controller<? extends TransactionHandler>> bean = (Bean<Controller<? extends TransactionHandler>>) iter.next();
            Class<Controller<? extends TransactionHandler>> clazz = (Class<Controller<? extends TransactionHandler>>) bean.getBeanClass();

            if (clazz.isAnnotationPresent(Path.class)) {
                Path annot = clazz.getAnnotation(Path.class);
                Type rawType = clazz.getGenericSuperclass();
                if (!(rawType instanceof ParameterizedType)) {
                    throw new EnvironmentException(
                            Code.DTP_ENV_CONFIGURATION,
                            "Controller [%s] does not extends AbstractController<T extends TransactionHandler>.",
                            clazz.getName());
                }
                ParameterizedType parameterizedType = (ParameterizedType) rawType;
                if (!(AbstractController.class).isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                    throw new EnvironmentException(
                            Code.DTP_ENV_CONFIGURATION,
                            "Controller [%s] does not extends AbstractController<T extends TransactionHandler>.",
                            clazz.getName());
                }
                if (parameterizedType.getActualTypeArguments().length < 1) {
                    throw new EnvironmentException(
                            Code.DTP_ENV_CONFIGURATION,
                            "Controller [%s] does not extends AbstractController<T extends TransactionHandler>.",
                            clazz.getName());
                }
                Type type = parameterizedType.getActualTypeArguments()[0];
                if (!(TransactionHandler.class).isAssignableFrom((Class<?>) type)) {
                    throw new EnvironmentException(
                            Code.DTP_ENV_CONFIGURATION,
                            "Controller [%s] does not extends AbstractController<T extends TransactionHandler>.",
                            clazz.getName());
                }
                String[] paths = annot.value();
                for (String path : paths) {
                    Tuple tuple = new Tuple(path, (Class<? extends TransactionHandler>) type);
                    Class<Controller<? extends TransactionHandler>> handlerClass = handlers.get(tuple);
                    if (handlerClass != null) {
                        throw new EnvironmentException(Code.DTP_ENV_CONFIGURATION, "Multiple controller implementation ([%s], [%s]) found for path [%s] and type [%s].", clazz.getName(), handlerClass.getName(), path, type);
                    }
                    handlers.put(tuple, clazz);
                }
            } else {
                throw new EnvironmentException(Code.DTP_ENV_CONFIGURATION, "No @Path annotation found on controller implementation [%s].", clazz.getName());
            }
            
        }
    }

    /* ********************* PROTECTED/PRIVATE METHODS ****************** */

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final StopWatch stopWatch = new Log4JStopWatch();
        String tenant = null;
        String applicationName = null;
        String aid = null;
        String file = null;

            String[] pathToken = req.getPathInfo().split("/");
            switch (pathToken.length) {
            case 6:
                tenant = pathToken[1];
                applicationName = pathToken[2];
                aid = pathToken[3];
                file = "";
                break;
            case 7:
                tenant = pathToken[1];
                applicationName = pathToken[2];
                aid = pathToken[3];
                file = pathToken[6];
                break;
            default:
                throw new UserException(
                        com.dictao.dtp.core.exceptions.UserException.Code.DTP_USER_INVALID_PARAMETER,
                        "Excpected url format is [%stenant/application/user_access_id/<page>], received url [%s]",
                        req.getServletPath(), req.getRequestURL().toString());
            }

            // Determine controller for the current request.
            TransactionHandler txHanlder = factory.find(aid);
            Class<Controller<? extends TransactionHandler>> ctrlClass = handlers.get(new Tuple("/" + file, txHanlder.getClass()));
            if (ctrlClass == null) {
                ctrlClass = handlers.get(new Tuple("/" + file, TransactionHandler.class));
                if (ctrlClass == null) {
                    throw new UserException(
                        com.dictao.dtp.core.exceptions.UserException.Code.DTP_USER_INVALID_PARAMETER,
                        "No controller matched for url [%s]", req
                                .getRequestURL().toString());
                }
            }
            Controller controller = getReference(ctrlClass);
            controller.setTransactionHandler(txHanlder);
            ModelAndView mv = controller.handle(aid, "/" + file, req, resp);
            render(mv, req, resp);
    }
    
    private void render(ModelAndView mv, HttpServletRequest req,
            HttpServletResponse resp) {
        final StringBuilder url = new StringBuilder();
        for (Map.Entry<String, Object> entry : mv.getModel().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }
        try {
            final String redirectPrefix = "redirect:";
            if (mv.getViewName().startsWith(redirectPrefix)) {
                url.append(mv.getViewName().substring(redirectPrefix.length()));
                resp.sendRedirect(url.toString());
            } else {
                req.setAttribute("jspName", mv.getViewName());
                url.append(ClientConstants.WEB_INF);
                if (! ModelAndView.NO_THEME.equals(mv.getTheme())) {
                    url.append(ClientConstants.THEME_URL);
                    url.append("/");
                    url.append(findTheme(mv.getTheme(),req));
                }
                url.append("/jsp/");
                url.append(mv.getViewName() + ".jsp");
                File jspFile = new File(getServletContext().getRealPath(url.toString()));
                if (!jspFile.exists()) {
                    throw new UserException(
                            com.dictao.dtp.core.exceptions.UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "No jsp found [%s]", url.toString());
                }
                getServletContext().getRequestDispatcher(url.toString())
                        .forward(req, resp);
            }
        } catch (ServletException e) {
            throw new EnvironmentException(e, Code.DTP_ENV_INTERNAL_ERROR, "Error during jsp forward");
        } catch (IOException e) {
            throw new EnvironmentException(e, Code.DTP_ENV_FILE, "Error during jsp rendering");
        }

    }
    
    
    /**
     * @param name of company supposed matches theme
     * @param req
     * @return name, if name matches a existing theme directory, or default theme alternatively.
     */
    private String findTheme(String name, HttpServletRequest req){
       String theme = ClientConstants.DEFAULT_THEME;
       ServletContext servletContext = getServletContext();
        File directory = new File(servletContext.getRealPath(ClientConstants.WEB_INF
                                        + ClientConstants.THEME_URL));
        if (null != directory && directory.isDirectory()) {
            String folders[] = directory.list();
            for (String folder : folders) {
                if (folder.equals(name)) {
                    File target = new File(
                            servletContext.getRealPath(ClientConstants.WEB_INF
                                + ClientConstants.THEME_URL + "/" + name));
                    if (null != target && target.isDirectory()) {
                        theme = name;
                        break;
                    }
                }
            }
        }
        return theme;  
    }

    protected <T> T getReference(Class<?> clazz) {
        Bean<T> bean = (Bean<T>) bmgr.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = bmgr.createCreationalContext(bean);
        return (T) bmgr.getReference(bean, clazz, ctx);
    }

    private static class Tuple {
        
        private final String path;
        private final Class<? extends TransactionHandler> txHanlder;

        public Tuple(String path, Class<? extends TransactionHandler> txHanlder) {
            super();
            this.path = path;
            this.txHanlder = txHanlder;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = result + ((path == null) ? 0 : path.hashCode());
            result = result
                    + ((txHanlder == null) ? 0 : txHanlder.toString().hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Tuple other = (Tuple) obj;
            if (path == null) {
                if (other.path != null)
                    return false;
            } else if (!path.equals(other.path))
                return false;
            if (txHanlder == null) {
                if (other.txHanlder != null)
                    return false;
            } else if (!txHanlder.equals(other.txHanlder))
                return false;
            return true;
        }
    }
    
}
