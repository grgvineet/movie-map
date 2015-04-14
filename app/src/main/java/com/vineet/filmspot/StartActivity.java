package com.vineet.filmspot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StartActivity extends Activity {

    JSONObject obj;
    JSONArray rows;
    JSONArray arr;


    public static String filename = "Data";
    SharedPreferences preferences;

    ProgressDialog progressDialog;

    DatabaseManager mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);

        preferences = getSharedPreferences(filename,0);

        progressDialog = new ProgressDialog(this);

        mydb = new DatabaseManager(StartActivity.this);


        if (preferences.getBoolean("DatabaseCreated",false)){
            Thread timer = new Thread(){
                public void run(){
                    try{
                        sleep(1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };
            timer.start();
        }else{
            new DatabaseCreator().execute();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class DatabaseCreator extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setIcon(R.drawable.ic_launcher);
            progressDialog.setTitle("Welcome to Movie Map (San Francisco)");
            progressDialog.setMessage("Creating database,\nIt will take some time, please wait ...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            InputStream inputStream = getResources().openRawResource(R.raw.rows);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputreader);

            String line, results = "";
            try {
                while( (line = reader.readLine()) != null) {
                    results += line;
                }


                inputreader.close();
                inputreader.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                obj = new JSONObject(results);
                rows = obj.getJSONArray("data");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            try{
                mydb.open();
                for (int i=0;i<rows.length();i++){
                    arr = rows.getJSONArray(i);
                    mydb.createEntry(arr.get(8).toString(),arr.get(9).toString(),arr.get(10).toString(),arr.get(11).toString(),arr.get(12).toString(),arr.get(13).toString(),arr.get(14).toString(),arr.get(15).toString(),arr.get(16).toString(),arr.get(17).toString(),arr.get(18).toString());
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                mydb.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("DatabaseCreated",true);
            editor.commit();

            progressDialog.dismiss();

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
