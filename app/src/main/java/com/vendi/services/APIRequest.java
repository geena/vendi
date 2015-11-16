package com.vendi.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vendi.data.Result;


import android.os.AsyncTask;
import android.os.Handler;


public class APIRequest
{
	private static final String SERVER_URL 	= "https://maps.googleapis.com/maps/api/place/" +
			"nearbysearch/json?location=";
	private static final String SERVER_URL_2 ="&radius=3000&keyword=";
	private static final String SERVER_URL_3 = "&sensor=false&key=AIzaSyCpyh_HQWp07mYovc_PGzgLQlpzdE03B44";
	private String ITEM_NAME = "";
	private String LAT;
	private String LONG;
	private String _jsonString;
	private static final int TIME_OUT = 10000;

	public Closure<Void, APIRequest, APIResponse> _successClosure;
	public Closure<Void, APIRequest, APIResponse> _errorClosure;
	public Closure<Void, APIRequest, APIResponse> _cancelClosure;

	private APIResponse _response;

	public APIRequest(Closure<Void, APIRequest, APIResponse> successClosure,
			Closure<Void, APIRequest, APIResponse> errorClosure,
			Closure<Void, APIRequest, APIResponse> cancelClosure)
	{
		_successClosure = successClosure;
		_errorClosure 	= errorClosure;
		_cancelClosure 	= cancelClosure;
		_response 	= new APIResponse();
	}

	final Runnable resultsRunnable = new Runnable() {
		public void run() {
			if (!(_jsonString == null)){
				parse(_jsonString);
			}
		}
	};

	final Handler resultsHandler = new Handler();

	protected void setAPIResponse(APIResponse response)
	{
		_response = response;
	}

	public void setItemName(String item){
		ITEM_NAME = item;
	}

	/*
	private String getAPIName(){
		return ITEM_NAME + "&zip=" + ZIP;
	}
	 */

	public void setLatitude(String lat){
		LAT = lat;
	}

	public void setLongitude(String longi){
		LONG = longi;
	}

	public String getLocation(){
		return (LAT + "," + LONG);
	}

	public APIResponse getAPIResponse()
	{
		return _response;
	}

	public void parse(String json)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(json);
			JSONArray results = jsonObject.getJSONArray("results");
			
			for (int i = 0; i < results.length(); i++) {
				ObjectMapper mapper = new ObjectMapper();
				
				Result result = mapper.readValue(results.getJSONObject(i).toString(), Result.class);
				
				_response.addResult(result);
			}
			_successClosure.invoke(this, _response);
			_response.clearResults();
		}
		catch (JsonParseException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void doRequest()
	{
		new GoogleAPITask().execute();
	}

	protected String run(int timeout) throws IOException
	{
		HttpURLConnection connection = null;
		URL serverAddress = null;
		BufferedReader resultReader  = null;

		try
		{
			serverAddress  = new URL(SERVER_URL + getLocation() + SERVER_URL_2 + ITEM_NAME + SERVER_URL_3);
			connection = (HttpURLConnection)serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(timeout);
			connection.connect();

			resultReader  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder resultStr = new StringBuilder();
			String line = null;
			while ((line = resultReader.readLine()) != null)
			{
				resultStr.append(line);
			}
			return resultStr.toString();
		} catch (SocketTimeoutException e)
		{
			_response = new APIResponse();
			_response.setException(new Exception("Connection Timed Out"));
			_errorClosure.invoke(this, _response);
			connection.disconnect();
			resultReader = null;
			connection = null;
			return null;
		} catch (MalformedURLException e) 
		{
			_response = new APIResponse();
			_response.setException(e);
			_errorClosure.invoke(this, _response);
			connection.disconnect();
			resultReader = null;
			connection = null;
			return null;
		} catch (ProtocolException e) 
		{
			_response = new APIResponse();
			_response.setException(e);
			_errorClosure.invoke(this, _response);
			connection.disconnect();
			resultReader = null;
			connection = null;
			return null;
		} catch (IOException e) 
		{
			_response = new APIResponse();
			_response.setException(new Exception("No Connection"));
			_errorClosure.invoke(this, _response);
			connection.disconnect();
			resultReader = null;
			connection = null;
			return null;
		}
	}

	class GoogleAPITask extends AsyncTask<Void, Void, Void> {

		protected void onPostExecute(Void v) {
		}

		@Override
		protected Void doInBackground(Void... v) {
			try {
				_jsonString = run(TIME_OUT);
				resultsHandler.post(resultsRunnable);
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}