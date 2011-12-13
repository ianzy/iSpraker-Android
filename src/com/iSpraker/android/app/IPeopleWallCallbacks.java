package com.iSpraker.android.app;

import java.util.List;

import com.iSpraker.android.dos.Paging;
import com.iSpraker.android.dos.User;

public interface IPeopleWallCallbacks {
	List<User> getUsers();
	int getWallIndex();
	double getLat();
	double getLng();
	Paging getPaging();
	void onWallModeChange(List<User> users, double lat, double lng, int wallIndex, Paging pagingInfo);
}
