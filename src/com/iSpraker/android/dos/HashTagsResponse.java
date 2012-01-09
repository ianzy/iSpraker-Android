package com.iSpraker.android.dos;

import java.util.List;

public class HashTagsResponse {
	private List<HashTag> hashTags;
	private Paging paging;
	private int responseCode;
	
	public HashTagsResponse(List<HashTag> hashTags, Paging paging, int responseCode) {
		this.hashTags = hashTags;
		this.paging = paging;
		this.responseCode = responseCode;
	}
	
	public HashTagsResponse() {
		
	}
	public List<HashTag> getHashTags() {
		return hashTags;
	}
	public void setHashTags(List<HashTag> hashTags) {
		this.hashTags = hashTags;
	}
	public Paging getPaging() {
		return paging;
	}
	public void setPaging(Paging paging) {
		this.paging = paging;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
}
