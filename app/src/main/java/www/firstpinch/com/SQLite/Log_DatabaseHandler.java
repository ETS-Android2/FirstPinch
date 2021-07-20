package www.firstpinch.com.firstpinch2.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedObject;

/**
 * Created by Rianaa Admin on 12-01-2017.
 */

public class Log_DatabaseHandler extends SQLiteOpenHelper {

    Context context;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "DB_Log";

    //table name
    private static final String TABLE_LOG = "Log";

    //Table Columns names
    private static final String KEY_ID = "id";   //0
    private static final String KEY_ERROR = "error"; //1
    private static final String KEY_DEVICE_ID = "device_id"; //2
    private static final String KEY_DEVICE_WIDTH = "device_width"; //3
    private static final String KEY_DEVICE_HEIGHT = "device_height"; //4

    public Log_DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    //Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOG_TABLE = "CREATE TABLE " + TABLE_LOG + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_ERROR + " TEXT," + KEY_DEVICE_ID + " TEXT," + KEY_DEVICE_WIDTH + " TEXT,"
                + KEY_DEVICE_HEIGHT + " TEXT" + ")";

        db.execSQL(CREATE_LOG_TABLE);
    }

    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
        //Create tables again
        onCreate(db);
    }

    // Adding new
    public void addErrorData(String error,String device_id,String device_width,String device_height) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_ERROR, error);
        values.put(KEY_DEVICE_ID, device_id);
        values.put(KEY_DEVICE_WIDTH, device_width);
        values.put(KEY_DEVICE_HEIGHT, device_height);

        //Inserting Row
        db.insert(TABLE_LOG, null, values);
        db.close(); // Closing database connection
    }

    //Getting All Errors
    public String getAllErrorData() {
        List<Log_Error_Object> data = new ArrayList<Log_Error_Object>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LOG;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        /*//looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log_Error_Object current = new Log_Error_Object();
                current.setLog_error_id(cursor.getString(0));
                current.setLog_error(cursor.getString(1));
                current.setDevice_id(cursor.getString(2));
                current.setDevice_width(cursor.getString(3));
                current.setDevice_height(cursor.getString(4));

                // Adding contact to list
                data.add(current);
            } while (cursor.moveToNext());
        }

        // return contact list
        return data;*/

        JSONArray resultSet     = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            //Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        //Log.d("TAG_NAME", e.getMessage()  );
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(context, e.toString());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString() );
        return resultSet.toString();
    }

    /**
     * Remove all users and groups from database.
     */
    public void removeAllError() {
        //If whereClause is null, it will delete all rows.
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TABLE_LOG, null, null);
        db.close();
    }

}