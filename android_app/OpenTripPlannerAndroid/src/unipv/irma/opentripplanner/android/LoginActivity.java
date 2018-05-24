package unipv.irma.opentripplanner.android;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import unipv.irma.opentripplanner.android.model.UserModel;
import unipv.irma.opentripplanner.android.sqlite.ConnectionDatabase;
import unipv.irma.opentripplanner.android.util.Constants;
import unipv.irma.opentripplanner.android.util.EncryptPsw;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
//import static unipv.irma.opentripplanner.gcm.CommonUtilities.SENDER_ID;

public class LoginActivity extends Activity {

	ConnectionDatabase connection;
	JSONObject obj;
	int status;
	String result;
	JSONObject data;
	String jsonResult;
	
	ProgressDialog progressDialog;

	Button btn_login;
	Button btn_sign_up;
	EditText et_mail;
	EditText et_password;
	TextView txt_forgot_psw;
	
	SharedPreferences sharedPref;
	

	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "C4RP55SBWYYT9W9R7ZF2");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
			
		// XML Resources
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_sign_up = (Button) findViewById(R.id.btn_sign_up);
		et_mail = (EditText) findViewById(R.id.et_mail);
		et_password = (EditText) findViewById(R.id.et_password);
		txt_forgot_psw = (TextView) findViewById(R.id.txt_forgot_psw);
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String username = sharedPref.getString("username", "");
		String password = sharedPref.getString("psw", "");
		
		et_mail.setText(username);
		et_password.setText(password);

		if (!password.equals("")) {
			new LocateTask().execute();
		} 
		
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

				if (et_mail.getText().toString().equals("") || et_password.getText().toString().equals("")) {
					getDialog(getResources().getString(R.string.compile), false);
				} else {
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putString("username", et_mail.getText().toString());
					editor.putString("psw", et_password.getText().toString());
					editor.commit();

					
					new LocateTask().execute();
				}
			}
		});
		
		btn_sign_up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				finish();
			}
		});
		
		txt_forgot_psw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(LoginActivity.this, ForgotPswActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				finish();
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
	    	progressDialog = ProgressDialog.show(LoginActivity.this,"",getResources().getString(R.string.login_connecting));
            progressDialog.setCancelable(false);
	    	connection = new ConnectionDatabase();
	    }

	    @Override
	    protected Void doInBackground(Void... params) 
	    {
	    	String url = Constants.PATH_SERVER + Constants.PATH_GET_USER;
	    	ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
	    	values.add(new BasicNameValuePair("email",et_mail.getText().toString().trim()));
	    	values.add(new BasicNameValuePair("password",EncryptPsw.setShaPsw(et_password.getText().toString().trim())));

	    	jsonResult = connection.getResult(LoginActivity.this, url, values);
			
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
						data = obj.getJSONObject("data");
						UserModel.getInstance().setId(data.getInt("id"));
						UserModel.getInstance().setPassword(et_password.getText().toString().trim());
						UserModel.getInstance().setEmail(data.getString("email"));
						UserModel.getInstance().setUsername(data.getString("username"));
						UserModel.getInstance().setSex(data.getString("sex"));
						UserModel.getInstance().setAge(data.getString("age"));
						UserModel.getInstance().setCategory(data.getString("category"));
		    			
						Map<String, String> articleParams = new HashMap<String, String>();
				        articleParams.put("Username", data.getString("username")); // Capture author info
				        articleParams.put("Sex", data.getString("sex")); // Capture user status
				        articleParams.put("Age", data.getString("age")); // Capture user status
				        articleParams.put("Category", data.getString("category")); // Capture user status

				        FlurryAgent.logEvent("Info Events", articleParams);
				
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			
	    			Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
					startActivity(i);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					finish();
	    		} 
	    		// userId or Email not exist!
	    		else {
		    		getDialog(getResources().getString(R.string.login_fail), false);
	    		}
	    	} 
	    	
	    	// connecion KO
	    	else {
	    		getDialog(getResources().getString(R.string.registration_fail_internet), false);
	    	}
	    }
	}
	
	public void getDialog(String msg, final boolean ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(getResources().getString(R.string.login_login))
        	   .setMessage(msg)
               .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
           	    		if (ok) {
	                     	Intent i = new Intent(LoginActivity.this, LoginActivity.class);
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
	
	
	
}
