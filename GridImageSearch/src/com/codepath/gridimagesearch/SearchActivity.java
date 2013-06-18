package com.codepath.gridimagesearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity {

	private static final int NUMBER_OF_IMAGES_PER_REQUEST = 8;
	Button btnSearch;
	EditText etQuery;
	GridView gvResults;
	EditText etSiteFilter;
	Spinner spnImageSize;
	Spinner spnColorFilter;
	Spinner spnImageType;
	Button btnMore;
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	private int startIndex = 0;
	private List<ImageResult> imageResults = new ArrayList<ImageResult>();
	private ImageResultArrayAdapter imageAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getPreferences(0);
		editor = settings.edit();
		setContentView(R.layout.activity_search);
		setupViews();
	}

	private void setupViews() {
		btnSearch = (Button) this.findViewById(R.id.btnSearch);
		etQuery = (EditText) this.findViewById(R.id.etQuery);
		gvResults = (GridView) this.findViewById(R.id.gvResults);
		etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);
		spnImageSize = (Spinner) findViewById(R.id.spnImageSize);
		spnColorFilter = (Spinner) findViewById(R.id.spnColorFilter);
		spnImageType = (Spinner) findViewById(R.id.spnImageType);
		btnMore = (Button) findViewById(R.id.btMore);
		btnMore.setVisibility(View.INVISIBLE);
		
		etQuery.setText(settings.getString("etQuery", ""));
		etSiteFilter.setText(settings.getString("etSiteFilter", ""));
		spnImageSize.setSelection(settings.getInt("spnImageSize", 0));
		spnColorFilter.setSelection(settings.getInt("spnColorFilter", 0));
		spnImageType.setSelection(settings.getInt("spnImageType", 0));
		
		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gvResults.setAdapter(imageAdapter);
		gvResults.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position,
					long rowId) {
				Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				i.putExtra("image_result", imageResult);
				startActivity(i);
			}
			
		});
		
		spnImageSize.setOnItemSelectedListener(new SpinnerOnItemClickListener("spnImageType"));
		spnImageType.setOnItemSelectedListener(new SpinnerOnItemClickListener("spnImageType"));
		spnColorFilter.setOnItemSelectedListener(new SpinnerOnItemClickListener("spnColorFilter"));
		etQuery.addTextChangedListener(new EditTextChangedListener("etQuery"));
		etSiteFilter.addTextChangedListener(new EditTextChangedListener("etSiteFilter"));
		
	}
	
	/**
	 * Needed to reset the start index if a user changes a setting after
	 * a query had already been requested.
	 */
	class SpinnerOnItemClickListener implements OnItemSelectedListener {

		private String spinnerName;
		
		public SpinnerOnItemClickListener(String spinnerName) {
			this.spinnerName = spinnerName;
		}

		@Override
		public void onItemSelected(AdapterView<?> adapter, View parent, int position,
				long rowId) {
			startIndex = 0;
			btnMore.setVisibility(View.INVISIBLE);
			editor.putInt(spinnerName, position);
			editor.commit();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
		
	}
	
	class EditTextChangedListener implements TextWatcher {

		private String name;
		
		public EditTextChangedListener(String name) {
			this.name = name;
		}

		@Override
		public void afterTextChanged(Editable s) {
			startIndex = 0;
			btnMore.setVisibility(View.INVISIBLE);
			editor.putString(name, s.toString());
			editor.commit();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	public void moreButtonClicked(View v){
		queryThumbnailImages();
	}
	
	public void onImageSearch(View view){
		btnMore.setVisibility(View.VISIBLE);
		startIndex = 0;
		queryThumbnailImages();
		
	}

	private void queryThumbnailImages() {
		String query = etQuery.getText().toString();
		//String queryRequest = "http://www.flickr.com/services/rest/?method=flickr.test.echo&format=json&api_key=f3554c0bd142560b90bb9ca268650738";
		Toast.makeText(this, "Searching for " + query, Toast.LENGTH_SHORT).show();
		
		String siteFilter = etSiteFilter.getText().toString();
		String imageColor = spnColorFilter.getSelectedItem().toString();
		String imageType = spnImageType.getSelectedItem().toString();
		String imageSize = spnImageSize.getSelectedItem().toString();
		
		String queryRequest = "https://ajax.googleapis.com/ajax/services/search/images?q=" + Uri.encode(query) + 
				"&rsz=" + NUMBER_OF_IMAGES_PER_REQUEST +"&v=1.0&start=" + startIndex + "&as_sitesearch=" + Uri.encode(siteFilter) + "&imgcolor=" + Uri.encode(imageColor) +
				"&imgsz=" + Uri.encode(imageSize) + "&imgtype=" + Uri.encode(imageType);
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
		
		startIndex += NUMBER_OF_IMAGES_PER_REQUEST;
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
			c	medium 800, 800 on longest side�
			b	large, 1024 on longest side*
			o	original image, either a jpg, gif or png, depending on source format
		 */
		//http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=f9c560348dd42f10a7f4684948e2cd33&text=android&per_page=8&format=json&nojsoncallback=1
	}
	
	
}
