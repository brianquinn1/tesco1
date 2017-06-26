package com.example.tesco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import static android.R.attr.fragment;


public class MainActivity extends Activity implements OnClickListener {
	/**
	 * Called when the activity is first created.
	 */
	private Button scanBtn;
	TextView formatTxt, contentTxt, responseView, free, allergen, data, data1, data2, data3, data4, data5;
	String description, pricetext, pic, json, name, valuePer100, valuePerServing, per100Header, perServingHeader, pricesource, descriptionsource, image, tpnb, allergenName, allergenValues;
	String scanContent;
	String scanFormat;
	String nf = "Product not Found";
	JazzyListView mylist;
	JazzyListView mylist1;
	ImageView item_pic;
	private ProgressDialog pDialog;
	DBAdapter db;
	URL url1;
	Cursor cursor;
	Double price;
	int selected = 0;
	int quantity = 0;
	double totalprice = 0.0;
	Animation FabOpen, FabClose, FabClockWise, FabAntiClockWise;
	FloatingActionButton floatButton, floatButton1, floatButton2, floatButton3;
	ArrayList<HashMap<String, String>> contactList;
	ArrayList<HashMap<String, String>> allergenList;
	boolean isOpen = false;
	static final String API_KEY = "fade0e50b2894e97aef7f79da0b4f50d";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, Test.class);
			this.startActivity(intent);
			return true;
		}

		if (id == R.id.action_search) {
			Intent myIntent = new Intent(this,
					Search.class);
			startActivity(myIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		scanBtn = (Button) findViewById(R.id.scan_button);
		formatTxt = (TextView) findViewById(R.id.scan_format);
		contentTxt = (TextView) findViewById(R.id.scan_content);
		responseView = (TextView) findViewById(R.id.responseView);
		scanBtn.setOnClickListener(this);
		data = (TextView) findViewById(R.id.data);
		data1 = (TextView) findViewById(R.id.data1);
		data2 = (TextView) findViewById(R.id.data2);
		data3 = (TextView) findViewById(R.id.data3);
		data4 = (TextView) findViewById(R.id.data4);
		data5 = (TextView) findViewById(R.id.data5);
		item_pic = (ImageView) findViewById(R.id.imageView1);

		contactList = new ArrayList<HashMap<String, String>>();
		allergenList = new ArrayList<HashMap<String, String>>();
		mylist = (JazzyListView) findViewById(R.id.containervalue);
		mylist1 = (JazzyListView) findViewById(R.id.containerallergens);
		FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
		FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
		FabClockWise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
		FabAntiClockWise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
		floatButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
		floatButton1 = (FloatingActionButton) findViewById(R.id.floatingActionButton1);
		floatButton2 = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
		floatButton3 = (FloatingActionButton) findViewById(R.id.floatingActionButton3);

		Bundle bundle = getIntent().getExtras();
		scanContent = bundle.getString("scanContent");
		scanFormat = bundle.getString("scanFormat");

		if (scanContent != null) {
			scan();
		} else {
			Toast.makeText(getApplicationContext(), " Fucked",
					Toast.LENGTH_SHORT).show();
		}

		floatButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (isOpen) {
					floatButton1.startAnimation(FabClose);
					floatButton2.startAnimation(FabClose);
					floatButton3.startAnimation(FabClose);
					floatButton.startAnimation(FabAntiClockWise);
					floatButton1.setClickable(false);
					floatButton2.setClickable(false);
					floatButton3.setClickable(false);
					isOpen = false;

				} else {
					floatButton1.startAnimation(FabOpen);
					floatButton2.startAnimation(FabOpen);
					floatButton3.startAnimation(FabOpen);
					floatButton.startAnimation(FabClockWise);
					floatButton1.setClickable(true);
					floatButton2.setClickable(true);
					floatButton3.setClickable(true);
					isOpen = true;
				}
			}
		});

		floatButton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), Delete.class);
				v.getContext().startActivity(intent);
			}
		});

		floatButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), Test.class);
				v.getContext().startActivity(intent);
			}
		});

		floatButton3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), List.class);
				v.getContext().startActivity(myIntent);
			}
		});

		db = new DBAdapter(this);
		try {
			String destPath = "/data/data/" + getPackageName() +
					"/databases";
			File f = new File(destPath);
			if (!f.exists()) {
				f.mkdirs();
				f.createNewFile();
				// Copy the db from the assets folder into the databases folder
				CopyDB(getBaseContext().getAssets().open("SuperDataBasetest"),
						new FileOutputStream(destPath + "/SuperDataBasetest"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		db.open();
		Cursor c = db.getAllRows();
		if (c.moveToFirst()) {
			do {

				DisplayContactInDatabase(c);
			} while (c.moveToNext());
		}
		db.close();
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("description", description);
		outState.putString("scanContent", scanContent);
		outState.putString("descriptionsource", descriptionsource);
		outState.putString("pricesource", pricesource);
		outState.putString("pic", pic);
		outState.putString("name", name);
		outState.putString("valuePer100", valuePer100);
		outState.putString("valuePerServing", valuePerServing);
		outState.putString("per100Header", per100Header);
		outState.putString("perServingHeader", perServingHeader);
		outState.putString("allergenName", allergenName);
		outState.putString("allergenValues", allergenValues);
		outState.putSerializable("contactList", contactList);
		outState.putSerializable("allergenList", allergenList);


	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		description = savedInstanceState.getString("description");
		scanContent = savedInstanceState.getString("scanContent");
		descriptionsource = savedInstanceState.getString("descriptionsource");
		pricesource = savedInstanceState.getString("pricesource");
		pic = savedInstanceState.getString("pic");
		name = savedInstanceState.getString("name");
		valuePer100 = savedInstanceState.getString("valuePer100");
		valuePerServing = savedInstanceState.getString("valuePerServing");
		per100Header = savedInstanceState.getString("per100Header");
		perServingHeader = savedInstanceState.getString("perServingHeader");
		allergenName = savedInstanceState.getString("allergenName");
		allergenValues = savedInstanceState.getString("allergenValues");
		//String strOutput = allergenValues.replace("[", "").replace("]", "");
		String dtest = descriptionsource;
		String ptest = pricesource;
		if (descriptionsource != null) {
			data.setText(Html.fromHtml(dtest));
		}

		if (pricesource != null) {
			responseView.setText(Html.fromHtml(ptest));
		}
		if (description != null) {
			contactList = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("contactList");
			Picasso.with(getApplicationContext()).load(pic).resize(250, 250).into(item_pic);

			ListAdapter adapter = new SimpleAdapter(
					MainActivity.this, contactList,
					R.layout.jsonvalues, new String[]{"name", "valuePer100",
					"valuePerServing", "per100Header", "perServingHeader"}, new int[]{R.id.data3,
					R.id.data4, R.id.data5, R.id.data1, R.id.data2});

			mylist.setAdapter(adapter);
			mylist.setTransitionEffect(JazzyHelper.FLY);
		}
		if (description != null) {
			allergenList = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("allergenList");

			ListAdapter adapter1 = new SimpleAdapter(
					MainActivity.this, allergenList,
					R.layout.jsonvalues, new String[]{"free", "allergen",}, new int[]{R.id.data3,
					R.id.data4});

			mylist1.setAdapter(adapter1);
			mylist1.setTransitionEffect(JazzyHelper.FLY);
		}
	}

	public void onClick(View v) {
		//respond to clicks
		if (v.getId() == R.id.scan_button) {
			contactList.clear();
			contentTxt.setText(null);
			formatTxt.setText(null);
			responseView.setText(null);
			data.setText(null);
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
			item_pic.setImageResource(R.drawable.no);

		}
	}

	public void onClickAdd(View v) {
		if (v.getId() == R.id.add_button) {
			if (scanContent != null) {

				db.open();
				populateDB();
				db.close();
			} else {
				Toast.makeText(getApplicationContext(), " Click else",
						Toast.LENGTH_SHORT).show();
			}
		}
	}


	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			//we have a result
			scanContent = scanningResult.getContents();
			scanFormat = scanningResult.getFormatName();
			String format = "FORMAT: ";
			String content = "CONTENT: ";
			String sourceStringf = "<b>" + format + "</b> " + scanFormat;
			formatTxt.setText(Html.fromHtml(sourceStringf));
			String sourceStringc = "<b>" + content + "</b> " + scanContent;
			contentTxt.setText(Html.fromHtml(sourceStringc));
			scan();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private void scan() {
		if (scanContent != null) {
			new gettpnb().execute();
			URL url = null;
			try {
				url = new URL("https://dev.tescolabs.com/product/?gtin=" + scanContent + "&KEY=" + API_KEY);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	private void populateDB() {
		cursor = db.getBarcode();
		String des = null;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int position = cursor.getPosition();
			do {
				des = cursor.getString(position);
				if (data.getText().toString().contains(nf) || pic == "https://afterlifeconnect.com/images/NoImageAvailable.png") {
					Toast.makeText(getApplicationContext(), " Cannot add to List as no data found",
							Toast.LENGTH_SHORT).show();
				} else {
					if (scanContent.equals(des)) {
						// record exist
						Toast.makeText(getApplicationContext(), description + " record exists",
								Toast.LENGTH_SHORT).show();
						cursor.moveToLast();
					} else {
						if (cursor.isLast() == true) {
							long id = db.insertContact(scanContent, description, selected, quantity, price, pic, json, pricetext, totalprice);
							Toast.makeText(getApplicationContext(), price + description + " Added to list",
									Toast.LENGTH_SHORT).show();
							cursor.close();
						} else {
						}
					}
				}
			} while (cursor.moveToNext());
		} else {
			long id = db.insertContact(scanContent, description, selected, quantity, price, pic, json, pricetext, totalprice);
			Toast.makeText(getApplicationContext(), description + " Added to list last",
					Toast.LENGTH_SHORT).show();
			cursor.close();
		}
		cursor.close();
	}

	private void DisplayContactInDatabase(Cursor c) {

	}

	public void CopyDB(InputStream inputStream, OutputStream outputStream) throws IOException {
		//---copy 1K bytes at a time---
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.close();
	}


	class gettpnb extends AsyncTask<Void, Void, String> {

		private Exception exception;

		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
			new Handler().postDelayed(new Runnable() {
				public void run() {
					pDialog.dismiss();
				}
			}, 20000);
			data.setText("Product not Found");
			pic = "https://afterlifeconnect.com/images/NoImageAvailable.png";
			Picasso.with(getApplicationContext()).load(pic).resize(250, 250).into(item_pic);
		}

		protected String doInBackground(Void... urls) {
			// Do some validation here
			try {
				URL url = new URL("https://dev.tescolabs.com/product/?gtin=" + scanContent + "&KEY=" + API_KEY);
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
				} finally {
					urlConnection.disconnect();
				}
			} catch (Exception e) {
				Log.e("ERROR", e.getMessage(), e);
				return null;
			}
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			if (response == null) {
				response = "THERE WAS AN ERROR";
			}

			Log.i("INFO", response);
			JSONObject jObject;
			JSONObject j = null;
			JSONArray calcNutrients = null;
			try {
				jObject = new JSONObject(response);
				json = response;
				JSONArray mResponse = jObject.getJSONArray("products");
				for (int i = 0; i < mResponse.length(); i++) {
					j = mResponse.getJSONObject(i);
					if (j != null) {
						tpnb = j.getString("tpnb");


						new RetrieveFeedTask().execute();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

		private Exception exception;

		protected void onPreExecute() {
			super.onPreExecute();

		}

		protected String doInBackground(Void... urls) {
			// Do some validation here
			try {
				URL url = new URL("https://dev.tescolabs.com/product/?tpnb=" + tpnb + "&KEY=" + API_KEY);
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
				} finally {
					urlConnection.disconnect();
				}
			} catch (Exception e) {
				Log.e("ERROR", e.getMessage(), e);
				return null;
			}
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			if (response == null) {
				response = "THERE WAS AN ERROR";
			}

			Log.i("INFO", response);
			JSONObject jObject;
			JSONObject j = null;
			JSONArray calcNutrients = null;
			JSONObject allergenAdvice;
			JSONArray allergens;
			try {
				jObject = new JSONObject(response);
				json = response;
				JSONArray mResponse = jObject.getJSONArray("products");
				for (int i = 0; i < mResponse.length(); i++) {
					j = mResponse.getJSONObject(i);
					if (j != null) {
						description = j.getString("description");
						String des = "Description: ";
						descriptionsource = "<b>" + des + "</b> " + description;
						data.setText(Html.fromHtml(descriptionsource));
					}
					if (j.has("allergenAdvice")) {
						allergenList.clear();
						allergenAdvice = j.getJSONObject("allergenAdvice");
						allergens = allergenAdvice.getJSONArray("allergens");
						for (int q = 0; q <= allergens.length(); q++) {

							JSONObject p = allergens.getJSONObject(q);
							allergenName = p.getString("allergenName");
							allergenValues = p.getString("allergenValues");
							String strOutput = allergenValues.replace("[", "").replace("]", "");

							HashMap<String, String> listAllergen = new HashMap<String, String>();

							allergenList.add(listAllergen);
							listAllergen.put("free", allergenName);
							listAllergen.put("allergen", strOutput);


							ListAdapter adapter = new SimpleAdapter(
									MainActivity.this, allergenList,
									R.layout.jsonvalues, new String[]{"free", "allergen",}, new int[]{R.id.data3,
									R.id.data4});

							mylist1.setAdapter(adapter);
							mylist1.setTransitionEffect(JazzyHelper.FLY);
						}
					} else {
						allergenList.clear();
						String allergenName = "Alergens";
						String strOutput = "None";
						HashMap<String, String> listAllergen = new HashMap<String, String>();

						allergenList.add(listAllergen);
						listAllergen.put("free", allergenName);
						listAllergen.put("allergen", strOutput);


						ListAdapter adapter = new SimpleAdapter(
								MainActivity.this, allergenList,
								R.layout.jsonvalues, new String[]{"free", "allergen",}, new int[]{R.id.data3,
								R.id.data4});

						mylist1.setAdapter(adapter);
						mylist1.setTransitionEffect(JazzyHelper.FLY);
					}

				}


			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				jObject = new JSONObject(response);
				json = response;
				JSONArray mResponse = jObject.getJSONArray("products");
				for (int i = 0; i < mResponse.length(); i++) {
					j = mResponse.getJSONObject(i);

					if (j.has("calcNutrition")) {
						contactList.clear();
						JSONObject calcNutrition = j.getJSONObject("calcNutrition");
						if (calcNutrition.has("per100Header")) {
							per100Header = calcNutrition.getString("per100Header") + ": ";
						} else {
							per100Header = " ";
						}
						if (calcNutrition.has("perServingHeader")) {
							perServingHeader = calcNutrition.getString("perServingHeader") + ": ";
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
								per100Header = calcNutrition.getString("per100Header") + ": ";
							} else {
								valuePer100 = " ";
								per100Header = " ";
							}


							if (l.has("valuePerServing")) {
								valuePerServing = l.getString("valuePerServing");
								perServingHeader = calcNutrition.getString("perServingHeader") + ": ";
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
									MainActivity.this, contactList,
									R.layout.jsonvalues, new String[]{"name", "valuePer100",
									"valuePerServing", "per100Header", "perServingHeader"}, new int[]{R.id.data3,
									R.id.data4, R.id.data5, R.id.data1, R.id.data2});
							mylist.setAdapter(adapter);
							mylist.setTransitionEffect(JazzyHelper.FLY);
							new RetrieveFeedTask1().execute();
						}

					} else {

						per100Header = " ";
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
								MainActivity.this, contactList,
								R.layout.jsonvalues, new String[]{"name", "valuePer100",
								"valuePerServing", "per100Header", "perServingHeader"}, new int[]{R.id.data3,
								R.id.data4, R.id.data5, R.id.data1, R.id.data2});

						mylist.setAdapter(adapter);
						mylist.setTransitionEffect(JazzyHelper.FLY);
						new RetrieveFeedTask1().execute();
					}

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}


	class RetrieveFeedTask1 extends AsyncTask<Void, Void, String> {

		private Exception exception;

		protected void onPreExecute() {

		}

		protected String doInBackground(Void... urls) {
			// Do some validation here
			try {
				String d = description.replaceAll(" ", "%20");
				url1 = new URL("https://dev.tescolabs.com/grocery/products/?query=" + d + "&offset=0&limit=1&KEY=" + API_KEY);
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
				} finally {
					urlConnection.disconnect();
				}
			} catch (Exception e) {
				Log.e("ERROR", e.getMessage(), e);
				return null;
			}
		}

		protected void onPostExecute(String response) {
			if (response == null) {
				response = "THERE WAS AN ERROR";
			}
			Log.i("INFO", response);

			if (pDialog.isShowing())
				pDialog.dismiss();

			JSONObject jObject;

			try {
				jObject = new JSONObject(response);
				JSONObject mResponse = jObject.getJSONObject("uk");
				JSONObject mResponse1 = mResponse.getJSONObject("ghs");
				JSONObject mResponse2 = mResponse1.getJSONObject("products");
				JSONArray values = mResponse2.getJSONArray("results");

				for (int i = 0; i < values.length(); i++) {
					JSONObject j = values.getJSONObject(i);

					pic = "http://afterlifeconnect.com/images/NoImageAvailable.png";
					image = j.getString("image");
					price = j.getDouble("price");

					pic = image.replaceAll("http:", "https:");
					Picasso.with(getApplicationContext()).load(pic).resize(250, 250).into(item_pic);

					NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
					pricetext = formatter.format(price);
					String pri = "Price: ";
					pricesource = "<b>" + pri + "</b> " + pricetext;
					responseView.setText(Html.fromHtml(pricesource));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}



}

