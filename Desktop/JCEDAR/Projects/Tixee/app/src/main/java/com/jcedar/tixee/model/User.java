package com.jcedar.tixee.model;

/**
 * Created by OLUWAPHEMMY on 2/14/2017.
 */

public class User {

    private String userID;
    private String name;
    private String email;
    private String role;
    private UserCampaign userCampaign;

    public User () {

    }


    public User(String userID, String name, String email, String role, UserCampaign userCampaign) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.role = role;
        this.userCampaign = userCampaign;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserCampaign getUserCampaign() {
        return userCampaign;
    }

    public void setUserCampaign(UserCampaign userCampaign) {
        this.userCampaign = userCampaign;
    }
}
