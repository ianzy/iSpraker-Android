package com.iSpraker.android.dao.impl;

import com.google.gson.Gson;
import com.iSpraker.android.utils.RestfulClient;

abstract public class JsonDAOBase {
	protected RestfulClient client;
	protected Gson jsonSerDes;
	
	public JsonDAOBase(String url) {
		client = new RestfulClient(url);
	}

}
