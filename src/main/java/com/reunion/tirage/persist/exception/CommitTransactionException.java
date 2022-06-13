package com.reunion.tirage.persist.exception;


/**
 * La classe CommitTransactionException sert � taiter les exceptions au sein du
 * manager de persistance concernant la validation d'une transaction de gestion
 * de la persistance des donn�es.<br>
 * 
 * @author FAO
 */
public class CommitTransactionException extends PersistenceManagerException
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
    public CommitTransactionException(String message)
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
    public CommitTransactionException(Throwable ex)
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
    public CommitTransactionException(String message, Throwable ex)
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
