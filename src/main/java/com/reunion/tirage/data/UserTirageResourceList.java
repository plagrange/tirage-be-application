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
public class UserTirageResourceList implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4348253494119879795L;
    private List<UserTirageResponse> userResourceList = new ArrayList<UserTirageResponse>();
    private String message;
    private String company;

    public UserTirageResourceList() {

    }

    public UserTirageResourceList(List<UserTirageResponse> userResourceList, String company) {
        this.userResourceList = userResourceList;
        this.company = company;
    }

    public List<UserTirageResponse> getUserResourceList() {
        return userResourceList;
    }

    public void setUserResourceList(List<UserTirageResponse> userResourceList) {
        this.userResourceList = userResourceList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

}
