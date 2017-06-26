package com.example.tesco;


import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.picasso.Picasso;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import static android.R.attr.id;
import static com.example.tesco.R.id.data3;



public class Details extends Activity {
	JazzyListView mylist;
    JazzyListView mylist1;
	String description;
	String pricetext;
	String pic;
	String json;
	String per100Header;
	String perServingHeader;
	String name;
	String valuePer100;
	String valuePerServing;
	long dbid;
	ArrayList<HashMap<String, String>> contactList;
	ArrayList<HashMap<String, String>>  allergenList;
	TextView free;
	TextView allergen;
	DBAdapter db;
	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		mylist = (JazzyListView) findViewById(R.id.containerdetails);
        mylist1 = (JazzyListView) findViewById(R.id.containerallergens);
		contactList = new ArrayList<HashMap<String, String>>();
		allergenList = new ArrayList<HashMap<String, String>>();
		Bundle bundle = getIntent().getExtras();
		db = new DBAdapter(this);
		if (bundle != null) {
			dbid = bundle.getLong("dbid");
			description = bundle.getString("description");
			pricetext = bundle.getString("pricetext");
			json = bundle.getString("json");

			TextView descriptionView = (TextView) findViewById(R.id.desresult);
			TextView priceView = (TextView) findViewById(R.id.priceresult);
			ImageView item_pic = (ImageView) findViewById(R.id.imagedetails);

			String product = "Product: ";
			String sourceString = "<b>" + product + "</b> " + description;
			descriptionView.setText(Html.fromHtml(sourceString));

			String price = "Price: ";
			String sourceStringp = "<b>" + price + "</b> " + pricetext;
			priceView.setText(Html.fromHtml(sourceStringp));

			pic = bundle.getString("pic");
			Picasso.with(getApplicationContext()).load(pic).resize(220, 220).into(item_pic);
			JSONObject jObject;
			JSONObject j;
			JSONArray calcNutrients;
			JSONObject allergenAdvice;
			JSONArray allergens;

			try {
				jObject = new JSONObject(json);
				JSONArray mResponse = jObject.getJSONArray("products");
				for (int i = 0; i < mResponse.length(); i++) {
					j = mResponse.getJSONObject(i);

					if(j.has("calcNutrition")) {
						contactList.clear();
						JSONObject calcNutrition = j.getJSONObject("calcNutrition");
						if (calcNutrition.has("per100Header")) {
							per100Header = calcNutrition.getString("per100Header")+ ": ";
						}
						else{
							per100Header=" ";
						}
						if (calcNutrition.has("perServingHeader")) {
							perServingHeader = calcNutrition.getString("perServingHeader")+ ": ";
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
									Details.this, contactList,
									R.layout.jsonvalues, new String[]{"name", "valuePer100",
									"valuePerServing", "per100Header", "perServingHeader"}, new int[]{data3,
									R.id.data4, R.id.data5, R.id.data1, R.id.data2});
							mylist.setAdapter(adapter);
							mylist.setTransitionEffect(JazzyHelper.FLY);
						}

					}
					else {

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
									Details.this, contactList,
									R.layout.jsonvalues, new String[]{"name", "valuePer100",
									"valuePerServing", "per100Header", "perServingHeader"}, new int[]{data3,
									R.id.data4, R.id.data5, R.id.data1, R.id.data2});

							mylist.setAdapter(adapter);
							mylist.setTransitionEffect(JazzyHelper.FLY);
						}

					}
			}
        	catch (JSONException e) {
				e.printStackTrace();
        	}
			try {
				jObject = new JSONObject(json);

				JSONArray mResponse = jObject.getJSONArray("products");
				for (int i = 0; i < mResponse.length(); i++) {
					j = mResponse.getJSONObject(i);

					if (j.has("allergenAdvice")) {
						allergenList.clear();
						allergenAdvice = j.getJSONObject("allergenAdvice");
						allergens = allergenAdvice.getJSONArray("allergens");
						for (int q = 0; q <= allergens.length(); q++) {

							JSONObject p = allergens.getJSONObject(q);
							String allergenName = p.getString("allergenName");
							String allergenValues = p.getString("allergenValues");
							String strOutput = allergenValues.replace("[", "").replace("]", "");

							HashMap<String, String> listAllergen = new HashMap<String, String>();

							allergenList.add(listAllergen);
							listAllergen.put("free", allergenName);
							listAllergen.put("allergen", strOutput);


							ListAdapter adapter = new SimpleAdapter(
									Details.this, allergenList,
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
								Details.this, allergenList,
								R.layout.jsonvalues, new String[]{"free", "allergen",}, new int[]{data3,
								R.id.data4});

						mylist1.setAdapter(adapter);
						mylist1.setTransitionEffect(JazzyHelper.FLY);
					}

				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}

        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			AlertDialog diaBox = AskOption(dbid);
			diaBox.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private AlertDialog AskOption(final long id){
		AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
				//set message, title, and icon
				.setTitle("Delete")
				.setMessage("Do you want to Delete")
				.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						db.open();
						cursor = db.getRow(id);
						if(cursor.moveToFirst()){
							db.deleteRow(id);
							db.close();
							Toast.makeText(Details.this, "delete"+id, Toast.LENGTH_LONG).show();
						}
						dialog.dismiss();
						finish();
						Intent list = new Intent(Details.this, List.class);
						startActivity(list);
					}
				})
				.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create();
		return myQuittingDialogBox;
	}

	public void onBackPressed()
	{
		//do whatever you want the 'Back' button to do
		//as an example the 'Back' button is set to start a new Activity named 'NewActivity'
		this.startActivity(new Intent(Details.this,List.class));

		return;
	}
}
