package com.vangelov.avmodel;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vangelov.sqlite.AVModel.SQLRow;

public class PassengersAdapter extends ArrayAdapter<SQLRow> {
	private Context context;
	private int viewResourceId;
	private List<SQLRow> passengers = new ArrayList<SQLRow>();
	private TextView itemPassenger;
	private TextView itemService;
	public PassengersAdapter(Context context, int viewResourceId, List<SQLRow> objects) {
		super(context, viewResourceId, objects);
		this.context = context;
		this.viewResourceId = viewResourceId;
		this.passengers = objects;
	}
	
	public int getCount() {
		return this.passengers.size();
	}
	
	public SQLRow getItem(int index) {
		return this.passengers.get(index);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(viewResourceId, parent, false);
		}
		
		SQLRow passenger = getItem(position);
		itemPassenger = (TextView) row.findViewById(R.id.itemPassenger);
		itemPassenger.setText((CharSequence) passenger.get("name"));
		
		itemService = (TextView) row.findViewById(R.id.itemService);
		
		if (passenger.get("service_id") == null) {
			itemService.setText("n/a");
		} else {
			SQLRow service = (SQLRow) passenger.getRelation("service",true);
			itemService.setText((CharSequence) service.get("name"));
		}
		
		return row;
	}

	public void update(ArrayList<SQLRow> objects) {
		this.passengers = objects;
		this.notifyDataSetChanged();
	}
	
	@Override
	public void add(SQLRow r) {
		passengers.add(r);
	}
}
