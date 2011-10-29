package com.iSpraker.android.dos;

import java.util.Date;

public class User {
	
	private String email;
	private Date createdAt;
	private Date updatedAt;
	private double lat;
	private double lng;
	private String token;
	private String uid;
	private String screenName;
	private String profileImageURL;
	private String description;
	private String timeZone;
	private String phoneNumber;
	private String twitterId;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public Date getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public double getLatitude() {
		return lat;
	}
	
	public void setLatitude(double lat) {
		this.lat = lat;
	}
	
	public double getLongitude() {
		return lng;
	}
	
	public void setLongitude(double lng) {
		this.lng = lng;
	}
	
	public String getUid() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getScreenName() {
		return screenName;
	}
	
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
	public String getProfileImageURL() {
		return profileImageURL;
	}
	
	public void setProfileImageURL(String profileImageURL) {
		this.profileImageURL = profileImageURL;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getTimeZone() {
		return timeZone;
	}
	
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public String getTwitterId() {
		return twitterId;
	}
	
	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

}
