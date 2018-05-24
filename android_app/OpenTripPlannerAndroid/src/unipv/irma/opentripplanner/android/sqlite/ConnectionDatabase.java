/**
 * 
 */
package unipv.irma.opentripplanner.android.sqlite;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.util.Log;

/**
 * @author Giovanni Miceli (dev.giovannimiceli@gmail.com)
 *
 */
public class ConnectionDatabase {

	public String getResult(Activity activity, String url, ArrayList<NameValuePair> values) {
		
		InputStream is = null;
		StringBuilder sb = null;
		
		//http post
		try{
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost(url);
		        
		        if (values != null) {
		        	httppost.setEntity(new UrlEncodedFormEntity(values));
		        }
		        
		        httppost.setHeader("User-Agent", "Android");
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();
		}
		
		catch(Exception e)
		{
		        Log.e("log_tag", "Error in http connection "+e.toString());
		}
		
		//convert response to string
		try{
		        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
		        sb = new StringBuilder();
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		                sb.append(line + "\n");
		        }
		        is.close();
		 
		        return sb.toString();
		        
		}catch(Exception e){
		        Log.e("log_tag", "Error converting result "+e.toString());
		}
		
		if (sb == null) {
			// bad connection
			return "";
		} else {
			return sb.toString();
		}
		
	}
}
