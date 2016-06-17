package com.a_corp.rp_imagelocation.fragment;




import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;



import com.a_corp.rp_imagelocation.R;
import com.a_corp.rp_imagelocation.activity.ImageCollectionActivity;
import com.a_corp.rp_imagelocation.activity.MainActivity;
import com.a_corp.rp_imagelocation.net.ApiUtilities;

// Location
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

//Json Helpers
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Stream and network packages
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Purpose: 1. Loads the Primary fragment into the main activity
 *          2. Handles latitude/longitude discovery via LocationManager as well as other location-based functions
 *          3. Using a rest call and JSON parsing, retrieves public images from flicker based upon current location
 *
 * @author Magela Moore
 *         <p/>
 *         ©2016 a Company. All rights reserved.
 *
 * @history
 * 06.09.2016    - Initial Creation
 * 06.10/13.2016 - Implemented initial latitude/longitude discovery
 * 06.14.2016    - Parametrized/abstracted values
 *               - code cleanup and documentation
 * 06.14.2016    - Added logic to enable gridview photos to be selected and rendered in full screen
 * 06.16.2016    - Additional Code documentation and cleanup
 * 06.17.2016    - Pushed code to git repo
 *
 */

//MM_Notes:

//COMPLETED - 6/13/16: 1- (For View.OnClickListener ) lat/long is currently set to my location. Verified correct images  are returned via:https://www.flickr.com/map
//COMPLETED - 6/14/16: 2 - Cleanup Code
//COMPLETED - 6/14/16: 3 - Parameterize Values
//COMPLETED - 6/15/16: 4 - REQ: Using the GridViewLayout sample code as a basis, allow photos in the list to be tapped which will display the photo full screen in new view.
//COMPLETED - 6/16/16: 5 - Cleanup Code
//COMPLETED - 6/16/16: 6 - REQ: Incorporate custom animation
//COMPLETED - 6/16/16: 7 - In OnActivityCreated, set a toast indicating that we cannot find user's location
//COMPLETED - 6/16/16: 8 - Add additional Exception handling
//COMPLETED - 6/16/16: 9 - Run Lint Checks
//COMPLETED - 6/16/16: 10 - Test against both SGS4 and SGS6

public class MainActivityFragment extends Fragment implements LocationListener{

    final String TAG = MainActivity.class.getSimpleName();
    public static String[] returnedImageURIs;
    private String provider;

    private double latitude;
    private double longitude;

    //Minimum time interval between location updates, in milliseconds
    private long minUpdateInterval=400;

    //Minimum distance between location updates, in meters
    private float minLocationUpdateDistance =1;

    private LocationManager locationManager;
    private Location location;

    private Button button;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Location Manager
        locationManager = (LocationManager)this.getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Define the criteria how to select the locatioin provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        // Retrieve the provider
        provider = locationManager.getBestProvider(criteria, true);

        //Attempt to retrieve last known location
         try {
                location = locationManager.getLastKnownLocation(provider);
         } catch (SecurityException e) {
         }

         // Retrieve location coordinates if not null.
         // Otherwise, notify the user that the location is unavailable.
        if (location != null) {
            System.out.println("Your selected provider is: " + provider+".");
           // Ensure we have the most recent location
            onLocationChanged(location);
        } else {
            // If location services are disabled, prompt user to enable location
            // and display Location Settings activity
            Toast.makeText(getActivity().getApplicationContext(), "Unable to determine your location. Please enable location services.", Toast.LENGTH_LONG).show();
            enableLocationSettings();
        }

        button = (Button)getView().findViewById(R.id.button);
        button.setOnClickListener(button_listener);
    }



    /**
     * Obtain the most recent (latitude, longitude)
     **/
    @Override
    public void onLocationChanged(Location location) {
        latitude  = location.getLatitude();
        longitude = location.getLongitude();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderDisabled(String provider) {
       Toast.makeText(getActivity().getApplicationContext(), "GPS is disabled, will use coarse location.", Toast.LENGTH_LONG).show();
    }

    public void onProviderEnabled(String provider) {
       Toast.makeText(getActivity().getApplicationContext(), "GPS is enabled.", Toast.LENGTH_LONG).show();
    }

    /**
     * Request updates at startup
     **/
    @Override
    public void onResume() {
        super.onResume();
            try{
                locationManager.requestLocationUpdates(provider, minUpdateInterval, minLocationUpdateDistance, this);
            } catch (SecurityException e) {
        }
    }

    /**
     * Remove the locationlistener updates when Activity is paused
     * */
    @Override
    public void onPause() {
        super.onPause();
        try{
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
        }
    }

    /**
     * Return location-based images once user presses button
     */
    private View.OnClickListener button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /*
             * Button Animations
             * Use AnimatorSet to Rotate button and animate color transitions
             * */
            int start = Color.rgb(0xff, 0x66, 0x00);//start = orange
            int end = Color.rgb(0x00, 0xff, 0xff);//end = blue

            final AnimatorSet mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    ObjectAnimator.ofFloat(button, "rotation", 0f, 360f),
                    ObjectAnimator.ofInt(button, "backgroundColor", start, end)
            );

            mAnimatorSet.setDuration(750);
            mAnimatorSet.start();


            //Use singletons & local variables to access the parameters
            String url = ApiUtilities.getInstance().BASE_URL_IMAGES+"?method="+ApiUtilities.getInstance().ENDPOINT_SEARCH_IMAGES+"&api_key="+ApiUtilities.getInstance().API_IMAGE_CLIENT_KEY+"&lat="+latitude+"&lon="+longitude+"&format=json&nojsoncallback=1&text=";

            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
    }

    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }
/**
 * The DownloadTask class makes a service request and
 * downloads and parses the json response.
 */
    private class DownloadTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = params[0];
                // make a url connection
                URL url = new URL(BASE_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    jsonStr = null;
                }
                assert inputStream != null;
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    jsonStr = null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                android.util.Log.e("DOWNLOAD", "Error ", e);
                jsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        android.util.Log.e("DOWNLOAD", "Error closing stream", e);
                    }
                }
            }

            String[] returnedImageURIs = null;
            try {
                JSONObject jsonObject = new JSONObject(jsonStr).getJSONObject("photos");

                JSONArray jsonArray = (JSONArray) jsonObject.get("photo");
                returnedImageURIs = new String[jsonArray.length()];
                //build the returned image uri
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    int farm = obj.getInt("farm");
                    String server = obj.getString("server");
                    String id = obj.getString("id");
                    String secret = obj.getString("secret");
                    returnedImageURIs[i] = "http://farm"+farm+".static.flickr.com/"+server+"/"+id+"_"+secret+".jpg";
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return returnedImageURIs;
        }
        @Override
        protected void onPostExecute(String[] returnedImageURIs) {
            //Create an intent and add extras to it.
            Intent intent = new Intent(getActivity(), ImageCollectionActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, returnedImageURIs);
            startActivity(intent);
        }
    }
}
