package com.example.tesco;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import static android.R.attr.id;

public class Delete extends Activity {
	ListView mylist;
	DBAdapter db;
	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete);

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
		;
		populateListView();
		mylist = (ListView) findViewById(R.id.container1);
		mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, final long id) {
		AlertDialog diaBox = AskOption(id);
		diaBox.show();
		}

		});

	}

	public void populateListView(){
		db.open();
		Cursor cursor = db.getAllRows();
		String [] fromFieldNames = new String [] {DBAdapter.KEY_PRICETEXT, DBAdapter.KEY_DESCRIPTION,DBAdapter.KEY_PIC};
		int[] toViewIDs = new int [] {R.id.barcode, R.id.description,R.id.image};
		SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this,R.layout.single_row, cursor , fromFieldNames, toViewIDs,0);
		mylist = (ListView) findViewById(R.id.container1);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.delete, menu);
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
					populateListView();
					db.close();
					Toast.makeText(Delete.this, "delete"+id, Toast.LENGTH_LONG).show();
				}
				dialog.dismiss();
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
}
