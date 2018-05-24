package unipv.irma.opentripplanner.android;

import unipv.irma.opentripplanner.android.sqlite.ConnectionDatabase;
import unipv.irma.opentripplanner.android.util.EncryptPsw;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SignUpActivity extends Activity {

	public static Activity signUp;
	
	ConnectionDatabase connection;
	int status;
	String result;
	String jsonResult;
	
	ProgressDialog progressDialog;
	
	Button btn_next;
	Button btn_back;
	
	EditText et_mail;
	EditText et_password;
	EditText et_username;
	Spinner sp_age;
	Spinner sp_sex;
	Spinner sp_category;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		signUp = this;
		
		// XML Resources
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_back = (Button) findViewById(R.id.btn_back);
		et_mail = (EditText) findViewById(R.id.et_mail);
		et_password = (EditText) findViewById(R.id.et_password);
		et_username = (EditText) findViewById(R.id.et_username);
		sp_age = (Spinner) findViewById(R.id.sp_age);
		sp_sex = (Spinner) findViewById(R.id.sp_sex);
		sp_category = (Spinner) findViewById(R.id.sp_category);

		sp_age.setAdapter(ArrayAdapter.createFromResource(this, R.array.array_age, R.layout.spinner_text ));
		sp_sex.setAdapter(ArrayAdapter.createFromResource(this, R.array.array_sex, R.layout.spinner_text ));
		sp_category.setAdapter(ArrayAdapter.createFromResource(this, R.array.array_category, R.layout.spinner_text ));
		
		// sent data to second page
		btn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (et_mail.getText().toString().equals("") || et_password.getText().toString().equals("")
						|| et_username.getText().toString().equals("") || sp_age.getSelectedItemPosition() == 0 || sp_sex.getSelectedItemPosition() == 0 || sp_category.getSelectedItemPosition() == 0) {
					getDialog(getResources().getString(R.string.compile), false);
				} else {
					Intent i = new Intent (SignUpActivity.this, SignUpTermsActivity.class);
					i.putExtra("password",EncryptPsw.setShaPsw(et_password.getText().toString().trim()));
					i.putExtra("email",et_mail.getText().toString().trim());
					i.putExtra("username",et_username.getText().toString().trim());
					i.putExtra("age",sp_age.getSelectedItemPosition() + "");
					i.putExtra("sex",sp_sex.getSelectedItemPosition() + "");
					i.putExtra("category",sp_category.getSelectedItemPosition() + "");
					
					startActivity(i);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}
			}
		});
		
		btn_back.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				finish();
			}
		});
	}
	
	// **********************************************************************
	
	public void getDialog(String msg, final boolean ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle(getResources().getString(R.string.sign_up_registration_title))
        	   .setMessage(msg)
               .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
           	    		if (ok) {
	                     	Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
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
			Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
