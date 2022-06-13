/**
 * 
 */
package com.reunion.tirage.data;

import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pmekeze
 *
 */
@ApiModel
public class CreateTirageRequest {
	
	private List<UserResource> userListRequest = new ArrayList<UserResource>();
	private String company;
	private boolean notificationEnabled = false;

	public CreateTirageRequest(){
		
	}
	
	public CreateTirageRequest(List<UserResource> userResourceList, String company, boolean notificationEnabled){
		this.userListRequest = userResourceList;
		this.company = company;
		this.notificationEnabled = notificationEnabled;
	}
	
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public List<UserResource> getUserListRequest() {
		return userListRequest;
	}

	public void setUserListRequest(List<UserResource> userListRequest) {
		this.userListRequest = userListRequest;
	}

	public boolean isNotificationEnabled() {
		return notificationEnabled;
	}

	public void setNotificationEnabled(boolean notificationEnabled) {
		this.notificationEnabled = notificationEnabled;
	}
}
