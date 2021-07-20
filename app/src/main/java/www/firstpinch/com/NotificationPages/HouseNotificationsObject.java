package www.firstpinch.com.firstpinch2.NotificationPages;

/**
 * Created by Rianaa Admin on 13-09-2016.
 */

//object for house notifications
public class HouseNotificationsObject {

    public String notifhIndicatiorImageUrl;
    public String notifhUserName;
    public String notifhText;
    public String notifhpost_question_response;
    public String notifhTitle;
    public String notifhProfileImageUrl;
    public int notifhPost_Question;
    public String notifhId;
    public String notifType;


    public String getNotifType() {
        return notifType;
    }

    public void setNotifType(String notifType) {
        this.notifType = notifType;
    }

    public int getNotifhPost_Question() {
        return notifhPost_Question;
    }

    public String getNotifhId() {
        return notifhId;
    }

    public void setNotifhPost_Question(int notifhPost_Question) {
        this.notifhPost_Question = notifhPost_Question;
    }

    public void setNotifhId(String notifhId) {
        this.notifhId = notifhId;
    }

    public String getNotifhUserName() {
        return notifhUserName;
    }

    public String getNotifhpost_question_response() {
        return notifhpost_question_response;
    }

    public String getNotifhTitle() {
        return notifhTitle;
    }

    public void setNotifhUserName(String notifhUserName) {
        this.notifhUserName = notifhUserName;
    }

    public void setNotifhpost_question_response(String notifhpost_question_response) {
        this.notifhpost_question_response = notifhpost_question_response;
    }

    public void setNotifhTitle(String notifhTitle) {
        this.notifhTitle = notifhTitle;
    }

    public String getNotifhIndicatiorImageUrl() {
        return notifhIndicatiorImageUrl;
    }

    public String getNotifhText() {
        return notifhText;
    }

    public String getNotifhProfileImageUrl() {
        return notifhProfileImageUrl;
    }

    public void setNotifhIndicatiorImageUrl(String notifhIndicatiorImageUrl) {
        this.notifhIndicatiorImageUrl = notifhIndicatiorImageUrl;
    }

    public void setNotifhText(String notifhText) {
        this.notifhText = notifhText;
    }

    public void setNotifhProfileImageUrl(String notifhProfileImageUrl) {
        this.notifhProfileImageUrl = notifhProfileImageUrl;
    }

}