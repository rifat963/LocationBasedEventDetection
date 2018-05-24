package unipv.irma.opentripplanner.android;




import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class AccountActivity extends Activity {

	ConnectionDatabase connection;
	JSONObject obj;
	int status;
	String result;
	JSONObject data;
	String jsonResult;
	
	ProgressDialog progressDialog;
	
	Button btn_save;
	ImageButton btn_back;

	EditText et_mail;
	EditText et_password;
	EditText et_username;
	Spinner sp_age;
	Spinner sp_sex;
	Spinner sp_category;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		// XML Resources
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		et_mail = (EditText) findViewById(R.id.et_mail);
		et_password = (EditText) findViewById(R.id.et_password);
		et_username = (EditText) findViewById(R.id.et_username);
		sp_age = (Spinner) findViewById(R.id.sp_age);
		sp_sex = (Spinner) findViewById(R.id.sp_sex);
		sp_category = (Spinner) findViewById(R.id.sp_category);
		
		// set Resources
		et_mail.setText(UserModel.getInstance().getEmail());
		et_password.setText(UserModel.getInstance().getPassword());
		et_username.setText(UserModel.getInstance().getUsername());
		
		sp_age.setAdapter(ArrayAdapter.createFromResource(this, R.array.array_age, R.layout.spinner_text ));
		sp_sex.setAdapter(ArrayAdapter.createFromResource(this, R.array.array_sex, R.layout.spinner_text ));
		sp_category.setAdapter(ArrayAdapter.createFromResource(this, R.array.array_category, R.layout.spinner_text ));
		sp_age.setSelection(Integer.parseInt(UserModel.getInstance().getAge()));
		sp_sex.setSelection(Integer.parseInt(UserModel.getInstance().getSex()));
		sp_category.setSelection(Integer.parseInt(UserModel.getInstance().getCategory()));
		
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(AccountActivity.this, DashboardActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				finish();
			}
		});
		
		// sent data to mySql server to registration
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

				if (et_mail.getText().toString().equals("") || et_password.getText().toString().equals("")) {
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
	    	progressDialog = ProgressDialog.show(AccountActivity.this,"",getResources().getString(R.string.account_changing));
            progressDialog.setCancelable(false);
	    	connection = new ConnectionDatabase();
	    }

	    @Override
	    protected Void doInBackground(Void... params) 
	    {
	    	String timestamp = System.currentTimeMillis() / 1000L + "";

	    	String url = Constants.PATH_SERVER + Constants.PATH_EDIT_USER;
	    	ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
	    	values.add(new BasicNameValuePair("id",UserModel.getInstance().getId() + ""));
	    	values.add(new BasicNameValuePair("password",EncryptPsw.setShaPsw(et_password.getText().toString().trim())));
	    	values.add(new BasicNameValuePair("email",et_mail.getText().toString().trim()));
	    	values.add(new BasicNameValuePair("username",et_username.getText().toString().trim()));
	    	values.add(new BasicNameValuePair("age",sp_age.getSelectedItemPosition() + ""));
	    	values.add(new BasicNameValuePair("sex",sp_sex.getSelectedItemPosition() + ""));
	    	values.add(new BasicNameValuePair("category",sp_category.getSelectedItemPosition() + ""));
	    	values.add(new BasicNameValuePair("last_update",timestamp));

	    	jsonResult = connection.getResult(AccountActivity.this, url, values);
			
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
					try {
						data = obj.getJSONObject("data");
						UserModel.getInstance().setId(data.getInt("id"));
						UserModel.getInstance().setPassword(et_password.getText().toString().trim());
						UserModel.getInstance().setEmail(data.getString("email"));
						UserModel.getInstance().setUsername(data.getString("username"));
						UserModel.getInstance().setSex(data.getString("sex"));
						UserModel.getInstance().setAge(data.getString("age"));
						UserModel.getInstance().setCategory(data.getString("category"));
						
						SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putString("username", et_mail.getText().toString());
						editor.putString("psw", et_password.getText().toString());
						editor.commit();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			getDialog(getResources().getString(R.string.account_change_done), true);
	    		} 
	    		// userId exist!
	    		else {
		    		getDialog(getResources().getString(R.string.account_change_fail), false);
	    		}
	    	} 
	    	
	    	// connecion KO
	    	else {
	    		getDialog(getResources().getString(R.string.registration_fail_internet), false);
	    	}
	    }
	}
	
	public void getDialog(String msg, final boolean ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        builder.setTitle(getResources().getString(R.string.activity_account))
        	   .setMessage(msg)
               .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
           	    		if (ok) {
	                     	Intent i = new Intent(AccountActivity.this, DashboardActivity.class);
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
			Intent i = new Intent(AccountActivity.this, DashboardActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
