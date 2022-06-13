package com.dictao.dtp.web.gwt.common.client.util;

/**
 * Utilitraire chaine de caratère.
 * 
 * @author gla
 */
public class TextUtil
{
    /* *********************************************************************/
    /* CONSTANTS */
    /* *********************************************************************/
    
    /* *********************************************************************/
    /* ATTRIBUTES */
    /* *********************************************************************/
    
    /* *********************************************************************/
    /* PUBLIC METHODS */
    /* *********************************************************************/
    	
    /**
     * Permet séparer des chaines par rapport au caractère '|'
     * 
     * @param line Chaine à analyser.
     * @return La liste des chaines séparées.
     */
    public static String[] separate(String line)
    {
        String[] res = null;
        
        if(line != null && !line.equals(""))
        {
            //TODO caractère | en dur pour le moment
            res = line.split("\\|");
        }
        return res;
    }
    
    /**
     * Permet d'extraire la clé valeur par rapport à un caractère séparateur.
     * 
     * @param line Chaine à analyser.
     * @param separator Caractère séparateur.
     * @return Liste de 2 éléments : clé / valeur
     */
    public static String[] extract(String line)
    {
        String[] res = null;
        if(line != null && !line.equals(""))
        {
            //TODO caractère | en dur pour le moment
            int index = line.indexOf(":");
            
            if(index > 0)
            {
                res = new String[2];
                res[0] = line.substring(0, index);
                res[1] = line.substring(index + 1, line.length());
            }
        }
        return res;
    }
    /* *********************************************************************/
    /* PROTECTED/PRIVATE METHODS */
    /* *********************************************************************/
}
