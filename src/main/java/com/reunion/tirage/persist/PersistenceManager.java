package com.reunion.tirage.persist;

import java.util.List;

import javax.persistence.Query;

import com.reunion.tirage.persist.exception.PersistenceManagerException;

@SuppressWarnings("all")
public interface PersistenceManager
{
    /**
     * Enregistrer en base l'ensemble des entités passées en paramètres.
     * 
     * @param entities
     *            table contenant l'ensemble des entités à enregistrer en base.
     * @exception PersistenceManagerException
     *                <br>
     * 
     * L'exception PersistenceManagerException est l'exception générique levée
     * par cette méthode. <br>
     * Les exceptions suivantes qui héritent de la classe
     * PersistenceManagerException, permettent à l'utilisateur d'identifier plus
     * finement le problème.
     * <ul>
     * <li>L'exception EntityAlreadyExistsException est levée si l'entité
     * existe déjà en base,</li>
     * <li>L'exception InvalidEntityException est levé si:
     * <ul>
     * <li>l'entité n'est pas une des entités définit dans le fichier de
     * configuration (META_INF/persistence.xml),</li>
     * <li>l'entité ne respecte pas les règles définient dans les annotations
     * de sa classe,</li>
     * </ul>
     * </li>
     * <li>L'exception InvalidParametersException est levé si le paramètre de
     * la méthode est à null ou vide,</li>
     * <li> L'exception PersistenceManagerException est levé si:
     * <ul>
     * <li>l'accès à la base est fermé,</li>
     * <li>une erreur intervient dans la gestion des transaction régissant le
     * dialogue avec le gestionnaire de persistance,</li>
     * <li>l'entité n'a pu être enregistrée en base.</li>
     * </ul>
     * </li>
     * </ul>
     */
    void create(Object... entities) throws PersistenceManagerException;
    
    /**
     * Effacer de la base l'ensemble des entités passées en paramètres.
     * 
     * @param entities
     *            table contenant l'ensemble des entités à effacer de la base.
     * @exception PersistenceManagerException
     *                <br>
     * 
     * L'exception PersistenceManagerException est l'exception générique levée
     * par cette méthode. <br>
     * Les exceptions suivantes qui héritent de la classe
     * PersistenceManagerException, permettent à l'utilisateur d'identifier plus
     * finement le problème.
     * <ul>
     * <li>L'exception InvalidParametersException est levé si le paramètre de
     * la méthode est à null ou vide,</li>
     * <li>L'exception InvalidEntityException est levé si l'entité n'est pas
     * une des entités définit dans le fichier de configuration
     * (META_INF/persistence.xml),</li>
     * <li> L'exception PersistenceManagerException est levé si:
     * <ul>
     * <li>l'accès à la base est fermé,</li>
     * <li>une erreur intervient dans la gestion des transaction régissant le
     * dialogue avec le gestionnaire de persistance,</li>
     * <li>l'entité n'a pu être effacàe de la base.</li>
     * </ul>
     * </li>
     * </ul>
     */
    void delete(Object... entities) throws PersistenceManagerException;
    
    /**
     * Mettre à jour en base l'ensemble des entités passées en paramètres.
     * 
     * @param entities
     *            table contenant l'ensemble des entités à mettre à jour en
     *            base.
     * @exception PersistenceManagerException
     * 
     * <br>
     * 
     * L'exception PersistenceManagerException est l'exception générique levée
     * par cette méthode. <br>
     * Les exceptions suivantes qui héritent de la classe
     * PersistenceManagerException, permettent à l'utilisateur d'identifier plus
     * finement le problème.
     * <ul>
     * <li>L'exception InvalidEntityException est levé si:
     * <ul>
     * <li>l'entité n'est pas une des entités définit dans le fichier de
     * configuration (META_INF/persistence.xml),</li>
     * <li>l'entité ne respecte pas les régles définient dans les annotations
     * de sa classe,</li>
     * </ul>
     * </li>
     * <li>L'exception InvalidParametersException est levé si le paramètre de
     * la méthode est à null ou vide,</li>
     * <li> L'exception PersistenceManagerException est levé si:
     * <ul>
     * <li>l'accès à la base est fermé,</li>
     * <li>une erreur intervient dans la gestion des transaction régissant le
     * dialogue avec le gestionnaire de persistance,</li>
     * <li>l'entité n'a pas été trouvé en base pour réaliser les modifications.</li>
     * </ul>
     * </li>
     * </ul>
     */
    void update(Object... entities) throws PersistenceManagerException;
    
