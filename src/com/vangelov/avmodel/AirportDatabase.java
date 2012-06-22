package com.vangelov.avmodel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class AirportDatabase {
	private static final String TAG = "AirportDatabase";
	private static final String DATABASE_NAME = "airport.db";
    private static final int DATABASE_VERSION = 2;
    
	public SQLiteDatabase mDb;
    private DatabaseHelper mDatabaseHelper;

    static final String CREATE_AIRPLANES = "CREATE TABLE 'AIRPLANES' ('ID' INTEGER PRIMARY KEY AUTOINCREMENT,'NAME' TEXT,'IS_BUSY' INTEGER DEFAULT (0))";
    static final String DROP_AIRPLANES = "DROP TABLE IF EXISTS 'AIRPLANES'";
    static final String CREATE_FLIGHTS = "CREATE TABLE 'FLIGHTS' ('ID' INTEGER PRIMARY KEY AUTOINCREMENT,'DEPARTURE' TEXT, 'AIRPLANE_ID' INTEGER REFERENCES AIRPLANES(ID))";
    static final String DROP_FLIGHTS = "DROP TABLE IF EXISTS 'FLIGHTS'";
    static final String CREATE_SERVICES = "CREATE TABLE 'SERVICES' ('ID' INTEGER PRIMARY KEY AUTOINCREMENT,'NAME' TEXT)";
    static final String DROP_SERVICES = "DROP TABLE IF EXISTS 'SERVICES'";
    static final String CREATE_PASSENGERS = "CREATE TABLE 'PASSENGERS' ('ID' INTEGER PRIMARY KEY AUTOINCREMENT,'NAME' TEXT,'SERVICE_ID' INTEGER REFERENCES SERVICES(ID),'FLIGHT_ID' INTEGER REFERENCES FLIGHTS(ID))";
    static final String DROP_PASSENGERS = "DROP TABLE IF EXISTS 'PASSENGERS'";
    
	public AirportDatabase(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        mDb = mDatabaseHelper.getWritableDatabase();
    }
	
	public void close() {
        mDatabaseHelper.close();
    }
	
	private class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		@Override
		public void onCreate(SQLiteDatabase db) {
			createAllTables(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (newVersion != DATABASE_VERSION) {
                Log.w(TAG, "Database upgrade from old: " + oldVersion + " to: " + newVersion);
                dropAllTables(db);
                createAllTables(db);
                return;
            }
		}
		
		private void dropAllTables(SQLiteDatabase db) {
			db.execSQL(DROP_PASSENGERS);
			db.execSQL(DROP_SERVICES);
			db.execSQL(DROP_FLIGHTS);
			db.execSQL(DROP_AIRPLANES);
		}
		private void createAllTables(SQLiteDatabase db) {
			db.execSQL(CREATE_AIRPLANES);
			db.execSQL(CREATE_SERVICES);
			db.execSQL(CREATE_FLIGHTS);
			db.execSQL(CREATE_PASSENGERS);
				// add some data for demo
				db.execSQL("INSERT into AIRPLANES (NAME,IS_BUSY) values ('Plane 1',1)");
				db.execSQL("INSERT into AIRPLANES (NAME,IS_BUSY) values ('Plane 2',0)");
				db.execSQL("INSERT into AIRPLANES (NAME,IS_BUSY) values ('Plane 3',0)");
				db.execSQL("INSERT into SERVICES (NAME) values ('First class')");
				db.execSQL("INSERT into SERVICES (NAME) values ('Second class')");
				db.execSQL("INSERT into FLIGHTS (AIRPLANE_ID,DEPARTURE) values (1,'2012-12-12 12:12')");
				db.execSQL("INSERT into PASSENGERS (FLIGHT_ID,NAME,SERVICE_ID) values (1,'AlexanderVangelov@gmail.com',1)");
		}
	}

}
