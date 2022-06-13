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
public class UserResourceList implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = -2326501517825606290L;
    private List<UserResource> userResourceList = new ArrayList<UserResource>();
	

	public UserResourceList(){
		
	}
	
	public UserResourceList(List<UserResource> userResourceList){
		this.userResourceList = userResourceList;
	}

	public List<UserResource> getUserResourceList() {
		return userResourceList;
	}
	
	public void setUserResourceList(List<UserResource> userResourceList) {
		this.userResourceList = userResourceList;
	}
	
	public void addUserResourceToList(UserResource userResource){
		this.userResourceList.add(userResource);
	}
}
