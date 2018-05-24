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

package unipv.irma.opentripplanner.android.fragments;
import static unipv.irma.opentripplanner.android.OTPApp.PREFERENCE_KEY_CUSTOM_SERVER_BOUNDS;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.miscwidgets.widget.Panel;
import org.opentripplanner.api.ws.GraphMetadata;
import org.opentripplanner.api.ws.Request;
import org.opentripplanner.routing.core.OptimizeType;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.core.TraverseModeSet;
import org.opentripplanner.v092snapshot.api.model.Itinerary;
import org.opentripplanner.v092snapshot.api.model.Leg;

import unipv.irma.opentripplanner.android.DashboardActivity;
import unipv.irma.opentripplanner.android.MyActivity;
import unipv.irma.opentripplanner.android.OTPApp;
import unipv.irma.opentripplanner.android.R;
import unipv.irma.opentripplanner.android.SettingsActivity;
import unipv.irma.opentripplanner.android.TripMonitoredMapActivity;
import unipv.irma.opentripplanner.android.listeners.DateCompleteListener;
import unipv.irma.opentripplanner.android.listeners.MetadataRequestCompleteListener;
import unipv.irma.opentripplanner.android.listeners.OTPGeocodingListener;
import unipv.irma.opentripplanner.android.listeners.OnFragmentListener;
import unipv.irma.opentripplanner.android.listeners.ServerSelectorCompleteListener;
import unipv.irma.opentripplanner.android.listeners.TripRequestCompleteListener;
import unipv.irma.opentripplanner.android.maps.MyUrlTileProvider;
import unipv.irma.opentripplanner.android.model.OTPBundle;
import unipv.irma.opentripplanner.android.model.OptimizeSpinnerItem;
import unipv.irma.opentripplanner.android.model.Server;
import unipv.irma.opentripplanner.android.model.ShowRouteModel;
import unipv.irma.opentripplanner.android.model.TraverseModeSpinnerItem;
import unipv.irma.opentripplanner.android.model.TripMonitoredModel;
import unipv.irma.opentripplanner.android.model.UserModel;
import unipv.irma.opentripplanner.android.sqlite.ConnectionDatabase;
import unipv.irma.opentripplanner.android.sqlite.ServersDataSource;
import unipv.irma.opentripplanner.android.tasks.MetadataRequest;
import unipv.irma.opentripplanner.android.tasks.OTPGeocoding;
import unipv.irma.opentripplanner.android.tasks.ServerSelector;
import unipv.irma.opentripplanner.android.tasks.TripRequest;
import unipv.irma.opentripplanner.android.util.Constants;
import unipv.irma.opentripplanner.android.util.DateTimeConversion;
import unipv.irma.opentripplanner.android.util.DateTimeDialog;
import unipv.irma.opentripplanner.android.util.LocationUtil;
import unipv.irma.opentripplanner.android.util.RightDrawableOnTouchListener;
import unipv.irma.opentripplanner.googleplaces.Place;
import unipv.irma.opentripplanner.googleplaces.PlacesService;
import android.R.integer;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.criterias.TipsCriteria;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.ImageRequestListener;
import br.com.condesales.listeners.TipsResquestListener;
import br.com.condesales.models.Tip;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Main UI screen of the app, showing the map.
 * 
 * @author Giovanni Miceli
 * @author rifat abir
 */

public class MainFragment extends Fragment implements
		ServerSelectorCompleteListener,
		TripRequestCompleteListener, MetadataRequestCompleteListener,
		OTPGeocodingListener, LocationListener,
		DateCompleteListener,
		GooglePlayServicesClient.OnConnectionFailedListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GoogleMap.OnCameraChangeListener,
		AccessTokenRequestListener, 
		ImageRequestListener
	
//		OnRangeSeekBarChangeListener<Double>
		{
	
	//private View mainView;
	private EasyFoursquareAsync async;
	
	private ImageButton btn_dashboard;
	private List<LatLng> allGeoPoints;
	private GoogleMap mMap;
	private TileOverlay actualTileOverlay;
	private MenuItem mGPS;
	private String start;
	private String end;
	
	boolean flag_re;
	List<String> result_list;
	/*
	 * Real Time Data
	 * */
	
	String stopId;
	ArrayList<String> stoplist;
	CharSequence[] realtimedata;
	private ProgressDialog progressDialogReal;
	ArrayList<String> realInfo;
	
	GoogleMap googleMap;
	
	
	private double endLat;
	private double endlng;
	private LocationClient mLocationClient;
	
    LocationRequest mLocationRequest;
	
	private List<Polyline> route;

	private EditText tbStartLocation;
	private EditText tbEndLocation;
	private ListView ddlOptimization;
	private ListView ddlTravelMode;
	private ImageButton btnPlanTrip;
	private ImageButton btnDateDialog;
	private ImageButton btnPoi_route;
	private ImageButton btnRealTime;
	private ImageButton btnReRoute;
	private ImageButton btnPoi_google;
	private ImageButton btnPOI;
	private LatLng savedLastLocation;
	private LatLng savedLastLocationCheckedForServer;

	private ImageButton btn_favourite;
	private ImageButton btn_monitoring;
	private ImageButton btn_poi;
	private Address startAddress;
	private Address endAddress;
	
	private String resultTripStartLocation;
	private String resultTripEndLocation;
	
	private View panelDisplayDirection;
	
	private Spinner itinerarySelectionSpinner;
	
//	private RangeSeekBar<Double> bikeTriangleParameters;
//	private ViewGroup bikeTriangleParametersLayout;	
	
	private ViewGroup navigationDrawerLeftPane;	

	private boolean appStarts = true;
	
	private boolean isStartLocationGeocodingProcessed = false;
	private boolean isEndLocationGeocodingProcessed = false;
	
	private boolean isStartLocationChangedByUser = true;
	private boolean isEndLocationChangedByUser = true;
	
	private boolean requestTripAfterStartGeocoding = false;
	private boolean requestTripAfterEndGeocoding = false;
	private boolean requestTripAfterStartEndGeocoding = false;
	private boolean geocodingBeenRequested = false;
	
	private Context applicationContext;
	
	private boolean mapFailed;


	public LatLng getSavedLastLocation() {
		return savedLastLocation;
	}

	Panel directionPanel;

	private ImageButton btnDisplayDirection;
	
	private ImageButton btnMyLocation;
	
	private ImageButton btnHandle;
	private DrawerLayout drawerLayout;

	Marker startMarker;
	LatLng startMarkerPosition;

	Marker endMarker;
	LatLng endMarkerPosition;
	
	ArrayList<Marker> modeMarkers;
	ArrayList<String> mSelectedItems;
	Polyline boundariesPolyline;

	private SharedPreferences prefs;
	private OTPApp app;
	private static LocationManager locationManager;

	ArrayList<String> directionText = new ArrayList<String>();

	private Boolean needToUpdateServersList = false;
	
	public Boolean getNeedToUpdateServersList() {
		return needToUpdateServersList;
	}

	public void setNeedToUpdateServersList(Boolean needToUpdateServersList) {
		this.needToUpdateServersList = needToUpdateServersList;
	}
	
	private Boolean needToRunAutoDetect = false;
	
	public Boolean getNeedToRunAutoDetect() {
		return needToRunAutoDetect;
	}

	public void setNeedToRunAutoDetect(Boolean needToRunAutoDetect) {
		this.needToRunAutoDetect = needToRunAutoDetect;
	}
	
	private boolean appResumed;

	private OnFragmentListener fragmentListener;

	private boolean isRealLostFocus = true;
	
	public static final String TAG = "OTP";

	private float maxZoomLevel;
	
	private Date tripDate;

	private boolean arriveBy;
	
//	private double bikeTriangleMinValue = OTPApp.BIKE_PARAMETERS_QUICK_DEFAULT_VALUE; 
//	private double bikeTriangleMaxValue = OTPApp.BIKE_PARAMETERS_FLAT_DEFAULT_VALUE;

	ProgressDialog progressDialog;
	ProgressDialog progressDialogGeocoding;

	ConnectionDatabase connection;
	int status;
	String result;
	String jsonResult;
	
	String user_id;
	String start_location;
	String end_location;
	String start_lat;
	String start_lon;
	String end_lat;
	String end_lon;
	String date;
	String time;
	long timestamp;
	
	/*Point of interest Four Square*/
	
	String name_poi;
	String description_poi;
	CharSequence[] poi_names;
	List<String> poi_name_temp = new ArrayList<String>();
	String poi_status;
	private ImageView poi_image;
	
	private final String TAG_poi = getClass().getSimpleName();
	
	private String[] places;
	private LocationManager locManager;
	private Location loc_poi;
	
	
	
	//private final LatLng HAMBURG ;
	//private final LatLng KIEL;
	private Marker marker;
	private Hashtable<String, String> markers;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	double lat;
	double lng;
	View CustomMarker ;

	String redId;
	SharedPreferences sharedPreferences;
	
	private List<Leg> itineraryTemp;
	private boolean animateCameraTemp;
	private boolean isMainFinished;
		
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			((MyActivity) activity).setDateCompleteCallback(this);
			setFragmentListener((OnFragmentListener) activity);
			//async = new EasyFoursquareAsync(activity);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View mainView = inflater.inflate(R.layout.activity_trip, container, false);
		
		sharedPreferences = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		redId =sharedPreferences.getString("regId","");
				
        markers = new Hashtable<String, String>();
        initImageLoader();
        imageLoader = ImageLoader.getInstance();
         
        options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.marker0)		//	Display Stub Image
		.showImageForEmptyUri(R.drawable.marker0)	//	If Empty image found
		.cacheInMemory()
		.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
        
		ViewTreeObserver vto = mainView.getViewTreeObserver(); 
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
		    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override 
		    public void onGlobalLayout() { 
		        MainFragment.removeOnGlobalLayoutListener(mainView, this);
				int locationtbEndLocation[] = new int[2];
				tbEndLocation.getLocationInWindow(locationtbEndLocation);
				int locationItinerarySelectionSpinner[] = new int[2];
				itinerarySelectionSpinner.getLocationInWindow(locationItinerarySelectionSpinner);
				int locationbtnHandle[] = new int[2];
				btnHandle.getLocationInWindow(locationbtnHandle);
				DisplayMetrics metrics = MainFragment.this.getResources().getDisplayMetrics();
				int windowHeight = metrics.heightPixels;
				int paddingMargin = MainFragment.this.getResources().getInteger(R.integer.map_padding_margin);
				if (mMap != null){
					mMap.setPadding(locationbtnHandle[0] + btnHandle.getWidth()/2 + paddingMargin,
							locationtbEndLocation[1] + tbEndLocation.getHeight()/2 + paddingMargin,
							0,
							windowHeight - locationItinerarySelectionSpinner[1] + paddingMargin);
				}
		    } 
		});
	
		
		btn_dashboard = (ImageButton) mainView.findViewById(R.id.btn_dashboard);
		tbStartLocation = (EditText) mainView.findViewById(R.id.tbStartLocation);
		tbEndLocation = (EditText) mainView.findViewById(R.id.tbEndLocation);
		
		btnPlanTrip = (ImageButton) mainView.findViewById(R.id.btnPlanTrip);
		btnPOI = (ImageButton) mainView.findViewById(R.id.btnPOI);
		
		
		btn_favourite = (ImageButton) mainView.findViewById(R.id.btn_favourite);
		btn_monitoring = (ImageButton) mainView.findViewById(R.id.btn_monitoring);

		ddlOptimization = (ListView) mainView.findViewById(R.id.spinOptimization);
		ddlTravelMode = (ListView) mainView.findViewById(R.id.spinTravelMode);

//		bikeTriangleParameters = new RangeSeekBar<Double>(OTPApp.BIKE_PARAMETERS_MIN_VALUE, OTPApp.BIKE_PARAMETERS_MAX_VALUE, this.getActivity().getApplicationContext(), R.color.sysRed, R.color.sysGreen, R.color.sysBlue, R.drawable.seek_thumb_normal, R.drawable.seek_thumb_pressed);
		
		// add RangeSeekBar to pre-defined layout
//		bikeTriangleParametersLayout = (ViewGroup) mainView.findViewById(R.id.bikeParametersLayout);

//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		params.addRule(RelativeLayout.BELOW, R.id.bikeParametersTags);
				
//		bikeTriangleParametersLayout.addView(bikeTriangleParameters, params);
		
		btnMyLocation = (ImageButton) mainView.findViewById(R.id.btnMyLocation);
		
		btnDateDialog = (ImageButton) mainView.findViewById(R.id.btnDateDialog);
		btnPoi_route= (ImageButton) mainView.findViewById(R.id.btnPoiRoute);
		btnRealTime= (ImageButton) mainView.findViewById(R.id.btnRealTime);
		btnReRoute= (ImageButton) mainView.findViewById(R.id.btnReRoute);
		btnPoi_google= (ImageButton) mainView.findViewById(R.id.btnPoiGoogle);
		btnDisplayDirection = (ImageButton) mainView.findViewById(R.id.btnDisplayDirection);
		
		navigationDrawerLeftPane = (ViewGroup) mainView.findViewById(R.id.navigationDrawerLeftPane);
		panelDisplayDirection = (ViewGroup) mainView.findViewById(R.id.panelDisplayDirection);
		
		btnHandle = (ImageButton) mainView.findViewById(R.id.btnHandle);
		drawerLayout = (DrawerLayout) mainView.findViewById(R.id.drawerLayout);
		
		tbStartLocation.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		tbEndLocation.setImeOptions(EditorInfo.IME_ACTION_DONE);
		tbEndLocation.requestFocus();
		
		itinerarySelectionSpinner = (Spinner) mainView.findViewById(R.id.itinerarySelection);

		Log.v(TAG, "finish onStart()");

		
		return mainView;
	}
	
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
	    if (Build.VERSION.SDK_INT < 16) {
	        v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
	    } else {
	        v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
	    }
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.v(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		
		applicationContext = getActivity().getApplicationContext();
		
		
        mMap = retrieveMap(mMap);

		app = ((OTPApp) getActivity().getApplication());
		
		prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		
		mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(OTPApp.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(OTPApp.FASTEST_INTERVAL);

		
		if (savedInstanceState == null){
			SharedPreferences.Editor prefsEditor = prefs.edit();
			prefsEditor.putBoolean(OTPApp.PREFERENCE_KEY_ORIGIN_IS_MY_LOCATION, true);
			prefsEditor.putBoolean(OTPApp.PREFERENCE_KEY_DESTINATION_IS_MY_LOCATION, false);
			prefsEditor.commit();
//			bikeTriangleParameters.setSelectedMinValue(OTPApp.BIKE_PARAMETERS_QUICK_DEFAULT_VALUE);
//			bikeTriangleParameters.setSelectedMaxValue(OTPApp.BIKE_PARAMETERS_FLAT_DEFAULT_VALUE);
		}
		
		if (!mapFailed){
			if (prefs.getBoolean(OTPApp.PREFERENCE_KEY_SELECTED_CUSTOM_SERVER, false)){
				String baseURL = prefs.getString(OTPApp.PREFERENCE_KEY_CUSTOM_SERVER_URL, "");
				Server s = new Server(baseURL, applicationContext);
				String bounds;
				setSelectedServer(s, false);
				if ((bounds = prefs.getString(OTPApp.PREFERENCE_KEY_CUSTOM_SERVER_BOUNDS, null)) != null){
					s.setBounds(bounds);
					addBoundariesRectangle(s);
				}
				
				Log.v(TAG, "Now using custom OTP server: " + baseURL);
			}
			else{
				ServersDataSource dataSource = ServersDataSource.getInstance(applicationContext);
				long serverId = prefs.getLong(OTPApp.PREFERENCE_KEY_SELECTED_SERVER, 0);
				if (serverId != 0){
					dataSource.open();
					Server s = dataSource.getServer(prefs.getLong(OTPApp.PREFERENCE_KEY_SELECTED_SERVER, 0));
					dataSource.close();
					
					if (s != null){
						setSelectedServer(s, false);
						addBoundariesRectangle(s);
						Log.v(TAG, "Now using OTP server: " + s.getRegion());
					}
				}
			}
		}
		
		ArrayAdapter<OptimizeSpinnerItem> optimizationAdapter = new ArrayAdapter<OptimizeSpinnerItem>(
				getActivity(),
				android.R.layout.simple_list_item_single_choice,
				new OptimizeSpinnerItem[] {
						new OptimizeSpinnerItem(getResources().getString(R.string.optimization_quick), OptimizeType.QUICK),
						new OptimizeSpinnerItem(getResources().getString(R.string.optimization_safe), OptimizeType.SAFE),
						new OptimizeSpinnerItem(getResources().getString(R.string.optimization_fewest_transfers),
								OptimizeType.TRANSFERS) });
		ddlOptimization.setAdapter(optimizationAdapter);

		ArrayAdapter<TraverseModeSpinnerItem> traverseModeAdapter = new ArrayAdapter<TraverseModeSpinnerItem>(
				getActivity(), android.R.layout.simple_list_item_single_choice,
				new TraverseModeSpinnerItem[] {
						new TraverseModeSpinnerItem(getResources().getString(R.string.mode_transit),
								new TraverseModeSet(TraverseMode.TRANSIT,
										TraverseMode.WALK)),
						new TraverseModeSpinnerItem(getResources().getString(R.string.mode_bus),
								new TraverseModeSet(TraverseMode.BUSISH,
										TraverseMode.WALK)),
//						new TraverseModeSpinnerItem(getResources().getString(R.string.mode_train),
//								new TraverseModeSet(TraverseMode.TRAINISH,
//										TraverseMode.WALK)), // not sure
						new TraverseModeSpinnerItem(getResources().getString(R.string.mode_walk),
								new TraverseModeSet(TraverseMode.WALK)),
//						new TraverseModeSpinnerItem(getResources().getString(R.string.mode_bicycle),
//								new TraverseModeSet(TraverseMode.BICYCLE)),
//						new TraverseModeSpinnerItem(getResources().getString(R.string.mode_transit_bicycle),
//								new TraverseModeSet(TraverseMode.TRANSIT,
//										TraverseMode.BICYCLE)) 
								});
		ddlTravelMode.setAdapter(traverseModeAdapter);	
		
		
		Server selectedServer = app.getSelectedServer();	
		if (selectedServer != null){
			if (!mapFailed){
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getServerCenter(selectedServer), getServerInitialZoom(selectedServer)));			
			}
		}

		restoreState(savedInstanceState);
		
			
		if (savedInstanceState == null){
			ddlOptimization.setItemChecked(0, true);
			ddlTravelMode.setItemChecked(0, true);
//			showBikeParameters(false);
			arriveBy = false;
			setTextBoxLocation(getResources().getString(R.string.my_location), true);
		}
			
		if (!mapFailed){
			initializeMapInterface(mMap);
		}
	}
	
	
	private void initializeMapInterface(GoogleMap mMap){
		UiSettings uiSettings = mMap.getUiSettings();
//		mMap.setMyLocationEnabled(true);
		mMap.setOnCameraChangeListener(this);
		uiSettings.setMyLocationButtonEnabled(false);
		uiSettings.setCompassEnabled(true);
		uiSettings.setAllGesturesEnabled(true);
		uiSettings.setZoomControlsEnabled(false);
		
		String overlayString = prefs.getString(OTPApp.PREFERENCE_KEY_MAP_TILE_SOURCE, applicationContext.getResources().getString(R.string.map_tiles_default_server));
		updateOverlay(overlayString);
		
		addMapListeners();
		addInterfaceListeners();
	}
	
	
	private void addInterfaceListeners(){
		
		DrawerListener dl = new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int arg0) {
			}
			
			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				
				InputMethodManager imm = (InputMethodManager) MainFragment.this.getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(tbEndLocation.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(tbStartLocation.getWindowToken(), 0);
			}
			
			@Override
			public void onDrawerOpened(View arg0) {
				tbStartLocation.clearFocus();
				tbEndLocation.clearFocus();				
			}
			
			@Override
			public void onDrawerClosed(View arg0) {
			}
		};
		drawerLayout.setDrawerListener(dl);
		
		OnTouchListener otlStart = new RightDrawableOnTouchListener(tbStartLocation) {
			@Override
			public boolean onDrawableTouch(final MotionEvent event) {
				// mBoundService.updateNotification();

				final CharSequence[] items = { getResources().getString(R.string.location_type_current_location), getResources().getString(R.string.location_type_map_point) };

				AlertDialog.Builder builder = new AlertDialog.Builder(MainFragment.this.getActivity());
				builder.setTitle(getResources().getString(R.string.choose_location_type_start));
				builder.setItems(items, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int item) {
						if (items[item].equals(getResources().getString(R.string.location_type_current_location))) {	
					/*		myActivity = (MyActivity) activity;
							myActivity.getmLocationClient();
							Location loc = this.MainFragment.getmLocationClient().getLastLocation();*/
							LatLng mCurrentLatLng = getLastLocation();
							if (mCurrentLatLng != null){
								SharedPreferences.Editor prefsEditor = prefs.edit();
								setTextBoxLocation(getResources().getString(R.string.my_location), true);
								prefsEditor.putBoolean(OTPApp.PREFERENCE_KEY_ORIGIN_IS_MY_LOCATION, true);

								if (mCurrentLatLng != null) {
									if (startMarker != null){
										startMarker.remove();
										startMarker = null;
									}
								}

								prefsEditor.commit();
							}
							else{
								Toast.makeText(MainFragment.this.applicationContext, applicationContext.getResources().getString(R.string.location_error), Toast.LENGTH_LONG).show();
							}
							
							
						}  else { // Point on Map
							if (startMarker != null){
								updateMarkerPosition(startMarker.getPosition(), true);
							}
							else{
								setTextBoxLocation("", true);
								tbStartLocation.setHint(getResources().getString(R.string.need_to_place_marker));
								tbStartLocation.requestFocus();
							}
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}

		};
		
		tbStartLocation.setOnTouchListener(otlStart);
		
		
		OnTouchListener otlEnd = new RightDrawableOnTouchListener(tbEndLocation) {
			@Override
			public boolean onDrawableTouch(final MotionEvent event) {
				// mBoundService.updateNotification();

				final CharSequence[] items = { getResources().getString(R.string.location_type_current_location), getResources().getString(R.string.location_type_map_point) };

				AlertDialog.Builder builder = new AlertDialog.Builder(MainFragment.this.getActivity());
				builder.setTitle(getResources().getString(R.string.choose_location_type_end));
				builder.setItems(items, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int item) {
						if (items[item].equals(getResources().getString(R.string.location_type_current_location))) {	
					/*		myActivity = (MyActivity) activity;
							myActivity.getmLocationClient();
							Location loc = this.MainFragment.getmLocationClient().getLastLocation();*/
							LatLng mCurrentLatLng = getLastLocation();
							if (mCurrentLatLng != null){
								SharedPreferences.Editor prefsEditor = prefs.edit();
								setTextBoxLocation(getResources().getString(R.string.my_location), false);
								prefsEditor.putBoolean(OTPApp.PREFERENCE_KEY_DESTINATION_IS_MY_LOCATION, true);

								if (mCurrentLatLng != null) {
									if (endMarker != null){
										endMarker.remove();
										endMarker = null;
									}
								}

								prefsEditor.commit();
							}
							else{
								Toast.makeText(MainFragment.this.applicationContext, applicationContext.getResources().getString(R.string.location_error), Toast.LENGTH_LONG).show();
							}
							
							
						} else { // Point on Map
							if (endMarker != null){
								updateMarkerPosition(endMarker.getPosition(), false);
							}
							else{
								setTextBoxLocation("", false);
								tbEndLocation.setHint(getResources().getString(R.string.need_to_place_marker));
								tbEndLocation.requestFocus();
							}
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}

		};
		
		tbEndLocation.setOnTouchListener(otlEnd);
		

		btnPlanTrip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				flag_re = false;
				processRequestTrip();
			}
		});
		
		btnPOI.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			//	SharedPreferences sharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
				//Editor editor = sharedpreferences.edit();
			//	editor.putString("regId", regId);
				//editor.commit();
				
				async = new EasyFoursquareAsync(getActivity());
		        async.requestAccess(MainFragment.this);
				
				//Intent i = new Intent (MainFragment.this.getActivity(), poi.class);
				//MainFragment.this.getActivity().startActivity(i);
				
				
				//MainFragment.this.getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				//MainFragment.this.getActivity().finish();
			}
		});
		
		
		btn_dashboard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					Intent i = new Intent (MainFragment.this.getActivity(), DashboardActivity.class);
					MainFragment.this.getActivity().startActivity(i);
					MainFragment.this.getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
					MainFragment.this.getActivity().finish();
				}
		});
		
		OnFocusChangeListener tbLocationOnFocusChangeListener = new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!isRealLostFocus) {
					isRealLostFocus = true;
					return;
				}
				TextView tv = (TextView) v;
				if (!hasFocus) {
					String text = tv.getText().toString();
					
					if (!TextUtils.isEmpty(text)){
						if (v.getId() == R.id.tbStartLocation 
								&& !isStartLocationGeocodingProcessed 
								&& !prefs.getBoolean(OTPApp.PREFERENCE_KEY_ORIGIN_IS_MY_LOCATION, true)) {
							processAddress(true, tv.getText().toString(), false);
						} else if (v.getId() == R.id.tbEndLocation 
								&& !isEndLocationGeocodingProcessed
								&& !prefs.getBoolean(OTPApp.PREFERENCE_KEY_DESTINATION_IS_MY_LOCATION, true)) {
							processAddress(false, tv.getText().toString(), false);
						}
					} else {
						if (v.getId() == R.id.tbStartLocation){
							tv.setHint(getResources().getString(R.string.start_location_hint));
						}
						else if (v.getId() == R.id.tbEndLocation){	
							tv.setHint(getResources().getString(R.string.end_location_hint));
						}
					}
				}
			}
		};
