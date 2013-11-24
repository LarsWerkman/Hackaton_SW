package org.dutchaug.hackathon.soakingwet;

import java.io.IOException;
import java.util.Map;

import org.dutchaug.hackathon.soakingwet.dialog.ErrorDialogFragment;

import org.dutchaug.hackathon.soakingwet.services.WeatherDataCollector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
	
	
public class MainActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationClient locationClient;
    
	private double latitude;
	private double longtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationClient = new LocationClient(this, this, this);
        setContentView(R.layout.activity_main);
        
        latitude = 54.0d;
        longtitude = 4.0d;
        
        AsyncTask<Double, Integer, String> data = new WDownloader().execute(latitude , longtitude);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationClient.connect();
    }

    protected void onStop() {
        // Disconnecting the client invalidates it.
        locationClient.disconnect();
        super.onStop();
    }

    /*
     * Handle results returned to the FragmentActivity by Google Play services
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                /*
                 * If the result code is Activity.RESULT_OK, try to connect again
                 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        /*
                         * Try the request again
                         */
                        break;
                }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // Get the error code
        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(arg0.getErrorCode(), this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
        // If Google Play services can provide an error dialog
        if (errorDialog != null) {
            // Create a new DialogFragment for the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);
            // Show the error dialog in the DialogFragment
            errorFragment.show(getFragmentManager(), "Location Updates");
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        Location lastLocation = locationClient.getLastLocation();
        TextView textView = (TextView) findViewById(R.id.mainactivity_text);
        textView.setText("lat: " + lastLocation.getLatitude() + ", long: " + lastLocation.getLongitude());
    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub
    }
    
    class WDownloader extends AsyncTask<Double, Integer, Map<String,String>> {
    	
    	private WeatherDataCollector weatherDataCollector = new WeatherDataCollector();

		@SuppressLint("DefaultLocale")
		@Override
		protected Map<String,String> doInBackground(Double... params) {
			try {
				return weatherDataCollector.getPrecipitationForLatLong(latitude, longtitude);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
    }
    
}