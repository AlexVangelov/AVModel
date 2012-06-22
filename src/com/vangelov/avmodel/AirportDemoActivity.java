package com.vangelov.avmodel;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.vangelov.sqlite.AVModel.SQLCollection;
import com.vangelov.sqlite.AVModel.SQLRow;
 
public class AirportDemoActivity extends Activity {
	private AirportDatabase mAirportDatabase;
	private AirplanesAdapter mAirplanesAdapter;
	private FlightsAdapter mFlightsAdapter;
	private SQLCollection airplanes;
	private GridView gridAirplanes;
	private ListView listFlights;
	private TextView flightsLabel;
	private SQLRow selectedPlane = null;
	private AirplaneModel airplaneModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mAirportDatabase = new AirportDatabase(this);
        
        airplaneModel = new AirplaneModel(mAirportDatabase.mDb);
        airplanes = airplaneModel.find_all();
        
        gridAirplanes = (GridView) findViewById(R.id.gridAirplanes);
        listFlights = (ListView) findViewById(R.id.listFlights);
        flightsLabel = (TextView) findViewById(R.id.flightsLabel);
        
        mAirplanesAdapter = new AirplanesAdapter(this,airplanes);
        gridAirplanes.setAdapter(mAirplanesAdapter);
        
        mFlightsAdapter = new FlightsAdapter(this,R.layout.flight_item,new ArrayList<SQLRow>());
        listFlights.setAdapter(mFlightsAdapter);
        
