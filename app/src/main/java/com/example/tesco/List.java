package com.example.tesco;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class List extends Activity {
	ListView mylist;
	DBAdapter db;
	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
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
		populateListView();
		mylist = (ListView) findViewById(R.id.container);
		mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, final long id) {
			Cursor cursor = (Cursor) mylist.getItemAtPosition(position);
			String description = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_DESCRIPTION));
			String pricetext = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_PRICETEXT));
			String pic = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_PIC));
			String json = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_JSON));

			Intent details = new Intent(List.this, Details.class);
			details.putExtra("description", description);
			details.putExtra("pricetext", pricetext);
			details.putExtra("pic", pic);
			details.putExtra("json", json);
			startActivity(details);

		}
		});
	}


	public void populateListView(){
		db.open();
		Cursor cursor = db.getAllRows();
		String [] fromFieldNames = new String [] {DBAdapter.KEY_PRICETEXT, DBAdapter.KEY_DESCRIPTION,DBAdapter.KEY_PIC};
		int[] toViewIDs = new int [] {R.id.barcode, R.id.description,R.id.image};
		SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this,R.layout.single_row, cursor , fromFieldNames, toViewIDs,0);
		mylist = (ListView) findViewById(R.id.container);
		mylist.setAdapter(myCursorAdapter);
		myCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){

		public boolean setViewValue(View view, Cursor cursor, int columnIndex){
			if(view.getId() == R.id.image){
				ImageView item_pic =(ImageView) view.findViewById(R.id.image);
				String imageString = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_PIC));
				Picasso.with(getApplicationContext()).load(imageString).resize(200, 200).into(item_pic);
				return true;
			}
			return false;
		}
		});
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
}