    public int updateQuery(Query query) throws PersistenceManagerException;
    /**
     * Obtenir l'objet correspondant à la classe et à l'id passé en paramètre.
     * 
     * @param type
     *            contient le type de la classe dont l'objet recherché est une
     *            instance.
     * @param id
     *            contient un numéro d'identification de l'objet en base.
     * @return l'objet du type de la classe passé en paramètre ou null si il
     *         n'est pas trouvé.
     * @throws PersistenceManagerException
     *             <br>
     * 
     * L'exception PersistenceManagerException est l'exception générique levée
     * par cette méthode. <br>
     * Les exceptions suivantes qui héritent de la classe
     * PersistenceManagerException, permettent à l'utilisateur d'identifier plus
     * finement le problème.
     * <ul>
     * <li>L'exception InvalidParametersException est levé si l'un des
     * paramètres de la méthode est à null ou invalide,</li>
     * <li> L'exception PersistenceManagerException est levé si:
     * <ul>
     * <li>l'accès à la base est fermé,</li>
     * <li>la requete est erronée,</li>
     * <li>une erreur intervient dans la gestion des transaction régissant le
     * dialogue avec le gestionnaire de persistance,</li>
     * <li>une erreur interne est intervenu pendant la recherche.</li>
     * </ul>
     * </li>
     * </ul>
     */
    Object findById(Class type, Long id) throws PersistenceManagerException;
    
    /**
     * Obtenir l'ensemble des objets correspondant à la classe en paramètre.
     * 
     * @param type
     *            contient le type de la classe dont on recherche tous les
     *            instances persistantes en bases.
     * @param nombreMaxResultat
     *            deux cas possibles:
     *            <li>Si non null: indique le nombre d'objet maximum que doit
     *            contenir la liste renvoyée</li>
     *            <li>Sinon : aucun filtrage du nombre d'objet</li>
     * @return l'ensemble des objets du type de la classe passé en paramètre.
     * @throws PersistenceManagerException
     *             <br>
     * 
     * L'exception PersistenceManagerException est l'exception générique levée
     * par cette méthode. <br>
     * Les exceptions suivantes qui héritent de la classe
     * PersistenceManagerException, permettent à l'utilisateur d'identifier plus
     * finement le problème.
     * <ul>
     * <li>L'exception InvalidParametersException est levé si l'un des
     * paramètres de la méthode est à null ou invalide,</li>
     * <li> L'exception PersistenceManagerException est levé si:
     * <ul>
     * <li>l'accès à la base est fermé,</li>
     * <li>la requete est erronée,</li>
     * <li>une erreur intervient dans la gestion des transaction régissant le
     * dialogue avec le gestionnaire de persistance,</li>
     * <li>une erreur interne est intervenu pendant la recherche.</li>
     * </ul>
     * </li>
     * </ul>
     */
    List<Object> findAll(Class type, Integer nombreMaxResultat)
            throws PersistenceManagerException;
    
    /**
     * Obtenir l'ensemble des objets correspondant à une requete JPQL passée en
     * paramètre.
     * 
     * @param query
     *            contient un requete utilisant le format JPQL.
     * @param nombreMaxResultat
     *            deux cas possibles:
     *            <li>Si non null: indique le nombre d'objet maximum que doit
     *            contenir la liste renvoyée</li>
     *            <li>Sinon : aucun filtrage du nombre d'objet</li>
     * @return l'ensemble des objets repondant au critère de la requete passée
     *         en paramètre.
     * @throws PersistenceManagerException
     *             <br>
     * 
     * L'exception PersistenceManagerException est l'exception générique levée
     * par cette méthode. <br>
     * Les exceptions suivantes qui héritent de la classe
     * PersistenceManagerException, permettent à l'utilisateur d'identifier plus
     * finement le problème.
     * <ul>
     * <li>L'exception InvalidParametersException est levé si l'un des
     * paramètres de la méthode est à null ou invalide,</li>
     * <li> L'exception PersistenceManagerException est levé si:
     * <ul>
     * <li>l'accès à la base est fermé,</li>
     * <li>la requete est erronée,</li>
     * <li>une erreur intervient dans la gestion des transaction régissant le
     * dialogue avec le gestionnaire de persistance,</li>
     * <li>une erreur interne est intervenu pendant la recherche.</li>
     * </ul>
     * </li>
     * </ul>
     */
    List<Object> find(String query, Integer nombreMaxResultat)
            throws PersistenceManagerException;
    