//		tbStartLocation.setOnFocusChangeListener(tbLocationOnFocusChangeListener);
//		tbEndLocation.setOnFocusChangeListener(tbLocationOnFocusChangeListener);

		TextWatcher textWatcherStart = new TextWatcher() {

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        }

	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count,
	                int after) {
	        }

	        @Override
	        public void afterTextChanged(Editable s) {
	        	if (isStartLocationChangedByUser){
					SharedPreferences.Editor prefsEditor = prefs.edit();
					prefsEditor.putBoolean(OTPApp.PREFERENCE_KEY_ORIGIN_IS_MY_LOCATION, false);
					prefsEditor.commit();
		            isStartLocationGeocodingProcessed = false;
	        	}
	        	else{
	        		isStartLocationChangedByUser = true;
	        	}
	        }
	    };
	    
	    

		TextWatcher textWatcherEnd = new TextWatcher() {

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        }

	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count,
	                int after) {
	        }

	        @Override
	        public void afterTextChanged(Editable s) {
	        	if (isEndLocationChangedByUser){
					SharedPreferences.Editor prefsEditor = prefs.edit();
					prefsEditor.putBoolean(OTPApp.PREFERENCE_KEY_DESTINATION_IS_MY_LOCATION, false);
					prefsEditor.commit();
	        		isEndLocationGeocodingProcessed = false;
	        	}
	        	else{
	        		isEndLocationChangedByUser = true;
	        	}
	        }
	    };
	    
	    tbStartLocation.addTextChangedListener(textWatcherStart);
	    tbEndLocation.addTextChangedListener(textWatcherEnd);
		
		OnEditorActionListener tbLocationOnEditorActionListener = new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (v.getId() == R.id.tbStartLocation){
						if (actionId == EditorInfo.IME_ACTION_NEXT
						|| (event != null
								&& event.getAction() == KeyEvent.ACTION_DOWN && event
								.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							isRealLostFocus = false;
							if (!isStartLocationGeocodingProcessed
									&& !prefs.getBoolean(OTPApp.PREFERENCE_KEY_ORIGIN_IS_MY_LOCATION, true)){
								geocodingBeenRequested = true;
								processAddress(true, v.getText().toString(), false);
							}
						}
				} else if (v.getId() == R.id.tbEndLocation){
						if (actionId == EditorInfo.IME_ACTION_DONE
						|| (event != null
								&& event.getAction() == KeyEvent.ACTION_DOWN && event
								.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							processRequestTrip();
						}
				}
				return false;
			}
		};

//		tbStartLocation.setOnEditorActionListener(tbLocationOnEditorActionListener);
//		tbEndLocation.setOnEditorActionListener(tbLocationOnEditorActionListener);

		
		OnClickListener oclDisplayDirection = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				saveOTPBundle();
				getFragmentListener().onSwitchedToDirectionFragment();
			}
		};
		btnDisplayDirection.setOnClickListener(oclDisplayDirection);

		// Do NOT show direction icon if there is no direction yet
		if (getFragmentListener().getCurrentItinerary().isEmpty()) {
			panelDisplayDirection.setVisibility(View.INVISIBLE);
			btn_favourite.setVisibility(View.INVISIBLE);
			btn_monitoring.setVisibility(View.INVISIBLE);
		    btnPOI.setVisibility(View.INVISIBLE);
		    btnRealTime.setVisibility(View.INVISIBLE);
		    btnReRoute.setVisibility(View.INVISIBLE);
		    btnPoi_route.setVisibility(View.INVISIBLE);

		} else {
			panelDisplayDirection.setVisibility(View.VISIBLE);
			btn_favourite.setVisibility(View.VISIBLE);
			btn_monitoring.setVisibility(View.VISIBLE);
			btnPOI.setVisibility(View.VISIBLE);
			btnRealTime.setVisibility(View.VISIBLE);
			//btnReRoute.setVisibility(View.VISIBLE);
			btnPoi_route.setVisibility(View.VISIBLE);		
		}
		
		OnClickListener oclMyLocation = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LatLng mCurrentLatLng = getLastLocation();
				
				if (mCurrentLatLng == null){
					Toast.makeText(applicationContext, applicationContext.getResources().getString(R.string.location_error), Toast.LENGTH_LONG).show();
				}
				else{
					if (mMap.getCameraPosition().zoom < OTPApp.defaultMyLocationZoomLevel){
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, OTPApp.defaultMyLocationZoomLevel));
					}
					else{
						mMap.animateCamera(CameraUpdateFactory.newLatLng(getLastLocation()));
					}
					
					// show current position
					Marker mark = mMap.addMarker(new MarkerOptions()
			        .position(mCurrentLatLng)
			        .title("You are Here!")
			        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
					
					mark.showInfoWindow();
				}
			}
		};
		btnMyLocation.setOnClickListener(oclMyLocation);
		/*
		 * Dialog Date and time
		 * Listener for Date and time
		 * */
		OnClickListener oclDateDialog = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			    FragmentTransaction ft = MainFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
			    Fragment prev = MainFragment.this.getActivity().getSupportFragmentManager().findFragmentByTag(OTPApp.TAG_FRAGMENT_DATE_TIME_DIALOG);
			    if (prev != null) {
			        ft.remove(prev);
			    }
			    ft.addToBackStack(null);

			    // Create and show the dialog.
			    DateTimeDialog newFragment = new DateTimeDialog();
			    
			    Date dateDialogDate;
			    if (tripDate == null){
			    	dateDialogDate = Calendar.getInstance().getTime();
			    }
			    else{
			    	dateDialogDate = tripDate;
			    }
			    
				Bundle bundle = new Bundle();
				bundle.putSerializable(OTPApp.BUNDLE_KEY_TRIP_DATE, dateDialogDate);
				bundle.putBoolean(OTPApp.BUNDLE_KEY_ARRIVE_BY, arriveBy);
				newFragment.setArguments(bundle);
				ft.commit();

				newFragment.show(MainFragment.this.getActivity().getSupportFragmentManager(), OTPApp.TAG_FRAGMENT_DATE_TIME_DIALOG);
		    }
		};
		btnDateDialog.setOnClickListener(oclDateDialog);
		/*
		 * ---------End
		 * */
		
		/*
		 * Dialog Point of interest Route
		 * Listener for Point of interest
		 * 
		 * Button : Point of Interest
		 * 
		 * */
		
		OnClickListener poi_detect = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			   
				new dataAccess_poi().execute();
		    }
		};
		btnPoi_route.setOnClickListener(poi_detect);
		
		/*
		 * ---------End
		 * */
		
		/*
		 * Google Poi
		 * */
		
		OnClickListener oclGooglePlaces = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			     
				//String pl=places[0].toLowerCase().replace("-", "_").replace(" ", "_");
				places = getResources().getStringArray(R.array.places);
				loc_poi.setLatitude(endLat);
		        loc_poi.setLongitude(endlng);
		        new GetPlaces(MainFragment.this.getActivity(),"Cafe").execute();
		    }
		};
		btnPoi_google.setOnClickListener(oclGooglePlaces);
		
		/*-----------------------------*/
		
		
		/*
		 * Button : Real Time Data 
		 *
		 * */
		
		OnClickListener oclRealTimeData = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				new dataAccessReal().execute();
				
		    }
		};
		btnRealTime.setOnClickListener(oclRealTimeData);
		
		
		/******************************************************
		 * 
		 * Calculating Reroute
		 * 
		 */
		OnClickListener oclReRoute = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				flag_re = true;
				processRequestTrip();
				
		    }
		};
		btnReRoute.setOnClickListener(oclReRoute);
			
		AdapterView.OnItemSelectedListener itinerarySpinnerListener = new AdapterView.OnItemSelectedListener() {
		    @Override
		    public void  onItemSelected (AdapterView<?> parent, View view, int position, long id){
//		    	Toast.makeText(parent.getContext(), 
//		    				   Long.toString(id) + " chosen " +
//		    				   parent.getItemAtPosition(position).toString(), 
//		    				   Toast.LENGTH_SHORT).show();
		    	fragmentListener.onItinerarySelected(position);
		    	
		    	if (isMainFinished){
		    		if (!appResumed){
				    	showRouteOnMap(fragmentListener.getCurrentItinerary(), true);
		    		}
		    		else{
				    	showRouteOnMap(fragmentListener.getCurrentItinerary(), false);
				    	appResumed = false;
		    		}
		    	}
	    		
		    }

		    @Override
		    public void onNothingSelected (AdapterView<?> parent) {
		    	
		    }
		};
		int currentItineraryIndex = fragmentListener.getCurrentItineraryIndex();

		itinerarySelectionSpinner.setSelection(currentItineraryIndex);
		itinerarySelectionSpinner.setOnItemSelectedListener(itinerarySpinnerListener);
		

		
//		bikeTriangleParameters.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Double>() {
//	        @Override
//	        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> rangeSeekBar, Double minValue, Double maxValue) {
//	                // handle changed range values
//	                Log.i(TAG, "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
//	        }
//
//		});
		
		ddlTravelMode.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view,
	                int position, long id) { 
//	        	TraverseModeSpinnerItem traverseModeSpinnerItem = (TraverseModeSpinnerItem) ddlTravelMode.getItemAtPosition(position);			
//	        	if (traverseModeSpinnerItem.getTraverseModeSet().contains(TraverseMode.BICYCLE)){
//		    		setBikeOptimizationAdapter(true);
//		    		showBikeParameters(true);
//	        	}
//	        	else{
		    		setBikeOptimizationAdapter(false);
//		    		showBikeParameters(false);
//	        	}
	        }
	    });
		
