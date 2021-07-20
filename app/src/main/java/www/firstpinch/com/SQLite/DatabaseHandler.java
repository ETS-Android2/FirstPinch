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

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedObject;
import www.firstpinch.com.firstpinch2.Settings.UpdatePassword;

/**
 * Created by Rianaa Admin on 14-11-2016.
 */

//database handler for MainFeed (SQLite)
public class DatabaseHandler extends SQLiteOpenHelper {

    Context context;
    //All Static variables
    //Database Version
    private static final int DATABASE_VERSION = 14;

    //Database Name
    private static final String DATABASE_NAME = "DB";

    //table name
    private static final String TABLE_MAIN_FEED = "Main_Feed";

    //Table Columns names
    private static final String KEY_ID = "id";   //0
    private static final String KEY_PROFILE_NAME = "profile_name";  //1
    private static final String KEY_TITLE = "title"; //2
    private static final String KEY_HOUSE_ID = "house_id"; //3
    private static final String KEY_HOUSE_NAME = "house_name"; //4
    private static final String KEY_HOUSE_DESC = "house_desc"; //5
    private static final String KEY_HOUSE_IMAGE = "house_image"; //6
    private static final String KEY_USER_ID = "user_id"; //7
    private static final String KEY_USER_NAME = "user_name"; //8
    private static final String KEY_USER_IMAGE = "user_image"; //9
    private static final String KEY_HINT_PROFILE_NAME = "hint_profile_name"; //10
    private static final String KEY_HINT_PROFILE_IMAGE = "hint_profile_image"; //11
    private static final String KEY_HINT_PROFILE_TITLE = "hint_profile_title"; //12
    private static final String KEY_NUMBER_OF_IMAGES = "number_of_images"; //13
    private static final String KEY_DESC = "desc"; //14
    private static final String KEY_APPRECIATION_COUNT = "appreciation_count"; //15
    private static final String KEY_SCORE = "score"; //16
    private static final String KEY_USER_RATE = "user_rate"; //17
    private static final String KEY_RESPONSES = "responses"; //18
    private static final String KEY_BOOKMARK_ID = "bookmark_id"; //19
    private static final String KEY_BOOKMARK_STATUS = "bookmark_status"; //20
    private static final String KEY_POST_QUES = "post_ques"; //21
    private static final String KEY_ARRAY_OF_IMAGES = "array_of_images"; //22
    private static final String KEY_PREVIEW_TITLE = "preview_title"; //23
    private static final String KEY_PREVIEW_DESCRIPTION = "preview_description"; //24
    private static final String KEY_PREVIEW_IMAGE = "preview_image"; //25
    private static final String KEY_PREVIEW_LINK = "preview_link"; //26

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    //Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_MAIN_FEED + "("
                + KEY_ID + " TEXT," + KEY_PROFILE_NAME + " TEXT,"
                + KEY_TITLE + " TEXT," + KEY_HOUSE_ID + " TEXT," + KEY_HOUSE_NAME + " TEXT,"
                + KEY_HOUSE_DESC + " TEXT," + KEY_HOUSE_IMAGE + " TEXT," + KEY_USER_ID + " TEXT,"
                + KEY_USER_NAME + " TEXT," + KEY_USER_IMAGE + " TEXT," + KEY_HINT_PROFILE_NAME + " TEXT,"
                + KEY_HINT_PROFILE_IMAGE + " TEXT," + KEY_HINT_PROFILE_TITLE + " TEXT," + KEY_NUMBER_OF_IMAGES + " TEXT,"
                + KEY_DESC + " TEXT," + KEY_APPRECIATION_COUNT + " TEXT," + KEY_SCORE + " TEXT,"
                + KEY_USER_RATE + " TEXT," + KEY_RESPONSES + " TEXT," + KEY_BOOKMARK_ID + " TEXT,"
                + KEY_BOOKMARK_STATUS + " TEXT," + KEY_POST_QUES + " TEXT," + KEY_ARRAY_OF_IMAGES + " TEXT,"
                + KEY_PREVIEW_TITLE + " TEXT," + KEY_PREVIEW_DESCRIPTION + " TEXT," + KEY_PREVIEW_IMAGE
                + " TEXT," + KEY_PREVIEW_LINK + " TEXT"+ ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN_FEED);

        //Create tables again
        onCreate(db);
    }

    //Adding new data
    public void addFeedData(MainFeedObject current) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<String> arr_images = current.arr_images;
        JSONObject json = new JSONObject();
        try {
            json.put("imagesArray", new JSONArray(arr_images));
        } catch (JSONException e) {
            e.printStackTrace();
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(context, e.toString());
        }
        String arrayList = json.toString();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, current.getMainfeed_question_id()); // Contact Phone
        values.put(KEY_PROFILE_NAME, current.getProfileName()); // Contact Phone
        values.put(KEY_TITLE, current.getTitle()); // Contact Name
        values.put(KEY_HOUSE_ID, current.getMainfeed_house_id()); // Contact Phone
        values.put(KEY_HOUSE_NAME, current.getHouseName()); // Contact Phone
        values.put(KEY_HOUSE_DESC, current.getHouseDesc()); // Contact Phone
        values.put(KEY_HOUSE_IMAGE, current.getHouseImageUrl()); // Contact Phone
        values.put(KEY_USER_ID, current.getMainfeed_user_id()); // Contact Phone
        values.put(KEY_USER_NAME, current.getProfileName()); // Contact Phone
        values.put(KEY_USER_IMAGE, current.getProfileImageUrl()); // Contact Phone
        values.put(KEY_HINT_PROFILE_NAME, current.getHint_comm_profile_name()); // Contact Phone
        values.put(KEY_HINT_PROFILE_IMAGE, current.getHint_comm_profile_image()); // Contact Phone
        values.put(KEY_HINT_PROFILE_TITLE, current.getHint_comm_title()); // Contact Phone
        values.put(KEY_NUMBER_OF_IMAGES, current.getNum_of_images()); // Contact Phone
        values.put(KEY_DESC, current.getDesc()); // Contact Phone
        values.put(KEY_APPRECIATION_COUNT, current.getAppreciations_count()); // Contact Phone
        values.put(KEY_SCORE, current.getScore()); // Contact Phone
        values.put(KEY_USER_RATE, current.getUser_rate()); // Contact Phone
        values.put(KEY_RESPONSES, current.getResponses()); // Contact Phone
        values.put(KEY_BOOKMARK_ID, current.getBookmark_id()); // Contact Phone
        values.put(KEY_BOOKMARK_STATUS, current.getBookmark_status()); // Contact Phone
        values.put(KEY_POST_QUES, current.getPost_ques()); // Contact Phone
        values.put(KEY_ARRAY_OF_IMAGES, arrayList); // Contact Phone
        if(current.getPreview_image().isEmpty()&&current.preview_title.isEmpty()){
            values.put(KEY_PREVIEW_TITLE, " "); // Contact Phone
            values.put(KEY_PREVIEW_DESCRIPTION, " "); // Contact Phone
            values.put(KEY_PREVIEW_IMAGE, " "); // Contact Phone
            values.put(KEY_PREVIEW_LINK, " "); // Contact Phone
        } else {
            values.put(KEY_PREVIEW_TITLE, current.getPreview_title()); // Contact Phone
            values.put(KEY_PREVIEW_DESCRIPTION, current.getPreview_description()); // Contact Phone
            values.put(KEY_PREVIEW_IMAGE, current.getPreview_image()); // Contact Phone
            values.put(KEY_PREVIEW_LINK, current.getPreview_link()); // Contact Phone
        }

        // Inserting Row
        db.insert(TABLE_MAIN_FEED, null, values);
        db.close(); // Closing database connection
    }

    //Getting All data
    public List<MainFeedObject> getAllFeedData() {
        List<MainFeedObject> data = new ArrayList<MainFeedObject>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MAIN_FEED;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MainFeedObject current = new MainFeedObject();
                current.setMainfeed_question_id(cursor.getString(0));
                current.setProfileName(cursor.getString(1));
                current.setTitle(cursor.getString(2));
                current.setMainfeed_house_id(cursor.getInt(3));
                current.setHouseName(cursor.getString(4));
                current.setHouseDesc(cursor.getString(5));
                current.setHouseImageUrl(cursor.getString(6));
                current.setMainfeed_user_id(cursor.getInt(7));
                current.setProfileName(cursor.getString(8));
                current.setProfileImageUrl(cursor.getString(9));
                current.setHint_comm_profile_name(cursor.getString(10));
                current.setHint_comm_profile_image(cursor.getString(11));
                current.setHint_comm_title(cursor.getString(12));
                current.setNum_of_images(cursor.getInt(13));
                current.setDesc(cursor.getString(14));
                current.setAppreciations_count(cursor.getInt(15));
                current.setScore(cursor.getDouble(16));
                current.setUser_rate(cursor.getInt(17));
                current.setResponses(cursor.getInt(18));
                current.setBookmark_id(cursor.getString(19));
                current.setBookmark_status(cursor.getString(20));
                current.setPost_ques(cursor.getInt(21));
                current.setPreview_title(cursor.getString(23));
                current.setPreview_description(cursor.getString(24));
                current.setPreview_image(cursor.getString(25));
                current.setPreview_link(cursor.getString(26));

                JSONObject json = null;
                try {
                    json = new JSONObject(cursor.getString(22));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(context, e.toString());
                }
                //ArrayList items = json.optJSONArray("uniqueArrays");
                ArrayList<String> arrStr = new ArrayList<String>();

                JSONArray jArray = json.optJSONArray("imagesArray");

                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        arrStr.add(jArray.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(context, e.toString());
                    }
                }
                current.arr_images = arrStr;

                // Adding contact to list
                data.add(current);
            } while (cursor.moveToNext());
        }

        // return contact list
        return data;
    }

    /**
     * Remove all users and groups from database.
     */
    public void removeAll() {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TABLE_MAIN_FEED, null, null);
        db.close();
    }

}
