package com.iSpraker.android.dos;

import java.util.List;

public class UsersResponse {
	private List<User> users;
	private Paging paging;
	
	public UsersResponse() {
		
	}
	
	public UsersResponse(List<User> users, Paging paging) {
		this.users = users;
		this.paging = paging;
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
	
}
