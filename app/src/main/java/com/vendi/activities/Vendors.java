package com.vendi.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vendi.R;
import com.vendi.common.RequestCodes;
import com.vendi.data.Result;
import com.vendi.services.APIResponse;

public class Vendors extends Activity{

	ListView _resultsList;
	ArrayList<Result> _results;
	List<Map<String, String>> _resultsMap = new ArrayList<Map<String,String>>();
	SimpleAdapter _resultsAdpt;
	Button _showMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.vendors);

		Bundle bundle = getIntent().getBundleExtra(APIResponse.class.getName());

		_results = APIResponse.Converters.fromBundle(bundle);

		_resultsMap = createMap(_results);

		_resultsList = (ListView) findViewById(R.id.results);

		_showMap = (Button) findViewById(R.id.mapbutton);

		_resultsAdpt = new SimpleAdapter(this, _resultsMap, android.R.layout.simple_list_item_1, new String[] {"result"}, new int[] {android.R.id.text1});

		_resultsList.setAdapter(_resultsAdpt);

		_resultsList.setOnItemClickListener(new resultClickListener());

		_showMap.setOnClickListener(new ShowMapListener());

	}

	private List<Map<String,String>> createMap(ArrayList<Result> names) {

		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		for (Result name: names){
			mapList.add(createResult("result", name.getName()));
		}
		return mapList;
	}

	private HashMap<String, String> createResult(String key, String name) {

		HashMap<String, String> result = new HashMap<String, String>();
		result.put(key, name);
		return result;
	}

	public class resultClickListener implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			TextView clickedView = (TextView) arg1;

			showMap(arg2);

		}
	}

	private class ShowMapListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {

			Intent intent = new Intent(Vendors.this, VendorsMap.class);
			intent.putExtra(Result.class.getName(), Result.Converters.toBundle(_results.get(0)));
			startActivity(intent);

		}

	}

	private void showMap(int resultPos){
		Intent intent = new Intent(Vendors.this, VendorsMap.class);
		intent.putExtra(Result.class.getName(), Result.Converters.toBundle(_results.get(resultPos)));
		startActivity(intent);
	}

}