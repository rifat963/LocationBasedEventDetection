/*
 * Copyright 2012 University of South Florida
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package unipv.irma.opentripplanner.android;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.opentripplanner.v092snapshot.api.model.Itinerary;
import org.opentripplanner.v092snapshot.api.model.Leg;

import unipv.irma.opentripplanner.android.fragments.DirectionListFragment;
import unipv.irma.opentripplanner.android.fragments.MainFragment;
import unipv.irma.opentripplanner.android.fragments.TripMonitoredFragment;
import unipv.irma.opentripplanner.android.listeners.DateCompleteListener;
import unipv.irma.opentripplanner.android.listeners.OnFragmentListener;
import unipv.irma.opentripplanner.android.model.OTPBundle;
import unipv.irma.opentripplanner.android.util.Constants;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * Main Activity for the OTP for Android app
 * 
 * @author Khoa Tran
 * @author Sean Barbeau (conversion to Jackson)
 * @author Vreixo Gonz�lez (update to Google Maps API v2, UI and general app improvements)
 * 
 */

public class TripMonitoredMapActivity extends FragmentActivity implements OnFragmentListener{

	private List<Leg> currentItinerary = new ArrayList<Leg>();
	
	private List<Itinerary> currentItineraryList = new ArrayList<Itinerary>();
	
	private int currentItineraryIndex = -1;

	private OTPBundle bundle = null;

	private TripMonitoredFragment mainFragment;

	private String TAG = "OTP";
	
	private String currentRequestString="";
	
	private boolean isButtonStartLocation = false;
	
	DateCompleteListener dateCompleteCallback;
	
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);

	//	bundle = (OTPBundle)getLastCustomNonConfigurationInstance();
		setContentView(R.layout.activity);

		if (savedInstanceState != null) {
			mainFragment = (TripMonitoredFragment) getSupportFragmentManager().findFragmentByTag(OTPApp.TAG_FRAGMENT_MAIN_FRAGMENT);//recuperar o tag adecuado e pillar ese fragment
			
	    }
		
		if(savedInstanceState==null){
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			mainFragment = new TripMonitoredFragment();
			fragmentTransaction.replace(R.id.mainFragment, mainFragment, OTPApp.TAG_FRAGMENT_MAIN_FRAGMENT);
			fragmentTransaction.commit();
		}
		
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data)
//	{
//		switch(requestCode) {
//		case OTPApp.SETTINGS_REQUEST_CODE: 
//			if (resultCode == RESULT_OK) {
//				boolean shouldRefresh = data.getBooleanExtra(OTPApp.REFRESH_SERVER_RETURN_KEY, false);
//				boolean changedSelectedCustomServer = data.getBooleanExtra(OTPApp.CHANGED_SELECTED_CUSTOM_SERVER_RETURN_KEY, false);
//				boolean changedTileProvider = data.getBooleanExtra(OTPApp.CHANGED_MAP_TILE_PROVIDER_RETURN_KEY, false);
//
//				//				Toast.makeText(this, "Should server list refresh? " + shouldRefresh, Toast.LENGTH_LONG).show();
//				if(shouldRefresh){
//					mainFragment.setNeedToRunAutoDetect(true);
//					mainFragment.setNeedToUpdateServersList(true);
//				}
//				if(changedSelectedCustomServer){
//					mainFragment.updateSelectedServer();
//				}
//				if(changedTileProvider){
//					mainFragment.updateOverlay(null);
//				}
//				break;
//			}
//		case OTPApp.CHOOSE_CONTACT_REQUEST_CODE:
//			if(resultCode == RESULT_OK){
//				Log.v(TAG, "CHOOSE CONTACT RESULT OK");
//				
//				Uri contactData = data.getData();
//				Cursor c =  managedQuery(contactData, null, null, null, null);
//				if (c.moveToFirst()) {
//				    String address = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
//				    mainFragment.setTextBoxLocation(address, isButtonStartLocation);
//				    mainFragment.processAddress(isButtonStartLocation, address, false);
//				}
//				
//				break;
//			}
//		}
//	}
	
	@Override
	protected void onDestroy() {		
		
		mainFragment = null;
		
		Log.d(TAG, "Released mainFragment with map in MyActivity.onDestroy()");
		
		super.onDestroy();
	}
	
	@Override
	public void onItinerariesLoaded(List<Itinerary> itineraries) {
		// TODO Auto-generated method stub
		currentItineraryList.clear();
		currentItineraryList.addAll(itineraries);
	}

	@Override
	public void onItinerarySelected(int i) {
		// TODO Auto-generated method stub
		if(i >= currentItineraryList.size()) return;
		
		currentItineraryIndex = i;
		currentItinerary.clear();
		currentItinerary.addAll(currentItineraryList.get(i).legs);
	}

	@Override
	public List<Leg> getCurrentItinerary() {
		// TODO Auto-generated method stub
		return currentItinerary;
	}

	@Override
	public void onSwitchedToDirectionFragment() {
		// TODO Auto-generated method stub
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		Fragment directionFragment = new DirectionListFragment();
		transaction.add(R.id.mainFragment, directionFragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.addToBackStack(null);

		transaction.commit();
	}

	@Override
	public OTPBundle getOTPBundle() {
		// TODO Auto-generated method stub
		return bundle;
	}

	@Override
	public void setOTPBundle(OTPBundle b) {
		// TODO Auto-generated method stub
		this.bundle = b;
		this.bundle.setCurrentItineraryIndex(currentItineraryIndex);
		this.bundle.setItineraryList(currentItineraryList);
	}

	@Override
	public void onSwitchedToMainFragment(Fragment f) {
		// TODO Auto-generated method stub
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.remove(f);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fm.popBackStack();
		transaction.commit();
		
		mainFragment.showRouteOnMap(currentItinerary, true);
	}

	@Override
	public void setCurrentRequestString(String url) {
		currentRequestString = url;
	}

	@Override
	public String getCurrentRequestString() {
		return currentRequestString;
	}
	
	@Override
	public void zoomToLocation(LatLng location){
		mainFragment.zoomToLocation(location);
	}

	@Override
	public List<Itinerary> getCurrentItineraryList() {
		// TODO Auto-generated method stub
		return currentItineraryList;
	}

	@Override
	public int getCurrentItineraryIndex() {
		// TODO Auto-generated method stub
		return currentItineraryIndex;
	}

	/**
	 * @return the isButtonStartLocation
	 */
	public boolean isButtonStartLocation() {
		return isButtonStartLocation;
	}

	/**
	 * @param isButtonStartLocation the isButtonStartLocation to set
	 */
	public void setButtonStartLocation(boolean isButtonStartLocation) {
		this.isButtonStartLocation = isButtonStartLocation;
	}
	
	public void setDateCompleteCallback(DateCompleteListener callback){
		this.dateCompleteCallback = callback;
	}

	public void onDateComplete(Date tripDate, boolean scheduleType) {
		dateCompleteCallback.onDateComplete(tripDate, scheduleType);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			
			
			
			Intent i = new Intent (TripMonitoredMapActivity.this, TripMonitoredActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
}
