package unipv.irma.opentripplanner.android;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;

import unipv.irma.opentripplanner.android.fragments.MainFragment;
import unipv.irma.opentripplanner.android.model.UserModel;
import unipv.irma.opentripplanner.android.util.Constants;
import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Colors;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DashboardActivity extends Activity {

	ImageButton btn_logout;
	ImageButton btn_options;
	Button btn_tripplanner;
	Button btn_favourite;
	Button btn_recent;
	Button btn_feedback;
	TextView txt_welcome;
	TextView txtbroadcast;
	TextView txtEventsDescriptions;
	
	
	SharedPreferences sharedPref;
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "C4RP55SBWYYT9W9R7ZF2");
		new dataAccess_broadcast().execute();
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
		finish();
	}
	public static String regId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		regId = GCMRegistrar.getRegistrationId(this);
		
		// XML Resources
		btn_logout = (ImageButton) findViewById(R.id.btn_logout);
		btn_options = (ImageButton) findViewById(R.id.btn_options);
		btn_tripplanner = (Button) findViewById(R.id.btn_tripplanner);
		btn_favourite = (Button) findViewById(R.id.btn_favourite);
		btn_recent = (Button) findViewById(R.id.btn_recent);
		btn_feedback = (Button) findViewById(R.id.btn_feedback);
		txt_welcome = (TextView) findViewById(R.id.txt_welcome);
		txtbroadcast=(TextView)findViewById(R.id.txtBroadcast);
		//txtEventsDescriptions=(TextView)findViewById(R.id.txtEventsDescription);
		txt_welcome.setText(getResources().getString(R.string.welcome) + " " + UserModel.getInstance().getUsername());
		
		
		
		//txtbroadcast.setText("Events:Events:Events:Events:Events:Events:Events:Events:Events:Events:Events:Events:Events:Events:Events:Events:");
		//txtEventsDescriptions.setText("Descriptions:");
		FlurryAgent.setUserId(UserModel.getInstance().getUsername());
		
		btn_logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("username", "");
				editor.putString("psw", "");
				editor.commit();
				
				Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				finish();
			}
		});
		
		btn_tripplanner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
				Editor editor = sharedpreferences.edit();
				editor.putString("regId", regId);
				editor.commit();
				
				Intent i = new Intent(DashboardActivity.this, MyActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				finish();
			}
		});
		
		btn_favourite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(DashboardActivity.this, TripFavouriteActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				finish();
			}
		});
		
		btn_recent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(DashboardActivity.this, TripMonitoredActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				finish();
			}
		});
		
		btn_feedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(DashboardActivity.this, FeedbackActivity.class);
				i.putExtra("Regid", regId);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				finish();
			}
		});
		
		btn_options.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(DashboardActivity.this, AccountActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				finish();
			}
		});
		
	}
	
	

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private class dataAccess_broadcast extends AsyncTask<String, Void, Void>{
		
		 String result_sub;
		 InputStream is = null;
	     private String Content;
	     private String result;
	     List<String> result_list2;
	     private  String s = "";
	     StringBuilder sb = null;
	     private String Error = null;
	   
		//private ProgressDialog Dialog = new ProgressDialog(test.this);
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			 try {
				
				//JSONObject object = new JSONObject(); 
				    String end="test";
	                HttpClient httpclient = new DefaultHttpClient();
	                HttpPost httppost = new HttpPost("http://eventmanager-irmatripplanner.rhcloud.com/gcmalert/service_android_broadcast.php");
	              //  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	                HttpParams parmsHttp =httpclient.getParams();
	                HttpConnectionParams.setConnectionTimeout(parmsHttp, 20000);
	                
	                List<NameValuePair> pp = new ArrayList<NameValuePair>();
	                pp.add(new BasicNameValuePair("end", end));
	                
	                httppost.setEntity(new UrlEncodedFormEntity(pp));
	                
	                HttpResponse response = httpclient.execute(httppost);

	                int a = response.getStatusLine().getStatusCode();
	                
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                
	                result = out.toString();
	                
			 }catch (Exception e) {
	                // TODO: handle exception
	                Log.e("log_tag", "Error Parsing Data "+e.toString());
	        }
			 
			 try{
				 	
				   if(!result.trim().equals("null")){
				  
				   JSONArray jsonArray = new JSONArray(result);
				   String[] strArr = new String[jsonArray.length()];

				   		for (int i = 0; i < jsonArray.length(); i++) {
				   				strArr[i] = jsonArray.getString(i);
				      
				   		}
				   		result_list2 = new ArrayList<String>(Arrays.asList(strArr));
				   }
				   
		        }catch (Exception e) {
		                // TODO: handle exception
		                Log.e("log_tag", "Error Parsing Data "+e.toString());
		        }
			 
			 
			
			return null;
		}
		
		protected void onPreExecute() {
			
			

		}
		Marker mk;
	    protected void onPostExecute(Void unused) {
         // NOTE: You can call UI Element here.

         // Close progress dialog
         //Dialog.dismiss();
	    	
	    	
	    if(result.trim().equals("null")){
	    	
	 
	    }else{
	    	ArrayList<String> all=new ArrayList<String>();
	    	for (int i = 0; i < result_list2.size(); i++) {
	    		
	    		String events=result_list2.get(i); 
		    	   
			 	String event_array[]=events.split("\\|");
			 	
			 	if(event_array[0]!="" && event_array[1]!=""){
			 	
			 	all.add("Event: "+event_array[0]+"; "+ event_array[1]+"\n");	
	    		
			 	}
	    	}
	    	txtbroadcast.setBackgroundColor(Color.WHITE);
	    	//txtbroadcast.setTextColor(color.darker_gray);
	    	txtbroadcast.setText(all.toString().substring(1, all.toString().length()-1).replace(",", ""));
			
	    	}   			 
	    }
     }
		
	
}
