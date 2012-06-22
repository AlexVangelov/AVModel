package com.vangelov.avmodel;

import android.database.sqlite.SQLiteDatabase;

import com.vangelov.sqlite.AVModel;

public class PassengerModel extends AVModel {
	
	public PassengerModel() {}
	public PassengerModel(SQLiteDatabase db) {
		super(db);
	}
	@Override
	public String getTableName() {
		return "PASSENGERS";
	}
	@Override
	public String getPrimaryKey() {
		return "ID";
	}
	@Override
	public void setRelations() {
		setBelongsTo("flight", FlightModel.class, "FLIGHT_ID");
		setBelongsTo("service", ServiceModel.class, "SERVICE_ID");
	}
}
