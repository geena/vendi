package com.vendi.services;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import android.os.Bundle;
import android.os.Parcelable;

import com.vendi.data.Result;

@JsonIgnoreProperties(ignoreUnknown = true)
public class APIResponse
{
	private static List<Result> _results = new ArrayList<Result>();
	private List<String> _names = new ArrayList<String>();
	private Exception _exception;
	
	@JsonProperty("results")
	public void addResult(Result result)
	{
		_results.add(result);
		_names.add(result.getName());
	}
	
	public List<String> getNames(){
		
		return _names;
	}
	
	public List<Result> getResults()
	{
		return _results;
	}
	
	public void clearResults()
	{
		_results = new ArrayList<Result>();
	}
	
	public void setException(Exception e)
	{
		_exception = e;
	}
	
	public Exception getException()
	{
		return _exception;
	}
	
	private static List<Bundle> bundledResults(){
		
		List<Bundle> bundles = new ArrayList<Bundle>();
		for(Result r : _results){
			Bundle b = Result.Converters.toBundle(r);
			bundles.add(b);
		}
		return bundles;
	}
	
	public static class Converters
	{
		private static final String RESULTS = "results";

		public static Bundle toBundle(List<Result> results)
		{
			if(results == null){
				return null;
			}

			Bundle bundledResults = new Bundle(); 
					
			bundledResults.putParcelableArrayList(RESULTS, (ArrayList<? extends Parcelable>) bundledResults());
			
			return bundledResults;
		}

		public static ArrayList<Result> fromBundle(Bundle bundle)
		{
			ArrayList<Result> results = new ArrayList<Result>();
			if(bundle == null){
				return null;
			}
			else
			{
				ArrayList<? extends Parcelable> bundles = bundle.getParcelableArrayList(RESULTS);
				
				for (Parcelable b : bundles){
					Result r = Result.Converters.fromBundle((Bundle) b);
					results.add(r);
				}

				return results;
			}
		}
	}
}