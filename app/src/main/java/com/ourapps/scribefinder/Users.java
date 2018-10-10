package com.ourapps.scribefinder;

public class Users {

    private String userId;
    private String email;
    private String password;
    private String accountType;
    private String name;
    private String mobileNumber;

    public Users() {
    }

    public Users(String userId, String email, String password, String accountType, String name, String mobileNumber) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
        this.name = name;
        this.mobileNumber = mobileNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
