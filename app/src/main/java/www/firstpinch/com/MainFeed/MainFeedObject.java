package www.firstpinch.com.firstpinch2.MainFeed;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Rianaa Admin on 08-09-2016.
 */

//object for main feed dataList in RecyclerView
public class MainFeedObject {

    public String preview_title;
    public String preview_description;
    public String preview_image;
    public String preview_link;
    public String houseImageUrl;
    public String houseName;
    public String houseDesc;
    public String profileImageUrl;
    public String profileName;
    public String profileUsername;
    public String title;
    public String desc;
    public String imageUrl;
    public int post_ques, totalComments, totalShares;
    public Float rating;
    public int user_rate;
    public int num_of_images;
    public String hint_comm_profile_id;
    public String hint_comm_profile_username;
    public String hint_comm_profile_name;
    public String hint_comm_profile_image;
    public String hint_comm_title;
    public String hint_comm_profile_id2;
    public String hint_comm_profile_name2;
    public String hint_comm_profile_image2;
    public String hint_comm_title2;
    public int appreciations_count;
    public Double score;
    public int responses;
    public ArrayList<String> arr_images = new ArrayList<String>();
    public int mainfeed_house_id;
    public int mainfeed_user_id;
    public String mainfeed_question_id;
    public String bookmark_id;
    public String bookmark_status;


    public void setHint_comm_profile_username2(String hint_comm_profile_username2) {
        this.hint_comm_profile_username2 = hint_comm_profile_username2;
    }

    public void setHint_comm_profile_name2(String hint_comm_profile_name2) {
        this.hint_comm_profile_name2 = hint_comm_profile_name2;
    }

    public void setHint_comm_profile_image2(String hint_comm_profile_image2) {
        this.hint_comm_profile_image2 = hint_comm_profile_image2;
    }

    public void setHint_comm_title2(String hint_comm_title2) {
        this.hint_comm_title2 = hint_comm_title2;
    }

    public void setHint_comm_profile_id2(String hint_comm_profile_id2) {
        this.hint_comm_profile_id2 = hint_comm_profile_id2;
    }

    public String hint_comm_profile_username2;

    public String getHint_comm_profile_id2() {
        return hint_comm_profile_id2;
    }

    public String getHint_comm_profile_username2() {
        return hint_comm_profile_username2;
    }

    public String getHint_comm_profile_name2() {
        return hint_comm_profile_name2;
    }

    public String getHint_comm_profile_image2() {
        return hint_comm_profile_image2;
    }

    public String getHint_comm_title2() {
        return hint_comm_title2;
    }

    public String getHint_comm_profile_username() {
        return hint_comm_profile_username;
    }

    public void setHint_comm_profile_username(String hint_comm_profile_username) {
        this.hint_comm_profile_username = hint_comm_profile_username;
    }

    public String getHint_comm_profile_id() {
        return hint_comm_profile_id;
    }

    public void setHint_comm_profile_id(String hint_comm_profile_id) {
        this.hint_comm_profile_id = hint_comm_profile_id;
    }

    public String getPreview_title() {
        return preview_title;
    }

    public String getPreview_description() {
        return preview_description;
    }

    public String getPreview_image() {
        return preview_image;
    }

    public String getPreview_link() {
        return preview_link;
    }

    public void setPreview_title(String preview_title) {
        this.preview_title = preview_title;
    }

    public void setPreview_description(String preview_description) {
        this.preview_description = preview_description;
    }

    public void setPreview_image(String preview_image) {
        this.preview_image = preview_image;
    }

    public void setPreview_link(String preview_link) {
        this.preview_link = preview_link;
    }

    public String getBookmark_id() {
        return bookmark_id;
    }

    public String getBookmark_status() {
        return bookmark_status;
    }

    public void setBookmark_id(String bookmark_id) {
        this.bookmark_id = bookmark_id;
    }

    public void setBookmark_status(String bookmark_status) {
        this.bookmark_status = bookmark_status;
    }


    public String getHouseDesc() {
        return houseDesc;
    }

    public void setHouseDesc(String houseDesc) {
        this.houseDesc = houseDesc;
    }


    public String getMainfeed_question_id() {
        return mainfeed_question_id;
    }

    public void setMainfeed_question_id(String mainfeed_question_id) {
        this.mainfeed_question_id = mainfeed_question_id;
    }


    public int getMainfeed_house_id() {
        return mainfeed_house_id;
    }

    public int getMainfeed_user_id() {
        return mainfeed_user_id;
    }

    public void setMainfeed_house_id(int mainfeed_house_id) {
        this.mainfeed_house_id = mainfeed_house_id;
    }

    public void setMainfeed_user_id(int mainfeed_user_id) {
        this.mainfeed_user_id = mainfeed_user_id;
    }


    public Double getScore() {
        return score;
    }

    public int getResponses() {
        return responses;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setResponses(int responses) {
        this.responses = responses;
    }



    public int getAppreciations_count() {
        return appreciations_count;
    }

    public void setAppreciations_count(int appreciations_count) {
        this.appreciations_count = appreciations_count;
    }


    public String getHint_comm_profile_name() {
        return hint_comm_profile_name;
    }

    public String getHint_comm_profile_image() {
        return hint_comm_profile_image;
    }

    public String getHint_comm_title() {
        return hint_comm_title;
    }

    public void setHint_comm_profile_name(String hint_comm_profile_name) {
        this.hint_comm_profile_name = hint_comm_profile_name;
    }

    public void setHint_comm_profile_image(String hint_comm_profile_image) {
        this.hint_comm_profile_image = hint_comm_profile_image;
    }

    public void setHint_comm_title(String hint_comm_title) {
        this.hint_comm_title = hint_comm_title;
    }

    public int getNum_of_images() {
        return num_of_images;
    }

    public void setNum_of_images(int num_of_images) {
        this.num_of_images = num_of_images;
    }


    public int getUser_rate() {
        return user_rate;
    }

    public void setUser_rate(int user_rate) {
        this.user_rate = user_rate;
    }


    public String getHouseImageUrl() {
        return houseImageUrl;
    }

    public String getHouseName() {
        return houseName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getProfileUsername() {
        return profileUsername;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getPost_ques() {
        return post_ques;
    }

    public Float getRating() {
        return rating;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setProfileUsername(String profileUsername) {
        this.profileUsername = profileUsername;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPost_ques(int post_ques) {
        this.post_ques = post_ques;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }

    public void setHouseImageUrl(String houseImageUrl) {
        this.houseImageUrl = houseImageUrl;
    }


}
