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
public class UserTirageResponse implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = -8076693466500879494L;
    private String email;
	private int numero;
	private String company;
	
	public UserTirageResponse(String email, int numero, String company){
		this.email = email;
		this.numero = numero;
		this.company = company;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}
	
	public String toString(){
		return this.email + " : " + this.numero;
	}
	
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
}