//		ddlOptimization.setOnItemClickListener(new OnItemClickListener() {
//	        public void onItemClick(AdapterView<?> parent, View view,
//	                int position, long id) {
//
//	        	OptimizeSpinnerItem optimizeSpinnerItem = (OptimizeSpinnerItem) ddlOptimization.getItemAtPosition(position);				
//	        	showBikeParameters(optimizeSpinnerItem.getOptimizeType().equals(OptimizeType.TRIANGLE));
//
//	        }
//	    });
//		
//		bikeTriangleParameters.setOnRangeSeekBarChangeListener(this);
	}
	private class dataAccessReal extends AsyncTask<String, Void, Void>{
		
		protected void onPreExecute() {
			
			progressDialogReal = ProgressDialog.show(MainFragment.this.getActivity(), "",
					"Fetching RT Data ......");
			progressDialogReal.setCancelable(true);
			
			
		}
		 
		//poi_names = poi_name_temp.toArray(new CharSequence[poi_name_temp.size()]);
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			realInfo=new ArrayList<String>();
			String rev="";
			String infoTotal="";
			for(int i=0;i<stoplist.size();i++)
			{
						HttpClient httpclient = new DefaultHttpClient();
					    
					    HttpPost httppost = new HttpPost("http://rlabgw0.unipv.it:15002/IRMA_RT_WEB/service/rtdata/realtime_android");
				
					    try {
							HttpEntity e = new StringEntity(stoplist.get(i));
							httppost.setEntity(e);


					        // Execute HTTP Post Request
					        HttpResponse response = httpclient.execute(httppost);
							int code = response.getStatusLine().getStatusCode();
							
							if (code == 200) {
								rev = EntityUtils.toString(response.getEntity());
								
							}
							JSONObject mainObject = new JSONObject(rev);
							JSONArray arrayRealTimeWrapper = mainObject.getJSONArray("RealTimeWrapper");
							JSONObject object = arrayRealTimeWrapper.getJSONObject(0);
							String delay = object.optString("delay");
							String estimated_arrival = object.optString("estimated_arrival");
							String Bus_no = object.optString("route_code");
							
							Integer hour=Integer.parseInt(estimated_arrival)/3600;
							Double minDouble=Math.ceil(((Double.parseDouble(estimated_arrival)%3600)/60));
							Integer min=(int)Math.round(minDouble);
							//Integer sec=Integer.parseInt(estimated_arrival)%60;
							
							Integer delayMin = Integer.parseInt(delay) / 60;
							
							//Integer delaySec=Integer.parseInt(delay)%60;
							//Double delayMin=Double.parseDouble(delay)/60;
							//Double delaySec=Double.parseDouble(delay)%60;
							//modeMarkerOption.snippet("Estimated Arrival:"+hour+" h "+min+" m "+sec+" s "+"Delay: " + delay + " min");
							
							infoTotal="Line no.: "+Bus_no+"\nEstimated Arrival Time: "+hour+":"+min+" \nBus Delay: "+delayMin+" min";
							realInfo.add(infoTotal);
							//}else{
								
							//	String erro = "Connection Error... Please Try again later";
							//	realInfo.add(erro);
							//}
							
							
						}catch(Exception e){
							String erro = "Connection Error... Please Try again later";
							realInfo.add(erro);
						}
			}				
			return null;
		}
		
		
		protected void onPostExecute(Void unused) {

			progressDialogReal.dismiss();
			


			AlertDialog.Builder builder = new AlertDialog.Builder(MainFragment.this.getActivity());
			//builder.setTitle("Real Time Bus Information");
			//builder.setIcon(R.drawable.realtime);
			
			realtimedata = realInfo.toArray(new CharSequence[realInfo.size()]);
			
			builder.setTitle("Real Time Bus Information").setIcon(R.drawable.realtime)
	           .setItems(realtimedata, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position
	               // of the selected item
	           }
	        });
			builder.setNeutralButton("Back",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			
			AlertDialog alert = builder.create();
			alert.show();
			/*builder.setPositiveButton("BACK",
					 new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int id) {
					   Toast.makeText(MainFragment.this.getActivity(), "Trip Planner", Toast.LENGTH_SHORT).show();
					  }
			 	});*/
			
			
			
			//builder.setMultiChoiceItems(items, null, new DialogInterface.OnClickListener() {
		//	    public void onClick(DialogInterface dialog, int item) {
			    	//Toast.makeText(MainFragment.this.getActivity().getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
			   // }
			//});
			
			/*builder.setMultiChoiceItems(items, null, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			
			
			 //builder.setTitle(R.string.pick_toppings)
			    // Specify the list array, the items to be selected by default (null for none),
			    // and the listener through which to receive callbacks when items are selected
			/*builder.setMultiChoiceItems(realtimedata, null,
			                      new DialogInterface.OnMultiChoiceClickListener() {
			               @Override
			               public void onClick(DialogInterface dialog, int which,boolean isChecked) {
			                       
			                   if (isChecked) {
			                       // If the user checked the item, add it to the selected items
			                      // mSelectedItems.add(which,items[which].toString());
			                   } //else if (mSelectedItems.contains(which)) {
			                       // Else, if the item is already in the array, remove it 
			                       //mSelectedItems.remove(Integer.valueOf(which));
			                   //}
			               }
			           });
			

			builder.setPositiveButton("Yes",
			 new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int id) {
			   Toast.makeText(MainFragment.this.getActivity(), "Success", Toast.LENGTH_SHORT).show();
			  }
			 });
			builder.setNegativeButton("No",
			 new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int id) {
			   Toast.makeText(MainFragment.this.getActivity(), "Fail", Toast.LENGTH_SHORT).show();
			  }
			 });
			/*AlertDialog alert = builder.create();
			alert.show();
			AlertDialog.Builder alert = new AlertDialog.Builder(MainFragment.this.getActivity());

			alert.setTitle("This is Alert Dialog");
			alert.setMessage("Sample alert dialog from http://www.android-codes-examples.blogspot.com");
			alert.setIcon(R.drawable.icon);
			alert.setPositiveButton("Yes",
			 new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int id) {
			   Toast.makeText(MainFragment.this.getActivity(), "Success", Toast.LENGTH_SHORT).show();
			  }
			 });
			alert.setNegativeButton("No",
			 new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int id) {
			   Toast.makeText(MainFragment.this.getActivity(), "Fail", Toast.LENGTH_SHORT).show();
			  }
			 });

			alert.show();*/
			
		}
		
	}
	
	private class dataAccess extends AsyncTask<String, Void, Void>{
		
		 String result_sub;
		 InputStream is = null;
	     private String Content;
	     private String result;
	     
	     private  String s = "";
	     StringBuilder sb = null;
	     private String Error = null;
	   
		//private ProgressDialog Dialog = new ProgressDialog(test.this);
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			 try {
				
				//JSONObject object = new JSONObject(); 
				 start = (startMarkerPosition.latitude + "," + startMarkerPosition.longitude);
				 end = (endMarkerPosition.latitude + "," + endMarkerPosition.longitude);
				
				 //Toast.makeText(getActivity(), end, Toast.LENGTH_LONG).show();
				 List<String> obj_lat=new ArrayList<String>();
				 List<String> obj_long=new ArrayList<String>();
				for(int i=0;i< allGeoPoints.size();i++)
				{
					//object.put("lat", allGeoPoints.get(i).latitude);
					//object.put("long", allGeoPoints.get(i).longitude);
					obj_lat.add(allGeoPoints.get(i).latitude+"");
					obj_long.add(allGeoPoints.get(i).longitude+"");	
					endLat=allGeoPoints.get(i).latitude;
					endlng=allGeoPoints.get(i).longitude;
					//object.add(allGeoPoints.get(i).latitude+","+allGeoPoints.get(i).longitude);
					
				}
				 
				    JSONArray jsonArray = new JSONArray(obj_lat);
				 
	                HttpClient httpclient = new DefaultHttpClient();
	                HttpPost httppost = new HttpPost("http://eventmanager-irmatripplanner.rhcloud.com/gcmalert/EventWithPushNotification.php");
	              //  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	                List<NameValuePair> pp = new ArrayList<NameValuePair>();
	                pp.add(new BasicNameValuePair("Regid", redId));
	                pp.add(new BasicNameValuePair("start", start));
	                pp.add(new BasicNameValuePair("end", end));
	                pp.add(new BasicNameValuePair("lat",obj_lat.toString()));
	                pp.add(new BasicNameValuePair("long",obj_long.toString()));
	                
	                httppost.setEntity(new UrlEncodedFormEntity(pp));
	                
	                HttpResponse response = httpclient.execute(httppost);

	                int a = response.getStatusLine().getStatusCode();
	                
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                
	                result = out.toString();
	                
			 }catch (Exception e) {
	                // TODO: handle exception
	                Log.e("log_tag", "Error Parsing Data "+e.toString());
	        }
			 
			 try{
				 	
				   if(!result.trim().equals("null") && !result.trim().equals("[]") ){
				  
					    JSONArray jsonArray = new JSONArray(result);
					    String[] strArr = new String[jsonArray.length()];

				   		for (int i = 0; i < jsonArray.length(); i++) {
				   			
				   				strArr[i] = jsonArray.getString(i);
				      
				   		}
				   		result_list = new ArrayList<String>(Arrays.asList(strArr));
				   }
				   
		        }catch (Exception e) {
		                // TODO: handle exception
		                Log.e("log_tag", "Error Parsing Data "+e.toString());
		        }
			 
			 
			 
			
			return null;
		}
		
		protected void onPreExecute() {
			
			// mMap.addMarker(new MarkerOptions().position(new LatLng(45.193531, 9.162783)).title(""));
			    
           // NOTE: You can call UI Element here.

           //UI Element
		   //	txtv.setText("Output : ");
           // Dialog.setMessage("Downloading source..");
           // Dialog.show();
       }
		Marker mk;
	    protected void onPostExecute(Void unused) {
          
	    	GoogleMap map = mMap; // get a map.
	    	
	    	if(!result.trim().equals("null") && !result.trim().equals("[]")){
	    	
	    		for (int i = 0; i < result_list.size(); i++) {
	    			
				String events=result_list.get(i); 
	    	   
			 	String event_array[]=events.split(",");
				
				String title_message=event_array[3].toString();
				String snipet_message=event_array[6].toString();
				String type=event_array[5].toString();
				if(type.equals("jam")){
				mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(event_array[1]), Double.parseDouble(event_array[2]))).title(title_message).snippet(snipet_message).icon(BitmapDescriptorFactory.fromResource(R.drawable.trafficjam)));
				}
				if(type.equals("accident")){
					mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(event_array[1]), Double.parseDouble(event_array[2]))).title(title_message).snippet(snipet_message).icon(BitmapDescriptorFactory.fromResource(R.drawable.accident)));
					modeMarkers.add(mk);
				}
				if(type.equals("market")){
					mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(event_array[1]), Double.parseDouble(event_array[2]))).title(title_message).snippet(snipet_message).icon(BitmapDescriptorFactory.fromResource(R.drawable.market)));
					modeMarkers.add(mk);
				}
				if(type.equals("concert")){
					mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(event_array[1]), Double.parseDouble(event_array[2]))).title(title_message).snippet(snipet_message).icon(BitmapDescriptorFactory.fromResource(R.drawable.concert)));
					modeMarkers.add(mk);
				}
				if(type.equals("party")){
					mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(event_array[1]), Double.parseDouble(event_array[2]))).title(title_message).snippet(snipet_message).icon(BitmapDescriptorFactory.fromResource(R.drawable.fire)));
					modeMarkers.add(mk);
				}
				if(type.equals("fire")){
					mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(event_array[1]), Double.parseDouble(event_array[2]))).title(title_message).snippet(snipet_message).icon(BitmapDescriptorFactory.fromResource(R.drawable.party)));
					modeMarkers.add(mk);
				}
				if(type.equals("maintenance")){
					mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(event_array[1]), Double.parseDouble(event_array[2]))).title(title_message).snippet(snipet_message).icon(BitmapDescriptorFactory.fromResource(R.drawable.repair)));
					modeMarkers.add(mk);
				}
				if(type.equals("misc")){
					mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(event_array[1]), Double.parseDouble(event_array[2]))).title(title_message).snippet(snipet_message).icon(BitmapDescriptorFactory.fromResource(R.drawable.misc)));
					modeMarkers.add(mk);
				}

	    	}
	    	 
	    	 }else{

	 	    	AlertDialog.Builder builder = new AlertDialog.Builder(MainFragment.this.getActivity());
	 			//builder.setTitle("Real Time Bus Information");
	 			//builder.setIcon(R.drawable.realtime);
	 			
	 	    	String error="No Nearest POI Location";
	 	    	ArrayList<String> error2=new ArrayList<String>();
	 	    	error2.add(error);
	 			CharSequence[] realtimedata2 ={"Unable to establish connection to Server"};
	 			
	 			builder.setTitle("Events").setIcon(R.drawable.accident)
	 	           .setItems(realtimedata2, new DialogInterface.OnClickListener() {
	 	               public void onClick(DialogInterface dialog, int which) {
	 	               // The 'which' argument contains the index position
	 	               // of the selected item
	 	           }
	 	        });
	 			builder.setNeutralButton("Back",new DialogInterface.OnClickListener() {
	 				
	 				@Override
	 				public void onClick(DialogInterface dialog, int which) {
	 					// TODO Auto-generated method stub
	 					
	 				}
	 			});
	 			
	 			AlertDialog alert = builder.create();
	 			alert.show(); 
	    		 
	    	 }
	    	 
	    	// mMap.addMarker(new MarkerOptions().position(new LatLng(45.193531, 9.162783)).title(result_sub));
	    			 
           if (Error != null) {

           

           } else {
              // lvView.setAdapter(new ArrayAdapter(MainActivityPost.this, android.R.layout.simple_expandable_list_item_1, r));
         //  	txtv.setText("Output : "+result);
          //.setText("Output : "+r.toString());

           }
       }
		
	}
	
	
	
	
	/**
	 * Wrapper to call request trip, triggering geocoding processes if it's
	 * necessary.
	 */
	private void processRequestTrip(){
		isRealLostFocus = false;
		mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		
	/*	mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

    	    @Override
    	    public boolean onMarkerClick(Marker arg0) {
    	    	
    	    	//if(markers.containsKey(arg0.getId())){
    	    		
    	    		
    	    		
    	    		poi_image.setVisibility(View.VISIBLE);
    	    	
    	    	
    	    //	}else {
    	    		//poi_image.setVisibility(View.GONE);
				
    	    		
    	    		
    	    //	}
    	    	
    	    	
    	    	
    	        return true;
    	    }
    	});*/
		new LocateTask2().execute();
//		try {
//			Geocoder geocoder = new Geocoder(MainFragment.this.applicationContext, Locale.getDefault());
//			List<Address> addresses = geocoder.getFromLocationName("Corso Cairoli 1, 27100, Pavia, PV, Italy", 1);
//			
//			double latitude = 0;
//			double longitude = 0;
//			Address address = addresses.get(0);
//			if(addresses.size() > 0) {
//			    latitude = addresses.get(0).getLatitude();
//			    longitude = addresses.get(0).getLongitude();
//			}
//			
//			String test_to = getLocationFromString("Corso Cairoli 1, 27100, Pavia, PV, Italy");
//			
//			
//			processAddress(true, tbStartLocation.getText().toString(), true);
//			processAddress(false, test_to, true);
//			requestTrip();
//		
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Sends information of the text boxes to fragment listener class through a
	 * bundle.
	 * <p>
	 * Fragment listener provides intercommunication with other fragments or classes.
	 */
	private void saveOTPBundle() {
		OTPBundle bundle = new OTPBundle();
		bundle.setFromText(resultTripStartLocation);
		bundle.setToText(resultTripEndLocation);

		this.getFragmentListener().setOTPBundle(bundle);
	}


	private void addMapListeners(){
		OnMapClickListener omcl = new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latlng) {
				InputMethodManager imm = (InputMethodManager) MainFragment.this.getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(tbEndLocation.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(tbStartLocation.getWindowToken(), 0);
				
				if (tbStartLocation.hasFocus()){
					setMarker(true, latlng, true);
				}
				else{
					setMarker(false, latlng, true);
				}
			}
		};
		mMap.setOnMapClickListener(omcl);
		
		OnMarkerDragListener omdl = new OnMarkerDragListener() {
			
			@Override
			public void onMarkerDrag(Marker marker) {
			}

			@Override
			public void onMarkerDragEnd(Marker marker) {	
				LatLng markerLatlng = marker.getPosition();

				if (((app.getSelectedServer() != null) && LocationUtil.checkPointInBoundingBox(markerLatlng, app.getSelectedServer(), OTPApp.CHECK_BOUNDS_ACCEPTABLE_ERROR)) 
						|| (app.getSelectedServer() == null)){
					if ((startMarker != null) && (marker.hashCode() == startMarker.hashCode())){
						if (prefs.getBoolean(OTPApp.PREFERENCE_KEY_USE_INTELLIGENT_MARKERS, true)){
							updateMarkerPosition(markerLatlng, true);
						}
						else{
							isStartLocationGeocodingProcessed = true;
						}
						startMarkerPosition = markerLatlng;
					}
					else if ((endMarker != null) && (marker.hashCode() == endMarker.hashCode())){
						if (prefs.getBoolean(OTPApp.PREFERENCE_KEY_USE_INTELLIGENT_MARKERS, true)){
							updateMarkerPosition(markerLatlng, false);
						}
						else{
							isEndLocationGeocodingProcessed = true;
						}
						endMarkerPosition = markerLatlng;
					}
				}
				else{

					if ((startMarker != null) && (marker.hashCode() == startMarker.hashCode())){
						marker.setPosition(startMarkerPosition);
					}
					else{
						marker.setPosition(endMarkerPosition);
					}
					Toast.makeText(applicationContext, applicationContext.getResources().getString(R.string.marker_out_of_boundaries), Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onMarkerDragStart(Marker marker) {
				InputMethodManager imm = (InputMethodManager) MainFragment.this.getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(tbEndLocation.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(tbStartLocation.getWindowToken(), 0);
			}
		};
		mMap.setOnMarkerDragListener(omdl);
		
		OnMapLongClickListener omlcl = new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latlng) {
				InputMethodManager imm = (InputMethodManager) MainFragment.this.getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(tbEndLocation.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(tbStartLocation.getWindowToken(), 0);
				
				final LatLng latLngFinal = latlng;
				final CharSequence[] items = {applicationContext.getResources().getString(R.string.start_marker_activated), applicationContext.getResources().getString(R.string.end_marker_activated)};

				AlertDialog.Builder builder = new AlertDialog.Builder(MainFragment.this.getActivity());
				builder.setTitle(getResources().getString(R.string.markers_dialog_title));
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0){
							setMarker(true, latLngFinal, true);
						}
						else{
							setMarker(false, latLngFinal, true);
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		};
		mMap.setOnMapLongClickListener(omlcl);
		
		OnClickListener oclH = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(Gravity.LEFT);
			}
		};
		btnHandle.setOnClickListener(oclH);
		
		OnInfoWindowClickListener omliwcl = new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker modeMarker) {
				saveOTPBundle();
				OTPBundle otpBundle = getFragmentListener().getOTPBundle();
				Matcher matcher = Pattern.compile("\\d+").matcher(modeMarker.getTitle());
				if(matcher.find()){
					String numberString = modeMarker.getTitle().substring(0, matcher.end());
					//Step indexes shown to the user are in a scale starting by 1 but instructions steps internally start by 0
					int currentStepIndex = Integer.parseInt(numberString) - 1;
					otpBundle.setCurrentStepIndex(currentStepIndex);
					otpBundle.setFromInfoWindow(true);
					getFragmentListener().setOTPBundle(otpBundle);
					getFragmentListener().onSwitchedToDirectionFragment();		
				}
							
			}
		};
		mMap.setOnInfoWindowClickListener(omliwcl);
	}
	
	
	private void restoreState(Bundle savedInstanceState){
		if (savedInstanceState != null){
			mMap = retrieveMap(mMap);
			
			if (!mapFailed){
				boolean mapFailedBefore = savedInstanceState.getBoolean(OTPApp.BUNDLE_KEY_MAP_FAILED);
				
				if (mapFailedBefore){
					enableUIElements(true);
					
					initializeMapInterface(mMap);
				}

	    		if (!mapFailedBefore){
					String overlayString = prefs.getString(OTPApp.PREFERENCE_KEY_MAP_TILE_SOURCE, applicationContext.getResources().getString(R.string.map_tiles_default_server));
					updateOverlay(overlayString);
	    		}
				
				setTextBoxLocation(savedInstanceState.getString(OTPApp.BUNDLE_KEY_TB_START_LOCATION), true);
				setTextBoxLocation(savedInstanceState.getString(OTPApp.BUNDLE_KEY_TB_END_LOCATION), false);
				CameraPosition camPosition = savedInstanceState.getParcelable(OTPApp.BUNDLE_KEY_MAP_CAMERA);
				if (camPosition != null){
					mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPosition));
				}
				
				if ((startMarkerPosition = savedInstanceState.getParcelable(OTPApp.BUNDLE_KEY_MAP_START_MARKER_POSITION)) != null){
					startMarker = addStartEndMarker(startMarkerPosition, true);
				}
				if ((endMarkerPosition = savedInstanceState.getParcelable(OTPApp.BUNDLE_KEY_MAP_END_MARKER_POSITION)) != null){
					endMarker = addStartEndMarker(endMarkerPosition, false);
				}
				
				isStartLocationGeocodingProcessed = savedInstanceState.getBoolean(OTPApp.BUNDLE_KEY_IS_START_LOCATION_GEOCODING_PROCESSED);
				isEndLocationGeocodingProcessed = savedInstanceState.getBoolean(OTPApp.BUNDLE_KEY_IS_END_LOCATION_GEOCODING_PROCESSED);
				appStarts = savedInstanceState.getBoolean(OTPApp.BUNDLE_KEY_APP_STARTS);
				isStartLocationChangedByUser = savedInstanceState.getBoolean(OTPApp.BUNDLE_KEY_IS_START_LOCATION_CHANGED_BY_USER);
				isEndLocationChangedByUser = savedInstanceState.getBoolean(OTPApp.BUNDLE_KEY_IS_END_LOCATION_CHANGED_BY_USER);
				
				savedLastLocation = savedInstanceState.getParcelable(OTPApp.BUNDLE_KEY_SAVED_LAST_LOCATION);
				savedLastLocationCheckedForServer = savedInstanceState.getParcelable(OTPApp.BUNDLE_KEY_SAVED_LAST_LOCATION_CHECKED_FOR_SERVER);

//	    		showBikeParameters(false);
	    		
				ddlTravelMode.setItemChecked(savedInstanceState.getInt(OTPApp.BUNDLE_KEY_DDL_TRAVEL_MODE), true);
	        	TraverseModeSpinnerItem traverseModeSpinnerItem = (TraverseModeSpinnerItem) ddlTravelMode.getItemAtPosition(ddlTravelMode.getCheckedItemPosition());			
	        	if (traverseModeSpinnerItem.getTraverseModeSet().contains(TraverseMode.BICYCLE)){
		    		setBikeOptimizationAdapter(true);
					ddlOptimization.setItemChecked(savedInstanceState.getInt(OTPApp.BUNDLE_KEY_DDL_OPTIMIZATION), true);
//		    		OptimizeSpinnerItem optimizeSpinnerItem = (OptimizeSpinnerItem) ddlOptimization.getItemAtPosition(ddlOptimization.getCheckedItemPosition());
//		    		if (optimizeSpinnerItem.getOptimizeType().equals(OptimizeType.TRIANGLE)){
//			    		showBikeParameters(true);
//		    		}
	        	}
				ddlTravelMode.setItemChecked(savedInstanceState.getInt(OTPApp.BUNDLE_KEY_DDL_TRAVEL_MODE), true);

				OTPBundle otpBundle = (OTPBundle) savedInstanceState.getSerializable(OTPApp.BUNDLE_KEY_OTP_BUNDLE);
				if (otpBundle != null){
					List<Itinerary> itineraries = otpBundle.getItineraryList(); 
					getFragmentListener().onItinerariesLoaded(itineraries);
					getFragmentListener().onItinerarySelected(otpBundle.getCurrentItineraryIndex());
					fillItinerariesSpinner(itineraries);
				}
				showRouteOnMap(getFragmentListener().getCurrentItinerary(), false);
				
//				Date savedTripDate = (Date) savedInstanceState.getSerializable(OTPApp.BUNDLE_KEY_TRIP_DATE);
//				if (savedTripDate != null){
//					tripDate = savedTripDate;
//				}
				arriveBy = savedInstanceState.getBoolean(OTPApp.BUNDLE_KEY_ARRIVE_BY, false);
				
				if (savedInstanceState.getString(OTPApp.BUNDLE_KEY_RESULT_TRIP_START_LOCATION) != null){
					resultTripStartLocation = savedInstanceState.getString(OTPApp.BUNDLE_KEY_RESULT_TRIP_START_LOCATION);
				}
				if (savedInstanceState.getString(OTPApp.BUNDLE_KEY_RESULT_TRIP_END_LOCATION) != null){
					resultTripEndLocation = savedInstanceState.getString(OTPApp.BUNDLE_KEY_RESULT_TRIP_END_LOCATION);
				}
				
//				bikeTriangleMinValue = savedInstanceState.getDouble(OTPApp.BUNDLE_KEY_SEEKBAR_MIN_VALUE);
//				bikeTriangleMaxValue = savedInstanceState.getDouble(OTPApp.BUNDLE_KEY_SEEKBAR_MAX_VALUE);
//				bikeTriangleParameters.setSelectedMinValue(bikeTriangleMinValue);
//				bikeTriangleParameters.setSelectedMaxValue(bikeTriangleMaxValue);

				isStartLocationChangedByUser = false;
				isEndLocationChangedByUser = false;
			}
		}
	}
	
	/**
	 * Activates/disactivates all the UI, avoiding to take care of the possible
	 * listeners functions if the application is in a non working state.
	 * 
	 * @param enable if true elements will be enabled
	 */
	private void enableUIElements(boolean enable){
		int visibility;
		if (enable){
			setHasOptionsMenu(true);
			visibility = View.VISIBLE;
		}
		else{
			setHasOptionsMenu(false);
			visibility = View.INVISIBLE;
		}
		tbStartLocation.setVisibility(visibility);
		tbEndLocation.setVisibility(visibility);
		btnPlanTrip.setVisibility(visibility);
		btnDateDialog.setVisibility(visibility);
		btnMyLocation.setVisibility(visibility);
		panelDisplayDirection.setVisibility(visibility);
		navigationDrawerLeftPane.setVisibility(visibility);
		btn_favourite.setVisibility(visibility);
		btn_monitoring.setVisibility(visibility);
		btnPOI.setVisibility(visibility);
		btnRealTime.setVisibility(visibility);
		//btnReRoute.setVisibility(visibility);
		btnPoi_route.setVisibility(visibility);

	}
	
	
	
	
	
	private void requestTrip(){
		
		LatLng mCurrentLatLng = getLastLocation();
		String startLocationString = null;
		String endLocationString = null;

		Boolean isOriginMyLocation = prefs.getBoolean(OTPApp.PREFERENCE_KEY_ORIGIN_IS_MY_LOCATION, false);
		Boolean isDestinationMyLocation = prefs.getBoolean(OTPApp.PREFERENCE_KEY_DESTINATION_IS_MY_LOCATION, false);
		
		panelDisplayDirection.setVisibility(View.INVISIBLE);
		if (route != null){
			for (Polyline p : route){
				p.remove();
			}
			route = null;
		}
		if (modeMarkers != null){
			for (Marker m : modeMarkers){
				m.remove();
			}
			modeMarkers = null;
		}

		
		if (isOriginMyLocation && isDestinationMyLocation){
			Toast.makeText(MainFragment.this.applicationContext, applicationContext.getResources().getString(R.string.origin_destination_are_mylocation), Toast.LENGTH_SHORT).show();
			return;
		}
		else if (isOriginMyLocation || isDestinationMyLocation){
			if (mCurrentLatLng == null){
				Toast.makeText(MainFragment.this.applicationContext, applicationContext.getResources().getString(R.string.location_error), Toast.LENGTH_LONG).show();
				return;
			}
			else {
				if (isOriginMyLocation){
					startLocationString = mCurrentLatLng.latitude + "," + mCurrentLatLng.longitude;
					if (endMarker == null){
//						Toast.makeText(MainFragment.this.applicationContext, applicationContext.getResources().getString(R.string.need_to_place_markers_before_planning), Toast.LENGTH_SHORT).show();
						return;
					}
					else{
						endLocationString = endMarker.getPosition().latitude + "," + endMarker.getPosition().longitude;
					}
				}
				else if (isDestinationMyLocation){
					endLocationString = mCurrentLatLng.latitude + "," + mCurrentLatLng.longitude;
					if (startMarker == null){
//						Toast.makeText(MainFragment.this.applicationContext, applicationContext.getResources().getString(R.string.need_to_place_markers_before_planning), Toast.LENGTH_SHORT).show();
						return;
					}
					else{
						startLocationString = startMarker.getPosition().latitude + "," + startMarker.getPosition().longitude;
					}
				}
			}
		}
		else{
			if ((startMarker == null) || (endMarker == null)){
//				Toast.makeText(MainFragment.this.applicationContext, applicationContext.getResources().getString(R.string.need_to_place_markers_before_planning), Toast.LENGTH_SHORT).show();
				return;
			}
			else{
				startLocationString = startMarker.getPosition().latitude + "," + startMarker.getPosition().longitude;
				endLocationString = endMarker.getPosition().latitude + "," + endMarker.getPosition().longitude;
			}
		}
				
		if (!isStartLocationGeocodingProcessed && !isOriginMyLocation){
//			Toast.makeText(MainFragment.this.applicationContext, applicationContext.getResources().getString(R.string.need_to_place_markers_before_planning), Toast.LENGTH_SHORT).show();
			return;
		}
		else if (!isEndLocationGeocodingProcessed && !isDestinationMyLocation){
//			Toast.makeText(MainFragment.this.applicationContext, applicationContext.getResources().getString(R.string.need_to_place_markers_before_planning), Toast.LENGTH_SHORT).show();
			return;
		}
		
		
		Request request = new Request();
		
		try {
			if(flag_re) {
				ArrayList<String> listIntermediate = new ArrayList<String>();
				
				if(result_list.size()>0){
					for(int i=0;i<result_list.size();i++){
						
						String events=result_list.get(i); 
				    	   
					 	String event_array[]=events.split(",");
						
						Double lat_in=Double.parseDouble(event_array[1]);
						Double lng_in=Double.parseDouble(event_array[2]);
						
						Double radius=500.0;
				        //$LatLng=$event;
				        Double latRadiansDistance = radius / 6378137;
				        Double latDegreesDistance = Math.toDegrees(latRadiansDistance);
				        Double lngDegreesDistance = Math.toDegrees(latRadiansDistance / Math.cos(Math.toRadians(lat_in)));
				    
				        // SW point
				        Double swLat = lat_in - latDegreesDistance;
				        Double swLng = lng_in - lngDegreesDistance;
				        //$sw = new LatLng($swLat, $swLng);
				        
				        // NE point
				        Double neLat = lat_in + latDegreesDistance;
				        Double neLng = lng_in + lngDegreesDistance;
				        //$ne = new LatLng($neLat, $neLng);
						String inPoint=neLat+","+neLng;
//						listIntermediate.add(URLEncoder.encode("45.186272,9.148822", OTPApp.URL_ENCODING));
				        listIntermediate.add(inPoint);
				      
					}
					    request.setFrom(URLEncoder.encode(startLocationString, OTPApp.URL_ENCODING));
						request.setIntermediatePlaces(listIntermediate);
						request.setTo(URLEncoder.encode(endLocationString, OTPApp.URL_ENCODING));

				}
							
			}else{
				
				request.setFrom(URLEncoder.encode(startLocationString, OTPApp.URL_ENCODING));
				request.setTo(URLEncoder.encode(endLocationString, OTPApp.URL_ENCODING));
				
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		
		request.setArriveBy(arriveBy);
		
		OptimizeSpinnerItem optimizeSpinnerItem = (OptimizeSpinnerItem) ddlOptimization.getItemAtPosition(ddlOptimization.getCheckedItemPosition());
		if (optimizeSpinnerItem == null){
			optimizeSpinnerItem = (OptimizeSpinnerItem) ddlOptimization.getItemAtPosition(0);
		}
		
		request.setOptimize(optimizeSpinnerItem.getOptimizeType());
		
//		if (optimizeSpinnerItem.getOptimizeType().equals(OptimizeType.TRIANGLE)){
//			request.setTriangleTimeFactor(bikeTriangleMinValue);
//			request.setTriangleSlopeFactor(bikeTriangleMaxValue - bikeTriangleMinValue);
//			request.setTriangleSafetyFactor(1 - bikeTriangleMaxValue);
//		}
		
		TraverseModeSpinnerItem traverseModeSpinnerItem = (TraverseModeSpinnerItem) ddlTravelMode.getItemAtPosition(ddlTravelMode.getCheckedItemPosition());				
		if (traverseModeSpinnerItem == null){
			traverseModeSpinnerItem = (TraverseModeSpinnerItem) ddlTravelMode.getItemAtPosition(0);
		}
		request.setModes(traverseModeSpinnerItem.getTraverseModeSet());
		
		
		Integer defaultMaxWalkInt = applicationContext.getResources().getInteger(R.integer.max_walking_distance);

		try {
			Double maxWalk = Double.parseDouble(prefs.getString(OTPApp.PREFERENCE_KEY_MAX_WALKING_DISTANCE,
					defaultMaxWalkInt.toString()));
			request.setMaxWalkDistance(maxWalk);
		} catch (NumberFormatException ex) {
			request.setMaxWalkDistance((double)defaultMaxWalkInt);
		}

		request.setWheelchair(prefs.getBoolean(OTPApp.PREFERENCE_KEY_WHEEL_ACCESSIBLE,
				false));
		
		Date requestTripDate;
		if (tripDate == null){
			requestTripDate = Calendar.getInstance().getTime();
		}
		else{
			requestTripDate = tripDate;
		}

		request.setDateTime(DateFormat.format(OTPApp.FORMAT_OTP_SERVER_DATE_QUERY,requestTripDate.getTime()).toString(),
				DateFormat.format(OTPApp.FORMAT_OTP_SERVER_TIME_QUERY, requestTripDate.getTime()).toString());

		request.setShowIntermediateStops(Boolean.TRUE);
		
		WeakReference<Activity> weakContext = new WeakReference<Activity>(MainFragment.this.getActivity());

		new TripRequest(weakContext, MainFragment.this.applicationContext, app
				.getSelectedServer(), MainFragment.this)
				.execute(request);

		InputMethodManager imm = (InputMethodManager) MainFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(tbEndLocation.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(tbStartLocation.getWindowToken(), 0);
		
//		tripDate = null;
	}
	
	/**
	 * Retrieves a map if the map fragment parameter is null.
	 * <p>
	 * If there is an error tries to solve it checking if it was because of
	 * "Google Play Services" sending the corresponding intent.
	 * 
	 * @param mMap map fragment to check if the map is already initialized
	 * @return initialized map fragment
	 */
	private GoogleMap retrieveMap(GoogleMap mMap) {
	    // Do a null check to confirm that we have not already instantiated the map.
		mapFailed = false;
		
	    if (mMap == null) {
	        mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

	        // Check if we were successful in obtaining the map.
	        if (mMap == null) {
		        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(applicationContext);
		        
		        if(status!=ConnectionResult.SUCCESS){
		        	enableUIElements(false);
		            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), OTPApp.CHECK_GOOGLE_PLAY_REQUEST_CODE);
		            dialog.show();
		            mapFailed = true;
		        }	        
		    }

	    }
		
	    return mMap;
	}
	
	/**
	 * Wrapper to trigger functions to disable bike parameters and effectively
	 * show them as inactive (faded).
	 * 
	 * @param enable when true parameters are shown
	 */
//	private void showBikeParameters(boolean enable){
//		setRangeSeekBarStateColors(enable, bikeTriangleParameters);
//		disableEnableControls(enable, bikeTriangleParametersLayout);
//	}
	
	/**
	 * Changes optimization spinner values to show values compatibles with
	 * bikes or with transit. 
	 * <p>
	 * Replaces fewest transfers with safer trip options.
	 * 
	 * @param enable when true spinner is set to bike values
	 */
	private void setBikeOptimizationAdapter(boolean enable){
		ArrayAdapter<OptimizeSpinnerItem> optimizationAdapter;
		
		if (enable){
			optimizationAdapter = new ArrayAdapter<OptimizeSpinnerItem>(
					getActivity(),
					android.R.layout.simple_list_item_single_choice,
					new OptimizeSpinnerItem[] {
							new OptimizeSpinnerItem(getResources().getString(R.string.optimization_quick), OptimizeType.QUICK),
							new OptimizeSpinnerItem(getResources().getString(R.string.optimization_safe), OptimizeType.SAFE),
							new OptimizeSpinnerItem(getResources().getString(R.string.optimization_bike_triangle), OptimizeType.TRIANGLE) });
			ddlOptimization.setAdapter(optimizationAdapter);
			ddlOptimization.setItemChecked(2, true);
		}
		else {
			optimizationAdapter = new ArrayAdapter<OptimizeSpinnerItem>(
					getActivity(),
					android.R.layout.simple_list_item_single_choice,
					new OptimizeSpinnerItem[] {
							new OptimizeSpinnerItem(getResources().getString(R.string.optimization_quick), OptimizeType.QUICK),
							new OptimizeSpinnerItem(getResources().getString(R.string.optimization_safe), OptimizeType.SAFE),
							new OptimizeSpinnerItem(getResources().getString(R.string.optimization_fewest_transfers),
									OptimizeType.TRANSFERS) });
			ddlOptimization.setAdapter(optimizationAdapter);
			ddlOptimization.setItemChecked(0, true);
		}
	}
	
	/**
	 * Toggles between standard colors and faded colors for the passed seekbar
	 * to visually show that it's disabled.
	 * 
	 * @param enable when true standard colors are used
	 * @param seekBar bar that will be toggled
	 */
//	private void setRangeSeekBarStateColors(boolean enable, RangeSeekBar<Double> seekBar){
//		if (enable){
//			seekBar.setLeftColor(getResources().getColor(R.color.sysRed));
//			seekBar.setMiddleColor(getResources().getColor(R.color.sysGreen));
//			seekBar.setRightColor(getResources().getColor(R.color.sysBlue));
//		}
//		else{
//			seekBar.setLeftColor(getResources().getColor(R.color.sysRedFaded));
//			seekBar.setMiddleColor(getResources().getColor(R.color.sysGreenFaded));
//			seekBar.setRightColor(getResources().getColor(R.color.sysBlueFaded));
//		}
//		
//	}
	
	/**
	 * Recursively enable/disable all the views contained in a ViewGroup and
	 * it's descendants. 
	 * 
	 * @param enable when true views will be disable
	 * @param vg a ViewGroup that will be modified
	 */
//	private void disableEnableControls(boolean enable, ViewGroup vg){
//	    for (int i = 0; i < vg.getChildCount(); i++){
//	       View child = vg.getChildAt(i);
//	       child.setEnabled(enable);
//	       if (child instanceof ViewGroup){ 
//	          disableEnableControls(enable, (ViewGroup)child);
//	       }
//	    }
//	}


	/**
	 * Triggers ServerSelector task to retrieve servers list.
	 * <p>
	 * Server list will be downloaded or retrieved from the database.
	 * <p>
	 * A valid location should be passed to perform server autodetection if the
	 * preference is set. If location is null a toast will be displayed
	 * informing of the error.
	 * <p>
	 * It it's not possible or not requested to autodetect the server list will
	 * be displayed.
	 * 
	 * @param mCurrentLatLng location to use if servers should be detected
	 */
	public void runAutoDetectServer(LatLng mCurrentLatLng){
		if ((mCurrentLatLng == null) || (mMap == null)){
			Toast.makeText(applicationContext, applicationContext.getResources().getString(R.string.location_error), Toast.LENGTH_LONG).show();
		}
		else{
			ServersDataSource dataSource = ServersDataSource.getInstance(applicationContext);
			WeakReference<Activity> weakContext = new WeakReference<Activity>(getActivity());
	
			ServerSelector serverSelector = new ServerSelector(weakContext, applicationContext, dataSource, this, needToUpdateServersList);
			serverSelector.execute(mCurrentLatLng);
			savedLastLocationCheckedForServer = mCurrentLatLng;
		}
		setNeedToRunAutoDetect(false);
		setNeedToUpdateServersList(false);
	}
	
	/**
	 * Triggers ServerSelector task to retrieve servers list.
	 * <p>
	 * Server list will be downloaded or retrieved from the database.
	 * <p>
	 * A servers list will be displayed or a toast informing of the error.
	 * <p>
	 */
	public void runAutoDetectServerNoLocation(){
		ServersDataSource dataSource = ServersDataSource.getInstance(applicationContext);
		WeakReference<Activity> weakContext = new WeakReference<Activity>(getActivity());

		ServerSelector serverSelector = new ServerSelector(weakContext, applicationContext, dataSource, this, needToUpdateServersList);
		LatLng latLngList[] = new LatLng[1];
		latLngList[0] = null;
		serverSelector.execute(latLngList);
		setNeedToRunAutoDetect(false);
		setNeedToUpdateServersList(false);
	}
	
	/**
	 * Registers the server in the OTPApp class.
	 * <p>
	 * UI is restored to avoid presence of all server data, removing all
	 * objects from the map and restarting text boxes to default contents.
	 * <p>
	 * OTPApp can be requested calling to getActivity by other fragments.
	 * 
	 * @param s
	 * @param restartUI
	 */
	private void setSelectedServer(Server s, boolean restartUI){
		if (restartUI){
			restartMap();
			restartTextBoxes();
		}
		
		app.setSelectedServer(s);
	}
	
	/**
	 * Removes all map objects and the global variables that reference them in
	 * this fragment.
	 */
	private void restartMap(){
		if (startMarker != null){
			startMarker.remove();
		}
		if (endMarker != null){
			endMarker.remove();
		}
		if (modeMarkers != null){
			for (Marker m : modeMarkers){
				m.remove();
			}		}
		if (route != null){
			for (Polyline p : route){
				p.remove();
			}		}
		if (boundariesPolyline != null){
			boundariesPolyline.remove();
		}
		
		startMarker = null;
		startMarkerPosition = null;
		endMarker = null;
		endMarkerPosition = null;
		route = null;
		modeMarkers = null;
		boundariesPolyline = null;
	}
	
	/**
	 * Sets text boxes to initial default locations.
	 * <p>
	 * MyLocation for start text box and empty for end text box.
	 * <p>
	 * Accordingly preference with key PREFERENCE_KEY_ORIGIN_IS_MY_LOCATION
	 * is set. 
	 */
	private void restartTextBoxes(){
		SharedPreferences.Editor prefsEditor = prefs.edit();
		setTextBoxLocation(applicationContext.getResources().getString(R.string.my_location), true);
		prefsEditor.putBoolean(OTPApp.PREFERENCE_KEY_ORIGIN_IS_MY_LOCATION, true);
		prefsEditor.commit();
		
		setTextBoxLocation("", false);
	}
	
	/**
	 * Writes coordinates of latlng to the selected text box. 
	 * 
	 * @param latlng object containing the coordinates to set
	 * @param isStartTb when true start text box is set otherwise end text box
	 */
	private void setLocationTb(LatLng latlng, boolean isStartTb){
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		DecimalFormat decimalFormat = new DecimalFormat(OTPApp.FORMAT_COORDINATES, decimalFormatSymbols);
		if (isStartTb){
			setTextBoxLocation(decimalFormat.format(latlng.latitude) + ", " + decimalFormat.format(latlng.longitude), true);
		}
		else{
			setTextBoxLocation(decimalFormat.format(latlng.latitude) + ", " + decimalFormat.format(latlng.longitude), false);
		}
	}
	
	/**
	 * Moves or adds (if didn't existed) a start/end marker to latlng position
	 * and updates its text box.
	 * <p>
	 * If preference with key PREFERENCE_KEY_USE_INTELLIGENT_MARKERS is set
	 * geocoding will be triggered for text boxes.
	 * <p>
	 * If the marker does not fit in selected server bounds marker won't be set
	 * and a warning will be shown.
	 * 
	 * @param isStartMarker when true start marker will be set
	 * @param latlng the location to move on
	 * @param showMessage whether show or not informative message on success
	 */
	private void setMarker(boolean isStartMarker, LatLng latlng, boolean showMessage){
		SharedPreferences.Editor prefsEditor = prefs.edit();
		
		if (((app.getSelectedServer() != null) && LocationUtil.checkPointInBoundingBox(latlng, app.getSelectedServer(), OTPApp.CHECK_BOUNDS_ACCEPTABLE_ERROR)) 
				|| (app.getSelectedServer() == null)){
			if (showMessage){
				String toasText;
				if (isStartMarker){
					toasText = applicationContext.getResources().getString(R.string.start_marker_activated);
				}
				else{
					toasText = applicationContext.getResources().getString(R.string.end_marker_activated);
				}
				Toast.makeText(applicationContext, toasText, Toast.LENGTH_SHORT).show();
			}
			
			if(isStartMarker) {
				if (startMarker == null){
					startMarker = addStartEndMarker(latlng, true);
				}
				else{
					setMarkerPosition(true, latlng);
					startMarkerPosition = latlng;
				}
				MainFragment.this.setLocationTb(latlng, true);
				prefsEditor.putBoolean(OTPApp.PREFERENCE_KEY_ORIGIN_IS_MY_LOCATION, false);
				if (prefs.getBoolean(OTPApp.PREFERENCE_KEY_USE_INTELLIGENT_MARKERS, true)){
					updateMarkerPosition(latlng, true);
				}
				else{
					isStartLocationGeocodingProcessed = true;
				}
			}
			else {
				if (endMarker == null){
					endMarker = addStartEndMarker(latlng, false);
				}			
				else{
					setMarkerPosition(false, latlng);
					endMarkerPosition = latlng;
				}
				MainFragment.this.setLocationTb(latlng, false);
				prefsEditor.putBoolean(OTPApp.PREFERENCE_KEY_DESTINATION_IS_MY_LOCATION, false);
				if (prefs.getBoolean(OTPApp.PREFERENCE_KEY_USE_INTELLIGENT_MARKERS, true)){
					updateMarkerPosition(latlng, false);
				}
				else{
					isEndLocationGeocodingProcessed = true;
				}
			}
			prefsEditor.commit();
		}
		else{
			if (showMessage){
				Toast.makeText(applicationContext, applicationContext.getResources().getString(R.string.marker_out_of_boundaries), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * Updates marker or creates a new one if doesn't exit to the passed latlng
	 * <p>
	 * Accordingly updates the field used for save/restore purposes.
	 *   
	 * @param isStartMarker if true start marker will be changed, end marker
	 * otherwise
	 * @param latLng contains the coordinates of the position to be changed to
	 */
	private void setMarkerPosition(boolean isStartMarker, LatLng latLng){
		if (isStartMarker){
			if (startMarker == null){
				startMarker = addStartEndMarker(latLng, true);
			}
			else{
				startMarker.setPosition(latLng);
			}
			startMarkerPosition = latLng;
		}
		else{
			if (endMarker == null){
				endMarker = addStartEndMarker(latLng, false);
			}
			else{
				endMarker.setPosition(latLng);			
			}
			endMarkerPosition = latLng;
		}		
	}
	
	/**
	 * Creates and adds to the map a new start/end marker.
	 * <p>
	 * Accordingly updates the field used for save/restore purposes.
	 * 
	 * @param latLng the position to initialize the new marker
	 * @param isStartMarker if true a start marker will be created
	 * @return the new marker created
	 */
	private Marker addStartEndMarker(LatLng latLng, boolean isStartMarker){
		MarkerOptions markerOptions = new MarkerOptions().position(latLng)
														 .draggable(true);
		if (isStartMarker){
			markerOptions.title(applicationContext.getResources().getString(R.string.start_marker_title))
						 .snippet(applicationContext.getResources().getString(R.string.start_marker_description))
						 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			startMarkerPosition = latLng;
			return mMap.addMarker(markerOptions);
		}
		else{
			markerOptions.title(applicationContext.getResources().getString(R.string.end_marker_title))
						 .snippet(applicationContext.getResources().getString(R.string.end_marker_description))
						 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			endMarkerPosition = latLng;
			return mMap.addMarker(markerOptions);
		}
	}
	
	
	private String getLocationTbText(boolean isTbStartLocation){
		if (isTbStartLocation){
			return tbStartLocation.getText().toString();
		}
		else{
			return tbEndLocation.getText().toString();
		}
	}
	
	/**
	 * Updates the text box contents to the given location and triggers
	 * geocoding for that location to update the text box.
	 * <p>
	 * This is a wrapper for setLocationTb, processAddres and accordingly change
	 * the field to control if the text box was changed by the user.
	 * 
	 * @param newLatLng
	 * @param isStartMarker
	 */
	private void updateMarkerPosition(LatLng newLatLng, boolean isStartMarker){
		setLocationTb(newLatLng, isStartMarker);
		String locationText = getLocationTbText(isStartMarker);
		if (isStartMarker){
			isStartLocationChangedByUser = false;
		}
		else{
			isEndLocationChangedByUser = false;
		}
		processAddress(isStartMarker, locationText, true);
	}
	
	@Override
	public void onStart() {
		super.onStart();	
		
		mLocationClient = new LocationClient(applicationContext, this, this);
		//mLocationClient.connect();
		
		
		if (mapFailed){
				mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
                        .getMap();
    // Check if we were successful in obtaining the map.
				if (mMap != null) {
					enableUIElements(true);
					
					initializeMapInterface(mMap);
					
					runAutoDetectServerNoLocation();
				}
			}
		
		/*if (mapFailed){
			Intent i = this.getActivity().getBaseContext().getPackageManager()
		             .getLaunchIntentForPackage( this.getActivity().getBaseContext().getPackageName() );
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		}*/
		
		connectLocationClient();
	}
	
	/**
	 * Connects the LocationClient.
	 * <p>
	 * To avoid errors only tries if is not pending for another connection
	 * request or is disconnected.
	 */
	public void connectLocationClient(){
		if (!mLocationClient.isConnected() && !mLocationClient.isConnecting()){
			mLocationClient.connect();
		}
	}
	
	/**
	 * Disconnects the LocationClient.
	 * <p>
	 * To avoid errors only tries if it's connected.
	 */
	public void disconnectLocationClient(){
		if (mLocationClient.isConnected()){
			mLocationClient.disconnect();
		}
	}
	
	public void onSaveInstanceState(Bundle bundle){
		super.onSaveInstanceState(bundle);
		
		bundle.putBoolean(OTPApp.BUNDLE_KEY_MAP_FAILED, mapFailed);

		if (!mapFailed){
			bundle.putParcelable(OTPApp.BUNDLE_KEY_MAP_CAMERA, mMap.getCameraPosition());
			bundle.putParcelable(OTPApp.BUNDLE_KEY_MAP_START_MARKER_POSITION, startMarkerPosition);
			bundle.putParcelable(OTPApp.BUNDLE_KEY_MAP_END_MARKER_POSITION, endMarkerPosition);
			bundle.putBoolean(OTPApp.BUNDLE_KEY_APP_STARTS, appStarts);
			bundle.putBoolean(OTPApp.BUNDLE_KEY_IS_START_LOCATION_GEOCODING_PROCESSED, isStartLocationGeocodingProcessed);
			bundle.putBoolean(OTPApp.BUNDLE_KEY_IS_END_LOCATION_GEOCODING_PROCESSED, isEndLocationGeocodingProcessed);
			bundle.putBoolean(OTPApp.BUNDLE_KEY_IS_START_LOCATION_CHANGED_BY_USER, isStartLocationChangedByUser);
			bundle.putBoolean(OTPApp.BUNDLE_KEY_IS_END_LOCATION_CHANGED_BY_USER, isEndLocationChangedByUser);
			bundle.putString(OTPApp.BUNDLE_KEY_TB_START_LOCATION, tbStartLocation.getText().toString());
			bundle.putString(OTPApp.BUNDLE_KEY_TB_END_LOCATION, tbEndLocation.getText().toString());
			bundle.putInt(OTPApp.BUNDLE_KEY_DDL_OPTIMIZATION, ddlOptimization.getCheckedItemPosition());
			bundle.putInt(OTPApp.BUNDLE_KEY_DDL_TRAVEL_MODE, ddlTravelMode.getCheckedItemPosition());
			
			bundle.putParcelable(OTPApp.BUNDLE_KEY_SAVED_LAST_LOCATION, savedLastLocation);
			bundle.putParcelable(OTPApp.BUNDLE_KEY_SAVED_LAST_LOCATION_CHECKED_FOR_SERVER, savedLastLocationCheckedForServer);
			
			if (resultTripStartLocation != null){
				bundle.putString(OTPApp.BUNDLE_KEY_RESULT_TRIP_START_LOCATION, resultTripStartLocation);
			}
			if (resultTripEndLocation != null){
				bundle.putString(OTPApp.BUNDLE_KEY_RESULT_TRIP_END_LOCATION, resultTripEndLocation);
			}

//			bundle.putDouble(OTPApp.BUNDLE_KEY_SEEKBAR_MIN_VALUE, bikeTriangleMinValue);
//			bundle.putDouble(OTPApp.BUNDLE_KEY_SEEKBAR_MAX_VALUE, bikeTriangleMaxValue);

//			bundle.putSerializable(OTPApp.BUNDLE_KEY_TRIP_DATE, tripDate);
			bundle.putBoolean(OTPApp.BUNDLE_KEY_ARRIVE_BY, arriveBy);
			
			if (!fragmentListener.getCurrentItineraryList().isEmpty()){
				OTPBundle otpBundle = new OTPBundle();
				otpBundle.setFromText(resultTripStartLocation);
				otpBundle.setToText(resultTripEndLocation);
				otpBundle.setItineraryList(fragmentListener.getCurrentItineraryList());
				otpBundle.setCurrentItineraryIndex(fragmentListener.getCurrentItineraryIndex());
				otpBundle.setCurrentItinerary(fragmentListener.getCurrentItinerary());
				bundle.putSerializable(OTPApp.BUNDLE_KEY_OTP_BUNDLE, otpBundle);
			}
		}

	}

	/**
	 * Triggers geocoding for chosen text box with passed text.
	 * <p>
	 * If address contents are the String used to identify user's location
	 * ("MyLocation" for example) user location is passed to know the
	 * corresponding address. 
	 * In this case user's location shouldn't be null, if it is a toast is
	 * shown. 
	 * 
	 * @param isStartTextBox
	 * @param address
	 * @param geocodingForMarker
	 */
	public void processAddress(final boolean isStartTextBox, String address, boolean geocodingForMarker) {
		WeakReference<Activity> weakContext = new WeakReference<Activity>(getActivity());

		OTPGeocoding geocodingTask = new OTPGeocoding(weakContext, applicationContext,
				isStartTextBox, geocodingForMarker, app.getSelectedServer(), prefs.getString(
						OTPApp.PREFERENCE_KEY_GEOCODER_PROVIDER, applicationContext.getResources().getString(R.string.geocoder_nominatim)),
				this);	
		LatLng mCurrentLatLng = getLastLocation();

		if(address.equalsIgnoreCase(this.getResources().getString(R.string.my_location))) {
			if (mCurrentLatLng != null){
				geocodingTask.execute(address, String.valueOf(mCurrentLatLng.latitude), String.valueOf(mCurrentLatLng.longitude));
			}
			else{
				Toast.makeText(applicationContext, applicationContext.getResources().getString(R.string.location_error), Toast.LENGTH_LONG).show();	
			}
		}
		else{
			geocodingTask.execute(address);
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		appResumed = true;
		
		Log.v(TAG, "MainFragment onResume");
	}

	@Override
	public void onPause() {
		
		super.onPause();
	}
	
	@Override
	public void onStop() {
		disconnectLocationClient();
		getActivity().finish();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		// Release all map-related objects to make sure GPS is shut down when
		// the user leaves the app

		Log.d(TAG, "Released all map objects in MainFragment.onDestroy()");

		super.onDestroy();
	}

	
	public void updateSelectedServer(){
		if (prefs.getBoolean(OTPApp.PREFERENCE_KEY_SELECTED_CUSTOM_SERVER, false)){
			setSelectedServer(new Server(prefs.getString(OTPApp.PREFERENCE_KEY_CUSTOM_SERVER_URL, ""), applicationContext), true);
			Log.v(TAG, "Now using custom OTP server: " + prefs.getString(OTPApp.PREFERENCE_KEY_CUSTOM_SERVER_URL, ""));
			WeakReference<Activity> weakContext = new WeakReference<Activity>(getActivity());

			MetadataRequest metaRequest = new MetadataRequest(weakContext, applicationContext, this);
			metaRequest.execute(prefs.getString(OTPApp.PREFERENCE_KEY_CUSTOM_SERVER_URL, ""));
		}
		else{
			long serverId = prefs.getLong(OTPApp.PREFERENCE_KEY_SELECTED_SERVER, 0);
			if (serverId != 0){
				ServersDataSource dataSource = ServersDataSource.getInstance(applicationContext);
				dataSource.open();
				Server s = new Server(dataSource.getServer(prefs.getLong(OTPApp.PREFERENCE_KEY_SELECTED_SERVER, 0)));
				dataSource.close();
				
				setSelectedServer(s, true);
				addBoundariesRectangle(s);

				LatLng mCurrentLatLng = getLastLocation();
				
				if ((mCurrentLatLng != null) && (LocationUtil.checkPointInBoundingBox(mCurrentLatLng, s, OTPApp.CHECK_BOUNDS_ACCEPTABLE_ERROR))){
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, getServerInitialZoom(s)));
				}
				else{
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getServerCenter(s), getServerInitialZoom(s)));
					setMarker(true, getServerCenter(s), false);
				}
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu pMenu, MenuInflater inflater) {
		// MenuInflater inflater = getMenuInflater();
		super.onCreateOptionsMenu(pMenu, inflater);
		inflater.inflate(R.menu.menu, pMenu);
		mGPS = pMenu.getItem(0);
	}
	
	@Override
	public void onPrepareOptionsMenu(final Menu pMenu) {
		if (isGPSEnabled()) {
			mGPS.setTitle(R.string.disable_gps);
		} else {
			mGPS.setTitle(R.string.enable_gps);
		}
		super.onPrepareOptionsMenu(pMenu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem pItem) {
		OTPApp app = ((OTPApp) getActivity().getApplication());
		switch (pItem.getItemId()) {
		case R.id.gps_settings:
			Intent myIntent = new Intent(
					Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(myIntent);
			break;
		case R.id.settings:
			getActivity().startActivityForResult(
					new Intent(getActivity(), SettingsActivity.class),
					OTPApp.SETTINGS_REQUEST_CODE);
			break;
//		case R.id.feedback:
//			Server selectedServer = app.getSelectedServer();
//
//			String[] recipients = { selectedServer.getContactEmail(),
//					getString(R.string.feedback_email_android_developer) };
//
//			String uriText = "mailto:";
//			for (int i = 0; i < recipients.length; i++) {
//				uriText += recipients[i] + ";";
//			}
//
//			String subject = "";
//			subject += getResources().getString(R.string.feedback_subject);
//			Date d = Calendar.getInstance().getTime();
//			subject += "[" + d.toString() + "]";
//			uriText += "?subject=" + subject;
//
//			String content = ((MyActivity)getActivity()).getCurrentRequestString();
//			
//			try {
//				uriText += "&body=" + URLEncoder.encode(content, OTPApp.URL_ENCODING);
//			} catch (UnsupportedEncodingException e1) {
//				e1.printStackTrace();
//				return false;
//			}
//
//			Uri uri = Uri.parse(uriText);
//
//			Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
//			sendIntent.setData(uri);
//			startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.feedback_send_email)));
//
//			break;
//		case R.id.server_info:
//			Server server = app.getSelectedServer();
//			
//			if (server == null) {
//				Log.w(TAG,
//						"Tried to get server info when no server was selected");
//				Toast.makeText(applicationContext, applicationContext.getResources().getString(R.string.info_server_no_server_selected), Toast.LENGTH_SHORT).show();
//				break;
//			}
//		
//			WeakReference<Activity> weakContext = new WeakReference<Activity>(getActivity());
//
//			ServerChecker serverChecker = new ServerChecker(weakContext, applicationContext, true);
//			serverChecker.execute(server);
//				
//
//			break;
		default:
			break;
		}

		return false;
	}

	private Boolean isGPSEnabled() {
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * Wrapper to other functions: moves the marker to the location included
	 * in the address, updates text box and zooms to that position.
	 * 
	 * @param isStartMarker if true start marker will be changed
	 * @param addr will location and text information
	 */
	public void moveMarker(Boolean isStartMarker, Address addr) {
		if (isStartMarker) {
			startAddress = addr;
		} else {
			endAddress = addr;
		}
		LatLng latlng = new LatLng(addr.getLatitude(), addr.getLongitude());
		setMarkerPosition(isStartMarker, latlng);
		setTextBoxLocation(getStringAddress(addr, false), isStartMarker);
		zoomToGeocodingResult(isStartMarker, addr);
	}
	
	/**
	 * Wrapper to other functions: moves the marker to the location included
	 * in the address, updates text box and zooms to that position.
	 * <p>
	 * This only happens if the new location is closer than a constant to
	 * marker previous location. Otherwise address is only used as reference
	 * and text box is updated to "Marker close to [addr]".
	 * 
	 * @param isStartMarker if true start marker will be changed
	 * @param addr will location and text information
	 */
	public void moveMarkerRelative(Boolean isStartMarker, Address addr) {
		float results[] = new float[1];
		double addresLat = addr.getLatitude();
		double addresLon = addr.getLongitude();
		
		Marker marker;
		if (isStartMarker) {
			marker = startMarker;
			startAddress = addr;
		} else {
			marker = endMarker;
			endAddress = addr;
		}
		
		Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, addresLat, addresLon, results);
		
		if (results[0] < OTPApp.MARKER_GEOCODING_MAX_ERROR){
			LatLng newLatlng = new LatLng(addresLat, addresLon);
			setMarkerPosition(isStartMarker, newLatlng);
			setTextBoxLocation(getStringAddress(addr, false), isStartMarker);
		}
		else{
			setTextBoxLocation(getResources().getString(R.string.textbox_close_to_marker) + " " + getStringAddress(addr, false), isStartMarker);
		}

	}
	
	private String getStringAddress(Address address, boolean multilines){
		if (address.getMaxAddressLineIndex() >= 0){
			
			String result = address.getAddressLine(0);
			
			if (multilines){
				for (int i = 1; i <= address.getMaxAddressLineIndex(); i++){
					if (i == 1){
						result += "\n"; 
						if (address.getAddressLine(i) != null){
							result +=  address.getAddressLine(i);
						}
					}
					else if (i == 2){
						result += "\n"; 			
						if (address.getAddressLine(i) != null){
							result +=  address.getAddressLine(i);
						}
					}
					else{
						if (address.getAddressLine(i) != null){
							result += ", " + address.getAddressLine(i);
						}
					}
				}
			}
			else{
				for (int i = 1; i <= address.getMaxAddressLineIndex(); i++){
					if (address.getAddressLine(i) != null){
						result += ", " + address.getAddressLine(i);
					}
				}
			}

			return result;
		}
		else{
			return null;
		}
	}
	
	/**
	 * Zooms to addr or to addr and the location of the other marker if it's
	 * not the first marker.
	 * <p>
	 * If the other location is "MyLocation" will also be included in zoom.
	 * 
	 * @param isStartLocation if true addr is for start location
	 * @param addr with the location to zoom at
	 */
	public void zoomToGeocodingResult(boolean isStartLocation, Address addr) {
		LatLng latlng = new LatLng(addr.getLatitude(), addr.getLongitude());
		LatLng mCurrentLatLng = getLastLocation();
		
		if (isStartLocation){
			if (isStartLocationChangedByUser){
				if (endMarker != null){
					zoomToTwoPoints(latlng, endMarkerPosition);
				}
				else if (prefs.getBoolean(OTPApp.PREFERENCE_KEY_DESTINATION_IS_MY_LOCATION, false)){
					if (mCurrentLatLng == null){
						Toast.makeText(applicationContext, applicationContext.getResources().getString(R.string.location_error), Toast.LENGTH_LONG).show();
					}
					else{
						zoomToTwoPoints(latlng, mCurrentLatLng);
					}
				}
				else{
					zoomToLocation(latlng);
				}
			}
		}
		else {
			if (isEndLocationChangedByUser){
				if (startMarker != null){
					zoomToTwoPoints(startMarkerPosition, latlng);
				}
				else if (prefs.getBoolean(OTPApp.PREFERENCE_KEY_ORIGIN_IS_MY_LOCATION, false)){
					if (mCurrentLatLng == null){
						Toast.makeText(applicationContext, applicationContext.getResources().getString(R.string.location_error), Toast.LENGTH_LONG).show();
					}
					else{
						zoomToTwoPoints(mCurrentLatLng, latlng);
					}
				}
				else{
					zoomToLocation(latlng);
				}
			}
		}
	}

	public void zoomToLocation(LatLng latlng) {
		if (latlng != null) {
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, OTPApp.defaultMediumZoomLevel));
		}
	}
	
	public void zoomToTwoPoints(LatLng pointA, LatLng pointB) {
		if ((pointA.latitude != pointB.latitude) && (pointA.longitude != pointB.longitude)){
			LatLngBounds.Builder boundsCreator = LatLngBounds.builder();
			
			boundsCreator.include(pointA);
			boundsCreator.include(pointB);

			mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsCreator.build(), getResources().getInteger(R.integer.default_padding)));	
		}
	}

	/**
	 * Updates start/end text box contents to the given text.
	 * 
	 * @param text contents to insert
	 * @param isStartTextBox if true start box will be used
	 */
	public void setTextBoxLocation(String text, boolean isStartTextBox) {
		if (isStartTextBox) {
			isStartLocationChangedByUser = false;
			tbStartLocation.setText(text);
		} else {
			isEndLocationChangedByUser = false;
			tbEndLocation.setText(text);
		}
	}

	/**
	 * Draws the route on the map.
	 * <p>
	 * To indicate the full route a polyline will be drawn using all points in 
	 * itinerary.
	 * <p>
	 * On each method of transportation change a mode marker will be added.
	 * <p>
	 * Mode marker for transit step will display stop name, departure time and
	 * headsign.
	 * Mode marker for walk/bike connection, guidance to next point and distance and time
	 * to get there.
	 * <p>
	 * Previous routes are removed from the map.
	 * 
	 * @param itinerary the information to be drawn
	 * @param animateCamera if true map will be zoomed to exactly fit the route
	 * after the drawing
	 */
	public void showRouteOnMap(List<Leg> itinerary, boolean animateCamera) {
		itineraryTemp = itinerary;
		animateCameraTemp = animateCamera;
		
		new dataAccess5().execute();

	}
	
	private class dataAccess5 extends AsyncTask<Void, Void, Void>{
		
		String rev="";
		Marker firstTransitMarker = null;
		LatLngBounds.Builder boundsCreator = null;
		ArrayList<ShowRouteModel> listRouteModel;
		//stoplist=new ArrayList();
		protected void onPreExecute() {
			progressDialogReal = ProgressDialog.show(MainFragment.this.getActivity(), "",
					"Fetching Bus Information ......");
			progressDialogReal.setCancelable(true);
			isMainFinished = false;
		}
		
		//private ProgressDialog Dialog = new ProgressDialog(test.this);
		@Override
		protected Void doInBackground(Void... params) {
			if (!itineraryTemp.isEmpty()) {
			    allGeoPoints = new ArrayList<LatLng>();
				boundsCreator = LatLngBounds.builder();
				stoplist=new ArrayList<String>();
				
				int agencyTimeZoneOffset = 0;
				int stepIndex = 0;
				
				listRouteModel = new ArrayList<ShowRouteModel>();
				
				for (Leg leg : itineraryTemp) {
					ShowRouteModel routeModel = new ShowRouteModel();
					
					stepIndex++;
					
					if (leg.getAgencyTimeZoneOffset() != 0){
						agencyTimeZoneOffset = leg.getAgencyTimeZoneOffset();
					}
					
					List<LatLng> points = LocationUtil.decodePoly(leg.legGeometry.getPoints());
					
					float scaleFactor = getResources().getFraction(R.fraction.scaleFactor, 1, 1);
					 
					Drawable d= getResources().getDrawable(getPathIcon(leg.mode));
					BitmapDrawable bd=(BitmapDrawable) d.getCurrent();
					Bitmap b=bd.getBitmap();
					Bitmap bhalfsize=Bitmap.createScaledBitmap(b, (int)(b.getWidth()/scaleFactor), (int)(b.getHeight()/scaleFactor), false);
					
					MarkerOptions modeMarkerOption = new MarkerOptions().position(points.get(0)).icon(BitmapDescriptorFactory.fromBitmap(bhalfsize));

					TraverseMode traverseMode = TraverseMode.valueOf((String) leg.mode);

					if (traverseMode.isTransit()){
						
						stopId = leg.getFrom().stopId.getId();
						// Create a new HttpClient and Post Header
					    HttpClient httpclient = new DefaultHttpClient();
					    
					    HttpPost httppost = new HttpPost("http://rlabgw0.unipv.it:15002/IRMA_RT_WEB/service/rtdata/realtime_android");
						try {
							HttpEntity e = new StringEntity(stopId);
							httppost.setEntity(e);

							//modeMarkerOption.title(stepIndex + ". " + leg.getFrom().name + " " + DateTimeConversion.getTimeWithContext(applicationContext, agencyTimeZoneOffset, Long.parseLong(leg.getStartTime()), true));
							modeMarkerOption.title(stepIndex + ". " + leg.getFrom().name + " ");
							
							if (leg.getHeadsign() != null){
								modeMarkerOption.snippet(leg.getHeadsign());
							}
					        // Execute HTTP Post Request
					        HttpResponse response = httpclient.execute(httppost);
							int code = response.getStatusLine().getStatusCode();
							
							if (code == 200) {
								rev = EntityUtils.toString(response.getEntity());
								stoplist.add(stopId);
							}	
							JSONObject mainObject = new JSONObject(rev);
							JSONArray arrayRealTimeWrapper = mainObject.getJSONArray("RealTimeWrapper");
							JSONObject object = arrayRealTimeWrapper.getJSONObject(0);
							String delay = object.optString("delay");
							String estimated_arrival = object.optString("estimated_arrival");
							
							
							Integer hour=Integer.parseInt(estimated_arrival)/3600;
							Double minDouble=Math.ceil(((Double.parseDouble(estimated_arrival)%3600)/60));
							Integer min=(int)Math.round(minDouble);
							//Integer minDouble=(Integer.parseInt(estimated_arrival)%3600)/60;
						
							//Integer sec=Integer.parseInt(estimated_arrival)%60;	
						
							Integer delayMin = Integer.parseInt(delay) / 60;
							//Integer delaySec=Integer.parseInt(delay)%60;
							//Double delayMin=Double.parseDouble(delay)/60;
							//Double delaySec=Double.parseDouble(delay)%60;
							
							modeMarkerOption.snippet("Estimated Bus Arrival:"+hour+" : "+min+"  "+"Delay: " + delayMin + " min");
							
							//}
							//else{
								//modeMarkerOption.snippet("Estimated Arrival:"+" Connection Error");
								
							//}
							
							
						
						}catch(Exception e){
							modeMarkerOption.snippet("Estimated Arrival: Connection Error");
						}
					}
					else{
						if (traverseMode.equals(TraverseMode.WALK)){
							modeMarkerOption.title(stepIndex + ". " + getResources().getString(R.string.before_distance_walk)
									+ " " + getResources().getString(R.string.connector_destination) + " " + leg.getTo().name);
						}
						else if (traverseMode.equals(TraverseMode.BICYCLE)){
							modeMarkerOption.title(stepIndex + ". " + getResources().getString(R.string.before_distance_bike)
									+ " " + getResources().getString(R.string.connector_destination) + " " + leg.getTo().name);
						}
						modeMarkerOption.snippet(DateTimeConversion.getFormattedDurationTextNoSeconds(leg.duration/1000, applicationContext) + " " + "-" + " " 
								+ String.format(OTPApp.FORMAT_DISTANCE_METERS_SHORT, leg.getDistance()) + getResources().getString(R.string.distance_unit));
					}

					routeModel.setMarkerOptions(modeMarkerOption);
					routeModel.setPoints(points);
					routeModel.setScaleFator(scaleFactor);
					routeModel.setStepIndex(stepIndex);
					routeModel.setTraverseMode(traverseMode);
					
					listRouteModel.add(routeModel);
				}
			}
			
			return null;
			 
		}
		
		protected void onPostExecute(Void unused) { 
			
			progressDialogReal.dismiss();
			// TODO Auto-generated method stub
	
			if (route != null){
				for (Polyline legLine : route) {
					legLine.remove();
				}
				route.clear();
			}
			if (modeMarkers != null){
				for (Marker modeMarker : modeMarkers){
					modeMarker.remove();
				}
			}
			route = new ArrayList<Polyline>();
			modeMarkers = new ArrayList<Marker>();
						
			for (int i = 0; i < listRouteModel.size(); i++){
				List<LatLng> points = listRouteModel.get(i).getPoints();
				float scaleFactor = listRouteModel.get(i).getScaleFator();
				MarkerOptions markerOptions = listRouteModel.get(i).getMarkerOptions();
				
				Marker modeMarker = mMap.addMarker(markerOptions);
				modeMarkers.add(modeMarker);
				
				if(listRouteModel != null && listRouteModel.get(i).getTraverseMode().isTransit()){
					//because on transit two step-by-step indications are generated (get on / get off)
//					int stepIndexTmp = listRouteModel.get(i).getStepIndex();
//					stepIndexTmp++;
					
					if (firstTransitMarker == null){
						firstTransitMarker = modeMarker;
					}
				}
				
				
				PolylineOptions options = new PolylineOptions().addAll(points)
						   .width(5 * scaleFactor)
						   .color(OTPApp.COLOR_ROUTE_LINE);
				Polyline routeLine = mMap.addPolyline(options);
				route.add(routeLine);
				for (LatLng point : points) {
					boundsCreator.include(point);
				}
				allGeoPoints.addAll(points);
			}
			
			if (animateCameraTemp){
				if (firstTransitMarker != null){
					firstTransitMarker.showInfoWindow();
				}
				mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsCreator.build(), getResources().getInteger(R.integer.default_padding)));
			}
			
			isMainFinished = true;
			
			new dataAccess().execute();

		}
		
	}

	
	
	

	private int getPathIcon(String modeString){
		TraverseMode mode = TraverseMode.valueOf(modeString);
		int icon;
		
		if(mode.compareTo(TraverseMode.BICYCLE) == 0){
			icon = R.drawable.cycling;
		} else if(mode.compareTo(TraverseMode.CAR) == 0){
			icon = R.drawable.car;
		} else if((mode.compareTo(TraverseMode.BUS) == 0) || (mode.compareTo(TraverseMode.BUSISH) == 0)){
			icon = R.drawable.bus;
		} else if((mode.compareTo(TraverseMode.RAIL) == 0)  || (mode.compareTo(TraverseMode.TRAINISH) == 0)){
			icon = R.drawable.train;
		} else if(mode.compareTo(TraverseMode.FERRY) == 0){
			icon = R.drawable.ferry;
		} else if(mode.compareTo(TraverseMode.GONDOLA) == 0){
			icon = R.drawable.boat;
		} else if(mode.compareTo(TraverseMode.SUBWAY) == 0){
			icon = R.drawable.underground;
		} else if(mode.compareTo(TraverseMode.TRAM) == 0){
			icon = R.drawable.tramway;
		} else if(mode.compareTo(TraverseMode.WALK) == 0){
			icon = R.drawable.pedestriancrossing;
		} else if(mode.compareTo(TraverseMode.CABLE_CAR) == 0){
			icon = R.drawable.cablecar;
		} else if(mode.compareTo(TraverseMode.FUNICULAR) == 0){
			icon = R.drawable.funicolar;
		} else if(mode.compareTo(TraverseMode.TRANSIT) == 0){
			icon = R.drawable.road;
		} else if(mode.compareTo(TraverseMode.TRANSFER) == 0){
			icon = R.drawable.caution;
		}
		else{
			icon = R.drawable.road;
		}
		
		return icon;
	}

	public OnFragmentListener getFragmentListener() {
		return fragmentListener;
	}

	public void setFragmentListener(OnFragmentListener fragmentListener) {
		this.fragmentListener = fragmentListener;
	}

	@Override
	public void onServerSelectorComplete(Server server) {
		//Update application server
		if (getActivity() != null){
			setSelectedServer(server, true);
			updateSelectedServer();
		}
	}

	@Override
	public void onTripRequestComplete(final List<Itinerary> itineraries, String currentRequestString) {
		if (getActivity() != null){
			fillItinerariesSpinner(itineraries);
			if (!itineraries.isEmpty()){
				panelDisplayDirection.setVisibility(View.VISIBLE);
				btn_favourite.setVisibility(View.VISIBLE);
				btn_monitoring.setVisibility(View.VISIBLE);
				btnPOI.setVisibility(View.VISIBLE);
				//btnReRoute.setVisibility(View.VISIBLE);
				
				btnRealTime.setVisibility(View.VISIBLE);
				btnPoi_route.setVisibility(View.VISIBLE);
			}
			
			showRouteOnMap(itineraries.get(0).legs, true);
			OnFragmentListener ofl = getFragmentListener();

			// onItinerariesLoaded must be invoked before onItinerarySelected(0)
			ofl.onItinerariesLoaded(itineraries);
			ofl.onItinerarySelected(0);
			MyActivity myActivity = (MyActivity)getActivity();
			myActivity.setCurrentRequestString(currentRequestString);
			
			if ((startAddress != null) && (prefs.getBoolean(OTPApp.PREFERENCE_KEY_USE_INTELLIGENT_MARKERS, true))){
				resultTripStartLocation = ((startAddress.getAddressLine(0) != null) ? startAddress.getAddressLine(0) : "")
						+ ", "
						+ ((startAddress.getAddressLine(1) != null) ? startAddress.getAddressLine(1) : "");
				
				start_lat = startAddress.getLatitude() + "";
				start_lon = startAddress.getLongitude() + "";
			}
			else{
				resultTripStartLocation = tbStartLocation.getText().toString();
				
				start_lat = getLastLocation().latitude + "";
				start_lon = getLastLocation().longitude + "";
			}
			if ((endAddress != null) && (prefs.getBoolean(OTPApp.PREFERENCE_KEY_USE_INTELLIGENT_MARKERS, true))){
				resultTripEndLocation = ((endAddress.getAddressLine(0) != null) ? endAddress.getAddressLine(0) : "")
						+ ", "
						+ ((endAddress.getAddressLine(1) != null) ? endAddress.getAddressLine(1) : "");
				
				end_lat = endAddress.getLatitude() + "";
				end_lon = endAddress.getLongitude() + "";
			}
			else{
				resultTripEndLocation = tbEndLocation.getText().toString();
				
				end_lat = getLastLocation().latitude + "";
				end_lon = getLastLocation().longitude + "";
			}
								
			btn_favourite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
   				   ((InputMethodManager) MainFragment.this.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				    LayoutInflater inflater = getActivity().getLayoutInflater();

				    final View view = inflater.inflate(R.layout.dialog_save_favourite,null);
				    builder.setView(view)
				    // Add action buttons
				    	.setTitle(R.string.trip_favourite_title_dialog)
				           .setPositiveButton(R.string.trip_favourite_save, new DialogInterface.OnClickListener() {
				               @Override
				               public void onClick(DialogInterface dialog, int id) {
				   				   ((InputMethodManager) MainFragment.this.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

				            	   Itinerary firstItinerary = fragmentListener.getCurrentItineraryList().get(fragmentListener.getCurrentItineraryIndex());
									int agencyTimeZoneOffset = 0;
									long startTimeInSeconds = -1;
									
									for (Leg leg : firstItinerary.legs){
										if (startTimeInSeconds == -1){
											startTimeInSeconds = Long.parseLong(leg.getStartTime());
										}
										if (leg.getAgencyTimeZoneOffset() != 0){
											agencyTimeZoneOffset = leg.getAgencyTimeZoneOffset();
										}
									}
											
									EditText et_from = (EditText) view.findViewById(R.id.et_from);
									EditText et_to = (EditText) view.findViewById(R.id.et_to);

									if (et_from.getText().toString().equals("") || et_to.getText().toString().equals("")){
										getDialog(getResources().getString(R.string.compile), false);
									} else {
										time = DateTimeConversion.getTime(getActivity().getApplicationContext(), agencyTimeZoneOffset, startTimeInSeconds);
										date = DateTimeConversion.getDate(getActivity().getApplicationContext(), agencyTimeZoneOffset, startTimeInSeconds);
										user_id = UserModel.getInstance().getId() + "";
										start_location = et_from.getText().toString().trim();
										end_location = et_to.getText().toString().trim();
										
										new LocateTask().execute();
									}
				               }
				           })
				           .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int id) {
				               }
				           });  
				    
				    builder.create();
				    builder.show();
				   
				}
			});
		
		btn_monitoring.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setTitle(R.string.trip_monitored_title_dialog)
			        		.setMessage(R.string.trip_monitored_msg_dialog)
			                .setPositiveButton(R.string.trip_monitored_done_dialog, new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   //Aync for monitoring activation
			                	   new dataAccess_monitor().execute();
			                	   
				            	   Itinerary firstItinerary = fragmentListener.getCurrentItineraryList().get(fragmentListener.getCurrentItineraryIndex());
				            	   
				            	   int agencyTimeZoneOffset = 0;
				            	   long startTimeInSeconds = -1;

									for (Leg leg : firstItinerary.legs){
							
										if (startTimeInSeconds == -1){
											startTimeInSeconds = Long.parseLong(leg.getStartTime());
											//start_location = leg.getFrom().name;

										}
										if (leg.getAgencyTimeZoneOffset() != 0){
											agencyTimeZoneOffset = leg.getAgencyTimeZoneOffset();
										}
										//end_location = leg.getTo().name;
									}
									start_location = tbStartLocation.getText().toString().trim();
									end_location = tbEndLocation.getText().toString().trim();

									time = DateTimeConversion.getTime(getActivity().getApplicationContext(), agencyTimeZoneOffset, startTimeInSeconds);
									date = DateTimeConversion.getDate(getActivity().getApplicationContext(), agencyTimeZoneOffset, startTimeInSeconds);
									user_id = UserModel.getInstance().getId() + "";
									timestamp = System.currentTimeMillis() / 1000L;
									
									new LocateTaskMonitored().execute();
			                   }
			               })
			               .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                       // User cancelled the dialog
			                   }
			               });
				    builder.create();
				    builder.show();

			}
		});
	}
	}
	
	///*********************************************************************
	
	private class dataAccess_monitor extends AsyncTask<String, Void, Void>{
		
		 
		InputStream is = null;
	    
	    String flag="0";
		//private ProgressDialog Dialog = new ProgressDialog(test.this);
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			 try {
				    flag="1";
	                HttpClient httpclient = new DefaultHttpClient();
	                HttpPost httppost = new HttpPost("http://eventmanager-irmatripplanner.rhcloud.com/gcmalert/monitoring_event_activation.php");
	              //  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	                List<NameValuePair> pp = new ArrayList<NameValuePair>();
	                pp.add(new BasicNameValuePair("Regid", redId));
	                pp.add(new BasicNameValuePair("flag", flag));
	                
	                
	                httppost.setEntity(new UrlEncodedFormEntity(pp));
	                
	                HttpResponse response = httpclient.execute(httppost);
	                HttpEntity entity = response.getEntity();
	                is = entity.getContent();

	            }   catch(Exception e){
	              //  Toast.makeText(getBaseContext(),e.toString() ,Toast.LENGTH_LONG).show();
	            }
			  
			
			return null;
		}
		
		protected void onPreExecute() {
			
		
      }
		
	    protected void onPostExecute(Void unused) {
          // NOTE: You can call UI Element here.

          } 
      
		
	}
	
	
	// **********************************************************************

	// AsyncTask Favourite
	private class LocateTask extends AsyncTask<Void, Void, Void> 
	{	
	    @Override
	    protected void onPreExecute()
	    {
	    	super.onPreExecute();  
	    	progressDialog = ProgressDialog.show(MainFragment.this.getActivity(),"",getResources().getString(R.string.trip_favourite_saving));
            progressDialog.setCancelable(false);
	    	connection = new ConnectionDatabase();
	    }

	    @Override
	    protected Void doInBackground(Void... params) 
	    {			
	    	String url = Constants.PATH_SERVER + Constants.PATH_INSERT_FAVOURITE_TRIP;
	    	ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
	    	values.add(new BasicNameValuePair("user_id",user_id));
	    	values.add(new BasicNameValuePair("start_location",start_location));
	    	values.add(new BasicNameValuePair("end_location",end_location));
	    	values.add(new BasicNameValuePair("start_lat",start_lat));
	    	values.add(new BasicNameValuePair("start_lon",start_lon));
	    	values.add(new BasicNameValuePair("end_lat",end_lat));
	    	values.add(new BasicNameValuePair("end_lon",end_lon));
	    	values.add(new BasicNameValuePair("date",date));
	    	values.add(new BasicNameValuePair("time",time));

	    	jsonResult = connection.getResult(MainFragment.this.getActivity(), url, values);
			
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
		    		getDialog(getResources().getString(R.string.trip_favourite_save_done), true);
	    		} 
	    		// userId or Email exist!
	    		else {
		    		getDialog(getResources().getString(R.string.registration_fail_internet), false);
	    		}
	    	} 
	    	
	    	// connecion KO
	    	else {
	    		getDialog(getResources().getString(R.string.registration_fail_internet), false);
	    	}
	    }
	}
	
	// AsyncTask Monitored
	private class LocateTaskMonitored extends AsyncTask<Void, Void, Void> 
	{	
	    @Override
	    protected void onPreExecute()
	    {
	    	super.onPreExecute();  
	    	progressDialog = ProgressDialog.show(MainFragment.this.getActivity(),"",getResources().getString(R.string.trip_favourite_monitoring));
            progressDialog.setCancelable(false);
	    	connection = new ConnectionDatabase();
	    }

	    @Override
	    protected Void doInBackground(Void... params) 
	    {			
	    	String url = Constants.PATH_SERVER + Constants.PATH_INSERT_MONITORED_TRIP;
	    	ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
	    	values.add(new BasicNameValuePair("user_id",user_id));
	    	values.add(new BasicNameValuePair("start_location",start_location));
	    	values.add(new BasicNameValuePair("end_location",end_location));
	    	values.add(new BasicNameValuePair("start_lat",start_lat));
	    	values.add(new BasicNameValuePair("start_lon",start_lon));
	    	values.add(new BasicNameValuePair("end_lat",end_lat));
	    	values.add(new BasicNameValuePair("end_lon",end_lon));
	    	values.add(new BasicNameValuePair("date",date));
	    	values.add(new BasicNameValuePair("time",time));
	    	values.add(new BasicNameValuePair("timestamp",""+timestamp));

	    	jsonResult = connection.getResult(MainFragment.this.getActivity(), url, values);
			
			TripMonitoredModel.getInstance().setStart_lat(start_lat);
			TripMonitoredModel.getInstance().setStart_lon(start_lon);
			TripMonitoredModel.getInstance().setEnd_lat(end_lat);
			TripMonitoredModel.getInstance().setEnd_lon(end_lon);
			TripMonitoredModel.getInstance().setDate(date);
			TripMonitoredModel.getInstance().setTime(time);
			TripMonitoredModel.getInstance().setStart_location(start_location);
			TripMonitoredModel.getInstance().setEnd_location(end_location);
			TripMonitoredModel.getInstance().setTimestamp("" + timestamp);
			
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

	    			// INSERIRE l'intent al Fragment con il gps listner
					Intent i = new Intent(MainFragment.this.getActivity(), TripMonitoredMapActivity.class);
					MainFragment.this.getActivity().startActivity(i);
					MainFragment.this.getActivity().finish();
	    		} 
	    		// userId or Email exist!
	    		else {
		    		getDialog(getResources().getString(R.string.registration_fail_internet), false);
	    		}
	    	} 
	    	
	    	// connecion KO
	    	else {
	    		getDialog(getResources().getString(R.string.registration_fail_internet), false);
	    	}
	    }
	}
	
	public void getDialog(String msg, final boolean ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainFragment.this.getActivity());
        builder.setTitle(getResources().getString(R.string.activity_trip_favourite))
        	   .setMessage(msg)
               .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
           	    		if (ok) {
	                     	
           	    		}
           	    		else {
           	    			
           	    		}
    				}
               });
        builder.create();
        builder.show();
	}
	
	private void fillItinerariesSpinner(List<Itinerary> itineraryList){
		String[] itinerarySummaryList = new String[itineraryList.size()];

		int agencyTimeZoneOffset = 0;
		
		for(int i=0; i<itinerarySummaryList.length; i++){
			boolean isTagSet = false;
			Itinerary it = itineraryList.get(i);
			itinerarySummaryList[i] = Integer.toString(i+1) + ".   ";//Shown index is i + 1, to use 1-based indexes for the UI instead of 0-base
			for (Leg leg : it.legs){
				TraverseMode traverseMode = TraverseMode.valueOf((String) leg.mode);
				//OTP can't handle more than two timezones per request, so this is safe
				if (leg.getAgencyTimeZoneOffset() != 0){
					agencyTimeZoneOffset = leg.getAgencyTimeZoneOffset();
				}
				if(traverseMode.isTransit()){
					itinerarySummaryList[i] += getString(R.string.before_route) + " " + leg.getRouteShortName();
					itinerarySummaryList[i] += DateTimeConversion.getTimeWithContext(applicationContext, agencyTimeZoneOffset, Long.parseLong(leg.getStartTime()), true);
					itinerarySummaryList[i] += " " + "-" + " " + DateTimeConversion.getFormattedDurationTextNoSeconds(it.duration/1000, applicationContext);
					isTagSet = true;
					break;
				}
			}
			if (!isTagSet){
				if (it.legs.size() == 1){
					TraverseMode traverseMode = TraverseMode.valueOf((String) it.legs.get(0).mode);
					if (traverseMode.equals(TraverseMode.WALK)){
						itinerarySummaryList[i] += getString(R.string.before_distance_walk) + " " + String.format(OTPApp.FORMAT_DISTANCE_METERS_SHORT, it.walkDistance) + getResources().getString(R.string.distance_unit);
						itinerarySummaryList[i] += " " + getString(R.string.connector_time_full) + " " + DateTimeConversion.getFormattedDurationTextNoSeconds(it.duration/1000, applicationContext);
					}
					else if (traverseMode.equals(TraverseMode.BICYCLE)){
						itinerarySummaryList[i] += getString(R.string.before_distance_bike) + " " + String.format(OTPApp.FORMAT_DISTANCE_METERS_SHORT, it.walkDistance) + getResources().getString(R.string.distance_unit);
						itinerarySummaryList[i] += " " + getString(R.string.connector_time_full) + " " + DateTimeConversion.getFormattedDurationTextNoSeconds(it.duration/1000, applicationContext);
					}
				}
				else{
					itinerarySummaryList[i] += getString(R.string.total_duration) + " " + DateTimeConversion.getFormattedDurationTextNoSeconds(it.duration/1000, applicationContext);
				}
			}

		}
		
		ArrayAdapter<String> itineraryAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, itinerarySummaryList);
	
		itineraryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		itinerarySelectionSpinner.setAdapter(itineraryAdapter);
	}
  
	@Override
	public void onOTPGeocodingComplete(final boolean isStartTextbox,
			ArrayList<Address> addressesReturn, boolean geocodingForMarker) {
		if (getActivity() != null){	
			if (isStartTextbox){
				isStartLocationGeocodingProcessed = true;
			}
			else{
				isEndLocationGeocodingProcessed = true;
			}
			// isRealLostFocus = false;
			
			try{
				AlertDialog.Builder geocoderAlert = new AlertDialog.Builder(getActivity());
				geocoderAlert.setTitle(R.string.geocoder_results_title)
						.setMessage(R.string.geocoder_no_results_message)
						.setCancelable(false)
						.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		
				if (addressesReturn.isEmpty()) {
					AlertDialog alert = geocoderAlert.create();
					alert.show();
					return;
				} else if (addressesReturn.size() == 1) {
					if (geocodingForMarker){
						moveMarkerRelative(isStartTextbox, addressesReturn.get(0));
					}
					else{
						moveMarker(isStartTextbox, addressesReturn.get(0));
					}
					geocodingBeenRequested = false;
					requestTripAfterGeocoding();
					return;
				}
			
				AlertDialog.Builder geocoderSelector = new AlertDialog.Builder(getActivity());
				geocoderSelector.setTitle(R.string.choose_geocoder);
		
				final CharSequence[] addressesText = new CharSequence[addressesReturn.size()];
				
				for (int i = 0; i < addressesReturn.size(); i++) {
					Address addr = addressesReturn.get(i);
					addressesText[i] = getStringAddress(addr, true);

					Log.v(TAG, addressesText[i].toString());
				}
		
				final ArrayList<Address> addressesTemp = addressesReturn;
				geocoderSelector.setItems(addressesText,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								Address addr = addressesTemp.get(item);
								moveMarker(isStartTextbox, addr);
								Log.v(TAG, "Chosen: " + addressesText[item]);
								geocodingBeenRequested = false;
								MainFragment.this.requestTripAfterGeocoding();
							}
						});
				AlertDialog alertGeocoder = geocoderSelector.create();
				alertGeocoder.show();
			}catch(Exception e){
				Log.e(TAG, "Error in Main Fragment Geocoding callback: " + e);
			}
		}
	}
	
	/**
	 * Checks if a trip was requested, and requested geocoding done.
	 * <p>
	 * If it's necessary request will be processed and control variables
	 * restarted.
	 */
	private void requestTripAfterGeocoding(){
		if (requestTripAfterStartGeocoding){
			requestTripAfterStartGeocoding = false;
			if (!requestTripAfterStartEndGeocoding){
				requestTrip();
			}
		}
		else if (requestTripAfterEndGeocoding){
			requestTripAfterEndGeocoding = false;
			if (!requestTripAfterStartEndGeocoding){
				requestTrip();
			}		
		}
		else if (requestTripAfterStartEndGeocoding){
			requestTripAfterStartEndGeocoding = false;
			requestTrip();
		}
	}


	@Override
	public void onMetadataRequestComplete(GraphMetadata metadata) {
		if (getActivity() != null){
			double lowerLeftLatitude = metadata.getLowerLeftLatitude();
			double lowerLeftLongitude = metadata.getLowerLeftLongitude();
			double upperRightLatitude = metadata.getUpperRightLatitude();
			double upperRightLongitude = metadata.getUpperRightLongitude();
	
			Server selectedServer = app.getSelectedServer();
			
			String bounds = String.valueOf(lowerLeftLatitude) +
					"," + String.valueOf(lowerLeftLongitude) +
					"," + String.valueOf(upperRightLatitude) + "," + String.valueOf(upperRightLongitude);
			selectedServer.setBounds(bounds);
			
			SharedPreferences.Editor prefsEditor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit();
			prefsEditor.putString(PREFERENCE_KEY_CUSTOM_SERVER_BOUNDS, bounds);
			prefsEditor.commit();
			
			Log.v(TAG, "LowerLeft: " + Double.toString(lowerLeftLatitude)+","+Double.toString(lowerLeftLongitude));
			Log.v(TAG, "UpperRight" + Double.toString(upperRightLatitude)+","+Double.toString(upperRightLongitude));	
			
			addBoundariesRectangle(selectedServer);
			
			LatLng mCurrentLatLng = getLastLocation();
			
			if ((mCurrentLatLng != null) && (LocationUtil.checkPointInBoundingBox(mCurrentLatLng, selectedServer, OTPApp.CHECK_BOUNDS_ACCEPTABLE_ERROR))){
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, getServerInitialZoom(selectedServer)));
			}
			else{
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getServerCenter(selectedServer), getServerInitialZoom(selectedServer)));
				setMarker(true, getServerCenter(selectedServer), false);
			}
		}
	}
	
	/**
	 * Changes the tiles used to display the map and sets max zoom level.
	 * 
	 * @param overlayString tiles URL for custom tiles or description for
	 * Google ones
	 */
	public void updateOverlay(String overlayString){
		if (overlayString == null){
			overlayString = prefs.getString(OTPApp.PREFERENCE_KEY_MAP_TILE_SOURCE, applicationContext.getResources().getString(R.string.map_tiles_default_server));
		}
		if (actualTileOverlay != null){
			actualTileOverlay.remove();
		}
		if (overlayString.startsWith(OTPApp.MAP_TILE_GOOGLE)){
			int mapType = GoogleMap.MAP_TYPE_NORMAL;
			
			if (overlayString.equals(OTPApp.MAP_TILE_GOOGLE_HYBRID)){
				mapType = GoogleMap.MAP_TYPE_HYBRID;
			}
			else if (overlayString.equals(OTPApp.MAP_TILE_GOOGLE_NORMAL)){
				mapType = GoogleMap.MAP_TYPE_NORMAL;	
			}
			else if (overlayString.equals(OTPApp.MAP_TILE_GOOGLE_TERRAIN)){
				mapType = GoogleMap.MAP_TYPE_TERRAIN;
			}
			else if (overlayString.equals(OTPApp.MAP_TILE_GOOGLE_SATELLITE)){
				mapType = GoogleMap.MAP_TYPE_SATELLITE;	
			}
			mMap.setMapType(mapType);
			maxZoomLevel = mMap.getMaxZoomLevel();
		}
		else{
			if (overlayString.equals(getResources().getString(R.string.tiles_mapnik))){
				maxZoomLevel = getResources().getInteger(R.integer.tiles_mapnik_max_zoom);
			}
			else{
				maxZoomLevel = getResources().getInteger(R.integer.tiles_maquest_max_zoom);
			}
			mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
			MyUrlTileProvider mTileProvider = new MyUrlTileProvider(OTPApp.CUSTOM_MAP_TILE_HEIGHT, OTPApp.CUSTOM_MAP_TILE_HEIGHT, overlayString);
			actualTileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mTileProvider).zIndex(OTPApp.CUSTOM_MAP_TILE_Z_INDEX));
			
			if (mMap.getCameraPosition().zoom > maxZoomLevel){
				mMap.moveCamera(CameraUpdateFactory.zoomTo(maxZoomLevel));
			}
		}
	}
	
	/**
	 * Returns last location coordinates.
	 * <p>
	 * This is obtained from the Location Client if it's connected and retrurns
	 * a valid Location. If not saved last location is provided.
	 * <p>
	 * On successful call to Location Client saved last location is updated.
	 * 
	 * @return a LatLng object with the most updated user coordinates 
	 */
	public LatLng getLastLocation() {
		if (mLocationClient.isConnected()){
			Location loc = mLocationClient.getLastLocation();

			if (loc != null){
				LatLng mCurrentLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
				savedLastLocation = mCurrentLocation;
				return mCurrentLocation;
			}
		}
		if (savedLastLocation != null){
			return savedLastLocation;
		}
		return null;
	}

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
    	if (!mapFailed){
            if (connectionResult.hasResolution()) {
                try {
                    // Start an Activity that tries to resolve the error
                    connectionResult.startResolutionForResult(
                            getActivity(),
                            OTPApp.CONNECTION_FAILURE_RESOLUTION_REQUEST_CODE);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
    			AlertDialog.Builder errorPlay = new AlertDialog.Builder(getActivity());
    			errorPlay.setTitle(getResources().getString(R.string.play_services_error_title))
    					.setMessage(getResources().getString(R.string.play_services_error) + connectionResult.getErrorCode())
    					.setNeutralButton(getResources().getString(android.R.string.ok), null)
    					.create()
    					.show();
            }
    	}
    }

	@Override
	public void onConnected(Bundle connectionHint) {
		Location mCurrentLocation = mLocationClient.getLastLocation();

		if ((!mapFailed)){
			if (mCurrentLocation != null){
				//mLocationClient.requestLocationUpdates(mLocationRequest, this);
				double savedLatitude = 0;
				double savedLongitude = 0;
				float distance[] = new float[1];
				distance[0] = 0;
				if (savedLastLocationCheckedForServer != null){
					savedLatitude = savedLastLocationCheckedForServer.latitude;
					savedLongitude = savedLastLocationCheckedForServer.longitude;
				}
				
				LatLng mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
				
				Location.distanceBetween(savedLatitude, savedLongitude, mCurrentLatLng.latitude, mCurrentLatLng.longitude, distance);
		
				if (needToRunAutoDetect){
					runAutoDetectServer(mCurrentLatLng);
				}
				else if (prefs.getBoolean(OTPApp.PREFERENCE_KEY_AUTO_DETECT_SERVER, true)) {
					
					if ((app.getSelectedServer() != null) 
							&& (!LocationUtil.checkPointInBoundingBox(mCurrentLatLng, app.getSelectedServer(), OTPApp.CHECK_BOUNDS_ACCEPTABLE_ERROR))
							&& (((savedLastLocationCheckedForServer != null) && (distance[0] > OTPApp.COORDINATES_IMPORTANT_DIFFERENCE)) 
									|| (savedLastLocationCheckedForServer == null))){
						runAutoDetectServer(mCurrentLatLng);
					}
					else if (app.getSelectedServer() == null){
						runAutoDetectServer(mCurrentLatLng);
					}
				}
				else {
					if (mCurrentLatLng != null){
						if (appStarts){
							Server selectedServer = app.getSelectedServer();	
							if ((selectedServer != null) && selectedServer.areBoundsSet()){
								if (LocationUtil.checkPointInBoundingBox(mCurrentLatLng, selectedServer, OTPApp.CHECK_BOUNDS_ACCEPTABLE_ERROR)){
									mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, getServerInitialZoom(selectedServer)));
								}
								else{
									mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getServerCenter(selectedServer), getServerInitialZoom(selectedServer)));	
									setMarker(true, getServerCenter(selectedServer), false);
								}
							}
							else{
								mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, getServerInitialZoom(selectedServer)));
							}
						}
					}
				}
		
				appStarts = false;
			}
			else if (app.getSelectedServer() == null){
				runAutoDetectServerNoLocation();
			}
		}
	        
	}

	@Override
	public void onDisconnected() {		
	}

	@Override
	public void onLocationChanged(Location location) {
        LatLng mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
		Server selectedServer = app.getSelectedServer();	
        
        if ((mCurrentLatLng != null) && (selectedServer != null) && (LocationUtil.checkPointInBoundingBox(mCurrentLatLng, selectedServer, OTPApp.CHECK_BOUNDS_ACCEPTABLE_ERROR))){
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, getServerInitialZoom(selectedServer)));
		}
		else if ((selectedServer != null) && selectedServer.areBoundsSet()){
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getServerCenter(selectedServer), getServerInitialZoom(selectedServer)));
			setMarker(true, getServerCenter(selectedServer), false);
		}

        /*
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show();*/
		
	}

	/**
	 * Draws rectangle in the map to represent the bounds, using selected
	 * server fields for lower left and upper right coordinates.
	 * 
	 * @param server from which coordinates will be pulled
	 */
	public void addBoundariesRectangle(Server server){
		List<LatLng> bounds = new ArrayList<LatLng>();
		bounds.add(new LatLng(server.getLowerLeftLatitude(), server.getLowerLeftLongitude()));
		bounds.add(new LatLng(server.getLowerLeftLatitude(), server.getUpperRightLongitude()));
		bounds.add(new LatLng(server.getUpperRightLatitude(), server.getUpperRightLongitude()));
		bounds.add(new LatLng(server.getUpperRightLatitude(), server.getLowerLeftLongitude()));
		bounds.add(new LatLng(server.getLowerLeftLatitude(), server.getLowerLeftLongitude()));

		PolylineOptions boundariesPolylineOptions = new PolylineOptions()
						 .addAll(bounds)
						 .color(Color.GRAY);
		boundariesPolyline = mMap.addPolyline(boundariesPolylineOptions);
	}
	
	public float getServerInitialZoom(Server s){
		if (s.isZoomSet()){
			return s.getInitialZoom();
		}
		else{
			return OTPApp.defaultInitialZoomLevel;
		}
	}
	
	public LatLng getServerCenter(Server s){
		if (s.isCenterSet()){
			return new LatLng(s.getCenterLatitude(), s.getCenterLongitude());
		}
		else{
			return new LatLng(s.getGeometricalCenterLatitude(), s.getGeometricalCenterLongitude());
		}
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		if (position.zoom > maxZoomLevel) {
			mMap.moveCamera(CameraUpdateFactory.zoomTo(maxZoomLevel));
		}
	}


	@Override
	public void onDateComplete(Date tripDate, boolean arriveBy) {
		this.arriveBy = arriveBy;
		String tripTime = tripDate.toString() + arriveBy;
		Log.v(TAG, tripTime);
	}

