package com.vineet.filmspot;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class DisplayActivity extends Activity {

    Bundle extras;
    String name;
    String[] result;
    DatabaseManager mydb;
    TextView releaseYear,funfact,production,distributor,director,writer,actor1,plot,genre,imdbrating,award,trailer;
    String response;
    Button mapButton;

    String movieId,posterPath;
    int tmdbId;
    ArrayList arrayList;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_display);

        extras = getIntent().getExtras();
        name = extras.getString("name");
        System.out.println("name recieved " + name);

        actionBar = getActionBar();
        actionBar.setTitle(name);
        actionBar.setLogo(R.drawable.movie);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255,15,120,40)));

        releaseYear = (TextView)findViewById(R.id.releaseyear);
        funfact = (TextView)findViewById(R.id.funfact);
        production = (TextView)findViewById(R.id.production);
        distributor = (TextView)findViewById(R.id.distributor);
        director = (TextView)findViewById(R.id.director);
        writer = (TextView)findViewById(R.id.writer);
        actor1 = (TextView)findViewById(R.id.actor1);
        award = (TextView)findViewById(R.id.award);
        genre = (TextView)findViewById(R.id.genre);
        plot = (TextView)findViewById(R.id.plot);
        imdbrating = (TextView)findViewById(R.id.imdbrating);
        trailer = (TextView)findViewById(R.id.trailer);

        mapButton = (Button)findViewById(R.id.mapButton);

        mydb = new DatabaseManager(this);
        mydb.open();
        result = mydb.getData(name);
        if (result == null){
            System.out.println("null recieved");
        }
        arrayList = mydb.getLocationsForMovie(name);
        mydb.close();

        releaseYear.setText(result[2]);
        if(result[4].equals("null")) {
            funfact.setText("N/A");
        }else{
            funfact.setText(result[4]);
        }
        production.setText(result[5]);
        distributor.setText(result[6]);
        director.setText(result[7]);
        writer.setText(result[8]);



        new Httprequest().execute();

        for (int i=0;i < arrayList.size();i++){
            if (arrayList.get(0).toString().equals("null")){
                mapButton.setEnabled(false);
            }
        }

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShowLocations.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("Movie Map (San Francisco)");

            builder.setView(messageView);
            builder.create();
            builder.show();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private class Httprequest extends AsyncTask<Void,Void,Void>{

        Bitmap bitmap;
        JSONObject jsonObj;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                System.out.println("amking htto requset");
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity = null;
                HttpResponse httpResponse = null;
                String url = new String("http://www.omdbapi.com/?t=" + name + "&y=" + result[2] + "&tomatoes=false");
                url = url.replace(" ","+");

                HttpGet httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();

                response = EntityUtils.toString(httpEntity);
                System.out.println(response);

                jsonObj = new JSONObject(response);
                if (jsonObj.getString("Response").equals("True")) {
                    posterPath = jsonObj.getString("Poster");
                }

                bitmap = BitmapFactory.decodeStream((InputStream) new URL(posterPath).getContent());

            }catch (Exception e){
                System.out.println("http request failed");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog

            ImageView i = (ImageView)findViewById(R.id.imageView);
            i.setImageBitmap(bitmap);
            setProgressBarIndeterminateVisibility(false);

            try {
                plot.setText(jsonObj.getString("Plot"));
                award.setText(jsonObj.getString("Awards"));
                genre.setText(jsonObj.getString("Genre"));
                imdbrating.setText(jsonObj.getString("imdbRating"));
                actor1.setText(jsonObj.getString("Actors"));
                movieId = jsonObj.getString("imdbID");
            }catch (Exception e){
                e.printStackTrace();
            }

            new Trailer().execute();

        }
    }



    private class Trailer extends AsyncTask<Void,Void,Void>{


        JSONObject jsonObj;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                System.out.println("amking htto requset");
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity = null;
                HttpResponse httpResponse = null;
                String url = new String("http://api.themoviedb.org/3/search/movie?api_key=789a2d22b2198ad7de253df431e00579&query="+ name +"&year=" + result[2]);
                url = url.replace(" ","+");

                HttpGet httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();

                response = EntityUtils.toString(httpEntity);


                jsonObj = new JSONObject(response);
                tmdbId = ((JSONObject)jsonObj.getJSONArray("results").get(0)).getInt("id");



                url = new String("http://api.themoviedb.org/3/movie/" + tmdbId +"/videos?api_key=789a2d22b2198ad7de253df431e00579");
                url = url.replace(" ","+");

                httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();

                response = EntityUtils.toString(httpEntity);

                jsonObj = new JSONObject(response);


            }catch (Exception e){
                System.out.println("http request failed");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            setProgressBarIndeterminateVisibility(false);

            try {
                trailer.setText("https://www.youtube.com/watch?v=" + ((JSONObject)jsonObj.getJSONArray("results").get(0)).getString("key"));
                trailer.setTextColor(Color.BLUE);
                trailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getText().toString())));
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
