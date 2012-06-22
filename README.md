# AVModel

![Planes](planes.png)
![Edit Plane](edit_plane.png)
![Edit Flight](edit_flight.png)
![Passengers](passengers.png)
![Edit Passenger](edit_passenger.png)

## Scenario

Let's have an Airport

	Airport has many Airplanes
	Each Airplane has many Flights (Each Flight belongs to Airplane)
	Each Flight has many Passengers (Each Passenger belongs to Flight)
	Each Passenger belongs to Service /first or second class/ (so Service has many Passengers)
	
And now let's play with all this data and `Commit` and save it, or `Rollback` at once! 

Add `src/com/vangelov/sqlite/AVModel.java` to your project.
	
Create AirplaneModel.java
	<code>
	public class AirplaneModel extends AVModel {
		public AirplaneModel() {}
		public AirplaneModel(SQLiteDatabase db) { super(db); }
		@Override
		public String getTableName() { return "AIRPLANES"; }
		@Override
		public String getPrimaryKey() { return "ID"; }
		@Override
		public void setRelations() {
			setHasMany("flights", FlightModel.class, "AIRPLANE_ID");
		}
	}
	</code>
Create FlightModel.java
	<code>
	public class FlightModel extends AVModel {
		public FlightModel() {}
		public FlightModel(SQLiteDatabase db) { super(db); }
		@Override
		public String getTableName() { return "FLIGHTS"; }
		@Override
		public String getPrimaryKey() { return "ID"; }
		@Override
		public void setRelations() {
			setBelongsTo("airplane", AirplaneModel.class, "AIRPLANE_ID");
			setHasMany("passengers", PassengerModel.class, "FLIGHT_ID");
		}
	}
	</code>
Create PassengerModel.java
	<code>
	public class PassengerModel extends AVModel {
		public PassengerModel() {}
		public PassengerModel(SQLiteDatabase db) { super(db); }
		@Override
		public String getTableName() { return "PASSENGERS"; }
		@Override
		public String getPrimaryKey() { return "ID"; }
		@Override
		public void setRelations() {
			setBelongsTo("flight", FlightModel.class, "FLIGHT_ID");
			setBelongsTo("service", ServiceModel.class, "SERVICE_ID");
		}
	}
	</code>
Create ServiceModel.java
	<code>
	public class ServiceModel extends AVModel {
		public ServiceModel() {}
		public ServiceModel(SQLiteDatabase db) { super(db); }
		@Override
		public String getTableName() { return "SERVICES"; }
		@Override
		public String getPrimaryKey() { return "ID"; }
		@Override
		public void setRelations() { }
	}
	</code>
# How to get a collection of all Airplanes:
	private AirplaneModel airplaneModel;
	private SQLCollection airplanes;
	airplaneModel = new AirplaneModel((SQLiteDatabase)mDb);
	airplanes = airplaneModel.find_all();
	
# Get a single airplane from the airplanes collection:
	private SQLRow selectedPlane = null;
	selectedPlane = (SQLRow) airplanes.get(position);

# Get flights of this plane:
	SQLCollection flights = (SQLCollection) selectedPlane.getRelation("flights");

# Add a flight:	
	SQLRow newFlight = (new FlightModel(mAirportDatabase.mDb)).newRow();
	flights.add(newFlight);
	
# Change Airplane data through flight model
	SQLRow airplane = (SQLRow) newFlight.getRelation("airplane");
	airplane.put("is_busy", 0);
	
# Save changes to SQLite database:
	airplanes.save();
	
	
## Methods of AVModel:
	public SQLRow newRow();
	public int update(ContentValues values,String whereClause,String[] whereArgs);
	public long replace(ContentValues initialValues);
	public long replaceOrThrow(ContentValues initialValues);
	public long insert(ContentValues values);
	public long insertOrThrow(ContentValues values);
	public int delete(String whereClause,String[] whereArgs);
	public SQLRow find(int id);
	public SQLCollection find_with_limit(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit);
	public SQLCollection find_distinct(boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit);
	public SQLCollection find_with_factory(SQLiteDatabase.CursorFactory cursorFactory,boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit);
	public SQLCollection find_all();
	public SQLCollection find_all(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy);
	public SQLRow find_one(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy);
	
	
# class SQLRow extends `HashMap<String, Object>` with additional methods:
	public Object getRelation(String key,boolean reload);
	public int remove();
	public int save(); 

# class SQLCollection extends `ArrayList<SQLRow>` with additional methods:
	public void save();
	public void clear(boolean includeRelatedCollections);
	public boolean remove(Object object,boolean includeRelatedCollections);
	
	 
