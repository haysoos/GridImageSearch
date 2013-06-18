package com.codepath.gridimagesearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageResult implements Serializable {

	private static final long serialVersionUID = -1929190566976941504L;
	private String fullUrl;
	private String thumUrl;
	
	public ImageResult(JSONObject jsonObject){
		
		try {
			setFullUrl(jsonObject.getString("url"));
			setThumUrl(jsonObject.getString("tbUrl"));
			
		} catch (JSONException e) {
			fullUrl = null;
			thumUrl = null;
		}
		
		
	}
	
	public String getFullUrl() {
		return fullUrl;
	}
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}
	public String getThumUrl() {
		return thumUrl;
	}
	public void setThumUrl(String thumUrl) {
		this.thumUrl = thumUrl;
	}
	
	public String toString(){
		return this.thumUrl;
	}

	public static List<ImageResult> fromJSONArray(
			JSONArray imageJsonResults) throws JSONException {
		List<ImageResult> results = new ArrayList<ImageResult>();
		
		for(int i=0; i < imageJsonResults.length(); i++){
			results.add(new ImageResult(imageJsonResults.getJSONObject(i)));
		}
		return results;
	}
	
}
