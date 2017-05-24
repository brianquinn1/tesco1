package com.example.tesco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	static final String KEY_ROWID = "_id";
	static final String KEY_BARCODE = "barcode";
	static final String KEY_DESCRIPTION = "description";
	static final String KEY_SELECTED = "selected";
	static final String KEY_QUANTITY = "quantity";
	static final String KEY_PRICE = "price";
	static final String KEY_PIC = "pic";
	static final String KEY_JSON = "json";
	static final String KEY_PRICETEXT = "pricetext";
	static final String KEY_TOTALPRICE = "totalprice";
	public static final int COL_ID = 0;
	public static final int COL_BARCODE = 1;
	public static final int COL_DESCRIPTION = 2;
	static final String TAG = "DBAdapter";
	static final String DATABASE_NAME = "SuperDataBasetest";
	static final String DATABASE_TABLE = "tescoshop";
	static final int DATABASE_VERSION = 28;
	static final String DATABASE_CREATE =
	"create table tescoshop (_id integer primary key autoincrement, "
	+ "barcode text not null, description text not null, selected int not null, quantity int not null, price Double not null, pic text not null, json text not null, pricetext text not null,totalprice text not null);";

	final Context context;
	DatabaseHelper DBHelper;
	SQLiteDatabase db;
	
	public DBAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	

	// This private class is the helper class which manages the SQLite database
	// creation and version management
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		// This creates a new Database
		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(DATABASE_CREATE);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// This method checks if the database exists and if it does, it deletes(drops) it 
		// and recreates it again
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
			+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS tescoshop");
			onCreate(db);
		}
	}
	
	// Open the database
	public DBAdapter open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	// Close the database
	public void close()
	{
		DBHelper.close();
	}
	
	// Insert a contact into the database
	public long insertContact(String barcode, String description, int selected , int quantity, Double price, String pic, String json, String pricetext,Double totalprice)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_BARCODE, barcode);
		initialValues.put(KEY_DESCRIPTION, description);
		initialValues.put(KEY_SELECTED, selected);
		initialValues.put(KEY_QUANTITY, quantity);
		initialValues.put(KEY_PRICE, price);
		initialValues.put(KEY_PIC, pic);
		initialValues.put(KEY_JSON, json);
		initialValues.put(KEY_PRICETEXT, pricetext);
		initialValues.put(KEY_TOTALPRICE, totalprice);


		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	
	
	// Deletes a particular contact
	public boolean deleteRow(long rowId)
	{
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public Cursor getAllRows()
	{
		return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_BARCODE, KEY_DESCRIPTION, KEY_SELECTED, KEY_QUANTITY, KEY_PRICE, KEY_PIC, KEY_JSON, KEY_PRICETEXT,KEY_TOTALPRICE}, null, null, null, null, null);
	}

    public Cursor getTotlaprice()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_TOTALPRICE}, null, null, null, null, null);
    }
	
	public Cursor getBarcode()
	{
		return db.query(DATABASE_TABLE, new String[] {KEY_BARCODE}, null, null, null, null, null);
	}
	public Cursor getAllBarcodes()
	{
		return db.query(DATABASE_TABLE, new String[] {KEY_BARCODE}, null, null, null, null, null);
	}
	
	public Cursor getID()
	{
		return db.query(DATABASE_TABLE, new String[] {KEY_ROWID}, null, null, null, null, null);
	}
	
	// Retrieve a particular contact
	public Cursor getRow(long rowId) throws SQLException
	{
		Cursor mCursor =
		db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
		KEY_BARCODE, KEY_DESCRIPTION}, KEY_ROWID + "=" + rowId, null,
		null, null, null, null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	// Update a contact
	public boolean updateContact(long rowId, int selected, int quantity, Double totalprice)
	{
			ContentValues args = new ContentValues();
			args.put(KEY_SELECTED, selected);
			args.put(KEY_QUANTITY, quantity);
			args.put(KEY_TOTALPRICE, totalprice);

			return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId,null) > 0;
	}




}