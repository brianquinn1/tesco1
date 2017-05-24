package com.example.tesco;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tesco.MainActivity.RetrieveFeedTask1;
import com.squareup.picasso.Picasso;
import com.twotoasters.jazzylistview.JazzyEffect;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.SlideInEffect;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

import static com.example.tesco.R.menu.details;

public class Search extends Activity {
	SearchView searchView;
	String searchquery,pic,pricetext,d,tpnb,name;
	Double price=0.0;
	ImageView item_pic;
	JazzyListView mylist;
	ListAdapter adapter;
	ArrayList<HashMap<String, String>> contactList;
	static final String API_KEY ="fade0e50b2894e97aef7f79da0b4f50d";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		searchView=(SearchView)findViewById(R.id.searchView1);
		item_pic =(ImageView) findViewById(R.id.imageView1);
		contactList = new ArrayList<HashMap<String, String>>();
		mylist = (JazzyListView) findViewById(R.id.containerresult);
		searchView.setOnQueryTextListener(new OnQueryTextListener(){

		@Override
		public boolean onQueryTextSubmit(String query) {
			contactList.clear();
			searchquery=searchView.getQuery().toString();
			new RetrieveFeedTask1().execute();

			adapter = new SimpleAdapter(
			Search.this, contactList,
			R.layout.jsonvalues, new String[]{"name", "pricetext"}, new int[]{R.id.data3, R.id.data4});

			mylist.setAdapter(adapter);
			mylist.setTransitionEffect(JazzyHelper.FLY);

			return false;

		}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}

		});

			mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, final long id) {
			HashMap<String, String> obj = (HashMap<String, String>) adapter.getItem(position);
			String pname = (String) obj.get("name");
			String ppricetext = (String) obj.get("pricetext");
			String ppic = (String) obj.get("image");
				String pricey = (String) obj.get("price");
			pic =ppic.replaceAll("http:", "https:");
			tpnb=(String) obj.get("tpnb");

			Intent details = new Intent(Search.this, SearchDetails.class);
			details.putExtra("name", pname);
			details.putExtra("pricetext", ppricetext);
			details.putExtra("image", pic);
			details.putExtra("tpnb", tpnb);
			details.putExtra("pricey", pricey);
			startActivity(details);
				//Toast.makeText(getApplicationContext(),price +pricetext,
						//Toast.LENGTH_SHORT).show();

			}
			});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class RetrieveFeedTask1 extends AsyncTask<Void, Void, String> {

		private Exception exception;
		protected void onPreExecute() {

		}

		protected String doInBackground(Void... urls) {
			// Do some validation here
			try {
				d =searchquery.replaceAll(" ", "%20");
				URL url1 = new URL("https://dev.tescolabs.com/grocery/products/?query="+d+"&offset=0&limit=10&KEY="+API_KEY);
				HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
				try {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
					StringBuilder stringBuilder = new StringBuilder();
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line).append("\n");
					}
					bufferedReader.close();
					return stringBuilder.toString();
				}
				finally{
					urlConnection.disconnect();
				}
			}
			catch(Exception e) {
				Log.e("ERROR", e.getMessage(), e);
				return null;
			}
		}

		protected void onPostExecute(String response) {
			if(response == null) {
				response = "THERE WAS AN ERROR";
			}
			;

			Log.i("INFO", response);

			JSONObject jObject;

			try {
				jObject = new JSONObject(response);
				//JSONArray mResponse = jObject.getJSONArray("results");
				JSONObject mResponse = jObject.getJSONObject("uk");
				JSONObject mResponse1 = mResponse.getJSONObject("ghs");
				JSONObject mResponse2 = mResponse1.getJSONObject("products");
				JSONArray values = mResponse2.getJSONArray("results");

				for (int i = 0; i < values.length(); i++) {
					JSONObject j = values.getJSONObject(i);

					pic = "https://afterlifeconnect.com/images/NoImageAvailable.png";
					name = j.getString("name");
					price = j.getDouble("price");
					pic = j.getString("image");
					tpnb = j.getString("tpnb");

					NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
					pricetext = formatter.format(price);

					HashMap<String, String> contact = new HashMap<String, String>();

					contactList.add(contact);
					contact.put("name", name);
					contact.put("pricetext", pricetext);
					contact.put("price", ""+price);
					contact.put("image", pic);
					contact.put("tpnb", tpnb);
					mylist.invalidateViews();
					//Toast.makeText(getApplicationContext(),price +pricetext+ " record exists",
							//Toast.LENGTH_SHORT).show();
				}

			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
