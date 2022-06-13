package com.dictao.dtp.web.gwt.common.client.util;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Utilitraire chaine de caratère.
 * 
 * @author gla
 */
public class WidgetUtil
{
    /* *********************************************************************/
    /* CONSTANTS */
    /* *********************************************************************/
    private static final String SID = "dtp";
    private static final String DESABLE = "disable";
    private static final String ERROR = "error";
    /* *********************************************************************/
    /* ATTRIBUTES */
    /* *********************************************************************/
    
    /* *********************************************************************/
    /* PUBLIC METHODS */
    /* *********************************************************************/
    
    
    /**
     * Ajout d'un style à l'élément graphique.
     * 
     * @param widget Element graphique
     * @param id Id de l'élément.
     */
    public static void addStyle(Widget widget, String id)
    {
        widget.addStyleName(SID + "-" + id);
    }
    
    /**
     * Ajout d'un style à l'élément graphique.
     * 
     * @param widget Element graphique
     * 
     */
    public static void addStyleWithoutPrefix(Widget widget, String id)
    {
        widget.addStyleName(id);
    }
    
    /**
     * Ajout d'un style désactivé à l'élément graphique.
     * 
     * @param widget Element graphique
     * @param id Id de l'élément.
     */
    public static void addDisableStyle(Widget widget, String id)
    {
        widget.addStyleName(SID + "-" + id + "-" + DESABLE);
    }
    
    /**
     * Suppression d'un style désactivé à l'élément graphique.
     * 
     * @param widget Element graphique
     * @param id Id de l'élément.
     */
    public static void removeDisableStyle(Widget widget, String id)
    {
        widget.removeStyleName(SID + "-" + id + "-" + DESABLE);
    }
    
    /**
     * Ajout d'un style d'erreur à l'élément graphique.
     * 
     * @param widget Element graphique
     * @param id Id de l'élément.
     */
    public static void addErrorStyle(Widget widget, String id)
    {
        widget.addStyleName(SID + "-" + id + "-" + ERROR);
    }
    
    /**
     * Suppression d'un style d'erreur à l'élément graphique.
     * 
     * @param widget Element graphique
     * @param id Id de l'élément.
     */
    public static void removeErrorStyle(Widget widget, String id)
    {
        widget.removeStyleName(SID + "-" + id + "-" + ERROR);
    }
    
    /**
     * Préparation d'un élément graphique :
     *  - ajout d'un id de style.
     *  - substitution dans le dom.
     * 
     * @param widget Element graphique
     * @param id Id de l'élément.
     */
    public static void addRootPanel(Widget widget, Elements id)
    {
        addStyle(widget, id.getId());
        RootPanel panel = RootPanel.get(id.getId());
        if(panel != null)
        {
            panel.add(widget);
        }
    }
    
    /**
     * Préparation d'un élément graphique :
     *  - ajout d'un id de style.
     *  - substitution dans le dom.
     * 
     * @param widget Element graphique
     * @param id Id de l'élément.
     */
    public static void addRootPanel(Widget widget, String id)
    {
        addStyle(widget, id);
        RootPanel panel = RootPanel.get(id);
        if(panel != null)
        {
            panel.add(widget);
        }
    }

    public static Widget getFirstWidget(Elements id)
    {
        RootPanel panel = RootPanel.get(id.getId());
        if(panel != null)
        {
            return panel.getWidget(0);
        }
        return new Widget();
    }
    
    /* *********************************************************************/
    /* PROTECTED/PRIVATE METHODS */
    /* *********************************************************************/
}
