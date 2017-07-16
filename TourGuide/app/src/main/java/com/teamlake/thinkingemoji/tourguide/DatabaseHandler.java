package com.teamlake.thinkingemoji.tourguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul_000 on 7/15/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LocationManager";
    private static final String TABLE_LOCATIONS = "locations";

    private static final String KEY_ID = "id";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_NAME = "name";
    private static final String KEY_URL = "url";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create Table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LAT + " DOUBLE,"
                + KEY_LON + " DOUBLE," + KEY_NAME + " TEXT," + KEY_URL + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrade Table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        // Create tables again
        onCreate(db);
    }

    // Add a location
    public void addLocation(LocationData location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(KEY_ID, location.getID());
        values.put(KEY_LAT, location.getLat());
        values.put(KEY_LON, location.getLon());
        values.put(KEY_NAME, location.getName());
        values.put(KEY_URL, location.getURL());

        db.insert(TABLE_LOCATIONS, null, values);
        db.close();
    }

    // Get single location
    public LocationData getLocation(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOCATIONS,
                new String[] {KEY_ID, KEY_LAT, KEY_LON, KEY_NAME, KEY_URL}, KEY_ID + "=?",
                new String[] {String.valueOf(id) }, null, null, null, null);
        LocationData location = null;
        if(cursor!=null && cursor.moveToFirst()) {
            location = new LocationData(Integer.parseInt(cursor.getString(0)),
                    Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)),
                    cursor.getString(3), cursor.getString(4));
            cursor.close();
        }

        return location;
    }

    // Get all locations
    public List<LocationData> getAllLocations() {
        List<LocationData> locationList = new ArrayList<LocationData>();
        String selectQuery = "SELECT * FROM " + TABLE_LOCATIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                LocationData location = new LocationData();
                location.setID(Integer.parseInt(cursor.getString(0)));
                location.setLat(Double.parseDouble(cursor.getString(1)));
                location.setLon(Double.parseDouble(cursor.getString(2)));
                location.setName(cursor.getString(3));
                location.setURL(cursor.getString(4));
                locationList.add(location);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return locationList;
    }
}