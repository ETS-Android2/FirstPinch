package www.firstpinch.com.firstpinch2.ProfilePages;

/**
 * Created by Rianaa Admin on 16-09-2016.
 */

//object for your houses in profile
public class YourHousesProfileObject {

    public String yourHouseImageUrl;
    public String yourHouseName;
    public String yourHouseDesc;
    public int join_exit;
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getYourHouseDesc() {
        return yourHouseDesc;
    }

    public void setYourHouseDesc(String yourHouseDesc) {
        this.yourHouseDesc = yourHouseDesc;
    }

    public String getYourHouseImageUrl() {
        return yourHouseImageUrl;
    }

    public String getYourHouseName() {
        return yourHouseName;
    }

    public int getJoin_exit() {
        return join_exit;
    }

    public void setYourHouseImageUrl(String yourHouseImageUrl) {
        this.yourHouseImageUrl = yourHouseImageUrl;
    }

    public void setYourHouseName(String yourHouseName) {
        this.yourHouseName = yourHouseName;
    }

    public void setJoin_exit(int join_exit) {
        this.join_exit = join_exit;
    }


}
