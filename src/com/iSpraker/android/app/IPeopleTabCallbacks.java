package com.iSpraker.android.app;

import java.util.List;

import com.iSpraker.android.dos.Paging;
import com.iSpraker.android.dos.User;

public interface IPeopleTabCallbacks {
	List<User> getUsers();
	int getListIndex();
	double getLat();
	double getLng();
	Paging getPaging();
	void onListModeChange(List<User> users, double lat, double lng, int listIndex, Paging pagingInfo);
}
