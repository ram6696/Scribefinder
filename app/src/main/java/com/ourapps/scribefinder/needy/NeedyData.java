package com.ourapps.scribefinder.needy;

/**
 * Created by Srikanth on 10-03-2018.
 */

public class NeedyData {

    private String needyId;
    private String name;
    private String email;
    private String mobileNumber;
    private String password;
    private String accountType;
    private String photoUrl;

    public NeedyData() {
    }

    public NeedyData(String needyId, String name, String email, String mobileNumber, String password, String accountType, String photoUrl) {
        this.needyId = needyId;
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.accountType = accountType;
        this.photoUrl = photoUrl;
    }

    public String getNeedyId() {
        return needyId;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

}
