package com.vangelov.avmodel;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vangelov.sqlite.AVModel.SQLCollection;
import com.vangelov.sqlite.AVModel.SQLRow;

public class FlightsAdapter extends ArrayAdapter<SQLRow> {
	private Context context;
	private int viewResourceId;
	private List<SQLRow> flights = new ArrayList<SQLRow>();
	private TextView itemTime;
	private TextView itemInfo;
	
	public FlightsAdapter(Context context, int viewResourceId, List<SQLRow> objects) {
		super(context, viewResourceId, objects);
		this.context = context;
		this.viewResourceId = viewResourceId;
		this.flights = objects;
	}
	
	public int getCount() {
		return this.flights.size();
	}
	
	public SQLRow getItem(int index) {
		return this.flights.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(viewResourceId, parent, false);
		}
		
		SQLRow flight = getItem(position);
		itemTime = (TextView) row.findViewById(R.id.itemTime);
		itemTime.setText((CharSequence) flight.get("departure"));
		
		itemInfo = (TextView) row.findViewById(R.id.itemInfo);
		itemInfo.setText(String.format("%d", ((SQLCollection)flight.getRelation("passengers")).size()));
	
		return row;
	}

	public void update(ArrayList<SQLRow> objects) {
		this.flights = objects;
		this.notifyDataSetChanged();
	}
}
