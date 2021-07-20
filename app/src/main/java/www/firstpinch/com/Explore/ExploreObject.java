package www.firstpinch.com.firstpinch2.Explore;

/**
 * Created by Rianaa Admin on 16-09-2016.
 */
//explore object to view interests
public class ExploreObject {

    public String exp_interest_name;
    public String exp_interest_image;
    public String interest_id;

    public String getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(String interest_id) {
        this.interest_id = interest_id;
    }

    public String getExp_interest_name() {
        return exp_interest_name;
    }

    public String getExp_interest_image() {
        return exp_interest_image;
    }

    public void setExp_interest_name(String exp_interest_name) {
        this.exp_interest_name = exp_interest_name;
    }

    public void setExp_interest_image(String exp_interest_image) {
        this.exp_interest_image = exp_interest_image;
    }

}
