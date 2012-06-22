package com.vangelov.avmodel;

import android.database.sqlite.SQLiteDatabase;

import com.vangelov.sqlite.AVModel;

public class AirplaneModel extends AVModel {
	
	public AirplaneModel() {}
	public AirplaneModel(SQLiteDatabase db) {
		super(db);
	}
	@Override
	public String getTableName() {
		return "AIRPLANES";
	}
	@Override
	public String getPrimaryKey() {
		return "ID";
	}
	@Override
	public void setRelations() {
		setHasMany("flights", FlightModel.class, "AIRPLANE_ID");
	}
}
