package com.iSpraker.android.dao.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.reflect.TypeToken;
import com.iSpraker.android.dao.IUsersDAO;
import com.iSpraker.android.dos.Paging;
import com.iSpraker.android.dos.User;
import com.iSpraker.android.dos.UsersResponse;
import com.iSpraker.android.utils.RestfulClient;

public class JsonUsersDAO extends JsonDAOBase implements IUsersDAO {

	public JsonUsersDAO(String url) {
		super(url);
	}

//	public List<User> getUsersByLocation(double lat, double lng) {
//		client.AddParam("latitude", jsonSerDes.toJson(lat));
//		client.AddParam("longitude", jsonSerDes.toJson(lng));
//		
//		try {
//			client.Execute(RestfulClient.RequestMethod.GET);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		//error handling here
//
//		JSONObject responseJSON = null;
//		JSONArray data = null;
//		JSONObject paging = null;
//		try {
//			responseJSON = new JSONObject(client.getResponse());
//			data = responseJSON.getJSONArray("data");
//			paging = responseJSON.getJSONObject("paging");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		
//		Type typeToken = new TypeToken<List<User>>() {} .getType();
//		return jsonSerDes.fromJson(data.toString(), typeToken);
//	}

	@Override
	public UsersResponse getUsersDataByLocation(double lat, double lng) {
		return getUsersDataByLocation(lat, lng, 1);
	}

	@Override
	public UsersResponse getUsersDataByLocation(double lat, double lng, int page) {
		client.AddParam("latitude", jsonSerDes.toJson(lat));
		client.AddParam("longitude", jsonSerDes.toJson(lng));
		client.AddParam("page", String.valueOf(page));
		
		try {
			client.Execute(RestfulClient.RequestMethod.GET);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject responseJSON = null;
		JSONArray data = null;
		JSONObject paging = null;
		try {
			responseJSON = new JSONObject(client.getResponse());
			data = responseJSON.getJSONArray("data");
			paging = responseJSON.getJSONObject("paging");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Type usersTypeToken = new TypeToken<List<User>>() {} .getType();
		Type pagingTypeToken = new TypeToken<Paging>() {} .getType();
		UsersResponse response = new UsersResponse((List<User>)jsonSerDes.fromJson(data.toString(), usersTypeToken),
				(Paging)jsonSerDes.fromJson(paging.toString(), pagingTypeToken));
		
		return response;
	}

}
