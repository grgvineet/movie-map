package com.vineet.filmspot;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowLocations extends Activity {

    String name;
    DatabaseManager mydb;
    Bundle extras;
    ArrayList locations;

    int markeradded;

    public static String filename = "Data";
    SharedPreferences preferences;
    int mapType;

    ActionBar actionBar;
    GoogleMap map;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_show_locations);

        preferences = getSharedPreferences(filename,0);
        mapType = preferences.getInt("MapType",GoogleMap.MAP_TYPE_NORMAL);

        actionBar = getActionBar();
        actionBar.setLogo(R.drawable.map);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(37.7833, -122.4167), 11);
        map.animateCamera(cameraUpdate);
        map.setMapType(mapType);
        mydb = new DatabaseManager(this);

        extras = getIntent().getExtras();
        name = extras.getString("name");

        actionBar.setTitle("Locations - " + name);

        mydb.open();
        locations = mydb.getLocationsForMovie(name);
        mydb.close();


        setProgressBarIndeterminateVisibility(true);
        for (int i=0;i<locations.size();i++) {
            new MarkerAdder().execute(locations.get(i).toString());
        }


    }


    private class MarkerAdder extends AsyncTask<String,Void,Void> {

        double longitude,latitude;
        String response;
        String location;
        String tempLocation;
        @Override
        protected Void doInBackground(String... strings) {

            location = strings[0];
            tempLocation = location;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            location = location.replace(" ","+");
            if (location.contains("(")){
                location = location.substring(location.indexOf("(")+1,location.indexOf(")"));
            }else{
                location += "+sanfrancisco";
            }

            String url = new String("http://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&sensor=true_or_false");

            try {
                HttpGet httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();

                response = EntityUtils.toString(httpEntity);
                JSONObject jsonObj = new JSONObject(response);
                latitude = ((JSONObject)jsonObj.getJSONArray("results").get(0)).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                longitude = ((JSONObject)jsonObj.getJSONArray("results").get(0)).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

            }catch(Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            markeradded++;
            LatLng coordinates = new LatLng(latitude,longitude);
            try {
                if (longitude != 0 && latitude != 0) {
                    map.addMarker(new MarkerOptions().position(coordinates).title(tempLocation));
                }

                if (markeradded == locations.size()){
                    setProgressBarIndeterminateVisibility(false);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_locations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences.Editor editor = preferences.edit();
        int id = item.getItemId();

        if (id == R.id.normal) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            editor.putInt("MapType",GoogleMap.MAP_TYPE_NORMAL);
            editor.commit();
            return true;
        }else if (id == R.id.satellite){
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            editor.putInt("MapType",GoogleMap.MAP_TYPE_SATELLITE);
            editor.commit();
            return true;
        }else if(id == R.id.terrain){
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            editor.putInt("MapType",GoogleMap.MAP_TYPE_TERRAIN);
            editor.commit();
            return  true;
        }else if(id == R.id.hybrid){
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            editor.putInt("MapType",GoogleMap.MAP_TYPE_HYBRID);
            editor.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
