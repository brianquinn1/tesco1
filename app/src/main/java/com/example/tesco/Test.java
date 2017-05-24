package com.example.tesco;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream; 
import java.text.NumberFormat;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.format;
import static android.os.Build.VERSION_CODES.M;
import static com.example.tesco.R.id.priceresult;


public class Test extends Activity {
	ListView mylist;
	DBAdapter db;
	Cursor cursor;
	TextView textView;
	Double sum=0.0;
	Double cal=0.0;
	double zero;
	private static Double sumy;
	private static Double priceResult=0.0;
	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		textView=(TextView) findViewById(R.id.textView);
		db = new DBAdapter(this);

		try {
			String destPath = "/data/data/" + getPackageName() +
			"/databases";
			File f = new File(destPath);
			if (!f.exists()) {
				f.mkdirs();
				f.createNewFile();
				// Copy the db from the assets folder into the databases folder
				CopyDB(getBaseContext().getAssets().open("SuperDataBasetest1"),
				new FileOutputStream(destPath + "/SuperDataBasetest1"));
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		db.open();
		Cursor c = db.getAllRows();
		if (c.moveToFirst()) {
			do {
				DisplayContactInDatabase(c);
			} while (c.moveToNext());
		}

		Cursor cursor = db.getAllRows();
		int[] row = new int[cursor.getCount()];
		String[] bar = new String[cursor.getCount()];
		String[] des = new String[cursor.getCount()];
		Double[] price = new Double[cursor.getCount()];
		int[] select = new int[cursor.getCount()];
		int[] quantity = new int[cursor.getCount()];
		String[] pricetext = new String[cursor.getCount()];
		Double[] totalprice = new Double[cursor.getCount()];
		int rowcount = 0;
		int barcount =0;
		int descount =0;
		int pricount =0;
		int selectcount =0;
		int quantitycount =0;
		int pricetextcount =0;
		int totalpricecount =0;
		while (cursor.moveToNext()) {
			row[rowcount++] = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
			bar[barcount++] = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BARCODE));
			price[pricount++] = cursor.getDouble(cursor.getColumnIndex(DBAdapter.KEY_PRICE));
			des[descount++] = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_DESCRIPTION));
			select[selectcount++] = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_SELECTED));
			quantity[quantitycount++] = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_QUANTITY));
			pricetext[pricetextcount++] = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_PRICETEXT));
			totalprice[totalpricecount++] = cursor.getDouble(cursor.getColumnIndex(DBAdapter.KEY_TOTALPRICE));
		}

		populateListView();
		CustomView adapter = new CustomView(Test.this,row, bar, des,select,quantity,price,pricetext,totalprice);
		mylist = (ListView) findViewById(R.id.containertest);
		mylist.setAdapter(adapter);
		Thread t = new Thread() {

			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						Thread.sleep(1000);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {

								readprice();
							}
						});
					}
				} catch (InterruptedException e) {
				}
			}
		};

		t.start();

	}

	public void onClickDone(View v){
		//respond to clicks
		if(v.getId()==R.id.Done){
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.addCategory(Intent.CATEGORY_BROWSABLE);
			intent.setData(Uri.parse("https://www.tesco.ie/groceries/?sc_cmp=ppc*sl*me*bg*px_-_campaign_not_set*tesco%20shop%20online&gclid=CjwKEAjwlpbIBRCx4eT8l9W26igSJAAuQ_HGGWBnkVDL8GGuJaplDJnmMOezfjae50rtAsnpVZEE0hoCZeHw_wcB"));
			startActivity(intent);
		}
	}

	public void onClickClear_List(View v) {
		if(v.getId()==R.id.Clear_List){
			db.open();
			Cursor c = db.getAllRows();
			c.moveToFirst();
			if (c != null){
				if (c.getCount()>0) {
					int[] row= new int[c.getCount()];
					int rowcount =0;
					do {
						row[rowcount] = c.getInt(c.getColumnIndex(DBAdapter.KEY_ROWID));
						int rowId =row[rowcount];
						int selected =0;
						int quantity = 0;
						zero =0.0;
						rowcount++;
						db.updateContact(rowId, selected, quantity,0.0);

					}while(c.moveToNext());
					Toast.makeText(getApplicationContext(),  "Number of items set to 0" ,
							Toast.LENGTH_SHORT).show();
					NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
					String priceFormat =formatter.format(zero);
					String total="Total cost: ";
					String sourceStringprice = "<b>" + total + "</b> " + priceFormat;
					textView.setText(Html.fromHtml(sourceStringprice));
					refresh();
					readprice();
				}
			}
		}
	}

	public void populateListView(){
		db.open();
		Cursor cursor = db.getAllRows();
		String [] fromFieldNames = new String [] {DBAdapter.KEY_PRICE, DBAdapter.KEY_DESCRIPTION};
		int [] toViewIDs = new int [] {R.id.barcode, R.id.description};
		SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this,R.layout.single_row, cursor , fromFieldNames, toViewIDs, 0);
		mylist = (ListView) findViewById(R.id.containertest);
		mylist.setAdapter(myCursorAdapter);
		db.close();

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
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


	public void refresh() {
		finish();
		overridePendingTransition( 0, 0);
		startActivity(getIntent());
		overridePendingTransition( 0, 0);

	}


	public void readprice() {

			db.open();
			cursor = db.getAllRows();

			//priceResult = CustomView.getPrice();
		int[] row = new int[cursor.getCount()];
			Double[] price = new Double[cursor.getCount()];
		int[] select = new int[cursor.getCount()];
		int[] quantity = new int[cursor.getCount()];
		Double[] totalprice = new Double[cursor.getCount()];
		int rowcount=0;
		int pricecount = 0;
		int selectcount = 0;
		int quantitycount =0;
		int totalpricecount=0;
		sum= 0.0;

		//cursor.moveToFirst();
		while (cursor.moveToNext()){
			row[rowcount] = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
			price[pricecount] = cursor.getDouble(cursor.getColumnIndex(DBAdapter.KEY_PRICE));
			select[selectcount] = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_SELECTED));
			quantity[quantitycount] = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_QUANTITY));
			totalprice[totalpricecount++] = cursor.getDouble(cursor.getColumnIndex(DBAdapter.KEY_TOTALPRICE));
				//Toast.makeText(getApplicationContext(), ""+row[rowcount],
						//Toast.LENGTH_SHORT).show();
			if(select[selectcount]==1 || quantity[quantitycount] >0){
				cal=price[pricecount]*quantity[quantitycount];
				db.updateContact(row[rowcount], select[selectcount], quantity[quantitycount],cal);
				sum+=cal;
			}
			else{
				cal=0.0;
			}
				rowcount++;
				selectcount++;
				quantitycount++;
				pricecount++;
			}

			NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
			String priceFormat = formatter.format(sum);
			String total = "Total Cost: ";
			String sourceStringprice = "<b>" + total + "</b> " + priceFormat;
			textView.setText(Html.fromHtml(sourceStringprice));


	}


}