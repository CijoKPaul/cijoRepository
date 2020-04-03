package com.weather.weatherforecast;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.location.aravind.getlocation.GeoLocator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import utils.Constants;
import utils.NetworkUtil;

public class SearchWeatherActivity extends AppCompatActivity  {

    private TextView txtAddress;
    private Button btGps,btSearch;
    private ProgressBar progressBar;
    private final int MULTIPLE_PERMISSIONS = 10;
    private ArrayList<String> contentList = new ArrayList<>();
    private ArrayList<MultiSelectModel> list = new ArrayList<>();
    private ArrayList<HashMap<String,String>> listWeather = new ArrayList<>();
    private TextView txt_title;

    private String[] permissions= new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_weather);
        initView();

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);
        txt_title =  getSupportActionBar().getCustomView().findViewById(R.id.tvTitle);
        txt_title.setText("Weather Forecast");
        txt_title.setPadding(0,0,0,0);


        if (checkPermissions()){
        //  permissions  granted.
            GeoLocator geoLocator = new GeoLocator(getApplicationContext(),SearchWeatherActivity.this);

        }

        btGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissions()){
                    //  permissions  granted.
                    GeoLocator geoLocator = new GeoLocator(getApplicationContext(),SearchWeatherActivity.this);
                    Double lat = geoLocator.getLattitude();
                    Double longs = geoLocator.getLongitude();

                    convertLocationToAddress(lat,longs);
                }

            }
        });


        //Adding cities
        contentList.add("Abu Dhabi");
        contentList.add("Dubai");
        contentList.add("Ajman");
        contentList.add("Fujairah");
        contentList.add("Al Ain");
        contentList.add("Sharjah");
        contentList.add("Al Dhaid");

        for (int i = 0; i < 7; i++) {
            MultiSelectModel multiSelectModel = new MultiSelectModel(i,contentList.get(i));
            list.add(multiSelectModel);
        }

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog()
                        .title("Select Cities")
                        .titleSize(25)
                        .positiveText("Done")
                        .negativeText("Cancel")
                        .setMinSelectionLimit(3)
                        .setMaxSelectionLimit(7)
                        .multiSelectList(list)
                        .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                            @Override
                            public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String dataString) {

                                String result = String.join(",", selectedNames);
                                Intent intent = new Intent(SearchWeatherActivity.this,WeatherList.class);
                                intent.putExtra("data",result);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancel() {
                                Log.d("MSG","Dialog cancelled");
                            }


                        });

                multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");


            }
        });


    }

    private void convertLocationToAddress(Double lat, Double longs) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, longs, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            txtAddress.setText(address);

            Intent intent = new Intent(SearchWeatherActivity.this,WeatherList.class);
            intent.putExtra("data",city);
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        txtAddress = findViewById(R.id.textView6);
        btGps = findViewById(R.id.button2);
        btSearch = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);

    }

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(SearchWeatherActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissionsList[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if (grantResults.length > 0) {
                    String permissionsDenied = "";
                    for (String per : permissionsList) {
                        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                            permissionsDenied += "\n" + per;

                        }

                    }
                    // Show permissionsDenied

                }
                return;
            }
        }
    }

}
