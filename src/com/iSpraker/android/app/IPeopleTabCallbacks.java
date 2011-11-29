package com.iSpraker.android.app;

import java.util.List;

import com.iSpraker.android.dos.User;

public interface IPeopleTabCallbacks {
	List<User> getUsers();
	void onListModeChange(List<User> users);
}
