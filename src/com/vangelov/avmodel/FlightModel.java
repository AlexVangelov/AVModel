package com.vangelov.avmodel;

import android.database.sqlite.SQLiteDatabase;

import com.vangelov.sqlite.AVModel;

public class FlightModel extends AVModel {
	public FlightModel() {}
	public FlightModel(SQLiteDatabase db) {
		super(db);
	}
	@Override
	public String getTableName() {
		return "FLIGHTS";
	}
	@Override
	public String getPrimaryKey() {
		return "ID";
	}
	@Override
	public void setRelations() {
		setBelongsTo("airplane", AirplaneModel.class, "AIRPLANE_ID");
		setHasMany("passengers", PassengerModel.class, "FLIGHT_ID");
	}
}
