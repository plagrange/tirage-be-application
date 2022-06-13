/**
 * 
 */
package com.reunion.tirage.data;

/**
 * @author pmekeze
 *
 */
public class UserResponse {
	
	private String email;
	private boolean isNotificationSend;
	private String secureCode;
	
    public UserResponse(){
		
	}
	
	public UserResponse(String email, String secureCode, boolean isNotificationSend){
		
		this.email = email;
		this.secureCode = secureCode;
		this.isNotificationSend = isNotificationSend;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isNotificationSend() {
		return isNotificationSend;
	}

	public void setNotificationSend(boolean isNotificationSend) {
		this.isNotificationSend = isNotificationSend;
	}
	
	public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }
}
