package com.vineet.filmspot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by vineet on 06-Jul-14.
 */
public class DatabaseManager {

    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_NAME = "title";
    public static final String KEY_RELEASE_YEAR = "releaseyear";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_FUN_FACT = "funfact";
    public static final String KEY_PRODUCTION = "production";
    public static final String KEY_DISTRIBUTOR = "distributor";
    public static final String KEY_DIRECTOR = "director";
    public static final String KEY_WRITER = "writer";
    public static final String KEY_ACTOR_1 = "actor1";
    public static final String KEY_ACTOR_2 = "actor2";
    public static final String KEY_ACTOR_3 = "actor3";


    private static final String DATABASE_NAME = "Movies";
    private static final String DATABASE_TABLE = "Movies";
    private static final int DATABASE_VESRION = 1;

    private final Context ourContext;
    private SQLiteDatabase ourDatabase;
    private DbHelper ourHelper;

    public DatabaseManager(Context c) {
        ourContext = c;
    }


    private static class DbHelper extends SQLiteOpenHelper{

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VESRION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + "(" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_NAME + " TEXT NOT NULL, " + KEY_RELEASE_YEAR + " TEXT NOT NULL, " + KEY_LOCATION +
                    " TEXT NOT NULL, " + KEY_FUN_FACT + " TEXT NOT NULL, " + KEY_PRODUCTION + " TEXT NOT NULL, "
                    + KEY_DISTRIBUTOR + " TEXT NOT NULL, " + KEY_DIRECTOR + " TEXT NOT NULL, "
                    + KEY_WRITER + " TEXT NOT NULL, " + KEY_ACTOR_1 + " TEXT NOT NULL, " + KEY_ACTOR_2 + " TEXT NOT NULL, " + KEY_ACTOR_3 + " TEXT NOT NULL); ");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {
            db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE);
            onCreate(db);

        }

    }

    public DatabaseManager open(){
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }


    public void close(){
        ourHelper.close();
    }

    public ArrayList getAllMovies()
    {
        ArrayList array_list = new ArrayList();
        String[] coloumns = new String []{KEY_ROW_ID,KEY_NAME,KEY_RELEASE_YEAR,KEY_LOCATION,KEY_FUN_FACT,KEY_PRODUCTION,KEY_DISTRIBUTOR,KEY_DIRECTOR,KEY_WRITER,KEY_ACTOR_1,KEY_ACTOR_2,KEY_ACTOR_3} ;
        Cursor res =  ourDatabase.query(DATABASE_TABLE,coloumns,null,null,null,null,KEY_NAME);
        res.moveToFirst();
        array_list.add(res.getString(res.getColumnIndex(KEY_NAME)));
        res.moveToNext();
        while(res.isAfterLast() == false){

            if (array_list.get(array_list.size() - 1).toString().equals(res.getString(res.getColumnIndex(KEY_NAME)))) {

            } else {
                array_list.add(res.getString(res.getColumnIndex(KEY_NAME)));
            }
            res.moveToNext();

        }
        return array_list;
    }

    public ArrayList getAllLocations() {
        ArrayList array_list = new ArrayList();
        String[] coloumns = new String []{KEY_ROW_ID,KEY_NAME,KEY_RELEASE_YEAR,KEY_LOCATION,KEY_FUN_FACT,KEY_PRODUCTION,KEY_DISTRIBUTOR,KEY_DIRECTOR,KEY_WRITER,KEY_ACTOR_1,KEY_ACTOR_2,KEY_ACTOR_3} ;
        Cursor res =  ourDatabase.query(DATABASE_TABLE,coloumns,null,null,null,null,KEY_LOCATION);
        res.moveToFirst();
        array_list.add(res.getString(res.getColumnIndex(KEY_LOCATION)));
        res.moveToNext();
        while(res.isAfterLast() == false){

            if (array_list.get(array_list.size() - 1).toString().equals(res.getString(res.getColumnIndex(KEY_LOCATION)))) {
//
            } else {
                array_list.add(res.getString(res.getColumnIndex(KEY_LOCATION)));
            }
            res.moveToNext();

        }
        return array_list;
    }

    public String[] getData(String id){
        String[] coloumns = new String []{KEY_ROW_ID,KEY_NAME,KEY_RELEASE_YEAR,KEY_LOCATION,KEY_FUN_FACT,KEY_PRODUCTION,KEY_DISTRIBUTOR,KEY_DIRECTOR,KEY_WRITER,KEY_ACTOR_1,KEY_ACTOR_2,KEY_ACTOR_3} ;
        String[] result = new String[12];
        Cursor res =  ourDatabase.query(DATABASE_TABLE,coloumns,KEY_NAME + "=?",new String[]{id},null,null,null);
        if (res != null){
            res.moveToFirst();
            for (int i=0 ; i<12 ; i++){
                result[i] = res.getString(i);
            }
            return result;
        }
        return null;
    }

    public long createEntry(String nam, String release,String location,String funfact,String production,String distributor,String director,String writer,String actor1,String actor2,String actor3) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME,nam);
        cv.put(KEY_RELEASE_YEAR,release);
        cv.put(KEY_LOCATION,location);
        cv.put(KEY_FUN_FACT,funfact);
        cv.put(KEY_PRODUCTION,production);
        cv.put(KEY_DISTRIBUTOR,distributor);
        cv.put(KEY_DIRECTOR,director);
        cv.put(KEY_WRITER,writer);
        cv.put(KEY_ACTOR_1,actor1);
        cv.put(KEY_ACTOR_2,actor2);
        cv.put(KEY_ACTOR_3,actor3);

        return ourDatabase.insert(DATABASE_TABLE,null,cv);
    }


    public ArrayList getLocationsForMovie(String movie) {
        ArrayList array_list = new ArrayList();
        String[] coloumns = new String []{KEY_ROW_ID,KEY_NAME,KEY_RELEASE_YEAR,KEY_LOCATION,KEY_FUN_FACT,KEY_PRODUCTION,KEY_DISTRIBUTOR,KEY_DIRECTOR,KEY_WRITER,KEY_ACTOR_1,KEY_ACTOR_2,KEY_ACTOR_3} ;
        Cursor res =  ourDatabase.query(DATABASE_TABLE,coloumns,KEY_NAME + " =? ",new String[]{movie},null,null,null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(KEY_LOCATION)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList getMoviesFromLocation(String location){
        ArrayList array_list = new ArrayList();
        String[] coloumns = new String []{KEY_ROW_ID,KEY_NAME,KEY_RELEASE_YEAR,KEY_LOCATION,KEY_FUN_FACT,KEY_PRODUCTION,KEY_DISTRIBUTOR,KEY_DIRECTOR,KEY_WRITER,KEY_ACTOR_1,KEY_ACTOR_2,KEY_ACTOR_3} ;
        Cursor res =  ourDatabase.query(DATABASE_TABLE,coloumns,KEY_LOCATION + " =? ",new String[]{location},null,null,null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(KEY_NAME)));
            res.moveToNext();
        }
        return array_list;
    }


}
