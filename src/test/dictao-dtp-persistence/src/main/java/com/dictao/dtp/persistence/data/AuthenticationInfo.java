package com.dictao.dtp.persistence.data;


public class AuthenticationInfo {

    private String userId;
    private String phoneNumber;
    private Integer securityLevel;
    
    public AuthenticationInfo(String userId, String phoneNumber, Integer securityLevel){
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.securityLevel = securityLevel;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the securityLevel
     */
    public Integer getSecurityLevel() {
        return securityLevel;
    }

    /**
     * @param securityLevel the securityLevel to set
     */
    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }
}
