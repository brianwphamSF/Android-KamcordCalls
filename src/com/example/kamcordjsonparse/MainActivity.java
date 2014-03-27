package com.example.kamcordjsonparse;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.kamcordjsonparse.handler.ServiceHandler;

public class MainActivity extends ListActivity {

	private ProgressDialog pDialog;

	// URL that gets Kamcord's data in JSON format
	private static String url = "http://kamcord.com/api/ingameviewer/feed/?developer_key=665e00d21fb57dfe0714bbfc11df42a2&type=trending";

	// JSON nodes
	private static final String TAG_TITLE = "title";
	private static final String TAG_VIDEO_URL = "video_url";
	private static final String TAG_DESCRIPTION = "description";

	// initialize the JSON Array
	JSONArray kamcordData = null;

	// Hashmap for ListView
	ArrayList<HashMap<String, String>> kamcordList;

	ListView lvKamcordData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		kamcordList = new ArrayList<HashMap<String, String>>();

		lvKamcordData = getListView();
		
		new GetKamcordData().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class GetKamcordData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... params) {// Creating service
														// handler class
														// instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {
					
					// Getting JSON Array node
					kamcordData = new JSONArray(jsonStr);
					
					Log.d("DEBUG", "Length of array: " + kamcordData.length());

					// looping through All Kamcord objects in the array
					for (int i = 0; i < kamcordData.length(); i++) {
						// tmp hashmap for single data
						HashMap<String, String> data = new HashMap<String, String>();
						JSONObject c = kamcordData.getJSONObject(i);

						String title = c.getString(TAG_TITLE);
						String video_url = c.getString(TAG_VIDEO_URL);
						String description = c.getString(TAG_DESCRIPTION);
						
						data.put(TAG_TITLE, "Title: " + title);
						data.put(TAG_VIDEO_URL, "URL: " + video_url);
						data.put(TAG_DESCRIPTION, "Description: " + description);

						kamcordList.add(data);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, kamcordList,
                    R.layout.list_item, new String[] {
                    		TAG_TITLE,
                    		TAG_VIDEO_URL,
                    		TAG_DESCRIPTION }, new int[] {
                    		R.id.tvTitle,
                            R.id.tvUrl,
                            R.id.tvDescription });
 
            setListAdapter(adapter);
        }

	}

}
