package com.iSpraker.android.dos;

import java.util.Date;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	
	private String email;
	private Date created_at;
	private Date updated_at;
	private double lat;
	private double lng;
	private String token;
	private String uid;
	private String screen_name;
	private String profile_image_url;
	private String description;
	private String time_zone;
	private String phone_number;
	private String twitter_id;
	private Bitmap profileImage;
	
	public Bitmap getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(Bitmap profileImage) {
		this.profileImage = profileImage;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getCreatedAt() {
		return created_at;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.created_at = createdAt;
	}
	
	public Date getUpdatedAt() {
		return updated_at;
	}
	
	public void setUpdatedAt(Date updated_at) {
		this.updated_at = updated_at;
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
		return screen_name;
	}
	
	public void setScreenName(String screenName) {
		this.screen_name = screenName;
	}
	
	public String getProfileImageURL() {
		return profile_image_url;
	}
	
	public void setProfileImageURL(String profileImageURL) {
		this.profile_image_url = profileImageURL;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPhoneNumber() {
		return phone_number;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phone_number = phoneNumber;
	}
	
	public String getTimeZone() {
		return time_zone;
	}
	
	public void setTimeZone(String timeZone) {
		this.time_zone = timeZone;
	}
	
	public String getTwitterId() {
		return twitter_id;
	}
	
	public void setTwitterId(String twitterId) {
		this.twitter_id = twitterId;
	}
	
	public User() {
		
	}
	
	public static final Parcelable.Creator<User> CREATOR = 
        new Parcelable.Creator<User>() { 
        public User createFromParcel(Parcel in) { 
            return new User(in); 
        } 

        public User[] newArray(int size) { 
            return new User[size]; 
        } 
    }; 
	
	public User(Parcel in) {
		email = in.readString();
		lat = in.readDouble();
		lng = in.readDouble();
		token = in.readString();
		uid = in.readString();
		screen_name = in.readString();
		profile_image_url = in.readString();
		description = in.readString();
		time_zone = in.readString();
		phone_number = in.readString();
		twitter_id = in.readString();
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(email);
		dest.writeDouble(lat);
		dest.writeDouble(lng);
		dest.writeString(token);
		dest.writeString(uid);
		dest.writeString(screen_name);
		dest.writeString(profile_image_url);
		dest.writeString(description);
		dest.writeString(time_zone);
		dest.writeString(phone_number);
		dest.writeString(twitter_id);
	}

}
