/**
 * 
 */
package com.reunion.tirage.data;

/**
 * @author pmekeze
 *
 */
public class CompanyExisted {

	private boolean isCompanyExisted = false;
	
	public CompanyExisted(){
	
	}
	public CompanyExisted(boolean isCompanyExisted){
		this.isCompanyExisted = isCompanyExisted;
	}

	
	public boolean isCompanyExisted() {
		return isCompanyExisted;
	}

	public void setCompanyExisted(boolean isCompanyExisted) {
		this.isCompanyExisted = isCompanyExisted;
	}

}
