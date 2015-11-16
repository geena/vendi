package com.vendi.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import android.os.Bundle;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result{

	private String _name;
	private static Float _latitude;
	private static Float _longitude;
	private Geometry _geometry;
	private static Location _location;

	public Result(){

	}

	public Result(String name, Float lat, Float longi){
		_name = name;
		_latitude = lat;
		_longitude = longi;
	}

	@JsonProperty("name")
	public void addName(String name){
		_name = name;
	}

	@JsonProperty("geometry")
	public void addGeo(Geometry geo){
		_geometry = geo;
	}
	
	public Geometry getGeo(){
		return _geometry;
	}

	public static class Geometry{
		
		@JsonProperty("location")
		public void addLoc(Location loc){
			_location = loc;
		}
		
		public Location getLoc(){
			return _location;
		}
	}
	
	public static class Location{
		
		@JsonProperty("lat")
		public void addLat(String lat){
			_latitude = Float.parseFloat(lat);
		}

		@JsonProperty("lng")
		public void addLong(String longi){
			_longitude = Float.parseFloat(longi);
		}
		
	}

	public String getName(){
		return _name;
	}

	public Float getLat(){
		return _latitude;
	}

	public Float getLong(){
		return _longitude;
	}

	public static class Converters
	{
		private static final String NAME = "name";
		private static final String LONG = "long";
		private static final String LAT = "lat";

		public static Bundle toBundle(Result result)
		{
			if(result == null){
				return null;
			}

			Bundle bundledResult = new Bundle();
			bundledResult.putString	(NAME, result.getName());
			bundledResult.putFloat (LONG, result.getLong());
			bundledResult.putFloat (LAT, result.getLat());
			return bundledResult;
		}

		public static Result fromBundle(Bundle bundle)
		{
			if(bundle == null){
				return null;
			}
			else
			{
				String 	name = bundle.getString(NAME);
				Float lat = bundle.getFloat(LAT);
				Float longi = bundle.getFloat(LONG);

				Result result = new Result(name, lat, longi);

				return result;
			}
		}
	}
}