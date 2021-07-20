package www.firstpinch.com.firstpinch2.ErrorLogs;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import www.firstpinch.com.firstpinch2.SQLite.Log_DatabaseHandler;
import www.firstpinch.com.firstpinch2.SQLite.Log_Error_Object;

/**
 * Created by Rianaa Admin on 12-01-2017.
 */

public class Error_GlobalVariables {

    List<Log_Error_Object> errors = new ArrayList<>();
    Log_DatabaseHandler db;
    public static String device_id = "";
    public static String device_width = "";
    public static String device_height = "";

    public static void setDevice_id(String device_id) {
        Error_GlobalVariables.device_id = device_id;
    }

    public static void setDevice_width(String device_width) {
        Error_GlobalVariables.device_width = device_width;
    }

    public static void setDevice_height(String device_height) {
        Error_GlobalVariables.device_height = device_height;
    }

    public void initializeVar(Context context,String error){
        db = new Log_DatabaseHandler(context);
        db.onOpen(db.getWritableDatabase());
        db.addErrorData(error,"android "+device_id,device_width,device_height);
        Log.e("error data","in GlobalVariables class, data added to sqlite");
        /*errors = db.getAllErrorData();
        for(int i=0;i<errors.size();i++){
            Log_Error_Object current = errors.get(i);
            Log.e("error data",current.getLog_error_id()+", "+current.getDevice_id()+", "+current.getDevice_width()+
                    ", "+current.getDevice_height()+", "+current.getLog_error());
        }*/
    }
}
