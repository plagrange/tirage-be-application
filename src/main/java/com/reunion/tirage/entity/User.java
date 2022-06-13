/**
 * 
 */
package com.reunion.tirage.entity;

import java.io.Serializable;
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
@Table(name = "TBL_USER")
@NamedQueries({
    @NamedQuery(name=User.FIND_USER_BY_EMAIL,query="Select u from User u where u.email = :email and u.compagnie = :compagnie"),
    @NamedQuery(name=User.FIND_USER_BY_COMPAGNIE,query="Select u from User u where u.compagnie = :compagnie"),
    @NamedQuery(name=User.UPDATE_USER,query="update User u set u.notificationSend = :notificationSend where u.compagnie = :compagnie and u.email = :email"),
    @NamedQuery(name=User.DELETE_USER_BY_EMAIL,query="delete from User u where u.email = :email"),
@NamedQuery(name=User.DELETE_ALL,query="delete from User u")})
public class User implements Serializable{

    private static final long serialVersionUID = 4209093585499931386L;
    
	public static final String FIND_USER_BY_EMAIL = "getUserByEmail";
	public static final String FIND_USER_BY_COMPAGNIE = "selectAllUser";
	public static final String DELETE_USER_BY_EMAIL = "deleteUserByEmail";
	public static final String DELETE_ALL = "deleteAllUsers";
	public static final String UPDATE_USER = "updateUser";
	
	@Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
//	@Basic
//    @Column(name = "User_ID", nullable = false)
//    private String UserID;
    @Basic
    @Column(name = "EMAIL", nullable = false)
    private String email;
    @Basic
    @Column(name = "SECURECODE")
    private String codeSecurite;
    @Basic
    @Column(name = "COMPAGNIE")
    private String compagnie;
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createTimestamp;
	
	@Basic
    @Column(name = "NOTIFICATION_SEND")
    private boolean notificationSend;
    
    public User(){
    	
    }
    
	public User(String email, String codeSecurite, String compagnie){
		
		this.email = email;
		this.codeSecurite = codeSecurite;
		this.compagnie = compagnie;
	}
	
	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getCodeSecurite() {
		return codeSecurite;
	}


	public void setCodeSecurite(String codeSecurite) {
		this.codeSecurite = codeSecurite;
	}
	
	public String getCompagnie() {
		return compagnie;
	}

	public void setCompagnie(String compagnie) {
		this.compagnie = compagnie;
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

    public boolean isNotificationSend() {
        return notificationSend;
    }

    public void setNotificationSend(boolean notificationSend) {
        this.notificationSend = notificationSend;
    }
    @Override
    public String toString() {
    	return null;
    }
}
