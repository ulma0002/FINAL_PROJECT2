/**
 * Oc Transpo App
 *
 * @author  Sohaila Binte Ridwan
 * @version 1.1
 * @since   2018-04-18
 */

package com.example.poly.final_project.transport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.poly.final_project.transport.BusStopContract.BusStopEntry;

/**
 * This class manages database activity for the application
 */
public class OCTranspoDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "OCTranspo.db";

    private static OCTranspoDbHelper instance = null;
    private SQLiteDatabase db;
    private Context context;

    private static final String SQL_CREATE_BUS_STOPS =
            "CREATE TABLE IF NOT EXISTS " + BusStopEntry.TABLE_NAME + " (" +
                    BusStopEntry._ID + " INTEGER PRIMARY KEY," +
                    BusStopEntry.COLUMN_NAME_BUS_STOP_NO + " INTEGER)";

    private static final String SQL_DELETE_BUS_STOPS =
            "DROP TABLE IF EXISTS " + BusStopEntry.TABLE_NAME;

    private static final String SQL_DELETE_BUS_STOP_WHERE = "DELETE FROM " + BusStopEntry.TABLE_NAME + " WHERE " +
            BusStopEntry.COLUMN_NAME_BUS_STOP_NO + "=?";

    private OCTranspoDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method to get database connectivity instance
    public static OCTranspoDbHelper getInstance(Context context){
        if(instance == null){
            instance = new OCTranspoDbHelper(context);
            instance.open();
        }
        return instance;
    }

    private OCTranspoDbHelper open(){
        if(db == null){
            db = getWritableDatabase();
        }
        return this;
    }

    // Method to save bus stop numbers
    public long insert(ContentValues values){
        if(db==null) open();
        long newRowId = db.insert(BusStopEntry.TABLE_NAME, null, values);
        return newRowId;
    }

    // Method to retrieve saved bus stop numbers
    public Cursor getBusStops(){
        String[] projections = new String[]{
                BusStopEntry._ID,
                BusStopEntry.COLUMN_NAME_BUS_STOP_NO
        };
        return db.query(BusStopEntry.TABLE_NAME, projections, null, null, null, null, null);
    }

    // Helper method to get bus stop number from a set of results
    public Integer getBusStopNoFromCursor(Cursor cursor){
        Integer bus_stop_number = cursor.getInt(cursor.getColumnIndex(BusStopEntry.COLUMN_NAME_BUS_STOP_NO));
        return bus_stop_number;
    }


    public long getIdFromCursor(Cursor cursor){
        long id = cursor.getInt(cursor.getColumnIndex(BusStopEntry._ID));
        return id;
    }

    // Method to delete a bus stop number
    public void remove(long id){
        if(db==null) open();
        int dr = db.delete(BusStopEntry.TABLE_NAME, BusStopEntry._ID + "=" + id, null);
        Log.i("Deleted ",Integer.toString(dr));
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_BUS_STOPS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_BUS_STOPS);
        onCreate(sqLiteDatabase);
    }
}
