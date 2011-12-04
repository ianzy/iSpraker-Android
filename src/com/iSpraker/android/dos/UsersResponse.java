package com.iSpraker.android.dos;

import java.util.List;

public class UsersResponse {
	private List<User> users;
	private Paging paging;
	private int responseCode;

	public UsersResponse() {
		
	}
	
	public UsersResponse(List<User> users, Paging paging, int responseCode) {
		this.users = users;
		this.paging = paging;
		this.responseCode = responseCode;
	}
	
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public Paging getPaging() {
		return paging;
	}
	public void setPaging(Paging paging) {
		this.paging = paging;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
}
