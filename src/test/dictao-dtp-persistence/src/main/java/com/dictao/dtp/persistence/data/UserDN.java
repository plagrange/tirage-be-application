package com.dictao.dtp.persistence.data;


public class UserDN {
    
    private String countryName;
    private String organizationName;
    private String organizationalUnitName;
    private String emailAddress;
    private String commonName;
    private String subjectAltName;
    
    public UserDN(String countryName, String organizationName, String organizationalUnitName, String emailAddress, String commonName, String subjectAltName) {
    
        this.countryName = countryName;
        this.organizationName = organizationName;
        this.organizationalUnitName = organizationalUnitName;
        this.emailAddress = emailAddress;
        this.commonName = commonName;
        this.subjectAltName = subjectAltName;
    }

    /**
     * @return the countryName
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * @param countryName the countryName to set
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**
     * @return the organizationName
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * @param organizationName the organizationName to set
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * @return the organizationalUnitName
     */
    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }

    /**
     * @param organizationalUnitName the organizationalUnitName to set
     */
    public void setOrganizationalUnitName(String organizationalUnitName) {
        this.organizationalUnitName = organizationalUnitName;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * @return the commonName
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * @param commonName the commonName to set
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * @return the subjectAltName
     */
    public String getSubjectAltName() {
        return subjectAltName;
    }

    /**
     * @param subjectAltName the subjectAltName to set
     */
    public void setSubjectAltName(String subjectAltName) {
        this.subjectAltName = subjectAltName;
    }

}
