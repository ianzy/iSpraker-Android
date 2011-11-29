package com.iSpraker.android.app;

import java.util.List;

import com.iSpraker.android.dos.User;

public interface IPeopleWallCallbacks {
	List<User> getUsers();
	void onWallModeChange(List<User> users);
}
