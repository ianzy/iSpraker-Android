package com.iSpraker.android.dos;

import java.util.Date;

public class Event {
	private String name;
	private String description;
	private String url;
	private double lat;
	private double lng;
	private String address;
	private Date eventTime;
	private Date createdAt;
	private Date updatedAt;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
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
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public Date getEventTime() {
		return eventTime;
	}
	
	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
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

}
