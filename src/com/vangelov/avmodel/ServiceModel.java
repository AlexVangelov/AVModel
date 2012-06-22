package com.vangelov.avmodel;

import android.database.sqlite.SQLiteDatabase;

import com.vangelov.sqlite.AVModel;

public class ServiceModel extends AVModel {
	
	public ServiceModel() {}
	public ServiceModel(SQLiteDatabase db) {
		super(db);
	}
	@Override
	public String getTableName() {
		return "SERVICES";
	}
	@Override
	public String getPrimaryKey() {
		return "ID";
	}
	@Override
	public void setRelations() {
	}
}
