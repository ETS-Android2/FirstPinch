package www.firstpinch.com.firstpinch2.CustomGallery;

/**
 * Created by Rianaa Admin on 11-11-2016.
 */

//object used everywhere for images for pinching a post to store id and url
public class ImageObject {

    String image_url;
    String image_id;

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_url() {
        return image_url;
    }
}
