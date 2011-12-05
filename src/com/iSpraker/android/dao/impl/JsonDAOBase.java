package com.iSpraker.android.dao.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.iSpraker.android.utils.RestfulClient;

abstract public class JsonDAOBase {
	protected RestfulClient client;
	protected Gson jsonSerDes;
	
	public JsonDAOBase(String url, Context context) {
		client = new RestfulClient(url);
		jsonSerDes = new Gson();
		SharedPreferences settings = context.getSharedPreferences("PrefsFile", 0);
		client.AddParam("token", settings.getString("twitter_access_token", null));
	}

}
