package unipv.irma.opentripplanner.android.adapters;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import unipv.irma.opentripplanner.android.R;
import unipv.irma.opentripplanner.android.model.FeedbackModel;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FeedbackAdapter extends BaseAdapter {

	private Activity activity;
	protected ArrayList<FeedbackModel> data;
	private LayoutInflater inflater = null;
	private ViewHolder holder;
	
	static class ViewHolder {
		public ImageView img_user;
		public TextView txt_username;
		public TextView txt_date_time;
		public TextView txt_message;
		public LinearLayout ll_admin;
		public TextView txt_admin_message;
	}

	public FeedbackAdapter(Activity a, ArrayList<FeedbackModel> d) {
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
	
	@SuppressWarnings("static-access")
	public View getView(final int position, View convertView, ViewGroup parent) {		
		
		ViewHolder viewHolder;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.feedback_item, null);

			viewHolder = new ViewHolder();

			viewHolder.img_user = (ImageView) convertView.findViewById(R.id.img_user);
			viewHolder.txt_username = (TextView) convertView.findViewById(R.id.txt_username);
			viewHolder.txt_date_time = (TextView) convertView.findViewById(R.id.txt_date_time);
			viewHolder.txt_message = (TextView) convertView.findViewById(R.id.txt_message);
			viewHolder.ll_admin = (LinearLayout) convertView.findViewById(R.id.ll_admin);
			viewHolder.txt_admin_message = (TextView) convertView.findViewById(R.id.txt_admin_message);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String sex = data.get(position).getSex();
	
		// create data and time from timestamp
		
		Calendar cal = Calendar.getInstance();  
		Date d = new Date(Long.parseLong(data.get(position).getTimestamp()) * 1000);
	    
	    String time = getTime(cal, d);
	    
	    cal.setTime(d);
	    String date = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);

	    viewHolder.img_user.setImageResource (sex.equals("1") ? R.drawable.icon_female : R.drawable.icon_male);
	    viewHolder.txt_username.setText(data.get(position).getUsername());
	    viewHolder.txt_date_time.setText(date + " - " + time);
	    viewHolder.txt_message.setText(data.get(position).getText());
		
		if (!data.get(position).getReply().equals("null")) {
			viewHolder.ll_admin.setVisibility(convertView.VISIBLE);
			viewHolder.txt_admin_message.setText(data.get(position).getReply());
		} else {
			viewHolder.ll_admin.setVisibility(convertView.GONE);
			viewHolder.txt_admin_message.setText("");
		}
		return convertView;
	}
	
	// trasformo l'ora in 00:00
	private String getTime(Calendar time, Date date) {
		time.setTime(date);
		
	    StringBuffer timestamp = new StringBuffer();  
	    if (time.get(Calendar.HOUR_OF_DAY) >= 10)  
	        timestamp.append(time.get(Calendar.HOUR_OF_DAY));  
	    else  
	        timestamp.append("0" + time.get(Calendar.HOUR_OF_DAY));  
	      
	    timestamp.append(":");  
	      
	    if (time.get(Calendar.MINUTE) >= 10)  
	        timestamp.append(time.get(Calendar.MINUTE));  
	    else  
	        timestamp.append("0" + time.get(Calendar.MINUTE));  
	    
	    return timestamp.toString();
	}


}