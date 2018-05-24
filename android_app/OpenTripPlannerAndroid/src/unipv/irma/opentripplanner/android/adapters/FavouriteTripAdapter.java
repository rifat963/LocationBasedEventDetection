package unipv.irma.opentripplanner.android.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unipv.irma.opentripplanner.android.DashboardActivity;
import unipv.irma.opentripplanner.android.R;
import unipv.irma.opentripplanner.android.TripFavouriteMapActivity;
import unipv.irma.opentripplanner.android.model.TripFavouriteModel;
import unipv.irma.opentripplanner.android.sqlite.ConnectionDatabase;
import unipv.irma.opentripplanner.android.util.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class FavouriteTripAdapter extends BaseAdapter {

	private Activity activity;
	protected ArrayList<TripFavouriteModel> data;
	private LayoutInflater inflater = null;
	private ViewHolder holder;
	
	ConnectionDatabase connection;
	JSONObject obj;
	int status;
	String result;
	JSONArray dataJson;
	String jsonResult;
	ProgressDialog progressDialog;
	int positionDelete;
	
	static class ViewHolder {
		public ImageButton btn_delete;
		public ImageButton btn_view;
		public TextView txt_from;
		public TextView txt_to;
		public TextView txt_date;
		public TextView txt_time;
		public TextView txt_type;
	}

	public FavouriteTripAdapter(Activity a, ArrayList<TripFavouriteModel> d) {
		this.activity = a;
		this.data = d;
		this.inflater = LayoutInflater.from(activity);

	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public View getView(final int position, final View convertView, ViewGroup parent) {
		View vi = convertView;
		
		if (convertView == null) {
			vi = inflater.inflate(R.layout.trip_favourite_item, null);

			ViewHolder viewHolder = new ViewHolder();

			viewHolder.btn_delete = (ImageButton) vi.findViewById(R.id.btn_delete);
			viewHolder.btn_view = (ImageButton) vi.findViewById(R.id.btn_view);
			viewHolder.txt_from = (TextView) vi.findViewById(R.id.txt_from);
			viewHolder.txt_to = (TextView) vi.findViewById(R.id.txt_to);
			viewHolder.txt_date = (TextView) vi.findViewById(R.id.txt_date);
			viewHolder.txt_time = (TextView) vi.findViewById(R.id.txt_time);
			viewHolder.txt_type = (TextView) vi.findViewById(R.id.txt_type);
			
			vi.setTag(viewHolder);
		}

		holder = (ViewHolder) vi.getTag();

		holder.txt_from.setText(data.get(position).getStart_location());
		holder.txt_to.setText(data.get(position).getEnd_location());
		holder.txt_date.setText(data.get(position).getDate());
		holder.txt_time.setText(data.get(position).getTime());

		// set type data
		try {
		    SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		    Date date = sd.parse(data.get(position).getDate());
			Calendar cal = Calendar.getInstance();
		    cal.setTime(date);
		    int day = cal.get(Calendar.DAY_OF_WEEK);
		    
		    // sunday
		    if (day == 1) {
		    	holder.txt_type.setText(activity.getResources().getString(R.string.holiday));
		    } else
		    // saturday
		    if (day == 7) {
		    	holder.txt_type.setText(activity.getResources().getString(R.string.saturday));
		    } else {
		    	holder.txt_type.setText(activity.getResources().getString(R.string.weekday));
		    }
		    
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
		holder.btn_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				positionDelete = position;
				new LocateTask().execute(position);
			}
		});
		
		holder.btn_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				TripFavouriteModel.getInstance().setStart_lat(data.get(position).getStart_lat());
				TripFavouriteModel.getInstance().setStart_lon(data.get(position).getStart_lon());
				TripFavouriteModel.getInstance().setEnd_lat(data.get(position).getEnd_lat());
				TripFavouriteModel.getInstance().setEnd_lon(data.get(position).getEnd_lon());
				TripFavouriteModel.getInstance().setDate(data.get(position).getDate());
				TripFavouriteModel.getInstance().setTime(data.get(position).getTime());
				TripFavouriteModel.getInstance().setStart_location(data.get(position).getStart_location());
				TripFavouriteModel.getInstance().setEnd_location(data.get(position).getEnd_location());
				
				Intent i = new Intent(activity, TripFavouriteMapActivity.class);
            	activity.startActivity(i);
            	activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            	activity.finish();
			}
		});
		
		return vi;
	}
	
	// **********************************************************************

	// AsyncTask
	private class LocateTask extends AsyncTask<Integer, Void, Integer> 
	{	
	    @Override
	    protected void onPreExecute()
	    {
	    	super.onPreExecute();  
	    	progressDialog = ProgressDialog.show(activity,"",activity.getResources().getString(R.string.deleting));
            progressDialog.setCancelable(false);
	    	connection = new ConnectionDatabase();
	    }

	    @Override
	    protected Integer doInBackground(Integer... position) 
	    {
	    	String url = Constants.PATH_SERVER + Constants.PATH_DELETE_FAVOURITE_TRIP;
	    	ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
	    	values.add(new BasicNameValuePair("id",data.get(position[0]).getId() + ""));

	    	jsonResult = connection.getResult(activity, url, values);
			
	    	//parse json data
	    	try{
	    		obj = new JSONObject(jsonResult);
	    		status = obj.getInt("status");
	    		result = obj.getString("result");
	    	}catch(JSONException e){
	    		Log.e("log_tag", "Error parsing data "+e.toString());
	    	}
	    	
	    	return position[0]; 				         
	    }
	    
	    @Override
	    protected void onPostExecute(Integer number)
	    {
	    	super.onPostExecute(number); 
	    	progressDialog.cancel();
	    	
	    	// connection OK
	    	if (status == 200) {
	    		// registration DONE
	    		if (result.equals("OK")){
	    			data.remove(positionDelete);
	    			notifyDataSetChanged();
	    		} 
	    		else {
		    		getDialog(activity.getResources().getString(R.string.trip_favourite_fail), false);
	    		}
	    	} 
	    	
	    	// connecion KO
	    	else {
	    		getDialog(activity.getResources().getString(R.string.registration_fail_internet), false);
	    	}
	    }
	}
	
	public void getDialog(String msg, final boolean ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.activity_trip_favourite))
        	   .setMessage(msg)
               .setNegativeButton(activity.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                    	Intent i = new Intent(activity, DashboardActivity.class);
                    	activity.startActivity(i);
                    	activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    	activity.finish();
    				}
               });
        builder.create();
        builder.show();
	}


}