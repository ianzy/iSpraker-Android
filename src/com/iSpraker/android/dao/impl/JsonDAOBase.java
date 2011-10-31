package com.iSpraker.android.dao.impl;

import com.google.gson.Gson;
import com.iSpraker.android.utils.RestfulClient;

abstract public class JsonDAOBase {
	protected RestfulClient client;
	protected Gson jsonSerDes;
	
	public JsonDAOBase(String url) {
		client = new RestfulClient(url);
		jsonSerDes = new Gson();
		client.AddParam("token", "9b02756d6564a40dfa6436c3001a14410");
	}

}