//	@Override
//	public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
//			Double minValue, Double maxValue) {
//		bikeTriangleMinValue = minValue;
//		bikeTriangleMaxValue = maxValue;
//		String bikeParam = minValue.toString() + maxValue.toString();
//		Log.v(TAG, bikeParam);
//	}
	
	private class LocateTask2 extends AsyncTask<String, String, String> 
	{	
	    @Override
	    protected void onPreExecute()
	    {
	    	super.onPreExecute();  
	    	progressDialogGeocoding = ProgressDialog.show(MainFragment.this.getActivity(),"",getResources().getString(R.string.processing));
	    	progressDialogGeocoding.setCancelable(false);
	    	
			InputMethodManager imm = (InputMethodManager) MainFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(tbEndLocation.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(tbStartLocation.getWindowToken(), 0);
			
	    }

	    @Override
	    protected String doInBackground(String... params) 
	    {
            
			if (prefs.getBoolean(OTPApp.PREFERENCE_KEY_DESTINATION_IS_MY_LOCATION, true)){
		    	requestTripAfterStartGeocoding = true;
				LatLng mCurrentLatLng = getLastLocation();
				geocodingBeenRequested = true;
				processAddress(false, mCurrentLatLng.latitude + "," + mCurrentLatLng.longitude, false);
			} else 
				if (!isEndLocationGeocodingProcessed){
			    requestTripAfterStartGeocoding = true;
				geocodingBeenRequested = true;
				String addressGeocoded = getHttpCoordinate(tbEndLocation.getText().toString());
				processAddress(false, addressGeocoded, false);
			}
			
			if (prefs.getBoolean(OTPApp.PREFERENCE_KEY_ORIGIN_IS_MY_LOCATION, true)){
				requestTripAfterEndGeocoding = true;
				LatLng mCurrentLatLng = getLastLocation();
				geocodingBeenRequested = true;
				processAddress(true, mCurrentLatLng.latitude + "," + mCurrentLatLng.longitude, false);
			} else 
				if (!isStartLocationGeocodingProcessed){
				requestTripAfterEndGeocoding = true;
				geocodingBeenRequested = true;
				String addressGeocoded = getHttpCoordinate(tbStartLocation.getText().toString());
				processAddress(true, addressGeocoded, false);
			}
		    
	    	return null; 				         
	    }
	    
	    @Override
	    protected void onPostExecute(String v)
	    {
	    	 
	    	super.onPostExecute(v); 
	    	
	    	progressDialogGeocoding.cancel();
			requestTrip();

	    }
	}
	
	public String getHttpCoordinate(String address) { 
	    double lat = 0;
	    double lng = 0;
	    
		try {
	    	HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + URLEncoder.encode(address) + ",27100,Pavia,Lombardia,IT,Italy&ka&sensor=false");
		    HttpClient client = new DefaultHttpClient();
		    HttpResponse response;
		    StringBuilder stringBuilder = new StringBuilder();

	        response = client.execute(httpGet);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;
	        while ((b = stream.read()) != -1) {
	            stringBuilder.append((char) b);
	        }
	        
		    JSONObject jsonObject = new JSONObject();
		    jsonObject = new JSONObject(stringBuilder.toString());

		    lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
		            .getJSONObject("geometry").getJSONObject("location")
		            .getDouble("lng");

		    lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
		            .getJSONObject("geometry").getJSONObject("location")
		            .getDouble("lat");
	    } catch (ClientProtocolException e) {
	    } catch (IOException e) {
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return lat + "," + lng;
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
		
	}

	/* (non-Javadoc)
	 * @see br.com.condesales.listeners.AccessTokenRequestListener#onAccessGrant(java.lang.String)
	 */
	@Override
	public void onAccessGrant(String accessToken) {
		// TODO Auto-generated method stub
		googleMap=mMap;
		requestTipsNearby(accessToken);
	}
	
	private void requestTipsNearby(String accessToken) {
		
		CustomMarker  = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
		
        Location loc = new Location(end);
        
		//String start = getHttpCoordinate(tbStartLocation.getText().toString());
		//String end=getHttpCoordinate(tbEndLocation.getText().toString());
		loc.setLatitude(endLat);
        loc.setLongitude(endlng);
        //Toast.makeText(getActivity(), end, Toast.LENGTH_LONG).show();
		
        TipsCriteria criteria = new TipsCriteria();
        criteria.setLocation(loc);
        async.getTipsNearby(new TipsResquestListener() {

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(MainFragment.this.getActivity(), "error", Toast.LENGTH_LONG).show();
            }
          
            @Override
            public void onTipsFetched(ArrayList<Tip> tips) {

            	for(int i=0;i<tips.size();i++)
            	{
            	//String test = tips.get(i).getVenue().getName()+""+tips.get(i).getVenue().getLocation().getLat()+"";
            	lat=tips.get(i).getVenue().getLocation().getLat();
            	lng=tips.get(i).getVenue().getLocation().getLng();
            	//String title_message=event_array[3].toString()+","+event_array[6].toString();
            	name_poi = tips.get(i).getVenue().getName()+"";
            	description_poi=tips.get(i).getVenue().getLocation().getAddress();
                poi_status=tips.get(i).getPhotourl();
 
                if(!poi_name_temp.contains(name_poi))
            	poi_name_temp.add(name_poi);
            	
            	Marker mk = mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name_poi).snippet(description_poi).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker0)));
            	
            	modeMarkers.add(mk);
            	// Setting a custom info window adapter for the google map		
  
            	// initImageLoader();
                // imageLoader = ImageLoader.getInstance();
                 
                /* options = new DisplayImageOptions.Builder()
         			.showStubImage(R.drawable.marker0)		//	Display Stub Image
         			.showImageForEmptyUri(R.drawable.marker0)	//	If Empty image found
         			.cacheInMemory()
         			.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();*/
                 	 
                 	//googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
                 	
                 /*	final Marker hamburg = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng))
                 		        .title(name_poi));
                 	markers.put(hamburg.getId(), "http://img.india-forums.com/images/100x100/37525-a-still-image-of-akshay-kumar.jpg");
                 	modeMarkers.add(hamburg);*/
                	 
                	 
                 //	final Marker kiel = googleMap.addMarker(new MarkerOptions()
                 	//	        .position(new LatLng(lat,lng))
                 	//	        .title(name_poi)
                 	//	        .snippet(description_poi)
                 	//	        .icon(BitmapDescriptorFactory
                 		//            .fromResource(R.drawable.marker0)));
                    
               //  	markers.put(mk.getId(), "http://www.yodot.com/images/jpeg-images-sm.png");
                 	//modeMarkers.add(kiel);                 
                 }
            	
     
            	poi_names = poi_name_temp.toArray(new CharSequence[poi_name_temp.size()]);
        		
      	
            	//googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            	 //   @Override
            	  //  public boolean onMarkerClick(Marker arg0) {
            	    	
            	    		
            	    		
            	    		/*poi_image.setVisibility(View.VISIBLE);
            	    		drawCustomMarker(arg0.getPosition().latitude, arg0.getPosition().longitude, arg0.getTitle(),0);
            	    	
            	    		MarkerOptions markerOptions = new MarkerOptions();
              				
              				// Setting position on the MarkerOptions
              				markerOptions.position(new LatLng(lat,lng));				
              				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker0));
              				// Animating to the currently touched position
              				//googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));	
              				
              				// Adding marker on the GoogleMap
              				Marker marker = googleMap.addMarker(markerOptions);
              				// Showing InfoWindow on the GoogleMap
              				marker.showInfoWindow();
              				modeMarkers.add(marker);*/
            	    		
            	    		//final DataClass data = myMapData.get(arg0);
            	    	   // final Dialog d = new Dialog(MainFragment.this.getActivity());
            	    	   // d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            	    	    /// d.setTitle("Select");
            	    	    // d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            	    	    // d.setContentView(R.layout.info_window_layout);
            	    	      // ivPhoto = (ImageView)d.findViewById(R.id.infocontent_iv_image);
            	    	        //AddImageOnWindow executeDownload = new AddImageOnWindow();
            	    	      // final LatLng l = arg0.getPosition();
            	    	      //executeDownload.execute(l);
            	    	      // TextView tvName = (TextView)d.findViewById(R.id.tv_lng);
            	    	      // tvName.setText("test");

            	    	       //TextView tvType = (TextView)d.findViewById(R.id.tv_name);
            	    	       // tvType.setText("test");

            	    	       // TextView tvDesc = (TextView)d.findViewById(R.id.infocontent_tv_desc);
            	    	        //tvDesc.setText(data.getPlaceDesc());

            	    	        //TextView tvAddr = (TextView)d.findViewById(R.id.infocontent_tv_addr);
            	    	        //tvAddr.setText(Html.fromHtml(data.getPlaceAddr()));

            	    	     //   d.show();
            	    	
            	    	//}else {
            	    	//	poi_image.setVisibility(View.GONE);
						
            	    		
            	 
            	    	
            	    	
            	    	
            	       // return true;
            	   // }
            	//});
        		// Adding and showing marker while touching the GoogleMap
            	
            	/*googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
        			
        			// Use default InfoWindow frame
        			@Override
        			public View getInfoWindow(Marker arg0) {				
        				return null;
        			}			
        			
        			// Defines the contents of the InfoWindow
        			@Override
        			public View getInfoContents(Marker arg0) {
        				
        				// Getting view from the layout file info_window_layout
        				View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
        				
        				// Getting the position from the marker
        				LatLng latLng = arg0.getPosition();
        				
        				// Getting reference to the TextView to set latitude
        				TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
        				
        				// Getting reference to the TextView to set longitude
        				TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
        				TextView tvName = (TextView) v.findViewById(R.id.tv_name);
        				TextView tvDescription = (TextView) v.findViewById(R.id.tv_Des);
        		
        				// Setting the latitude
        				tvLat.setText("Latitude:" + latLng.latitude);
        				tvLng.setText("Longitude:"+ latLng.longitude);
        				// Setting the longitude
        				tvName.setText("Name:"+ poi_status);

        				tvDescription.setText("Location:"+ description_poi);
        				// Returning the view containing InfoWindow contents
        				return v;
        				
        			}
        			
        		});*/
            	
        		/*googleMap.setOnMapClickListener(new OnMapClickListener() {
        			
        			
        			public void onMapClick(LatLng arg0) {
        				// Clears any existing markers from the GoogleMap
        				//googleMap.clear();
        				
        				// Creating an instance of MarkerOptions to set position
  
        			   MarkerOptions markerOptions = new MarkerOptions();
        				
        				// Setting position on the MarkerOptions
        				markerOptions.position(new LatLng(lat,lng));				
        				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker0));
        				// Animating to the currently touched position
        				//googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));	
        				
        				// Adding marker on the GoogleMap
        				Marker marker = googleMap.addMarker(markerOptions);
        				
        				
        				
        				
        			
        				// Showing InfoWindow on the GoogleMap
        				marker.showInfoWindow();
        				modeMarkers.add(marker);
        				
        			}
        		});
				
            	*/
               // Toast.makeText(MainFragment.this.getActivity(), test, Toast.LENGTH_LONG).show();
            	
            }
            }, criteria);
        }
	
	
	
	
	private class CustomInfoWindowAdapter implements InfoWindowAdapter {
		

		private View view;

		public CustomInfoWindowAdapter() {
			view = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window,null);
		}

		@Override
		public View getInfoContents(Marker marker) {

			if (MainFragment.this.marker != null && MainFragment.this.marker.isInfoWindowShown()) {
				MainFragment.this.marker.hideInfoWindow();
				MainFragment.this.marker.showInfoWindow();
			}
			return null;
		}

		@Override
		public View getInfoWindow(final Marker marker) {
			MainFragment.this.marker = marker;

			poi_image = ((ImageView) view.findViewById(R.id.badge));

			String url = null;
			if (marker.getId() != null && markers != null && markers.size() > 0) {
				if (markers.get(marker.getId()) != null) {
					url = markers.get(marker.getId());
				} else {
					poi_image.setVisibility(View.GONE);
				}
			} else {
				poi_image.setVisibility(View.GONE);
			}
            
			if (url != null && !url.equalsIgnoreCase("null") && !url.equalsIgnoreCase("")) {
				imageLoader.displayImage(url, poi_image, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							poi_image.setVisibility(View.VISIBLE);
							getInfoContents(marker);
						}
				});
			} 

			final String title = marker.getTitle();
			final TextView titleUi = ((TextView) view.findViewById(R.id.title));
			if (title != null) {
				titleUi.setText(title);
			} else {
				titleUi.setText("");
			}

			final String snippet = marker.getSnippet();
			final TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
			if (snippet != null) {
				snippetUi.setText(snippet);
			} else {
				snippetUi.setText("");
			}

			return view;
		}
	
	}

    private void initImageLoader() {
		int memoryCacheSize;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			int memClass = ((ActivityManager) 
					getActivity().getSystemService(Context.ACTIVITY_SERVICE))
					.getMemoryClass();
			memoryCacheSize = (memClass / 8) * 1024 * 1024;
		} else {
			memoryCacheSize = 2 * 1024 * 1024;
		}

		final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				MainFragment.this.getActivity()).threadPoolSize(5)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(memoryCacheSize)
				.memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize-1000000))
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging() 
				.build();

		ImageLoader.getInstance().init(config);
	}

	
    private void drawCustomMarker(Double latitude, Double longitude, String title, int propertyId){
       
          
        	googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
    			
    			// Use default InfoWindow frame
    			@Override
    			public View getInfoWindow(Marker arg0) {				
    				return null;
    			}			
    			
    			// Defines the contents of the InfoWindow
    			@Override
    			public View getInfoContents(Marker arg0) {
    				
    				// Getting view from the layout file info_window_layout
    				View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
    				
    				// Getting the position from the marker
    				LatLng latLng = arg0.getPosition();
    				
    				// Getting reference to the TextView to set latitude
    				TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
    				
    				// Getting reference to the TextView to set longitude
    				TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
    				TextView tvName = (TextView) v.findViewById(R.id.tv_name);
    				TextView tvDescription = (TextView) v.findViewById(R.id.tv_Des);
    		
    				// Setting the latitude
    				tvLat.setText("Latitude:" + latLng.latitude);
    				tvLng.setText("Longitude:"+ latLng.longitude);
    				// Setting the longitude
    				tvName.setText("Name:"+ poi_status);

    				tvDescription.setText("Location:"+ description_poi);
    				// Returning the view containing InfoWindow contents
    				return v;
    				
    			}
    			
    		});

       
    }

    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

		private ProgressDialog dialog;
		private Context context;
		private String places;

		public GetPlaces(Context context, String places) {
			this.context = context;
			this.places = places;
		}

		@Override
		protected void onPostExecute(ArrayList<Place> result) {
			super.onPostExecute(result);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			for (int i = 0; i < result.size(); i++) {
				mMap.addMarker(new MarkerOptions()
						.title(result.get(i).getName())
						.position(
								new LatLng(result.get(i).getLatitude(), result
										.get(i).getLongitude()))
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.map_pin_foursquare))
						.snippet(result.get(i).getVicinity()));
			}
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(result.get(0).getLatitude(), result
							.get(0).getLongitude())) // Sets the center of the map to
											// Mountain View
					.zoom(14) // Sets the zoom
					.tilt(30) // Sets the tilt of the camera to 30 degrees
					.build(); // Creates a CameraPosition from the builder
			mMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setMessage("Loading..");
			dialog.isIndeterminate();
			dialog.show();
		}

		@Override
		protected ArrayList<Place> doInBackground(Void... arg0) {
			PlacesService service = new PlacesService(
					"AIzaSyAmYZJ7-t4bfBFigDg7J_fF504HLa4qzS4");
			ArrayList<Place> findPlaces = service.findPlaces(loc_poi.getLatitude(), // 28.632808
					loc_poi.getLongitude(), places); // 77.218276

			for (int i = 0; i < findPlaces.size(); i++) {

				Place placeDetail = findPlaces.get(i);
				Log.e(TAG, "places : " + placeDetail.getName());
			}
			return findPlaces;
		}

	}
    
    private class dataAccess_poi extends AsyncTask<String, Void, Void>{
		
		 String result_sub;
		 InputStream is = null;
	     private String Content;
	     private String result;
	     List<String> result_list2;
	     private  String s = "";
	     StringBuilder sb = null;
	     private String Error = null;
	   
		//private ProgressDialog Dialog = new ProgressDialog(test.this);
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			 try {
				
				//JSONObject object = new JSONObject(); 
				 start = (startMarkerPosition.latitude + "," + startMarkerPosition.longitude);
				 end = (endMarkerPosition.latitude + "," + endMarkerPosition.longitude);
				 //Toast.makeText(getActivity(), end, Toast.LENGTH_LONG).show();
				 
				
				 
				 
	                HttpClient httpclient = new DefaultHttpClient();
	                HttpPost httppost = new HttpPost("http://eventmanager-irmatripplanner.rhcloud.com/POI/POI_detect.php");
	              //  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	                HttpParams parmsHttp =httpclient.getParams();
	                HttpConnectionParams.setConnectionTimeout(parmsHttp, 20000);
	                
	                List<NameValuePair> pp = new ArrayList<NameValuePair>();
	                pp.add(new BasicNameValuePair("Regid", redId));
	                pp.add(new BasicNameValuePair("start", start));
	                pp.add(new BasicNameValuePair("end", end));
	                
	                httppost.setEntity(new UrlEncodedFormEntity(pp));
	                
	                HttpResponse response = httpclient.execute(httppost);

	                int a = response.getStatusLine().getStatusCode();
	                
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                
	                result = out.toString();
	                
			 }catch (Exception e) {
	                // TODO: handle exception
	                Log.e("log_tag", "Error Parsing Data "+e.toString());
	        }
			 
			 try{
				 	
				   if(!result.trim().equals("null") ){
				  
				   JSONArray jsonArray = new JSONArray(result);
				   String[] strArr = new String[jsonArray.length()];

				   		for (int i = 0; i < jsonArray.length(); i++) {
				   				strArr[i] = jsonArray.getString(i);
				      
				   		}
				   		result_list2 = new ArrayList<String>(Arrays.asList(strArr));
				   }
				   
		        }catch (Exception e) {
		                // TODO: handle exception
		                Log.e("log_tag", "Error Parsing Data "+e.toString());
		        }
			 
			 
			
			return null;
		}
		
		protected void onPreExecute() {
			

		}
		Marker mk;
	    protected void onPostExecute(Void unused) {
          // NOTE: You can call UI Element here.

          // Close progress dialog
          //Dialog.dismiss();
	    	
	    	
	    if(result.trim().equals("null")){
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(MainFragment.this.getActivity());
			//builder.setTitle("Real Time Bus Information");
			//builder.setIcon(R.drawable.realtime);
			
	    	String error="No Nearest POI Location";
	    	ArrayList<String> error2=new ArrayList<String>();
	    	error2.add(error);
			CharSequence[] realtimedata2 ={"No Nearest POI Location"};
			
			builder.setTitle("Point of Interest").setIcon(R.drawable.p_icon)
	           .setItems(realtimedata2, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position
	               // of the selected item
	           }
	        });
			builder.setNeutralButton("Back",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			
			AlertDialog alert = builder.create();
			alert.show();
			
	    	
	    }else{
	    	
	    	GoogleMap map = mMap; // get a map.
			
	    	for (int i = 0; i < result_list2.size(); i++) {
	    		
				String events=result_list2.get(i); 
	    	   
			 	String event_array[]=events.split("\\|");
				
				String title_message=event_array[1].toString();
				String snipet_message=event_array[2].toString();
				//String category=event_array[5].toString();
				String pic_link=event_array[5].toString();
							
				mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(event_array[3]), Double.parseDouble(event_array[4]))).title(title_message).snippet(snipet_message).icon(BitmapDescriptorFactory.fromResource(R.drawable.p_icon)));
				modeMarkers.add(mk);
				
				markers.put(mk.getId(), pic_link);
			
	    	}   			 
	    }
      }
		
	}
}