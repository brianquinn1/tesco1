package com.example.tesco;

import java.text.NumberFormat;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;



public class CustomView extends ArrayAdapter<String> {

	private static double totalpricetotal;
	private final Activity context;
	private final int[] row;
	private final String[] barcode;
	private final String[] description;
	private final int[] select;
	private final int[] quantity;
	private final Double[] price;
	private final Double[] totalprice;
	private final String[] pricetext;
	double tprice;
	static Test test = new Test();
	public static Double t = 0.0;
	Double total = 0.0;
	DBAdapter db;


	//Constructor
	public CustomView(Activity context, int[] row, String[] barcode, String[] description, int[] select, int[] quantity, Double[] price, String[] pricetext, Double[] totalprice) {

		super(context, R.layout.single_rowtest, barcode);
		this.context = context;
		this.row = row;
		this.barcode = barcode;
		this.description = description;
		this.select = select;
		this.quantity = quantity;
		this.price = price;
		this.pricetext = pricetext;
		this.totalprice = totalprice;
	}


	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		//thumbnailListener = new ThumbnailListener();
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.single_rowtest, null, true);
		TextView Barcode = (TextView) rowView.findViewById(R.id.barcode);
		Barcode.setText("Cost: " + (pricetext[position]));

		TextView Description = (TextView) rowView.findViewById(R.id.description);
		Description.setText(description[position]);

		TextView countview = (TextView) rowView.findViewById(R.id.count);
		countview.setText("Amount: " + quantity[position]);

		ImageButton add = (ImageButton) rowView.findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DBAdapter db = new DBAdapter(v.getContext());
				db.open();
				//t=getPricetest();
				if (v.getId() == R.id.Clear_List) {
					t = new Double(0);
				}
				add(position);
				tprice = price[position];
				if (totalprice[position] == 0.0) {
					totalprice[position] = tprice;
					t = t + totalprice[position];
				} else {
					t = t + totalprice[position];
				}

//				if(t<=0.0)
//				{
//					t=0.0;
//				}

				String priceformat = String.format("%.2f", totalprice);
				//Toast.makeText(context, "total: " +total + "tprice"+tprice +"totalpricetotal"+totalpricetotal +"t"+t+"totalprice[position]"+totalprice[position]+"postion"+position, Toast.LENGTH_SHORT).show();
				db.updateContact(row[position], select[position], quantity[position], t);
				//Toast.makeText(context, "row: " + row[position] +" selected: " + select[position] + " quantity: "  + quantity[position],  Toast.LENGTH_SHORT).show();
				db.close();
			}
		});

		ImageButton subtract = (ImageButton) rowView.findViewById(R.id.subtract);
		subtract.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				DBAdapter db = new DBAdapter(v.getContext());
				db.open();

				t = new Double(0);
				if (quantity[position] == 0) {
					Toast.makeText(context, "sorry ", Toast.LENGTH_SHORT).show();
				} else {
					long rowid = row[position];
					select[position] = 1;
					tprice = price[position];
					if (totalprice[position] == 0.0) {
						totalprice[position] = 0.0;
						total = 0.0;
					} else {
						t = t - totalprice[position];
					}

					//Toast.makeText(context, "total: " + total + "tprice" + tprice + "totalpricetotal" + totalpricetotal + "t" + t, Toast.LENGTH_SHORT).show();
					subtract(position);
					db.updateContact(row[position], select[position], quantity[position], t);
				}
			}
		});
		return rowView;
	}


	public void add(int position) {
		select[position] = 1;
		quantity[position]++;
		notifyDataSetChanged();

	}

	public void subtract(int position) {
		select[position] = 1;
		quantity[position]--;
		//Toast.makeText(context, "totalprice: " + totalprice+ "price"+price[position],  Toast.LENGTH_SHORT).show();
		if (quantity[position] <= 0) {
			select[position] = 0;
			quantity[position] = 0;
			//Toast.makeText(context, "totalprice: " + +price[position],  Toast.LENGTH_SHORT).show();
		}

		notifyDataSetChanged();
	}
}


