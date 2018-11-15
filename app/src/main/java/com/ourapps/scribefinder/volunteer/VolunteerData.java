package com.ourapps.scribefinder.volunteer;

/**
 * Created by Srikanth on 18-03-2018.
 */

public class VolunteerData {

    private String volunteerId;
    private String name;
    private String email;
    private String mobileNumber;
    private String password;
    private String gender;
    private String dob;
    private String address;
    private String pincode;
    private String city;
    private int cityPosition;
    private String district;
    private int districtPosition;
    private String state;
    private int statePosition;
    private boolean english;
    private boolean kannada;
    private boolean telugu;
    private boolean hindi;
    private boolean tamil;
    private String accountType;
    private String filterAddress;
    private String languages;
    private String photoUrl;

    public VolunteerData() {
    }

    public VolunteerData(String volunteerId, String name, String email, String mobileNumber, String password, String gender, String dob, String address, String pincode, String city, int cityPosition, String district, int districtPosition, String state, int statePosition, boolean english, boolean kannada, boolean telugu, boolean hindi, boolean tamil, String accountType, String filterAddress, String languages, String photoUrl) {
        this.volunteerId = volunteerId;
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.pincode = pincode;
        this.city = city;
        this.cityPosition = cityPosition;
        this.district = district;
        this.districtPosition = districtPosition;
        this.state = state;
        this.statePosition = statePosition;
        this.english = english;
        this.kannada = kannada;
        this.telugu = telugu;
        this.hindi = hindi;
        this.tamil = tamil;
        this.accountType = accountType;
        this.filterAddress = filterAddress;
        this.languages = languages;
        this.photoUrl = photoUrl;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCityPosition() {
        return cityPosition;
    }

    public void setCityPosition(int cityPosition) {
        this.cityPosition = cityPosition;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getDistrictPosition() {
        return districtPosition;
    }

    public void setDistrictPosition(int districtPosition) {
        this.districtPosition = districtPosition;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getStatePosition() {
        return statePosition;
    }

    public void setStatePosition(int statePosition) {
        this.statePosition = statePosition;
    }

    public boolean isEnglish() {
        return english;
    }

    public void setEnglish(boolean english) {
        this.english = english;
    }

    public boolean isKannada() {
        return kannada;
    }

    public void setKannada(boolean kannada) {
        this.kannada = kannada;
    }

    public boolean isTelugu() {
        return telugu;
    }

    public void setTelugu(boolean telugu) {
        this.telugu = telugu;
    }

    public boolean isHindi() {
        return hindi;
    }

    public void setHindi(boolean hindi) {
        this.hindi = hindi;
    }

    public boolean isTamil() {
        return tamil;
    }

    public void setTamil(boolean tamil) {
        this.tamil = tamil;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getFilterAddress() {
        return filterAddress;
    }

    public void setFilterAddress(String filterAddress) {
        this.filterAddress = filterAddress;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
