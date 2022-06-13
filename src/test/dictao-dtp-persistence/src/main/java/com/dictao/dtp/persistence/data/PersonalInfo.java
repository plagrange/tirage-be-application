package com.dictao.dtp.persistence.data;

import java.util.Date;

public class PersonalInfo {
    
    private String user;
    private String title;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private UserDN userDN;
    private boolean mainContractor;
    
    public PersonalInfo(String user, String title, String firstName,
            String lastName, Date birthDate, UserDN userDN, boolean mainContractor) {
    
        this.user = user;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.userDN = userDN;
        this.mainContractor = mainContractor;
    }
    
    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the birthDate
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate the birthDate to set
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return the mainContractor
     */
    public boolean isMainContractor() {
        return mainContractor;
    }

    /**
     * @param mainContractor the mainContractor to set
     */
    public void setMainContractor(boolean mainContractor) {
        this.mainContractor = mainContractor;
    }

    /**
     * @return the userDN
     */
    public UserDN getUserDN() {
        return userDN;
    }

    /**
     * @param userDN the userDN to set
     */
    public void setUserDN(UserDN userDN) {
        this.userDN = userDN;
    }
    
}
