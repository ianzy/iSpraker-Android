package com.iSpraker.android.dos;

import java.util.Date;

public class HashTag {
	private String hash_tag;
	private double lat;
	private double lng;
	private Date created_at;
	private Date updated_at;
	
	public String getHash_tag() {
		return hash_tag;
	}
	public void setHash_tag(String hash_tag) {
		this.hash_tag = hash_tag;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
}
