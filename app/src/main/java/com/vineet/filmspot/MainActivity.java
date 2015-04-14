package com.vineet.filmspot;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends Activity {

    Spinner spinner;
    ListView listView;
    EditText search;
    DatabaseManager mydb;
    ArrayList arrayList;

    ActionBar actionBar;


    private static final int MOVIES = 0;
    private static final int LOCATIONS = 1;
    private int optionSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getActionBar();
        actionBar.setTitle("Movie Map");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255,185,0,0)));

        optionSelected = MOVIES;

        listView = (ListView)findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);


        mydb = new DatabaseManager(MainActivity.this);
        mydb.open();
        if (optionSelected == MOVIES) {
            arrayList = mydb.getAllMovies();
        }else{
            arrayList = mydb.getAllLocations();
        }
        mydb.close();

        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList));




        spinner = (Spinner)findViewById(R.id.spinner);

        spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,new String[] {"movie","location"}));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    optionSelected = MOVIES;
                }else{
                    optionSelected = LOCATIONS;
                }
                mydb.open();
                if (optionSelected == MOVIES) {
                    arrayList = mydb.getAllMovies();

                }else{
                    arrayList = mydb.getAllLocations();

                }
                mydb.close();

                listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,arrayList));
                search.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        search = (EditText)findViewById(R.id.search);
        search.clearFocus();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String res =  adapterView.getItemAtPosition(i).toString();
                if (optionSelected == MOVIES){
                    System.out.println(adapterView.getItemAtPosition(i).toString());
                    Intent intent = new Intent(getApplicationContext(),DisplayActivity.class);
                    intent.putExtra("name",res);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(),OnLocationSelected.class);
                    intent.putExtra("name",res);
                    startActivity(intent);
                }
            }

        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
}
