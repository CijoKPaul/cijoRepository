package com.weather.weatherforecast;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import adapters.WeatherAdapter;
import utils.Constants;
import utils.NetworkUtil;

public class WeatherList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private ArrayList<HashMap<String,String>> list = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;
    private String data;
    private TextView txt_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_list);
        initView();

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);
        txt_title =  getSupportActionBar().getCustomView().findViewById(R.id.tvTitle);
        txt_title.setText("Weather Forecast");
        txt_title.setPadding(-150,0,0,0);

        weatherAdapter = new WeatherAdapter(getApplicationContext(),list);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(weatherAdapter);

        Intent intent = getIntent();
        data = intent.getStringExtra("data");

        callWebService();

    }

    private void callWebService() {
        if (!NetworkUtil.isNetworkAvailable(WeatherList.this,true)) {
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            new GetWeather().execute(Constants.GET_WEATHER_URL+data.replaceAll(" ", "%20")+"&appid="+Constants.API_KEY);
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.rvView);
        progressBar = findViewById(R.id.progressBar2);
    }

    //Weather WebService
    public class GetWeather extends AsyncTask<String , Void ,String> {
        String server_response;

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

            if (server_response != null) {
                String msg = null;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(server_response);
                    msg = jsonObject.optString("message");
                    int status = jsonObject.optInt("cod");
                    switch (status) {
                        case Constants.SUCCESS:
                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                JSONObject mainObject = jsonObject1.getJSONObject("main");
                                String temp = mainObject.getString("temp");
                                String desc= "";
                                JSONArray jsonArray1 = jsonObject1.getJSONArray("weather");
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject descObject = jsonArray1.getJSONObject(0);
                                    desc = descObject.getString("description");
                                }
                                JSONObject windObject = jsonObject1.getJSONObject("wind");
                                String wind = windObject.getString("speed");

                                HashMap<String,String> map = new HashMap<>();
                                map.put("temp",temp);
                                map.put("desc",desc);
                                map.put("wind",wind);
                                list.add(map);
                            }
                            weatherAdapter.notifyDataSetChanged();
                            break;
                        case Constants.FAILURE:
                            Toast.makeText(WeatherList.this,msg,Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                    Toast.makeText(WeatherList.this,"Service is not available right now. Please try after sometime.",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(WeatherList.this,"Service is not available right now. Please try after sometime.",Toast.LENGTH_LONG).show();
            }
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
