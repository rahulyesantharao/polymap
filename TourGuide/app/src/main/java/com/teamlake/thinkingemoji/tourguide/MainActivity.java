package com.teamlake.thinkingemoji.tourguide;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
//import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends Activity implements ClickInterface {

    private static final String[] PLACE_KEYWORDS = {"accounting", "airport", "amusement_park", "aquarium", "art_gallery", "atm",
        "bakery", "bank", "bar", "beauty_salon", "bicycle_store", "book_store", "bowling_alley", "bus_station", "cafe",
        "campground", "car_dealer", "car_rental", "car_repair", "car_wash", "casino", "cemetery", "church", "city_hall",
        "clothing_store", "convenience_store", "courthouse", "dentist", "department_store", "doctor", "electrician",
        "electronics_store", "embassy", "fire_station", "florist", "funeral_home", "furniture_store", "gas_station", "gym",
        "hair_care", "hardware_store", "hindu_temple", "home_goods_store", "hospital", "insurance_agency", "jewelry_store",
        "laundry", "lawyer", "library", "liquor_store", "local_government_office", "locksmith", "lodging", "meal_delivery",
        "meal_takeaway", "mosque", "movie_rental", "movie_theater", "moving_company", "museum", "night_club", "painter", "park",
        "parking", "pet_store", "pharmacy", "physiotherapist", "plumber", "police", "post_office", "real_estate_agency",
        "restaurant", "roofing_contractor", "rv_park", "school", "shoe_store", "shopping_mall", "spa", "stadium", "storage",
        "store", "subway_station", "synagogue", "taxi_stand", "train_station", "transit_station", "travel_agency", "university",
        "veterinary_care", "zoo"};
    private static final String CLOUD_VISION_API_KEY = "AIzaSyAvjSSy1WMhIxtfqEXu2vaOYrDiVi9C7nM";
    private static final String PLACE_VISION_API_KEY = "AIzaSyCIOI8zRmIkdqSzhyDaQwOl4sb1Z_Y-laE";
    public static final String FILE_NAME = "pic.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;
    private ImageView mMainImage;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private FusedLocationProviderClient mFusedLocationClient;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }

        db = new DatabaseHandler(this);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                } else {
                    // TODO:
                    //  Handle user not giving location
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void buttonClicked() {
        Log.d(TAG + " 77", "*****BUTTON WAS CLICKED");
        Uri photoUri = Uri.fromFile(getCameraFile()); //FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
        Log.d(TAG + " 79", photoUri.toString());
        uploadImage(photoUri);
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);
                //mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        //mImageDetails.setText(R.string.loading_message);
        Log.d(TAG + " 113", "LOADING");

        // Do the real work in an async task, because we need to use the network anyway
        class MyTask extends AsyncTask<Object, Void, String> {
            private Context mContext;

            public MyTask(Context context) {
                mContext = context;
            }

            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                //mImageDetails.setText(result);
                Log.d(TAG + " 292", result);
                String[] temp_results = result.split(":");
                ArrayList<String> results = new ArrayList<String>();
                String foundTypeParam = "";
                for(int i=2; i<temp_results.length; i++) {
                    results.add(temp_results[i].split("\n")[0].substring(1));
                    Log.d(TAG + " 297", results.get(i-2));
                }
                for(int i=0; i<results.size(); i++) {
                    if(Arrays.asList(PLACE_KEYWORDS).contains(results.get(i))) {
                        foundTypeParam = results.get(i);
                        break;
                    }
                }
                final String queryParam = results.get(0);
                final String typeParam = foundTypeParam;
                if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener((Activity) mContext, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    Log.d(TAG + " 302", location.toString());
                                    final double lat = location.getLatitude();
                                    final double lon = location.getLongitude();

                                    // Instantiate the RequestQueue.
                                    RequestQueue queue = Volley.newRequestQueue(mContext);
                                    //String url ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

                                    String url = new Uri.Builder()
                                            .scheme("https")
                                            .authority("maps.googleapis.com")
                                            .path("maps/api/place/nearbysearch/json")
                                            .appendQueryParameter("key", PLACE_VISION_API_KEY)
                                            .appendQueryParameter("location", lat + "," + lon)
                                            .appendQueryParameter("radius", "100")
                                            .appendQueryParameter("query", queryParam)
                                            .appendQueryParameter("type", typeParam)
                                            .build()
                                            .toString();
                                    Log.d(TAG + " 347", url);
                                    // Request a string response from the provided URL.
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    // Display the first 500 characters of the response string.
                                                    Log.d(TAG + " 325", "Response is: "+ response);
                                                    String query = null;
//                                                    String id = null;
                                                    LocationData toSave = new LocationData();
                                                    toSave.setLat(lat);
                                                    toSave.setLon(lon);
                                                    try {
                                                        query = getQuery(response);
//                                                        id = getQueryID(response);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Log.d(TAG + "361", "Query is " + query);
                                                    toSave.setName(query);
//                                                    toSave.setID(id);
                                                    getOutput(toSave, query);
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d(TAG + " 330", "That didn't work!");
                                        }
                                    });
                                    // Add the request to the RequestQueue.
                                    queue.add(stringRequest);
                                }
                            }
                        });
                }
            }
        }
        MyTask task = new MyTask(this);
        task.execute();
    }

    private String getQuery(String response) throws JSONException {
        final JSONObject obj = new JSONObject(response);
        final JSONArray results = obj.getJSONArray("results");
        final JSONObject location = results.getJSONObject(0);
        return location.getString("name");
    }

