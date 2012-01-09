package com.iSpraker.android.dao;

import com.iSpraker.android.dos.HashTagsResponse;

public interface IHashTagsDAO {
	public HashTagsResponse getHashTagsByLocation(double lat, double lng);
	public HashTagsResponse getHashTagsByLocation(double lat, double lng, int page);
}
