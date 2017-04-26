package com.jcedar.paperbag.model;

/**
 * Created by OLUWAPHEMMY on 3/27/2017.
 */

public class User {

    private String userID;
    private String name;
    private String email;
    private String role;
    private String activationId;

    public User () {

    }

    public User(String userID, String name, String email, String role, String activationId) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.role = role;
        this.activationId = activationId;
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

    public String getActivationId() {
        return activationId;
    }

    public void setActivationId(String activationId) {
        this.activationId = activationId;
    }
}
