package com.vendi.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vendi.R;
import com.vendi.R.id;
import com.vendi.R.layout;
import com.vendi.R.menu;
import com.vendi.data.Result;
import com.vendi.data.Result.Converters;
import com.vendi.services.APIRequest;
import com.vendi.services.APIResponse;
import com.vendi.services.Closure;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class MainActivity extends Activity {

	List<String> items = new ArrayList<String>();
	Button search;
	APIRequest request = new APIRequest(new GetDataSuccessClosure(), new GetDataErrorClosure(), new GetDataCancelClosure());
	private final static String TAG = "GPSTest";
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private String latitude;
	private String longitude;
	private Spinner cat1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		cat1 = (Spinner) findViewById(R.id.category1);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.categories, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cat1.setAdapter(adapter);
		
		cat1.setOnItemSelectedListener(new SpinnerActivity());

		search = (Button) findViewById(R.id.find);
		search.setOnClickListener(new FindListener());

		String serviceName = Context.LOCATION_SERVICE;
		mLocationManager = (LocationManager) getSystemService(serviceName);

		mLocationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				Log.e(TAG, "onLocationChanged");
				if (location != null) {
					Log.e(TAG, "Current Latitude = " + location.getLatitude());
					Log.e(TAG, "Current Longitude = " + location.getLongitude());
					latitude = String.valueOf(location.getLatitude());
					longitude = String.valueOf(location.getLongitude());
				}
				mLocationManager.removeUpdates(this);
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				Log.e(TAG, "onProviderDisabled");

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				Log.e(TAG, "onProviderEnabled");
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				Log.e(TAG, "onStatusChanged");
			}
		};

		if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, mLocationListener);
		} else {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, mLocationListener);
		}
	}

	@Override
	protected void onDestroy() {
		mLocationManager.removeUpdates(mLocationListener);
		super.onDestroy();
	}

	private class FindListener implements OnClickListener{
		@Override
		public void onClick(View v){
			
			makeRequest();
		}

	}

	private void makeRequest(){
		
	//	request.setItemName(item);
		request.setLatitude(latitude);
		request.setLongitude(longitude);

		request.doRequest();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class GetDataSuccessClosure implements Closure<Void, APIRequest, APIResponse>
	{
		@Override
		public Class<APIResponse> getResponseType()
		{
			return null;
		}

		@Override
		public Void invoke(APIRequest caller, APIResponse jsondata)
		{
			ArrayList<Result> results = new ArrayList<Result>(jsondata.getResults());
			if (results.isEmpty()){
				System.out.println("no data retrieved");
			} else {
				System.out.println(jsondata.getNames());
				Intent intent = new Intent(MainActivity.this, Vendors.class);
				intent.putExtra(APIResponse.class.getName(), APIResponse.Converters.toBundle(results));
				startActivity(intent);
			}

			return null;
		}
	}

	class GetDataErrorClosure implements Closure<Void, APIRequest, APIResponse>
	{
		@Override
		public Class<APIResponse> getResponseType()
		{
			return null;
		}

		@Override
		public Void invoke(APIRequest caller, APIResponse data)
		{
			Exception exception = data.getException();
			System.out.println("ERROR: " + exception.getMessage());

			return null;
		}
	}

	class GetDataCancelClosure implements Closure<Void, APIRequest, APIResponse>
	{
		@Override
		public Class<APIResponse> getResponseType()
		{
			return null;
		}

		@Override
		public Void invoke(APIRequest caller, APIResponse data)
		{
			return null;
		}
	}
	
	public class SpinnerActivity extends Activity implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	    	
	    	String category = parent.getItemAtPosition(pos).toString();
	    	request.setItemName(category);

	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	    }
	}

}
