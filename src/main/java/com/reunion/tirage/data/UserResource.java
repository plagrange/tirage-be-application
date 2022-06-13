/**
 * 
 */
package com.reunion.tirage.data;

import java.io.Serializable;

/**
 * @author pmekeze
 *
 */
public class UserResource implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = -7386637781428851272L;
    private String email;
	private String secureCode;
	private boolean isAdmin = false;
	
	public UserResource(){
	    
    }
	
    public UserResource(String email, String secureCode){
		this.email = email;
		this.secureCode = secureCode;
	}
	
	public UserResource(String email, String secureCode, boolean isAdmin){
        this.email = email;
        this.secureCode = secureCode;
        this.isAdmin = isAdmin;
    }
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSecureCode() {
		return secureCode;
	}

	public void setSecureCode(String secureCode) {
		this.secureCode = secureCode;
	}

	public boolean isAdmin() {
	    return isAdmin;
	}
	
	public void setAdmin(boolean isAdmin) {
	    this.isAdmin = isAdmin;
	}
	
	public String toString(){
		return "User email = " + this.email + ", secureCode = " + this.secureCode + "isAdmin = "+ this .isAdmin;
	}
}
