package www.firstpinch.com.firstpinch2;

/**
 * Created by Rianaa Admin on 26-08-2016.
 */
public class SelectedInterestsObject {
    public String interestname;
    public Boolean check;
    public String imageurl;
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getImageurl() {
        return imageurl;
    }


    public SelectedInterestsObject() {

    }

    public String getHouseName() {
        return interestname;
    }

    public void setHouseName(String housename) {
        this.interestname = housename;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

}
