/**
 * 
 */
package com.reunion.tirage.data;

/**
 * @author pmekeze
 *
 */
public class NotifyUserResponse {
	
	private String email;
	private String company;
	private boolean isNotificationSend;
	private String error; 
	
	public NotifyUserResponse(){
		
	}
	
	public NotifyUserResponse(String email, String company, boolean isNotificationSend){
		
		this.email = email;
		this.company = company;
		this.isNotificationSend = isNotificationSend;
	}

	public NotifyUserResponse(String email, String company, boolean isNotificationSend, String error){
		
		this.email = email;
		this.company = company;
		this.isNotificationSend = isNotificationSend;
		this.error = error;
	}

	public boolean isNotificationSend() {
		return isNotificationSend;
	}

	public void setNotificationSend(boolean isNotificationSend) {
		this.isNotificationSend = isNotificationSend;
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
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
