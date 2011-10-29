package com.iSpraker.android.dao;

import java.util.List;

import com.iSpraker.android.dos.User;

public interface IUsersDAO {
	public List<User> getUsersByLocation(double lat, double lng);
}
