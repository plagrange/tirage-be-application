/**
 * 
 */
package com.reunion.tirage.data;

/**
 * @author pmekeze
 *
 */
public class NotifyUserResource {
	
	private String email;
	private String company;
	
	public NotifyUserResource(){
		
	}
	
	public NotifyUserResource(String email, String company){
		
		this.email = email;
		this.company = company;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
}
