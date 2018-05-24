package unipv.irma.opentripplanner.android;

import static unipv.irma.opentripplanner.gcm.CommonUtlitiesGCM.EXTRA_MESSAGE;
import static unipv.irma.opentripplanner.gcm.CommonUtlitiesGCM.SENDER_ID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import unipv.irma.opentripplanner.android.sqlite.ConnectionDatabase;
import unipv.irma.opentripplanner.android.util.Constants;
import unipv.irma.opentripplanner.gcm.AlertDialogManager;
import unipv.irma.opentripplanner.gcm.ConnectionDetector;
import unipv.irma.opentripplanner.gcm.WakeLocker;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
//import static unipv.irma.opentripplanner.gcm.CommonUtilities.SENDER_ID;

public class SignUpTermsActivity extends Activity {
	Bundle bundle;
	ProgressDialog progressDialog;
	ConnectionDatabase connection;
	int status;
	String result;
	String jsonResult;
	
	TextView txt_terms;
	Button btn_decline;
	Button btn_accept;
	
	final Context context = this;

	/**
	 * For GCM connectivity 
	 * */
    AsyncTask<Void, Void, Void> mRegisterTask;
	
	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Connection detector
	ConnectionDetector cd;
	
	
	//public static String email;
	
	public static String regId;
	public static String name;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_terms);

		bundle = getIntent().getExtras();
		name = bundle.getString("username");
		    //registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
				
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
//		GCMRegistrar.checkManifest(this);
		
		// Get GCM registration id
		regId = GCMRegistrar.getRegistrationId(this);
		
    	// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM			
			GCMRegistrar.register(SignUpTermsActivity.this, SENDER_ID);
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(SignUpTermsActivity.this)) {
				// Skips registration.				
				Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
			} 
		}
		
		// XML Resources
		txt_terms = (TextView) findViewById(R.id.txt_terms);
		btn_accept = (Button) findViewById(R.id.btn_accept);
		btn_decline = (Button) findViewById(R.id.btn_decline);
		
		// set terms
		String txtEula = getStreamTextByLine("Eula.txt");
		txt_terms.setText(txtEula);
		
		btn_decline.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {	
				finish();
			}
		});
		
		btn_accept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new LocateTask().execute();
				
			}
		});
	}
	
	// **********************************************************************
	
	// AsyncTask
	private class LocateTask extends AsyncTask<Void, Void, Void> 
	{	
	    @Override
	    protected void onPreExecute()
	    {
	    	super.onPreExecute();  
	    	progressDialog = ProgressDialog.show(SignUpTermsActivity.this,"",getResources().getString(R.string.sign_up_registration));
            progressDialog.setCancelable(false);
	    	connection = new ConnectionDatabase();
	    
	    }

	    @Override
	    protected Void doInBackground(Void... params) 
	    {
	    	String timestamp = System.currentTimeMillis() / 1000L + "";
	    	
	    	String url = Constants.PATH_SERVER + Constants.PATH_INSERT_USER;
	    	ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
	    	values.add(new BasicNameValuePair("password",bundle.getString("password")));
	    	values.add(new BasicNameValuePair("email",bundle.getString("email")));
	    	values.add(new BasicNameValuePair("username",bundle.getString("username")));
	    	values.add(new BasicNameValuePair("age",bundle.getString("age")));
	    	values.add(new BasicNameValuePair("sex",bundle.getString("sex")));
	    	values.add(new BasicNameValuePair("category",bundle.getString("category")));
	    	values.add(new BasicNameValuePair("date_create",timestamp));
	    	values.add(new BasicNameValuePair("last_update",timestamp));

	    	jsonResult = connection.getResult(SignUpTermsActivity.this, url, values);
	    	
//			regId = GCMRegistrar.getRegistrationId(SignUpTermsActivity.this);
//			ServerUtilities.register(context, name, regId);
			
	    	//parse json data
	    	try{
	    		JSONObject obj = new JSONObject(jsonResult);
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
	    	
			mRegisterTask = null;

	    	// connection OK
	    	if (status == 200) {
	    		// registration DONE
	    		if (result.equals("OK")){
		    		getDialog(getResources().getString(R.string.sign_up_registration_done), true);
	    		} 
	    		// userId or Email exist!
	    		else {
		    		getDialog(getResources().getString(R.string.sign_up_registration_fail), false);
	    		}
	    	} 
	    	
	    	// connecion KO
	    	else {
	    		getDialog(getResources().getString(R.string.registration_fail_internet), false);
	    	}
	    }
	}
	
	// leggi i terms dal file txt
	private String getStreamTextByLine(String fileName) {
		StringBuilder mLine = new StringBuilder();
		try {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName), "ISO-8859-1")); 

		    // do reading, usually loop until end of file reading 
		    String mLineTemp;
		    while ((mLineTemp = reader.readLine()) != null) {
		       //process line
		       if (mLineTemp.equals("<br>")) {
		    	   mLine.append("\n"); 
		       } else {
		    	   mLine.append(mLineTemp); 
		       }
		    }

		    reader.close();
		} catch (IOException e) {
		    //log the exception
		}
        return mLine.toString();
    }
	
	public void getDialog(String msg, final boolean ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpTermsActivity.this);
        builder.setTitle(getResources().getString(R.string.sign_up_registration_title))
        	   .setMessage(msg)
               .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
           	    		if (ok) {
           	    			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        					SharedPreferences.Editor editor = sharedPref.edit();
        					editor.putString("username", bundle.getString("email"));
        					editor.commit();
        					
        					// close previosly activity
        					SignUpActivity.signUp.finish();
        					
           	    			Intent i = new Intent(SignUpTermsActivity.this, LoginActivity.class);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());
			
			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */
			
			// Showing received message
		//	lblMessage.append(newMessage + "\n");			
			Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
			
			// Releasing wake lock
			WakeLocker.release();
		}
	};
	
	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}
}
