package com.vangelov.avmodel;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vangelov.sqlite.AVModel.SQLCollection;
import com.vangelov.sqlite.AVModel.SQLRow;

public class AirplanesAdapter extends BaseAdapter {
	private Context context;
	private List<SQLRow> airplanes = new ArrayList<SQLRow>();
	
	public AirplanesAdapter(Context context,SQLCollection airplanes) {
		this.context = context;
		this.airplanes = airplanes;
	}
	
	@Override
	public int getCount() {
		return airplanes.size();
	}

	@Override
	public Object getItem(int position) {
		return airplanes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return ((SQLRow) getItem(position)).row_id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if ( convertView == null )
		{
			LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.grid_item, null);
		}
		SQLRow  r = (SQLRow) getItem(position);
		TextView tv = (TextView)v.findViewById(R.id.grid_item_text);
		tv.setText((CharSequence) r.get("name"));
		
		ImageView iv = (ImageView)v.findViewById(R.id.grid_item_image);
		if ((Integer)r.get("is_busy") == 0) {
			iv.setImageResource(R.drawable.airplane_gray);
		} else {
			iv.setImageResource(R.drawable.airplane);
		}
		
		return v;
	}

	public void update(ArrayList<SQLRow> objects) {
		this.airplanes = objects;
		this.notifyDataSetChanged();
	}
}
