package www.firstpinch.com.firstpinch2.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import www.firstpinch.com.firstpinch2.MainFeed.MainFeedObject;

/**
 * Created by Rianaa Admin on 23-11-2016.
 */

//data baseHandler for Draft
public class Draft_DatabaseHandler extends SQLiteOpenHelper {

    Context context;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 7;

    // Database Name
    private static final String DATABASE_NAME = "DB_Draft";

    //table name
    private static final String TABLE_POST = "Draft_Post";
    private static final String TABLE_QUESTION = "Draft_Question";

    //Table Columns names
    private static final String KEY_ID = "id";   //0
    private static final String KEY_TITLE = "title"; //1
    private static final String KEY_HOUSE_ID = "house_id"; //2
    private static final String KEY_HOUSE_NAME = "house_name"; //3
    private static final String KEY_NUMBER_OF_IMAGES = "number_of_images"; //4
    private static final String KEY_DESC = "desc"; //5
    private static final String KEY_POST_QUES = "post_ques"; //6
    private static final String KEY_ARRAY_OF_IMAGES = "array_of_images"; //7

    public Draft_DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    //Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POST_TABLE = "CREATE TABLE " + TABLE_POST + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_TITLE + " TEXT," + KEY_HOUSE_ID + " TEXT," + KEY_HOUSE_NAME + " TEXT,"
                + KEY_NUMBER_OF_IMAGES + " TEXT,"
                + KEY_DESC + " TEXT," + KEY_POST_QUES + " TEXT," + KEY_ARRAY_OF_IMAGES + " TEXT" + ")";
        String CREATE_QUESTION_TABLE = "CREATE TABLE " + TABLE_QUESTION + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_TITLE + " TEXT," + KEY_HOUSE_ID + " TEXT," + KEY_HOUSE_NAME + " TEXT,"
                + KEY_NUMBER_OF_IMAGES + " TEXT,"
                + KEY_DESC + " TEXT," + KEY_POST_QUES + " TEXT," + KEY_ARRAY_OF_IMAGES + " TEXT" + ")";

        db.execSQL(CREATE_POST_TABLE);
        db.execSQL(CREATE_QUESTION_TABLE);
    }

    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        //Create tables again
        onCreate(db);
    }

    // Adding new
    public void addPostData(String title,String desc,String house_id,String house_name,String post_question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_HOUSE_ID, house_id);
        values.put(KEY_HOUSE_NAME, house_name);
        values.put(KEY_DESC, desc);
        values.put(KEY_POST_QUES, post_question);

        //Inserting Row
        db.insert(TABLE_POST, null, values);
        db.close(); // Closing database connection
    }

    //Getting All Contacts
    public List<String> getAllDraftPostData() {
        List<String> data = new ArrayList<String>();
        //Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_POST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if (cursor.moveToLast()) {
            do {
                data.add(cursor.getString(1)); //title
                data.add(cursor.getString(2)); //house_id
                data.add(cursor.getString(3)); //house_name
                data.add(cursor.getString(5)); //desc
                data.add(cursor.getString(6)); //post_ques

            } while (cursor.moveToPrevious());
        }

        // return contact list
        return data;
    }

    /**
     * Remove all users and groups from database.
     */
    public void removeAllPost() {
        //If whereClause is null, it will delete all rows.
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TABLE_POST, null, null);
        db.close();
    }

    //Adding new contact
    public void addQuestionData(String title,String desc,String house_id,String house_name,String post_question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_HOUSE_ID, house_id);
        values.put(KEY_HOUSE_NAME, house_name);
        values.put(KEY_DESC, desc);
        values.put(KEY_POST_QUES, post_question);

        // Inserting Row
        db.insert(TABLE_QUESTION, null, values);
        db.close(); // Closing database connection
    }

    //Getting All Contacts
    public List<String> getAllDraftQuestionData() {
        List<String> data = new ArrayList<String>();
        //Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if (cursor.moveToLast()) {
            do {
                data.add(cursor.getString(1)); //title
                data.add(cursor.getString(2)); //house_id
                data.add(cursor.getString(3)); //house_name
                data.add(cursor.getString(5)); //desc
                data.add(cursor.getString(6)); //post_ques

            } while (cursor.moveToPrevious());
        }

        //return list
        return data;
    }

    /**
     * Remove all users and groups from database.
     */
    public void removeAllQuestion() {
        //If whereClause is null, it will delete all rows.
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TABLE_QUESTION, null, null);
        db.close();
    }

}