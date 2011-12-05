package com.iSpraker.android.dao.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.iSpraker.android.dao.IUsersDAO;
import com.iSpraker.android.dos.Paging;
import com.iSpraker.android.dos.User;
import com.iSpraker.android.dos.UsersResponse;
import com.iSpraker.android.utils.RestfulClient;

public class JsonUsersDAO extends JsonDAOBase implements IUsersDAO {
	
	private String baseUrl;

	public JsonUsersDAO(String url, Context context) {
		super(url, context);
		this.baseUrl = url;
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
		this.client.setUrl(this.baseUrl);
		client.AddParam("latitude", jsonSerDes.toJson(lat));
		client.AddParam("longitude", jsonSerDes.toJson(lng));
		client.AddParam("page", String.valueOf(page));
		
		try {
			client.Execute(RestfulClient.RequestMethod.GET);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (client.getResponseCode() != 200) {
			UsersResponse res = new UsersResponse();
			res.setResponseCode(client.getResponseCode());
			return res;
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
				(Paging)jsonSerDes.fromJson(paging.toString(), pagingTypeToken), client.getResponseCode());
		
		return response;
	}

	@Override
	public UsersResponse getUserByUid(String uid) {
		String userUrl = this.baseUrl.split(".json")[0];
		userUrl = userUrl + "/" + uid + ".json";
		this.client.setUrl(userUrl);
		
		try {
			client.Execute(RestfulClient.RequestMethod.GET);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (client.getResponseCode() != 200) {
			UsersResponse res = new UsersResponse();
			res.setResponseCode(client.getResponseCode());
			return res;
		}
		
		JSONObject responseJSON = null;
		JSONArray data = null;
		try {
			responseJSON = new JSONObject(client.getResponse());
			data = responseJSON.getJSONArray("data");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Type usersTypeToken = new TypeToken<List<User>>() {} .getType();
		UsersResponse response = new UsersResponse((List<User>)jsonSerDes.fromJson(data.toString(), usersTypeToken),
				null, client.getResponseCode());
		
		return response;
	}

	@Override
	public UsersResponse signUpUser(User user) {
		this.client.setUrl(this.baseUrl);
		
		// add user information
		this.client.AddParamForModel("user", "description", user.getDescription());
		this.client.AddParamForModel("user", "email", user.getEmail());
//		this.client.AddParamForModel("user", "phone_number", user.getPhoneNumber());
		this.client.AddParamForModel("user", "profile_image_url", user.getProfileImageURL());
		this.client.AddParamForModel("user", "screen_name", user.getScreenName());
		this.client.AddParamForModel("user", "time_zone", user.getTimeZone());
		this.client.AddParamForModel("user", "token", user.getToken());
//		this.client.AddParamForModel("user", "twitter_id", user.getTwitterId());
		this.client.AddParamForModel("user", "uid", user.getUid());
		
		try {
			client.Execute(RestfulClient.RequestMethod.POST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		UsersResponse response = new UsersResponse();
		response.setResponseCode(this.client.getResponseCode());
		return response;
	}

	@Override
	public UsersResponse updateLocation(double lat, double lng, User user) {
		String userUrl = this.baseUrl.split(".json")[0];
		userUrl = userUrl + "/" + user.getUid() + "/location.json";
		this.client.setUrl(userUrl);
		
		this.client.AddParamForModel("user", "lat", jsonSerDes.toJson(lat));
		this.client.AddParamForModel("user", "lng", jsonSerDes.toJson(lng));
		client.AddParam("_method", "put");
		try {
			client.Execute(RestfulClient.RequestMethod.POST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		UsersResponse response = new UsersResponse();
		response.setResponseCode(this.client.getResponseCode());
		return response;
	}

}