    /**
     * Obtenir l'ensemble des objets correspondant à une requete JPQL passée en
     * paramètre. La requete peut contenir la chaine "?1" qui sera remplacer par
     * l'objet passé en paramètre. <br>
     * <br>
     * <b>Exemple:</b><br>
     * find("Select cat from Category cat where cat.id=?1 ", category.getId());<br>
     * Ici la chaine "?1" sera remplacé par la valeur de category.getId().
     * 
     * @param query
     *            contient un requete utilisant le format JPQL.
     * @param value
     *            contient la valeur à utiliser dans la requete.
     * @param nombreMaxResultat
     *            deux cas possibles:
     *            <li>Si non null: indique le nombre d'objet maximum que doit
     *            contenir la liste renvoyée</li>
     *            <li>Sinon : aucun filtrage du nombre d'objet</li>
     * @return l'ensemble des objets repondant au critère de la requete passée
     *         en paramètre.
     * @throws PersistenceManagerException
     *             <br>
     * 
     * L'exception PersistenceManagerException est l'exception générique levée
     * par cette méthode. <br>
     * Les exceptions suivantes qui héritent de la classe
     * PersistenceManagerException, permettent à l'utilisateur d'identifier plus
     * finement le problème.
     * <ul>
     * <li>L'exception InvalidParametersException est levé si l'un des
     * paramètres de la méthode est à null ou invalide,</li>
     * <li> L'exception PersistenceManagerException est levé si:
     * <ul>
     * <li>l'accès à la base est fermé,</li>
     * <li>la requete est erronée,</li>
     * <li>une erreur intervient dans la gestion des transaction régissant le
     * dialogue avec le gestionnaire de persistance,</li>
     * <li>une erreur interne est intervenu pendant la recherche.</li>
     * </ul>
     * </li>
     * </ul>
     */
    List<Object> find(String query, Object value, Integer nombreMaxResultat)
            throws PersistenceManagerException;
    
    /**
     * Obtenir l'ensemble des objets correspondant à une requete JPQL passée en
     * paramètre. La requete peut contenir une chaine (paramètre name) qui sera
     * remplacer par l'objet value passé en paramètre. <br>
     * Un objet NamedQueryParameter contient l'association entre la chaine à
     * remplacer et la valeur.<br>
     * <br>
     * <b>Exemple:</b><br>
     * find( "Select cat from Category cat where cat.id=:id ", "id",
     * category.getId());<br>
     * Ici la chaine "id" sera remplacé par la valeur de category.getId().
     * 
     * @param query
     *            contient un requete utilisant le format JPQL.
     * @param name
     *            contient la chaine devant être remplcé dans la requête.
     * @param value
     *            contient la valeur à utiliser dans la requete
     * 
     * @param nombreMaxResultat
     *            deux cas possibles:
     *            <li>Si non null: indique le nombre d'objet maximum que doit
     *            contenir la liste renvoyée</li>
     *            <li>Sinon : aucun filtrage du nombre d'objet</li>
     * @return l'ensemble des objets repondant au critère de la requete passée
     *         en paramètre.
     * @throws PersistenceManagerException
     *             <br>
     * 
     * L'exception PersistenceManagerException est l'exception générique levée
     * par cette méthode. <br>
     * Les exceptions suivantes qui héritent de la classe
     * PersistenceManagerException, permettent à l'utilisateur d'identifier plus
     * finement le problème.
     * <ul>
     * <li>L'exception InvalidParametersException est levé si l'un des
     * paramètres de la méthode est à null ou invalide,</li>
     * <li> L'exception PersistenceManagerException est levé si:
     * <ul>
     * <li>l'accès à la base est fermé,</li>
     * <li>la requete est erronée,</li>
     * <li>une erreur intervient dans la gestion des transaction régissant le
     * dialogue avec le gestionnaire de persistance,</li>
     * <li>une erreur interne est intervenu pendant la recherche.</li>
     * </ul>
     * </li>
     * </ul>
     */
    List<Object> find(String query, String name, Object value,
            Integer nombreMaxResultat) throws PersistenceManagerException;
    
