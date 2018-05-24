package unipv.irma.opentripplanner.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unipv.irma.opentripplanner.android.adapters.FeedbackAdapter;
import unipv.irma.opentripplanner.android.model.FeedbackModel;
import unipv.irma.opentripplanner.android.model.UserModel;
import unipv.irma.opentripplanner.android.sqlite.ConnectionDatabase;
import unipv.irma.opentripplanner.android.util.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class FeedbackActivity extends Activity {
	InputMethodManager imm;
	
	ConnectionDatabase connection;
	JSONObject obj;
	int status;
	String result;
	String jsonResult;
	JSONArray data;

	ProgressDialog progressDialog;
	
	ImageButton btn_back;
	ImageButton btn_update;
	Button btn_send;
	EditText et_feedback;
	PullToRefreshListView lv;

	int last_id;
	int old_id;
	String param = "";
	
	ArrayList<FeedbackModel> listFeedback;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

	    imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 

		// XML Resources
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_update = (ImageButton) findViewById(R.id.btn_update);
		btn_send = (Button) findViewById(R.id.btn_send);
		et_feedback = (EditText) findViewById(R.id.et_feedback);
		lv = (PullToRefreshListView) findViewById(R.id.list);

		listFeedback = new ArrayList<FeedbackModel>();

		// getFeedback count 10
		getFeedback();
		
		lv.setMode(Mode.BOTH);
		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// imposto la scritta quando scrollo la listview prima di aggiornare
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				// controllo se sto facendo un PULL FROM END, quindi cerco i commenti piu vecchi in fondo alla lista
				// oppure se sto cercando commenti nuovi con PULL FROM START
				if (refreshView.getCurrentMode().name().equals("PULL_FROM_END")){
					getOldFeedback();
					}
				else {
					getNewFeedback();
					}
			}
		});
		
		btn_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listFeedback = new ArrayList<FeedbackModel>();
				getFeedback();
	    	}
		});
		
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(FeedbackActivity.this, DashboardActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				finish();
			}
		});
		
		btn_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (et_feedback.getText().toString().equals("")) {
					getDialog(getResources().getString(R.string.compile), false);
				} else {
					new LocateTask().execute();
				}
			}
		});
	}

	// **********************************************************************

	// getFeedback first time
	public void getFeedback(){
		new LocateTaskFeedback().execute();
	}
	// getNewFeedback
	public void getNewFeedback(){
		param = "last_id";
		new LocateTaskFeedback().execute();
	}
	// getOldFeedback
	public void getOldFeedback(){
		param = "old_id";
		new LocateTaskFeedback().execute();
	}
	
	// **********************************************************************

	// AsyncTask
	private class LocateTask extends AsyncTask<Void, Void, Void> 
	{	
	    @Override
	    protected void onPreExecute()
	    {
	    	super.onPreExecute();  
	    	// close keyboard
			imm.hideSoftInputFromWindow(et_feedback.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

	    	progressDialog = ProgressDialog.show(FeedbackActivity.this,"",getResources().getString(R.string.processing));
            progressDialog.setCancelable(false);
	    	connection = new ConnectionDatabase();
	    }

	    @Override
	    protected Void doInBackground(Void... params) 
	    {
	    	String timestamp = System.currentTimeMillis() / 1000L + "";
	    	    	
	    	String url = Constants.PATH_SERVER + Constants.PATH_INSERT_FEEDBACK;
	    	ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
	    	values.add(new BasicNameValuePair("user_id",UserModel.getInstance().getId() + ""));
	    	values.add(new BasicNameValuePair("username",UserModel.getInstance().getUsername()));
	    	values.add(new BasicNameValuePair("sex",UserModel.getInstance().getSex()));
	    	values.add(new BasicNameValuePair("timestamp",timestamp));
	    	values.add(new BasicNameValuePair("text",et_feedback.getText().toString().trim()));

	    	jsonResult = connection.getResult(FeedbackActivity.this, url, values);
			
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
	    		// DONE
	    		param = "last_id";
	    		new LocateTaskFeedback().execute();
	    		et_feedback.setText("");
	    	} 
	    	
	    	// connecion KO
	    	else {
	    		getDialog(getResources().getString(R.string.registration_fail_internet), false);
	    	}
	    }
	}
	
	// AsyncTaskFeedback
	private class LocateTaskFeedback extends AsyncTask<Void, Void, Void> 
	{	
	    @Override
	    protected void onPreExecute()
	    {
	    	super.onPreExecute(); 
	    	if (param.equals("")) {
		    	progressDialog = ProgressDialog.show(FeedbackActivity.this,"",getResources().getString(R.string.processing));
	            progressDialog.setCancelable(false);
	    	} 
	    	connection = new ConnectionDatabase();
	    }

	    @Override
	    protected Void doInBackground(Void... params) 
	    {		  
	    	String url = Constants.PATH_SERVER + Constants.PATH_GET_FEEDBACK;
	    	// no param
	    	if (param.equals("")) {
		    	jsonResult = connection.getResult(FeedbackActivity.this, url, null);
	    	} else 
	    	if (param.equals("last_id")) {
		    	ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
		    	values.add(new BasicNameValuePair("last_id",last_id + ""));
		    	jsonResult = connection.getResult(FeedbackActivity.this, url, values);
	    	} else {
		    	ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
		    	values.add(new BasicNameValuePair("old_id", old_id + ""));
		    	jsonResult = connection.getResult(FeedbackActivity.this, url, values);
	    	}
	    	
			
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
	    	
	    	// connection OK
	    	if (status == 200) {
	    		// DONE
    			try {
					data = obj.getJSONArray("data");
					int count = data.length();
					int countOldList = listFeedback.size();
					
					for (int i = 0; i < data.length(); i++) {
						FeedbackModel feedbackModel = new FeedbackModel();
						JSONObject objTrip = data.getJSONObject(i);
						
						feedbackModel.setId(objTrip.getInt("id"));
						feedbackModel.setUser_id(objTrip.getString("user_id"));
						feedbackModel.setUsername(objTrip.getString("username"));
						feedbackModel.setSex(objTrip.getString("sex"));
						feedbackModel.setTimestamp(objTrip.getString("timestamp"));
						feedbackModel.setText(objTrip.getString("text"));
						feedbackModel.setReply(objTrip.getString("reply"));

						listFeedback.add(feedbackModel);
					}
					
					Collections.sort(listFeedback, new CustomComparatorFeedback());
					
					FeedbackAdapter adapter = new FeedbackAdapter(FeedbackActivity.this, listFeedback);
		    		// popolo la listview
		    		lv.setAdapter(adapter);
		    		adapter.notifyDataSetChanged();
		    		lv.onRefreshComplete();
		    		
			    	if (param.equals("")) {
				    	progressDialog.cancel();
				    	old_id = data.getJSONObject(count-1).getInt("id");
				    	last_id = data.getJSONObject(0).getInt("id");
					} else 
			    	if (param.equals("last_id")) {
			    		param = "";
		                lv.getRefreshableView().setSelection(0);
			    		last_id = data.getJSONObject(count-1).getInt("id");
			    	} else {
			    		param = "";
		                lv.getRefreshableView().setSelection(countOldList);
			    		old_id = data.getJSONObject(count-1).getInt("id");
			    	}
			    	
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	} 
	    	// connecion KO
	    	else {
	    		getDialog(getResources().getString(R.string.registration_fail_internet), false);
	    	}
	    }
	}
		
	public void getDialog(String msg, final boolean ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
        builder.setTitle(getResources().getString(R.string.activity_feedback))
        	   .setMessage(msg)
               .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
           	    		
    				}
               });
        builder.create();
        builder.show();
	}

	public class CustomComparatorFeedback implements Comparator<FeedbackModel> {
	    @Override
	    public int compare(FeedbackModel p1, FeedbackModel p2) {
	        Integer obj1 = new Integer(p2.getId());
	        Integer obj2 = new Integer(p1.getId());
	        return obj1.compareTo(obj2);
	        
	    }

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
			Intent i = new Intent(FeedbackActivity.this, DashboardActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
