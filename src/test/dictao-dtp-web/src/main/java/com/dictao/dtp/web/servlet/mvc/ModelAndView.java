package com.dictao.dtp.web.servlet.mvc;

import java.util.HashMap;
import java.util.Map;

public final class ModelAndView {
    
    private final Map<String, Object> model;
    
    private final String viewName;

    private final String theme;
    
    public final static String NO_THEME = "NO_THEME/"; 
    
    public ModelAndView(final String viewName) {
        this(viewName, new HashMap<String, Object>());
    }

    public ModelAndView(final String viewName, final Map<String, Object> model) {
        this(viewName, model, null);
    }

    public ModelAndView(final String viewName, final Map<String, Object> model, final String theme) {
        this.model = model;
        this.theme = theme;
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public String getTheme() {
        return theme;
    }

    public String getViewName() {
        return viewName;
    }

}
