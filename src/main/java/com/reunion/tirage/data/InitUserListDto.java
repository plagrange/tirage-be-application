/**
 * 
 */
package com.reunion.tirage.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pmekeze
 *
 */
public class InitUserListDto implements Serializable{

	private List<UserResource> initUserList = new ArrayList<UserResource>();
	
	public InitUserListDto(List<UserResource> initUserList){
		this.initUserList = initUserList;
	}
	
	/**
	 * @return the initUserList
	 */
	public List<UserResource> getInitUserList() {
		return initUserList;
	}



	/**
	 * @param initUserList the initUserList to set
	 */
	public void setInitUserList(List<UserResource> initUserList) {
		this.initUserList = initUserList;
	}

	public void saddInitUserList(UserResource initUserDto) {
		this.initUserList.add(initUserDto);
	}

}
