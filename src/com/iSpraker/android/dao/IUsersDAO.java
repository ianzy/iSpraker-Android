package com.iSpraker.android.dao;

import com.iSpraker.android.dos.User;
import com.iSpraker.android.dos.UsersResponse;

public interface IUsersDAO {
	//deprecated
//	public List<User> getUsersByLocation(double lat, double lng);
	
	public UsersResponse getUsersDataByLocation(double lat, double lng);
	
	public UsersResponse getUsersDataByLocation(double lat, double lng, int page);
	
	public UsersResponse getUserByUid(String uid);
	
	public UsersResponse signUpUser(User user);
}
