package www.firstpinch.com.firstpinch2.Search;

/**
 * Created by Rianaa Admin on 28-09-2016.
 */

public class SearchActivityObject {

    public String search_image;
    public String search_name;
    public String search_desc;
    public String id;
    public String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSearch_desc(String search_desc) {
        this.search_desc = search_desc;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearch_desc() {
        return search_desc;
    }

    public String getId() {
        return id;
    }

    public String getSearch_name() {
        return search_name;
    }

    public String getSearch_image() {
        return search_image;
    }


    public void setSearch_image(String search_image) {
        this.search_image = search_image;
    }

    public void setSearch_name(String search_name) {
        this.search_name = search_name;
    }


}
