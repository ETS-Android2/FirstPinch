package www.firstpinch.com.firstpinch2.Search;

/**
 * Created by Rianaa Admin on 03-10-2016.
 */
public class SearchQuestionsFragmentObject {

    public String questionTitle;
    public String profileImageUrl;
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getQuestionTitle() {

        return questionTitle;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
