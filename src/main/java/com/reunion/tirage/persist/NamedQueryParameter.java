package com.reunion.tirage.persist;

/**
 * La classe NamedQueryParameter sert � d�finir les objets permettant de
 * transporter des param�tres pour les requetes textuelles au format JPQL.<br>
 * 
 * @author FAO
 */
public class NamedQueryParameter
{
    
    // ***********************************************************************
    // * CONSTRUCTEUR.
    // ***********************************************************************
    /**
     * Constructeur de la classe.
     * 
     * @param parameterName
     *            chaine de caract�re correspondant au nom du param�tre qui se
     *            trouve dans la requete JPQL. Cette chaine sera remplcer par la
     *            valeur value � l'execution de la requete.
     * 
     * @param value
     *            valeur devant �tre ins�rer dans une requete.
     */
    public NamedQueryParameter(String parameterName, Object value)
    {
        _parameterName = parameterName;
        _value = value;
    }
    
    // ***********************************************************************
    // * METHODE PUBLIC
    // ***********************************************************************
    
    /**
     * Obtenir la chaine de caract�re correspondant au nom du param�tre.
     * 
     * @return chaine de caract�re d�finissant un nom
     */
    public String getParameterName()
    {
        return _parameterName;
    }
    
    /**
     * Obtenir la valeur associ� au nom de param�tre.
     * 
     * @return objet correspondant � la valeur associ� au nom de param�tre.
     */
    public Object getValue()
    {
        return _value;
    }
    
    // ***********************************************************************
    // * METHODE PRIVEE
    // ***********************************************************************
    
    // ***********************************************************************
    // * CONSTANTES
    // ***********************************************************************
    
    // ***********************************************************************
    // * ATTRIBUT DE CLASSE
    // ***********************************************************************
    /** Attribut contenant le nom du param�tre. */
    private final String _parameterName;
    /** Attribut contenant la valeur associ� au nom de param�tre.*/
    private final Object _value;
    
}
