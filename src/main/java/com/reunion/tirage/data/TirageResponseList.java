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
public class TirageResponseList implements Serializable{

	private List<UserTirageResponse> tirageResponseList = new ArrayList<UserTirageResponse>();
	
	public TirageResponseList(){
		
	}
	
	public List<UserTirageResponse> getTirageResponseList() {
		return tirageResponseList;
	}

	public void setTirageResponseList(List<UserTirageResponse> tirageResponseList) {
		this.tirageResponseList = tirageResponseList;
	}


	public void addTirageResponseToList(UserTirageResponse initUserDto) {
		this.tirageResponseList.add(initUserDto);
	}

}