//    private String getQueryID(String response) throws JSONException {
//        final JSONObject obj = new JSONObject(response);
//        final JSONArray results = obj.getJSONArray("results");
//        final JSONObject location = results.getJSONObject(0);
//        return location.getString("id");
//    }

    private void getOutput(LocationData toSave, String query) {
        final LocationData toSaveFinal = toSave;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = new Uri.Builder()
                .scheme("https")
                .authority("en.wikipedia.org")
                .path("w/api.php")
                .appendQueryParameter("action", "opensearch")
                .appendQueryParameter("search", query)
                .appendQueryParameter("limit", "1")
                .appendQueryParameter("namespace", "0")
                .appendQueryParameter("format", "json")
                .build()
                .toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG + " 325", "Response is: "+ response);
                        String title = null;
                        try {
                            title = getWikiTitle(response);
                            toSaveFinal.setURL(getWikiUrl(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG + "361", "Title is " + title + "; URL is " + toSaveFinal.getURL());
                        getSnippet(toSaveFinal, title);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG + " 330", "That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private String getWikiUrl(String response) throws JSONException {
        final JSONArray arr = new JSONArray(response);
        final JSONArray urls = arr.getJSONArray(3);
        return urls.getString(0);
    }

    private String getWikiTitle(String response) throws JSONException {
        final JSONArray arr = new JSONArray(response);
        final JSONArray titles = arr.getJSONArray(1);
        return titles.getString(0);
    }

    private void getSnippet(LocationData toSave, String title) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String finalTitle = title;
        final LocationData toSaveFinal = toSave;
        String url = new Uri.Builder()
                .scheme("https")
                .authority("en.wikipedia.org")
                .path("w/api.php")
                .appendQueryParameter("format", "json")
                .appendQueryParameter("action", "query")
                .appendQueryParameter("prop", "extracts")
                .appendQueryParameter("exintro", "")
                .appendQueryParameter("explaintext", "")
                .appendQueryParameter("titles", title.replaceAll(" ", "_"))
                .build()
                .toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG + " 325", "Response is: "+ response);
                        try {
                            getFinalString(toSaveFinal, response, finalTitle);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG + " 330", "That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getFinalString(LocationData toSave, String response, String title) throws JSONException {
        final JSONObject obj = new JSONObject(response);
        final JSONObject results = obj.getJSONObject("query");
        final JSONObject pages = results.getJSONObject("pages");
        Iterator<String> keys = pages.keys();
        String strName = keys.next();
        final JSONObject finalObj = pages.getJSONObject(strName);
        String finalString = title + ": " + finalObj.getString("extract");
        Log.d(TAG + "479", "FINAL STRING IS " + finalString);

        final String finalURL = toSave.getURL();
        // Create the SnackBar and attach an OnClickListener
        Snackbar resultSB = Snackbar.make(findViewById(R.id.container), finalString, Snackbar.LENGTH_INDEFINITE)
            .setAction("More", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openWiki = new Intent(getBaseContext(), WebViewActivity.class);
                    openWiki.putExtra("url", finalURL);
                    startActivity(openWiki);
                }
            });

        View viewSB = resultSB.getView();
        TextView tvSB = (TextView) (viewSB).findViewById(android.support.design.R.id.snackbar_text);

        // Moves the SnackBar to the top of the screen
        CoordinatorLayout.LayoutParams paramsSB = (CoordinatorLayout.LayoutParams)viewSB.getLayoutParams();
        paramsSB.gravity = Gravity.TOP;
        viewSB.setLayoutParams(paramsSB);

        viewSB.setBackgroundColor(Color.parseColor("#80000000"));

        tvSB.setTextSize(25);
        tvSB.setTypeface(tvSB.getTypeface(), Typeface.BOLD);
        tvSB.setMaxLines(6);

        resultSB.show();

        // Save the toSave data
        db.addLocation(toSave);
        Log.d(TAG, db.getAllLocations().toString());
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        } else {
            message += "nothing";
        }

        return message;
    }
}
