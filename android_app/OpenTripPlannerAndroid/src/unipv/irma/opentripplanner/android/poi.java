/**
 * 
 */
package unipv.irma.opentripplanner.android;

import unipv.irma.opentripplanner.*;
import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

import br.com.condesales.EasyFoursquareAsync;


import br.com.condesales.criterias.TipsCriteria;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.ImageRequestListener;
import br.com.condesales.listeners.TipsResquestListener;
import br.com.condesales.listeners.UserInfoRequestListener;
import br.com.condesales.models.Tip;
import br.com.condesales.models.User;
import br.com.condesales.tasks.users.UserImageRequest;

/**
 * @author rifat
 *
 */
public class poi extends Activity implements AccessTokenRequestListener, ImageRequestListener
{
	 private EasyFoursquareAsync async;
	    private ImageView userImage;
	    private ViewSwitcher viewSwitcher;
	    private TextView userName;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.point_of_interest);
	    userImage = (ImageView) findViewById(R.id.imageView1);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
        userName = (TextView) findViewById(R.id.textView1);
        //ask for access
        async = new EasyFoursquareAsync(this);
        async.requestAccess(this);
	}
	
	
	private void requestTipsNearby(String accessToken) {
        Location loc = new Location("");
        loc.setLatitude(45.1958975);
        loc.setLongitude(9.1558951);

        TipsCriteria criteria = new TipsCriteria();
        criteria.setLocation(loc);
        async.getTipsNearby(new TipsResquestListener() {

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(poi.this, "error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTipsFetched(ArrayList<Tip> tips) {
            	for(int i=0;i<tips.size();i++)
            	{
            	String test = tips.get(i).getVenue().getName()+"";
                Toast.makeText(poi.this, test, Toast.LENGTH_LONG).show();
            	}
            }
            }, criteria);
        }
	
	
	
	    @Override
	    public void onAccessGrant(String accessToken) {
	        // with the access token you can perform any request to foursquare.
	        // example:
	        async.getUserInfo(new UserInfoRequestListener() {

	            @Override
	            public void onError(String errorMsg) {
	                // Some error getting user info
	            	
	                Toast.makeText(poi.this, errorMsg, Toast.LENGTH_LONG)
	                        .show();
	            }

	            @Override
	            public void onUserInfoFetched(User user) {
	                // OWww. did i already got user!?
	                if (user.getBitmapPhoto() == null) {
	                    UserImageRequest request = new UserImageRequest(
	                            poi.this, poi.this);
	                    request.execute(user.getPhoto());
	                } else {
	                    userImage.setImageBitmap(user.getBitmapPhoto());
	                }
	                userName.setText(user.getFirstName() + " " + user.getLastName());
	                viewSwitcher.showNext();
	                Toast.makeText(poi.this, "Got it!", Toast.LENGTH_LONG)
	                        .show();
	            }
	        });

	        //for another example uncomment line below:
	        requestTipsNearby(accessToken);
	    }

	/* (non-Javadoc)
	 * @see br.com.condesales.listeners.ErrorListener#onError(java.lang.String)
	 */
	@Override
	public void onError(String errorMsg) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see br.com.condesales.listeners.ImageRequestListener#onImageFetched(android.graphics.Bitmap)
	 */
	@Override
	public void onImageFetched(Bitmap bmp) {
		// TODO Auto-generated method stub
		 userImage.setImageBitmap(bmp);
	}

	/* (non-Javadoc)
	 * @see br.com.condesales.listeners.AccessTokenRequestListener#onAccessGrant(java.lang.String)
	 */
	
	
	
	

}
