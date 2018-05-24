package unipv.irma.opentripplanner.android;

import java.util.ArrayList;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPswActivity extends Activity {
	Button btn_back;
	Button btn_send;
	EditText et_mail;
	
	ProgressDialog progressDialog;
	ConnectionDatabase connection;
	int status;
	String result;
	String jsonResult;
	
	String email;
	String password;
	String sha1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_psw);

		// XML Resources
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_send = (Button) findViewById(R.id.btn_send);
		et_mail = (EditText) findViewById(R.id.et_mail);
		
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ForgotPswActivity.this, LoginActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				finish();
			}
		});
		
		btn_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (et_mail.getText().toString().equals("")) {
					getDialog(getResources().getString(R.string.compile), false);
				} else {	
					new LocateTask().execute();
				}
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
	    	progressDialog = ProgressDialog.show(ForgotPswActivity.this,"",getResources().getString(R.string.forgot_sending));
            progressDialog.setCancelable(false);
	    	connection = new ConnectionDatabase();
	    }

	    @Override
	    protected Void doInBackground(Void... params) 
	    {
	    	email = et_mail.getText().toString().trim();
	    	password = generateRandomWords();
	    	sha1 = EncryptPsw.setShaPsw(password);
	    	
	    	String url = Constants.PATH_SERVER + Constants.PATH_GENERATE_PSW;
	    	ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
	    	values.add(new BasicNameValuePair("email",email));
	    	values.add(new BasicNameValuePair("sha1",sha1));
	    	values.add(new BasicNameValuePair("password",password));

	    	jsonResult = connection.getResult(ForgotPswActivity.this, url, values);
			
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
	    	
	    	// connection OK
	    	if (status == 200) {
	    		// registration DONE
	    		if (result.equals("OK")){
		    		getDialog(getResources().getString(R.string.forgot_done), true);
	    		} 
	    		// userId or Email exist!
	    		else {
		    		getDialog(getResources().getString(R.string.forgot_fail), false);
	    		}
	    	} 
	    	
	    	// connecion KO
	    	else {
	    		getDialog(getResources().getString(R.string.registration_fail_internet), false);
	    	}
	    }
	}
	
	public void getDialog(String msg, final boolean ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPswActivity.this);
        builder.setTitle(getResources().getString(R.string.forgot_title))
        	   .setMessage(msg)
               .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
           	    		if (ok) {       					
           	    			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        					SharedPreferences.Editor editor = sharedPref.edit();
        					editor.putString("username", email);
        					editor.putString("psw", "");
        					editor.commit();
        					
           	    			Intent i = new Intent(ForgotPswActivity.this, LoginActivity.class);
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
	
	// generate password 5 letters
	public static String generateRandomWords()
	{
	    Random random = new Random();
	    StringBuilder sb = new StringBuilder();
	    
	    char[] word = new char[random.nextInt(8)+5]; 
	    for(int j = 0; j < word.length; j++)
	    {
	        word[j] = (char)('a' + random.nextInt(26));
	        sb.append(word[j]);
	    }
	    return sb.toString();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent i = new Intent(ForgotPswActivity.this, LoginActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
