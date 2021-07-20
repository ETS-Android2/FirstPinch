package www.firstpinch.com.firstpinch2.HouseProfilePages;

/**
 * Created by Rianaa Admin on 27-09-2016.
 */

//house member object
public class HouseMemberRecyclerObject {

    public String memberId;
    public String memberName;
    public String memberUserName;
    public String memberProfileImageUrl;
    public String memberBio;

    public String getMemberBio() {
        return memberBio;
    }

    public void setMemberBio(String memberBio) {
        this.memberBio = memberBio;
    }


    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberUserName() {
        return memberUserName;
    }

    public String getMemberProfileImageUrl() {
        return memberProfileImageUrl;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setMemberUserName(String memberUserName) {
        this.memberUserName = memberUserName;
    }

    public void setMemberProfileImageUrl(String memberProfileImageUrl) {
        this.memberProfileImageUrl = memberProfileImageUrl;
    }

}
