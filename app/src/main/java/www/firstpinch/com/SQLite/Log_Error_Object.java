package www.firstpinch.com.firstpinch2.SQLite;

/**
 * Created by Rianaa Admin on 12-01-2017.
 */

public class Log_Error_Object {

    public String log_error_id;
    public String log_error;
    public String device_id;
    public String device_width;
    public String device_height;

    public String getLog_error_id() {
        return log_error_id;
    }

    public void setLog_error_id(String log_error_id) {
        this.log_error_id = log_error_id;
    }

    public String getLog_error() {
        return log_error;
    }

    public String getDevice_id() {
        return device_id;
    }

    public String getDevice_width() {
        return device_width;
    }

    public String getDevice_height() {
        return device_height;
    }

    public void setLog_error(String log_error) {
        this.log_error = log_error;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setDevice_width(String device_width) {
        this.device_width = device_width;
    }

    public void setDevice_height(String device_height) {
        this.device_height = device_height;
    }
}
