package www.firstpinch.com.firstpinch2.MainFeed;

/**
 * Created by Rianaa Admin on 14-09-2016.
 */
//object for responses/answers
public class MainFeedDetailedCommentsObject {

    public String id;
    public String comment_profileImageUrl;
    public String comment_profileName;
    public String comment_profile_user_id;
    public String comment_profileUsername;
    public String comment_title;
    public String comment_imageUrl;
    public int rating;
    public int rating_status;
    public int answer_response;

    public String getComment_profile_user_id() {
        return comment_profile_user_id;
    }

    public void setComment_profile_user_id(String comment_profile_user_id) {
        this.comment_profile_user_id = comment_profile_user_id;
    }


    public int getComment_rating_status() {
        return rating_status;
    }

    public void setComment_rating_status(int rating_status) {
        this.rating_status = rating_status;
    }


    public int getAnswer_response() {
        return answer_response;
    }

    public void setAnswer_response(int answer_response) {
        this.answer_response = answer_response;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment_profileImageUrl() {
        return comment_profileImageUrl;
    }

    public String getComment_profileName() {
        return comment_profileName;
    }

    public String getComment_profileUsername() {
        return comment_profileUsername;
    }

    public String getComment_title() {
        return comment_title;
    }

    public String getComment_imageUrl() {
        return comment_imageUrl;
    }

    public int getComment_rating() {
        return rating;
    }

    public void setComment_profileImageUrl(String comment_profileImageUrl) {
        this.comment_profileImageUrl = comment_profileImageUrl;
    }

    public void setComment_profileName(String comment_profileName) {
        this.comment_profileName = comment_profileName;
    }

    public void setComment_profileUsername(String comment_profileUsername) {
        this.comment_profileUsername = comment_profileUsername;
    }

    public void setComment_title(String comment_title) {
        this.comment_title = comment_title;
    }

    public void setComment_imageUrl(String comment_imageUrl) {
        this.comment_imageUrl = comment_imageUrl;
    }

    public void setComment_rating(int comment_rating) {
        this.rating = comment_rating;
    }


}
