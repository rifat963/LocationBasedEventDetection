package unipv.irma.opentripplanner.android;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unipv.irma.opentripplanner.android.adapters.MonitoredTripAdapter;
import unipv.irma.opentripplanner.android.model.TripMonitoredModel;
import unipv.irma.opentripplanner.android.model.UserModel;
import unipv.irma.opentripplanner.android.sqlite.ConnectionDatabase;
import unipv.irma.opentripplanner.android.util.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class TripMonitoredActivity extends Activity {

	ConnectionDatabase connection;
	JSONObject obj;
	int status;
	String result;
	JSONArray data;
	String jsonResult;
	
	MonitoredTripAdapter adapter;
	ArrayList<TripMonitoredModel> listTrip;
	
	ProgressDialog progressDialog;

	ImageButton btn_back;
	ListView tripListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_monitored);

		// XML Resources
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		tripListView = (ListView) findViewById(R.id.tripListView);
		
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(TripMonitoredActivity.this, DashboardActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				finish();
			}
		});
		
		new LocateTask().execute();
	}
	
	// **********************************************************************

	// AsyncTask
	private class LocateTask extends AsyncTask<Void, Void, Void> 
	{	
	    @Override
	    protected void onPreExecute()
	    {
	    	super.onPreExecute();  
	    	progressDialog = ProgressDialog.show(TripMonitoredActivity.this,"",getResources().getString(R.string.connecting));
            progressDialog.setCancelable(false);
	    	connection = new ConnectionDatabase();
	    }

	    @Override
	    protected Void doInBackground(Void... params) 
	    {
	    	String url = Constants.PATH_SERVER + Constants.PATH_GET_MONITORED_TRIP;
	    	ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
	    	values.add(new BasicNameValuePair("user_id",UserModel.getInstance().getId() + ""));

	    	jsonResult = connection.getResult(TripMonitoredActivity.this, url, values);
			
	    	//parse json data
	    	try{
	    		obj = new JSONObject(jsonResult);
	    		status = obj.getInt("status");
	    		result = obj.getString("result");
	    	}catch(JSONException e){
	    		Log.e("log_tag", "Error parsing data "+e.toString());
	    	}
	    	
	    	return null; 				         
	    }
	    
	    @Override
	    protected void onPostExecute(Void v)
	    {
	    	super.onPostExecute(v); 
	    	progressDialog.cancel();
	    	
	    	// connection OK
	    	if (status == 200) {
	    		// registration DONE
	    		if (result.equals("OK")){
					// save on userModel instance
	    			try {
						data = obj.getJSONArray("data");
						
						listTrip = new ArrayList<TripMonitoredModel>();
						for (int i = 0; i < data.length(); i++) {
							TripMonitoredModel tripModel = new TripMonitoredModel();
							JSONObject objTrip = data.getJSONObject(i);
							
							tripModel.setId(objTrip.getInt("id"));
							tripModel.setUser_id(objTrip.getInt("user_id"));
							tripModel.setStart_location(objTrip.getString("start_location"));
							tripModel.setEnd_location(objTrip.getString("end_location"));
							tripModel.setStart_lat(objTrip.getString("start_lat"));
							tripModel.setEnd_lat(objTrip.getString("end_lat"));
							tripModel.setStart_lon(objTrip.getString("start_lon"));
							tripModel.setEnd_lon(objTrip.getString("end_lon"));
							tripModel.setDate(objTrip.getString("date"));
							tripModel.setTime(objTrip.getString("time"));
							tripModel.setTimestamp(objTrip.getString("timestamp"));
							tripModel.setCount(objTrip.getInt("count"));

							listTrip.add(tripModel);
							
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	    adapter = new MonitoredTripAdapter(TripMonitoredActivity.this, listTrip);
		    	    tripListView.setAdapter(adapter); 
	    		} 
	    		// userId or Email not exist!
	    		else {
		    		getDialog(getResources().getString(R.string.trip_monitored_fail), true);
	    		}
	    	} 
	    	
	    	// connecion KO
	    	else {
	    		getDialog(getResources().getString(R.string.registration_fail_internet), true);
	    	}
	    }
	}
	
	public void getDialog(String msg, final boolean ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TripMonitoredActivity.this);
        builder.setTitle(getResources().getString(R.string.activity_trip_monitored))
        	   .setMessage(msg)
               .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
           	    		if (ok) {
	                     	Intent i = new Intent(TripMonitoredActivity.this, DashboardActivity.class);
	           				startActivity(i);
	           				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	           				finish();
           	    		}
           	    		else {
           	    			
           	    		}
    				}
               });
        builder.create();
        builder.show();
	}

	@Override
	protected void onStop()
	{
		super.onStop();		
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent i = new Intent(TripMonitoredActivity.this, DashboardActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