        gridAirplanes.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	AirplanesAdapter adapter = (AirplanesAdapter) parent.getAdapter();
            	selectedPlane = (SQLRow) adapter.getItem(position); 
            	SQLCollection flights = (SQLCollection) selectedPlane.getRelation("flights");
            	flightsLabel.setText((CharSequence) selectedPlane.get("name"));
            	mFlightsAdapter.update(flights);
            }
        });
        gridAirplanes.setOnItemLongClickListener(new OnItemLongClickListener() {
        	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        		AirplanesAdapter adapter = (AirplanesAdapter) parent.getAdapter();
            	selectedPlane = (SQLRow) adapter.getItem(position); 
            	showPlaneActionDialog(selectedPlane);
				return true;
        	}
        });
        
        listFlights.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        		FlightsAdapter adapter = (FlightsAdapter) parent.getAdapter();
        		SQLRow flight = (SQLRow) adapter.getItem(position); 
        		showPassengersDialog(flight);
        	}
        });
        listFlights.setOnItemLongClickListener(new OnItemLongClickListener() {
        	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        		FlightsAdapter adapter = (FlightsAdapter) parent.getAdapter();
        		SQLRow flight = (SQLRow) adapter.getItem(position); 
        		showFlightActionDialog(flight);
				return true;
        	}
        });
        
        if (airplanes.size() > 0) {
        	selectedPlane = airplanes.get(0);
        	flightsLabel.setText((CharSequence) selectedPlane.get("name"));
        	mFlightsAdapter.update((SQLCollection) selectedPlane.getRelation("flights"));
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAirportDatabase.close();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		//exitApp();
    		//return true;
    	}
        return super.onKeyDown(keyCode, event);
    }
    
    public void newFlight(View v) {
    	if (selectedPlane != null) {
    		showFlightEditDialog(null);
    	} else {
    		flightsLabel.setText("Select airplane!");
    	}
    }
    
    public void saveAirplanes(View v) {
    	airplanes.save();
    }
    
    public void cancelChanges(View v) {
    	airplanes = airplaneModel.find_all();
    	mAirplanesAdapter.update(airplanes);
    	flightsLabel.setText(null);
    	mFlightsAdapter.update(new ArrayList<SQLRow>());
    }
    
    public void newAirplane (View v) {
    	showPlaneEditDialog(null);
    }
    
    public void showPassengerEditDialog(Context context,final PassengersAdapter passengersAdapter,final SQLRow passenger) {
    	View addview = View.inflate(context, R.layout.edit_a_passenger, null);
		final Spinner spinService = (Spinner) addview.findViewById(R.id.spinnerService);
		final EditText editName = (EditText) addview.findViewById(R.id.editName);
		final SQLCollection services = (new ServiceModel(mAirportDatabase.mDb)).find_all();
		ArrayAdapter<CharSequence> servicesAdapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item, services.arrayFromTextColumn("name"));
        servicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinService.setAdapter(servicesAdapter);
        
		Builder add_dialog = DialogHelper(context,"Add passenger",null, addview);
		if (passenger != null) {
			add_dialog.setTitle("Edit passenger");
			editName.setText((CharSequence) passenger.get("name"));
			spinService.setSelection(services.indexOf(passenger.getRelation("service")), true);
        } 
		add_dialog.setIcon(R.drawable.passenger);
		add_dialog.setPositiveButton("Save",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	SQLRow newPassenger;
            	if (passenger == null) {
            		newPassenger = (new PassengerModel(mAirportDatabase.mDb)).newRow();
            	} else {
            		newPassenger = passenger;
            	}
            	newPassenger.put("service_id", services.get(spinService.getSelectedItemPosition()).row_id);
            	newPassenger.put("name", editName.getText().toString() ); 
            	if (passenger == null) passengersAdapter.add(newPassenger);
            	passengersAdapter.notifyDataSetChanged();
            }
        });
		add_dialog.show();
    }
    
    public void showPassengersDialog(final SQLRow flight) {
    	final Context mContext = this; 
    	View view = View.inflate(this, R.layout.edit_passengers, null);
    	final ListView list = (ListView) view.findViewById(R.id.listPassengers);
    	final SQLCollection passengers = (SQLCollection) flight.getRelation("passengers");
    	final PassengersAdapter passengersAdapter = new PassengersAdapter(mContext, R.layout.passenger_item, passengers);
    	list.setAdapter(passengersAdapter);
    	
    	list.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        		PassengersAdapter adapter = (PassengersAdapter) parent.getAdapter();
        		SQLRow passenger = (SQLRow) adapter.getItem(position); 
        		showPassengerEditDialog(mContext,passengersAdapter,passenger);
        	}
        });
    	
    	list.setOnItemLongClickListener(new OnItemLongClickListener() {
        	public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
        		final PassengersAdapter adapter = (PassengersAdapter) parent.getAdapter();
        		SQLRow passenger = (SQLRow) adapter.getItem(position); 
        		Builder del_dialog = DialogHelper(mContext,"Delete passenger",(String) passenger.get("name"), null);
        		del_dialog.setIcon(android.R.drawable.ic_delete);
        		del_dialog.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                        	passengers.remove(adapter.getItem(position),true);
                    		adapter.notifyDataSetChanged();
                        }
                    }
                });
        		del_dialog.show();
				return true;
        	}
        });
    	
    	ImageButton buttonAdd = (ImageButton) view.findViewById(R.id.buttonAdd);
    	buttonAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPassengerEditDialog(mContext,passengersAdapter,null);
			}
    	});
    	
    	Builder dialog = new AlertDialog.Builder(mContext);
    	dialog.setIcon(R.drawable.flight);
    	dialog.setTitle("Flight: "+flight.get("departure")+" passengers");
    	dialog.setView(view);
    	dialog.setNegativeButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                	mFlightsAdapter.notifyDataSetChanged();
                    dialog.cancel();
                }
            }
        });
    	dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (dialog != null) {
                    dialog.cancel();
                }
            }
        });
    	dialog.show();
    }
    
    public void showFlightActionDialog(final SQLRow flight) {
    	Builder dialog = DialogHelper(this,"Flight "+flight.get("departure"),"* Delete will remove all related passengers!", null);
    	dialog.setIcon(R.drawable.flight);
    	dialog.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	SQLCollection flights = (SQLCollection)selectedPlane.getRelation("flights");
        		flights.remove(flight,true);
        		if (flights.size() == 0) {
        			selectedPlane.put("is_busy", 0);
        			mAirplanesAdapter.update(airplanes);
        		}
        		mFlightsAdapter.update(flights);
            }
        });
    	dialog.setNeutralButton("Edit",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	showFlightEditDialog(flight);
            }
        });
    	dialog.show();
    }
    
    public void showFlightEditDialog(final SQLRow flight) {
        View view = View.inflate(this, R.layout.edit_a_date, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        Builder dialog = DialogHelper(this,(flight == null) ? "New flight" : "Edit flight",null, view);
        dialog.setIcon(R.drawable.flight);
        dialog.setPositiveButton("Save",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if (flight == null) {
            		FlightModel flightModel = new FlightModel(mAirportDatabase.mDb);
            		SQLRow flight = flightModel.newRow();
            		flight.put("departure", String.format("%04d-%02d-%02d %02d:%02d",datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),timePicker.getCurrentHour(),timePicker.getCurrentMinute()));
            		flight.put("airplane_id", selectedPlane.row_id);
            		((SQLCollection) selectedPlane.getRelation("flights")).add(flight);
            		selectedPlane.put("is_busy", 1);
            	} else {
            		flight.put("departure", String.format("%04d-%02d-%02d %02d:%02d",datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),timePicker.getCurrentHour(),timePicker.getCurrentMinute()));
            	}
            	mFlightsAdapter.update((SQLCollection) selectedPlane.getRelation("flights"));
        		mAirplanesAdapter.notifyDataSetChanged();
            }
        });
        dialog.show();
    }
    
    public void showPlaneActionDialog(final SQLRow airplane) {
    	Builder dialog = DialogHelper(this,"Action "+airplane.get("name"),"* Delete will remove all related flights and passengers!", null);
    	dialog.setIcon(R.drawable.airplane);
    	dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	airplanes.remove(airplane,true);
            	mAirplanesAdapter.update(airplanes);
            	flightsLabel.setText(null);
            	mFlightsAdapter.update(new ArrayList<SQLRow>());
            }
        });
    	dialog.setNeutralButton("Edit",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	showPlaneEditDialog(airplane);
            }
        });
    	dialog.show();
    }
    
    public void showPlaneEditDialog(final SQLRow airplane) {
    	View view = View.inflate(this, R.layout.edit_a_string, null);
        final EditText editPlane = (EditText) view.findViewById(R.id.dialogEdit);
        if (airplane != null) editPlane.setText((CharSequence) airplane.get("name"));
        Builder dialog = DialogHelper(this,(airplane == null) ? "New airplane" : "Edit airplane",null, view);
        dialog.setIcon(R.drawable.airplane);
        dialog.setPositiveButton("Save",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if (airplane == null) {
            		SQLRow newAirplane = airplaneModel.newRow();
            		newAirplane.put("name", editPlane.getText().toString());
            		newAirplane.put("is_busy", 0);
            		airplanes.add(newAirplane);
            		selectedPlane = newAirplane;
            		flightsLabel.setText((CharSequence) selectedPlane.get("name"));
            		mFlightsAdapter.update((SQLCollection)selectedPlane.getRelation("flights"));
            	} else {
            		airplane.put("name", editPlane.getText().toString());
            	}
            	mAirplanesAdapter.update(airplanes);
            }
        }); 
        dialog.show();
    }
    
    public Builder DialogHelper(Context context,String title, String message, final View v) {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(context);
    	if (title != null) dialog.setTitle(title);
    	if (message != null) dialog.setMessage(message);
    	if (v != null) dialog.setView(v);
    	dialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.cancel();
                }
            }
        });
    	dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (dialog != null) {
                    dialog.cancel();
                }
            }
        });
    	return dialog;
    }
}