    /**
     * Obtenir l'ensemble des objets correspondant à une requete JPQL passée en
     * paramètre. La requete peut contenir plusieurs chaines qui seront
     * remplacer par l'objet passé en paramètre. <br>
     * <br>
     * <br>
     * <b>Exemple:</b><br>
     * findNamedParameter( <br>
     * "Select cat from Category cat where cat.id=:id and cat.name=:name", <br>
     * null, <br>
     * new NamedQueryParameter("id", category.getId()),<br>
     * new NamedQueryParameter("name", category.getName()));<br>
     * Ici la chaine "id" sera remplacé par la valeur de category.getId() et la
     * chaine "name" sera remplacé par la valeur de category.getName().
     * 
     * @param query
     *            contient un requete utilisant le format JPQL.
     * @param args
     *            contient un tableau de NamedQueryParameter.
     * @param nombreMaxResultat
     *            deux cas possibles:
     *            <li>Si non null: indique le nombre d'objet maximum que doit
     *            contenir la liste renvoyée</li>
     *            <li>Sinon : aucun filtrage du nombre d'objet</li>
     * @return l'ensemble des objets repondant au critère de la requete passée
     *         en paramètre.
     * @throws PersistenceManagerException
     *             <br>
     * 
     * L'exception PersistenceManagerException est l'exception générique levée
     * par cette méthode. <br>
     * Les exceptions suivantes qui héritent de la classe
     * PersistenceManagerException, permettent à l'utilisateur d'identifier plus
     * finement le problème.
     * <ul>
     * <li>L'exception InvalidParametersException est levé si l'un des
     * paramètres de la méthode est à null ou invalide,</li>
     * <li> L'exception PersistenceManagerException est levé si:
     * <ul>
     * <li>l'accès à la base est fermé,</li>
     * <li>la requete est erronée,</li>
     * <li>une erreur intervient dans la gestion des transaction régissant le
     * dialogue avec le gestionnaire de persistance,</li>
     * <li>une erreur interne est intervenu pendant la recherche.</li>
     * </ul>
     * </li>
     * </ul>
     */
    List<Object> findNamedParameter(String query, Integer nombreMaxResultat,
            NamedQueryParameter... args) throws PersistenceManagerException;
    
    /**
     * Obtenir l'ensemble des objets correspondant au nom d'une requete JPQL
     * passée en paramètre.<br>
     * La requete est définit en annotation d'une des classes géré en
     * persistances. Chaque requete est identifier par un nom de requete.
     * 
     * @param queryName
     *            contient un nom de requete utilisant le format JPQL.
     * @param nombreMaxResultat
     *            deux cas possibles:
     *            <li>Si non null: indique le nombre d'objet maximum que doit
     *            contenir la liste renvoyée</li>
     *            <li>Sinon : aucun filtrage du nombre d'objet</li>
     * @return l'ensemble des objets repondant au critère de la requete.
     * @throws PersistenceManagerException
     *             <br>
     * 
     * L'exception PersistenceManagerException est l'exception générique levée
     * par cette méthode. <br>
     * Les exceptions suivantes qui héritent de la classe
     * PersistenceManagerException, permettent à l'utilisateur d'identifier plus
     * finement le problème.
     * <ul>
     * <li>L'exception InvalidParametersException est levé si l'un des
     * paramètres de la méthode est à null ou invalide,</li>
     * <li> L'exception PersistenceManagerException est levé si:
     * <ul>
     * <li>l'accès à la base est fermé,</li>
     * <li>la requete est erronée,</li>
     * <li>une erreur intervient dans la gestion des transaction régissant le
     * dialogue avec le gestionnaire de persistance,</li>
     * <li>une erreur interne est intervenu pendant la recherche.</li>
     * </ul>
     * </li>
     * </ul>
     */
    List<Object> findByNamedQuery(String queryName, Integer nombreMaxResultat)
            throws PersistenceManagerException;
    
    /**
     * Obtenir l'objet correspondant au nom d'une requete JPQL passée en
     * paramètre.<br>
     * La requete est définit en annotation d'une des classes géré en
     * persistances. Chaque requete est identifier par un nom de requete.
     * 
     * @param queryName
     *            contient un nom de requete utilisant le format JPQL.
     * @return l'objet répondant au critère de la requete.
     * @throws PersistenceManagerException
     *             <br>
     * 
     * L'exception PersistenceManagerException est l'exception générique levée
     * par cette méthode. <br>
     * Les exceptions suivantes qui héritent de la classe
     * PersistenceManagerException, permettent à l'utilisateur d'identifier plus
     * finement le problème.
     * <ul>
     * <li>L'exception InvalidParametersException est levé si l'un des
     * paramètres de la méthode est à null ou invalide,</li>
     * <li> L'exception PersistenceManagerException est levé si:
     * <ul>
     * <li>l'accès à la base est fermé,</li>
     * <li>la requete est erronée,</li>
     * <li>il ya plus d'une réponse à la requete,</li>
     * <li>une erreur intervient dans la gestion des transaction régissant le
     * dialogue avec le gestionnaire de persistance,</li>
     * <li>une erreur interne est intervenu pendant la recherche.</li>
     * </ul>
     * </li>
     * </ul>
     */
    Object findInstanceByNamedQuery(String queryName)
            throws PersistenceManagerException;
    
    List<Object> find(Query query, int nombreMaxResultat) throws PersistenceManagerException;
    
    /**
     * Fermer le manager de persistance.
     */
    void closeSession();

    Query createNamedQuery(String findRequestById);

}
