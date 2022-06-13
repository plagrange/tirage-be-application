/**
 * 
 */
package com.reunion.tirage.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



/**
 * @author pmekeze
 *
 */
@Entity
@Table(name = "TBL_TIRAGE")
@NamedQueries({
	@NamedQuery(name=Tirage.DELETE_ALL,query="delete from Tirage r where r.compagnie = :compagnie"),
    @NamedQuery(name=Tirage.FIND_TIRAGE_BY_EMAIL_AND_COMPANY,query="Select r from Tirage r where r.email = :email and r.compagnie = :compagnie"),
    @NamedQuery(name=Tirage.SELECT_ALL,query="Select t from Tirage t where t.compagnie = :compagnie")})
public class Tirage {

	public static final String DELETE_BY_TID = null;
	public static final String DELETE_ALL = "deleteAllTirages";
	public static final String FIND_TIRAGE_BY_EMAIL_AND_COMPANY = "getTirageByEmailAndCompany";
	public static final String SELECT_ALL = "selectAll";
	
	@Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Basic
    @Column(name = "EMAIL", nullable = false)
    private String email;
    @Basic
    @Column(name = "NUMERO_TIRE")
    private int numeroTire;
    @Basic
    @Column(name = "COMPAGNIE", nullable = false)
    private String compagnie;
	@Basic
    @Column(name = "METADATA")
    private String metadata;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createTimestamp;
    
    public Tirage(){
    	
    }
    
	public Tirage(String email, int numeroTire, String tenantName){
		this.email = email;
		this.numeroTire = numeroTire;
		this.compagnie = tenantName;
	}
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}
	

    /**
	 * @return the compagnie
	 */
	public String getCompagnie() {
		return compagnie;
	}

	/**
	 * @param compagnie the compagnie to set
	 */
	public void setCompagnie(String compagnie) {
		this.compagnie = compagnie;
	}

	public int getNumeroTire() {
		return numeroTire;
	}


	public void setNumeroTire(int numeroTire) {
		this.numeroTire = numeroTire;
	}


	public String getMetadata() {
		return metadata;
	}


	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	
	/**
     * @return the createTimestamp
     */
    public Date getCeateTimestamp() {
        return createTimestamp;
    }

    /**
     * @param createTimestamp the createTimestamp to set
     */
    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    
    @Override
    public String toString() {
    	return null;
    }
}
