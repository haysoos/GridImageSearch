package com.codepath.gridimagesearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity {

	Button btnSearch;
	EditText etQuery;
	GridView gvResults;
	private List<ImageResult> imageResults = new ArrayList<ImageResult>();
	private ImageResultArrayAdapter imageAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
	}

	private void setupViews() {
		btnSearch = (Button) this.findViewById(R.id.btnSearch);
		etQuery = (EditText) this.findViewById(R.id.etQuery);
		gvResults = (GridView) this.findViewById(R.id.gvResults);
		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gvResults.setAdapter(imageAdapter);
		gvResults.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position,
					long rowId) {
				Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				i.putExtra("url", imageResult.getFullUrl());
				startActivity(i);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	public void onImageSearch(View view){
		String query = etQuery.getText().toString();
		//String queryRequest = "http://www.flickr.com/services/rest/?method=flickr.test.echo&format=json&api_key=f3554c0bd142560b90bb9ca268650738";
		Toast.makeText(this, "Searching for " + query, Toast.LENGTH_SHORT).show();
		
		String queryRequest = "https://ajax.googleapis.com/ajax/services/search/images?q=" + Uri.encode(query) + "&rsz=8&v=1.0&start=0";
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(queryRequest, new JsonHttpResponseHandler(){
			
			public void onSuccess(JSONObject response){
				JSONArray imageJsonResults = null;
				try {
					imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
					imageResults.clear();
					imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
					Log.d("DEBUG", imageResults.toString());
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	private void loadFlickrResults(String query){
		//http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
		/*
		 * Size Suffixes

			The letter suffixes are as follows:
			s	small square 75x75
			q	large square 150x150
			t	thumbnail, 100 on longest side
			m	small, 240 on longest side
			n	small, 320 on longest side
			-	medium, 500 on longest side
			z	medium 640, 640 on longest side
			c	medium 800, 800 on longest side 
			b	large, 1024 on longest side*
			o	original image, either a jpg, gif or png, depending on source format
		 */
		//http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=f9c560348dd42f10a7f4684948e2cd33&text=android&per_page=8&format=json&nojsoncallback=1
	}
	
	
}
