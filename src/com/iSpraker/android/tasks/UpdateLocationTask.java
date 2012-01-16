package com.iSpraker.android.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.iSpraker.android.R;
import com.iSpraker.android.dao.IUsersDAO;
import com.iSpraker.android.dao.impl.JsonUsersDAO;
import com.iSpraker.android.dos.User;

public class UpdateLocationTask extends AsyncTask<Double, Void, Void> {
	
	private Context context;
	private User user;
	
	public UpdateLocationTask(Context context, User user) {
		this.context = context;
		this.user = user;
	}

	@Override
	protected Void doInBackground(Double... location) {
//		String url = "http://ispraker.heroku.com//api/9b02756d6564a40dfa6436c3001a1441/users.json"; //PeopleTabFragment.this.getResources().getString(R.string.api_users);
    	String url = context.getResources().getString(R.string.api_users);
		IUsersDAO userDAO = new JsonUsersDAO(url, context);
    	userDAO.updateLocation(location[0], location[1], this.user);
    	return null;
	}
	
}
