package com.example.tesco;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tesco.MainActivity.RetrieveFeedTask;
import com.example.tesco.MainActivity.RetrieveFeedTask1;
import com.squareup.picasso.Picasso;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.description;
import static android.R.attr.format;
import static com.example.tesco.R.id.data3;

public class SearchDetails extends Activity implements OnClickListener {
	String description,name,pricetext,pic,tpnb,barcode,valuePer100,valuePerServing,perServingHeader,per100Header,json,allergenName,allergenValues,pricey;
	Cursor cursor;
	Context context;
	double price;
	JazzyListView mylist;
	JazzyListView mylist1;
	private ProgressDialog pDialog;
	TextView free,allergen,descriptionView,priceView;
	DBAdapter db;
	int selected=0;
	int quantity=0;
    double totalprice=0.0;
	Button add;
	ArrayList<HashMap<String, String>> contactList;
	ArrayList<HashMap<String, String>>  allergenList;
	static final String API_KEY ="fade0e50b2894e97aef7f79da0b4f50d";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_details);
		mylist = (JazzyListView) findViewById(R.id.containerdetails);
		mylist1 = (JazzyListView) findViewById(R.id.containerallergens);
		descriptionView = (TextView) findViewById(R.id.desresult);
		priceView = (TextView) findViewById(R.id.priceresult);
		add = (Button)findViewById(R.id.add_button_list);
		add.setOnClickListener(this);
		contactList = new ArrayList<HashMap<String, String>>();
		allergenList = new ArrayList<HashMap<String, String>>();
		db = new DBAdapter(this);

		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			description = bundle.getString("name");
			tpnb = bundle.getString("tpnb");
			pricetext = bundle.getString("pricetext");
			pricey = bundle.getString("pricey");
			String Product="Product: ";
			String Price="Price: ";
			String sourceStringpro = "<b>" + Product + "</b> " + description;
			String sourceStringpri = "<b>" + Price + "</b> " + pricetext;
			//descriptionView.setText(Html.fromHtml(sourceStringpro));
            price= Double.parseDouble(pricey);
			descriptionView.setText(description);
			priceView.setText(Html.fromHtml(sourceStringpri));

			pic = bundle.getString("image");
			ImageView item_pic =(ImageView) findViewById(R.id.imagedetails);
			Picasso.with(getApplicationContext()).load(pic).resize(220, 220).into(item_pic);

			new RetrieveFeedTask().execute();

			URL url = null;

			try {
				url = new URL("https://dev.tescolabs.com/product/?tpnb="+tpnb+"&KEY="+API_KEY);

			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_details, menu);
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

	class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

		private Exception exception;

		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(SearchDetails.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}
		protected String doInBackground(Void... urls) {
			// Do some validation here
			try {
				URL url = new URL("https://dev.tescolabs.com/product/?tpnb="+tpnb+"&KEY="+API_KEY);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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
			super.onPostExecute(response);
			if(response == null) {
				response = "THERE WAS AN ERROR";
			}
			if (pDialog.isShowing())
			pDialog.dismiss();

			Log.i("INFO", response);
			JSONObject jObject;
			JSONObject j = null;
			JSONArray calcNutrients = null;
			try {
				json = response;
				jObject = new JSONObject(response);
				JSONArray mResponse = jObject.getJSONArray("products");

				for (int i = 0; i < mResponse.length(); i++) {
					j = mResponse.getJSONObject(i);
					barcode = j.getString("gtin");
					if(j.has("calcNutrition")) {
						contactList.clear();
						JSONObject calcNutrition = j.getJSONObject("calcNutrition");
						per100Header = calcNutrition.getString("per100Header")+":";

						if (calcNutrition.has("perServingHeader")) {
							perServingHeader = calcNutrition.getString("perServingHeader")+":";
						} else {
							perServingHeader = " ";
						}

						calcNutrients = calcNutrition.getJSONArray("calcNutrients");
						String test = calcNutrients.toString();
						JSONArray obj = new JSONArray(test);
						for (int k = 0; k <= obj.length(); k++) {
							JSONObject l = obj.getJSONObject(k);

							if (l.has("name")) {
								name = l.getString("name");
							} else {
								name = "No Data to display";
							}

							if (l.has("valuePer100")) {
								valuePer100 = l.getString("valuePer100");
								per100Header = calcNutrition.getString("per100Header")+ ": ";
							} else {
								valuePer100 = " ";
								per100Header = " ";
							}


							if (l.has("valuePerServing")) {
								valuePerServing = l.getString("valuePerServing");
								perServingHeader= calcNutrition.getString("perServingHeader")+ ": ";
							} else {
								valuePerServing = " ";
								perServingHeader = " ";
							}
							HashMap<String, String> contact = new HashMap<String, String>();

							contactList.add(contact);
							contact.put("name", name);
							contact.put("valuePer100", valuePer100);
							contact.put("valuePerServing", valuePerServing);
							contact.put("per100Header", per100Header);
							contact.put("perServingHeader", perServingHeader);
							ListAdapter adapter = new SimpleAdapter(
									SearchDetails.this, contactList,
									R.layout.jsonvalues, new String[]{"name", "valuePer100",
									"valuePerServing", "per100Header", "perServingHeader"}, new int[]{data3,
									R.id.data4, R.id.data5, R.id.data1, R.id.data2});

							mylist.setAdapter(adapter);
							mylist.setTransitionEffect(JazzyHelper.FLY);
						}
					}
					else {

							per100Header = "";
							perServingHeader = " ";
							name = "No Data to display";
							valuePer100 = " ";
							valuePerServing = " ";
							HashMap<String, String> contact = new HashMap<String, String>();

							contactList.add(contact);
							contact.put("name", name);
							contact.put("valuePer100", valuePer100);
							contact.put("valuePerServing", valuePerServing);
							contact.put("per100Header", per100Header);
							contact.put("perServingHeader", perServingHeader);

							ListAdapter adapter = new SimpleAdapter(
									SearchDetails.this, contactList,
									R.layout.jsonvalues, new String[]{"name", "valuePer100",
									"valuePerServing", "per100Header", "perServingHeader"}, new int[]{data3,
									R.id.data4, R.id.data5, R.id.data1, R.id.data2});

							mylist.setAdapter(adapter);
							mylist.setTransitionEffect(JazzyHelper.FLY);

					}
				}

			} catch (JSONException e) {
			e.printStackTrace();
			}

			try {
				json = response;
				jObject = new JSONObject(response);
				JSONArray mResponse = jObject.getJSONArray("products");

				for (int i = 0; i < mResponse.length(); i++) {
					j = mResponse.getJSONObject(i);
					if (j.has("allergenAdvice")) {
						allergenList.clear();
						JSONObject allergenAdvice = j.getJSONObject("allergenAdvice");
						JSONArray allergens = allergenAdvice.getJSONArray("allergens");

						for (int q = 0; q <= allergens.length(); q++) {
							JSONObject p = allergens.getJSONObject(q);
							allergenName = p.getString("allergenName");
							String allergenValuestest = p.getString("allergenValues");
							allergenValues = allergenValuestest.replace("[", "").replace("]", "");
							String strOutput = allergenValues.replace("[", "").replace("]", "");
							HashMap<String, String> listAllergen = new HashMap<String, String>();

							allergenList.add(listAllergen);
							listAllergen.put("free", allergenName);
							listAllergen.put("allergen", strOutput);


							ListAdapter adapter = new SimpleAdapter(
									SearchDetails.this, allergenList,
									R.layout.jsonvalues, new String[]{"free", "allergen",}, new int[]{data3,
									R.id.data4});

							mylist1.setAdapter(adapter);
							mylist1.setTransitionEffect(JazzyHelper.FLY);
						}
					}
					else {
						String allergenName = "Alergens";
						String strOutput = "None";
						HashMap<String, String> listAllergen = new HashMap<String, String>();

						allergenList.add(listAllergen);
						listAllergen.put("free", allergenName);
						listAllergen.put("allergen", strOutput);


						ListAdapter adapter = new SimpleAdapter(
								SearchDetails.this, allergenList,
								R.layout.jsonvalues, new String[]{"free", "allergen",}, new int[]{data3,
								R.id.data4});

						mylist1.setAdapter(adapter);
						mylist1.setTransitionEffect(JazzyHelper.FLY);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void populateDB(){

		cursor = db.getBarcode();
		String  des =null;
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			int position=cursor.getPosition();

			do{
				des =cursor.getString(position);
				if (barcode.equals(des)) {
					// record exist
					Toast.makeText(getApplicationContext(),price +pricetext+ description + " record exists",
					Toast.LENGTH_SHORT).show();
					cursor.moveToLast();
				}
				else {
					if(cursor.isLast()==true){
						long id = db.insertContact(barcode, description, selected, quantity, price, pic, json, pricetext,totalprice);
						Toast.makeText(getApplicationContext(), price +pricetext+ description +" Added to list",
						Toast.LENGTH_SHORT).show();
						cursor.close();
					}
					else{
					}
				}

			}while(cursor.moveToNext());

		}
		else{
			long id = db.insertContact(barcode, description, selected, quantity, price, pic, json, pricetext,totalprice);
			Toast.makeText(getApplicationContext(), price +pricetext+description + " Added to list last",
			Toast.LENGTH_SHORT).show();
			cursor.close();
		}
		cursor.close();
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.add_button_list){
			if(barcode != null){
			db.open();
			populateDB();
			db.close();
			}
		}
	}
}
