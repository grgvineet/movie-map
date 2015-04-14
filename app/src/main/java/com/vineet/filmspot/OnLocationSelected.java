package com.vineet.filmspot;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class OnLocationSelected extends Activity {

    Bundle extras;
    String location;
    ListView listView;
    DatabaseManager mydb;
    ArrayList arrayList;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_location_selected);

        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255,185,0,0)));

        extras = getIntent().getExtras();
        location = extras.getString("name");

        actionBar.setTitle(location);

        listView = (ListView)findViewById(R.id.lvMoviesByLocation);

        mydb = new DatabaseManager(this);
        mydb.open();
        arrayList = mydb.getMoviesFromLocation(location);
        mydb.close();

        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),DisplayActivity.class);
                intent.putExtra("name",adapterView.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.on_location_selected, menu);
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

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
