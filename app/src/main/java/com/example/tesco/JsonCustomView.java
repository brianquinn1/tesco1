package com.example.tesco;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class JsonCustomView extends ArrayAdapter<String>{
	
	private final Activity context;
	private final String[] Name;
	private final String[] ValuePer100;
	private final String[] ValuePerServing;

	//Constructor
	public JsonCustomView(Activity context, String[] Name,  String[] ValuePer100, String[] ValuePerServing) {
		
			super(context, R.layout.jsonvalues, Name); 
			this.context = context;
			this.Name = Name;
			this.ValuePer100 = ValuePer100;
			this.ValuePerServing = ValuePerServing;


	}

	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		//thumbnailListener = new ThumbnailListener();
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.jsonvalues, null, true);
		TextView nameview = (TextView) rowView.findViewById(R.id.data3);
		nameview.setText(Name[position]+":" );
		
		TextView valuePer100view = (TextView) rowView.findViewById(R.id.data4);
		valuePer100view.setText("ValuePer100:"+ValuePer100[position]);
		
		TextView valuePerServingview = (TextView) rowView.findViewById(R.id.data5);
		valuePerServingview.setText("ValuePerServing: "+ValuePerServing[position]);
		
		return rowView;
	}

}