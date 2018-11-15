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
    private String certificateUrl;
    private boolean validUser;

    public NeedyData() {
    }

    NeedyData(String needyId, String name, String email, String mobileNumber, String password, String accountType, String photoUrl, String certificateUrl, boolean validUser) {
        this.needyId = needyId;
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.accountType = accountType;
        this.photoUrl = photoUrl;
        this.certificateUrl = certificateUrl;
        this.validUser = validUser;
    }

    String getNeedyId() {
        return needyId;
    }

    public void setNeedyId(String needyId) {
        this.needyId = needyId;
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

    String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    boolean isValidUser() {
        return validUser;
    }

    public void setValidUser(boolean validUser) {
        this.validUser = validUser;
    }
}
