/**
 * 
 */
package com.reunion.tirage.data;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pmekeze
 *
 */
@ApiModel
public class UsersResponse implements Serializable{
	
    private static final long serialVersionUID = -2326501517825606290L;
	private List<UserResponse> userResponseList = new ArrayList<UserResponse>();
	private String company;
	
	private String message;
	
	
    public UsersResponse(){
		
	}
	
	public UsersResponse(List<UserResponse> userResourceList, String company){
		this.userResponseList = userResourceList;
		this.company = company;
	}
	
	public List<UserResponse> getUserResponseList() {
		return userResponseList;
	}

	public void setUserResponseList(List<UserResponse> userResourceList) {
		this.userResponseList = userResourceList;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
	public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
	
}
