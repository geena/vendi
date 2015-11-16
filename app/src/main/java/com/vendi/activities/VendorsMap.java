package com.vendi.activities;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vendi.common.RequestCodes;
import com.vendi.data.Result;
import com.vendi.services.APIResponse;
import com.vendi.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class VendorsMap extends Activity {

	private GoogleMap mMap;
	private Result _result;
	private LatLng _latlng;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vendorsmap);

		if (mMap == null){
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		}
		
		Bundle bundle = getIntent().getBundleExtra(Result.class.getName());
		
		if (bundle != null)
		{
			_result = Result.Converters.fromBundle(bundle);
			_latlng = new LatLng(_result.getLat(), _result.getLong());
			addMarker();
			CameraPosition cameraPosition = new CameraPosition.Builder().target(_latlng).zoom(14.0f).build();
			CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
			mMap.moveCamera(cameraUpdate);
		}

	}
	
	private void addMarker() {
		
		mMap.addMarker(new MarkerOptions()
        .position(_latlng)
        .title(_result.getName()));
	}

}