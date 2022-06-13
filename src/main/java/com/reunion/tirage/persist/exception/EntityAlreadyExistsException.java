package com.reunion.tirage.persist.exception;

/**
 * La classe EntityAlreadyExistsException sert � taiter les exceptions
 * concernant la tentative de cr�ation d'une entit� qui est d�j� persistante en
 * base.<br>
 * 
 * @author FAO
 */
public class EntityAlreadyExistsException extends PersistenceManagerException
{
    
    // ***********************************************************************
    // * CONSTRUCTEUR.
    // ***********************************************************************
    /**
     * Constructeur de la classe prenant un texte en param�tre.
     * 
     * @param message
     *            contient un texte repr�sentant le message d'erreur � associer
     *            avec l'exception.
     */
    public EntityAlreadyExistsException(String message)
    {
        super(message);
    }
    
    /**
     * Constructeur de la classe prenant une exception en param�tre.
     * 
     * @param ex
     *            contient l'exception responsable de la cr�ation de cette
     *            exception.
     */
    public EntityAlreadyExistsException(Throwable ex)
    {
        super(ex);
    }
    
    /**
     * Constructeur de la classe prenant un texte et une exception en param�tre.
     * 
     * @param message
     *            contient un texte repr�sentant le message d'erreur � associer
     *            avec l'exception.
     * @param ex
     *            contient l'exception responsable de la cr�ation de cette
     *            exception.
     */
    public EntityAlreadyExistsException(String message, Throwable ex)
    {
        super(message, ex);
    }
    
    // ***********************************************************************
    // * CONSTANTES
    // ***********************************************************************
    /**
     * Attribut de classe contenant le num�ro de serie pour la serialisation de
     * l'objet.
     */
    private static final long serialVersionUID = 950790186073992770L;
    
}
