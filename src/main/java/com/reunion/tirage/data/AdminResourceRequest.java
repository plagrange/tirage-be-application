/**
 * 
 */
package com.reunion.tirage.data;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;

/**
 * @author pmekeze
 *
 */
@ApiModel
public class AdminResourceRequest implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String email;
	private String secureCode;
	private String company;
	

	public AdminResourceRequest(){
		
	}
	
	public AdminResourceRequest(String email, String secureCode, String company){
		this.email = email;
		this.secureCode = secureCode;
		this.company = company;
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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setSecureCode(String secureCode) {
		this.secureCode = secureCode;
	}

	
	public String toString(){
		return this.email + " : " + this.secureCode;
	}
}
