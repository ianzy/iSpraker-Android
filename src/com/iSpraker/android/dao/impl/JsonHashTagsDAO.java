package com.iSpraker.android.dao.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.iSpraker.android.dao.IHashTagsDAO;
import com.iSpraker.android.dos.HashTag;
import com.iSpraker.android.dos.HashTagsResponse;
import com.iSpraker.android.dos.Paging;
import com.iSpraker.android.utils.RestfulClient;

public class JsonHashTagsDAO extends JsonDAOBase implements IHashTagsDAO {

	public JsonHashTagsDAO(String url, Context context) {
		super(url, context);
	}

	@Override
	public HashTagsResponse getHashTagsByLocation(double lat, double lng) {
		return getHashTagsByLocation(lat, lng, 1);
	}

	@Override
	public HashTagsResponse getHashTagsByLocation(double lat, double lng,
			int page) {
		client.AddParam("latitude", jsonSerDes.toJson(lat));
		client.AddParam("longitude", jsonSerDes.toJson(lng));
		client.AddParam("page", String.valueOf(page));
		
		try {
			client.Execute(RestfulClient.RequestMethod.GET);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (client.getResponseCode() != 200) {
			HashTagsResponse res = new HashTagsResponse();
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

		Type hashTagsTypeToken = new TypeToken<List<HashTag>>() {} .getType();
		Type pagingTypeToken = new TypeToken<Paging>() {} .getType();
		HashTagsResponse response = new HashTagsResponse((List<HashTag>)jsonSerDes.fromJson(data.toString(), hashTagsTypeToken),
				(Paging)jsonSerDes.fromJson(paging.toString(), pagingTypeToken), client.getResponseCode());
		
		return response;
	}

}
