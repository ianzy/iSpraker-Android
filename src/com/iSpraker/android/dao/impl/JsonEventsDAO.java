package com.iSpraker.android.dao.impl;

import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.iSpraker.android.dao.IEventsDAO;
import com.iSpraker.android.dos.Event;
import com.iSpraker.android.utils.RestfulClient;

public class JsonEventsDAO extends JsonDAOBase implements IEventsDAO {
	
	public JsonEventsDAO(String url, Context context) {
		super(url, context);
	}

	public List<Event> getEventsByLocation(double lat, double lng) {
		client.AddParam("lat", jsonSerDes.toJson(lat));
		client.AddParam("long", jsonSerDes.toJson(lng));
		
		try {
			client.Execute(RestfulClient.RequestMethod.GET);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//error handling here
		
		Type typeToken = new TypeToken<List<Event>>() {} .getType();
		return jsonSerDes.fromJson(client.getResponse(), typeToken);
	}

}
