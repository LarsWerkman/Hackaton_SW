package org.dutchaug.hackathon.soakingwet;

import java.io.IOException;
import java.util.Map;

import org.dutchaug.hackathon.soakingwet.dialog.ErrorDialogFragment;
import org.dutchaug.hackathon.soakingwet.services.WeatherDataCollector;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends FragmentActivity implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    public static final String DATADATA = "message";
    // Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 60;

    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationClient locationClient;
    private LocationRequest locationRequest;

    public static String weatherText = "Heavy rain at 20:00";

    private double latitude;
    private double longtitude;

    private TextView textView;
    private TextView description;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationClient = new LocationClient(this, this, this);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.mainactivity_text);
        description = (TextView) findViewById(R.id.description);

        imageView = (ImageView) findViewById(R.id.display_icon);

        locationRequest = LocationRequest.create();
        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        locationRequest.setInterval(UPDATE_INTERVAL);

        Intent refreshIntent = new Intent("REFRESH_REQUEST_INTENT");
        sendBroadcast(refreshIntent);
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
        latitude = lastLocation.getLatitude();
        longtitude = lastLocation.getLongitude();
        textView.setText("lat: " + lastLocation.getLatitude() + ", long: " + lastLocation.getLongitude());

        locationClient.requestLocationUpdates(locationRequest, this);

        AsyncTask<Double, Integer, Map<String, Integer>> data = new WDownloader().execute(latitude, longtitude);

        Intent refreshIntent = new Intent("REFRESH_REQUEST_INTENT");
        sendBroadcast(refreshIntent);
    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    class WDownloader extends AsyncTask<Double, Integer, Map<String, Integer>> {

        private WeatherDataCollector weatherDataCollector = new WeatherDataCollector();

        @SuppressLint("DefaultLocale")
        @Override
        protected Map<String, Integer> doInBackground(Double... params) {
            try {
                return weatherDataCollector.getPrecipitationForLatLong(latitude, longtitude);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Map<String, Integer> result) {
            imageView.setImageDrawable(getResources().getDrawable(weatherDataCollector.getIconForUpcomingWeather(result)));
            String upcommingWeather = weatherDataCollector.getUpcommingWeather(result);
            description.setText(upcommingWeather);
            MainActivity.weatherText = upcommingWeather;
            Intent intent2 = new Intent("com.sonyericsson.extras.aef.widget.START_REFRESH_IMAGE_REQUEST");
            intent2.putExtra(DATADATA, upcommingWeather);
            // sendBroadcast(intent);
            sendBroadcast(intent2);
        }
    }

}