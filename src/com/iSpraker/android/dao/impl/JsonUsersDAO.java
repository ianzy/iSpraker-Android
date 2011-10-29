package com.iSpraker.android.dao.impl;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.iSpraker.android.dao.IUsersDAO;
import com.iSpraker.android.dos.User;
import com.iSpraker.android.utils.RestfulClient;

public class JsonUsersDAO extends JsonDAOBase implements IUsersDAO {

	public JsonUsersDAO(String url) {
		super(url);
	}

	public List<User> getUsersByLocation(double lat, double lng) {
		client.AddParam("lat", jsonSerDes.toJson(lat));
		client.AddParam("long", jsonSerDes.toJson(lng));
		
		try {
			client.Execute(RestfulClient.RequestMethod.GET);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//error handling here
		
		Type typeToken = new TypeToken<List<User>>() {} .getType();
		return jsonSerDes.fromJson(client.getResponse(), typeToken);
	}

}
