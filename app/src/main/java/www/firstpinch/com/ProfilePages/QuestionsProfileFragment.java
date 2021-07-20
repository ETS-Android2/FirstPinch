package www.firstpinch.com.firstpinch2.ProfilePages;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedObject;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedRecyclerViewAdapter;
import www.firstpinch.com.firstpinch2.MyLinearLayoutManager;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 15-09-2016.
 */

//questionProfileFragment of ProfileActivity.java
public class QuestionsProfileFragment extends Fragment {

    public static String house = "", GET_POST_PROFILE_FEED_URL = "http://54.169.84.123/api/getUserQuestion", profile_id;
    int count = 0;
    private RecyclerView recyclerView;
    private QuestionProfileFragmentAdapter recyclerAdapter;
    List<MainFeedObject> data = new ArrayList<MainFeedObject>();
    List<MainFeedObject> newdata = new ArrayList<MainFeedObject>();
    int user_id, page_index = 1;
    Boolean loading = false;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout ll_empty_recyclerview;
    TextView tv_empty_recyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.question_profile_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        tv_empty_recyclerview = (TextView) view.findViewById(R.id.tv_empty_recyclerview);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                data.clear();
                recyclerAdapter.notifyDataSetChanged();
                page_index = 1;
                get_feed(1);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        SharedPreferences sp = getActivity().getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        SharedPreferences sp1 = getActivity().getSharedPreferences("profile_id",
                Activity.MODE_PRIVATE);
        profile_id = sp1.getString("profile_id", "1");

        recyclerView = (RecyclerView) view.findViewById(R.id.house_recyclerview);
        ll_empty_recyclerview = (LinearLayout) view.findViewById(R.id.ll_empty_recyclerview);
        recyclerAdapter = new QuestionProfileFragmentAdapter(getActivity(), data);
        recyclerView.setAdapter(recyclerAdapter);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(null);
        registerForContextMenu(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (llm.findLastCompletelyVisibleItemPosition() == recyclerAdapter.getItemCount() - 3 && !loading) {
                    //bottom of list!
                    Log.e("position", "" + llm.findLastCompletelyVisibleItemPosition());
                    page_index++;
                    get_feed(page_index);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        get_feed(1);
        return view;
    }

    //delete item when deleted from profile activity
    public void get_feed_deleted(int id) {
        try {
            for(int i =0;i<data.size();i++){
                MainFeedObject current = data.get(i);
                if(current.getMainfeed_question_id().contentEquals(id+"")){
                    data.remove(i);
                    recyclerAdapter.notifyItemRemoved(i);
                    return;
                }
            }
        } catch(Exception e){
            Log.e("MAinfeedDeleted","PostProfileRecyclerView");
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(getActivity(), e.toString());
        }
    }

    //get question data for profile API request
    private void get_feed(final int page_index) {

        loading = true;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_POST_PROFILE_FEED_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONArray feed = jsonObj.getJSONArray("feed");
                            for (int i = 0; i < feed.length(); i++) {
                                MainFeedObject current = new MainFeedObject();
                                JSONObject feedBlock = feed.getJSONObject(i);
                                JSONObject block = feedBlock.getJSONObject("feedblock");
                                String type = block.getString("type");
                                JSONObject obj_user = block.getJSONObject("user");
                                current.setMainfeed_user_id(obj_user.getInt("user_id"));
                                current.setProfileName(obj_user.getString("user_name"));
                                current.setProfileImageUrl(obj_user.getString("user_image"));
                                JSONObject obj_club = block.getJSONObject("club");
                                current.setMainfeed_house_id(obj_club.getInt("club_id"));
                                current.setHouseName(obj_club.getString("club_name"));
                                current.setHouseImageUrl(obj_club.getString("club_image"));
                                if (type.contentEquals("story")) {
                                    JSONObject obj_story = block.getJSONObject("story");
                                    JSONArray arr_contents = obj_story.getJSONArray("contents");
                                    for (int j = 0; j < arr_contents.length(); j++) {
                                        JSONObject obj_images = arr_contents.getJSONObject(j);
                                        current.arr_images.add(obj_images.getString("content"));
                                    }
                                    JSONArray arr_comments = obj_story.getJSONArray("comment");
                                    for (int j = 0; j < arr_comments.length(); j++) {
                                        JSONObject obj_comments = arr_comments.getJSONObject(j);
                                        current.setHint_comm_profile_id(obj_comments.getString("user_id"));
                                        current.setHint_comm_profile_username(obj_comments.getString("user_uname"));
                                        current.setHint_comm_profile_name(obj_comments.getString("user_name"));
                                        current.setHint_comm_profile_image(obj_comments.getString("user_image"));
                                        current.setHint_comm_title(obj_comments.getString("comment"));
                                    }
                                    current.setPreview_title(obj_story.getString("preview_title"));
                                    current.setPreview_description(obj_story.getString("preview_description"));
                                    current.setPreview_image(obj_story.getString("preview_image"));
                                    current.setPreview_link(obj_story.getString("preview_link"));
                                    current.setNum_of_images(current.arr_images.size());
                                    current.setTitle(obj_story.getString("title"));
                                    current.setDesc(obj_story.getString("description"));
                                    current.setAppreciations_count(obj_story.getInt("appreciation_count"));
                                    current.setScore(obj_story.getDouble("score"));
                                    current.setResponses(obj_story.getInt("comment_count"));
                                    current.setPost_ques(0);

                                } else {
                                    JSONObject obj_story = block.getJSONObject("question");
                                    current.setMainfeed_question_id(obj_story.getString("id"));
                                    current.setTitle(obj_story.getString("title"));
                                    current.setResponses(obj_story.getInt("answer_count"));
                                    current.setDesc(obj_story.getString("content"));
                                    current.setAppreciations_count(obj_story.getInt("appreciation_count"));
                                    current.setUser_rate(obj_story.getInt("apprection_star"));
                                    current.setScore(obj_story.getDouble("score"));
                                    current.setBookmark_id(obj_story.getString("bookmark_id"));
                                    current.setBookmark_status(obj_story.getString("bookmark_status"));
                                    current.setPreview_title(obj_story.getString("preview_title"));
                                    current.setPreview_description(obj_story.getString("preview_description"));
                                    current.setPreview_image(obj_story.getString("preview_image"));
                                    current.setPreview_link(obj_story.getString("preview_link"));
                                    //current.setScore(obj_story.getDouble("score"));
                                    //current.setResponses(obj_story.getInt("comment_count"));
                                    JSONArray arr_comments = obj_story.getJSONArray("answer");
                                    for (int j = 0; j < arr_comments.length(); j++) {
                                        JSONObject obj_comments = arr_comments.getJSONObject(j);
                                        if(j==0) {
                                            current.setHint_comm_profile_id(obj_comments.getString("user_id"));
                                            current.setHint_comm_profile_username(obj_comments.getString("user_uname"));
                                            current.setHint_comm_profile_name(obj_comments.getString("user_name"));
                                            current.setHint_comm_profile_image(obj_comments.getString("user_image"));
                                            current.setHint_comm_title(obj_comments.getString("answer"));
                                        } else {
                                            current.setHint_comm_profile_id2(obj_comments.getString("user_id"));
                                            current.setHint_comm_profile_username2(obj_comments.getString("user_uname"));
                                            current.setHint_comm_profile_name2(obj_comments.getString("user_name"));
                                            current.setHint_comm_profile_image2(obj_comments.getString("user_image"));
                                            current.setHint_comm_title2(obj_comments.getString("answer"));
                                        }
                                    }
                                    current.setPost_ques(1);
                                    if (obj_story.getString("image").contentEquals("/default_interset.jpg")) {
                                        current.setNum_of_images(0);
                                    } else {
                                        current.setNum_of_images(1);
                                        current.arr_images.add(obj_story.getString("image"));
                                    }
                                }
                                //JSONObject obj_comment = block.getJSONObject("comment");

                                newdata.add(current);

                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(getActivity(), e.toString());
                        }
                        if (page_index == 1) {
                            data.addAll(0,newdata);
                            recyclerAdapter.notifyItemRangeInserted(0, newdata.size());
                            newdata.clear();
                        } else {
                            data.addAll(data.size(),newdata);
                            recyclerAdapter.notifyItemRangeInserted(data.size(), newdata.size());
                            newdata.clear();
                        }
                        if(data.isEmpty()){
                            swipeRefreshLayout.setVisibility(View.GONE);
                            ll_empty_recyclerview.setVisibility(View.VISIBLE);
                            if (profile_id.contentEquals(String.valueOf(user_id))) {
                                tv_empty_recyclerview.setText("Hey! We are waiting to read your first Question");
                            } else {
                                tv_empty_recyclerview.setText("This Firstpincher hasn't pinched any Questions yet !!");
                            }
                        } else {
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            ll_empty_recyclerview.setVisibility(View.GONE);
                        }
                        loading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                        /*if (page_index == 1)
                            progressDialog.dismiss();*/
                        loading = false;
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Page-Index", String.valueOf(page_index));
                headers.put("User-Id", profile_id);
                headers.put("Profile-Id", String.valueOf(user_id));
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        /*if (page_index == 1) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading Feed...");
            progressDialog.show();
        }*/

    }

    //for static app(not currently used)
    public static List<MainFeedObject> getData() {
        List<MainFeedObject> data = new ArrayList<MainFeedObject>();
        String houseImageUrlList[] = {"http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg"};

        String houseNameList[] = {"Decor Ideas", "My Travel Diaries", "Fitness & Health","My Favourite Literary Character",
                "Healthy Diet","Gardening Tips","Cooking Queries","Quotes from Books"};
        String profileImageUrlList[] = {"https://s9.postimg.org/7hbv6xicf/dp_1.png",
                "https://s15.postimg.org/fdf7jpld7/dp_2.png",
                "https://s21.postimg.org/9qx9kmyvb/dp_3.jpg",
                "https://s15.postimg.org/ujn1s8zor/dp_4.jpg",
                "https://s14.postimg.org/k384wb3ep/dp_5.jpg",
                "https://s16.postimg.org/tlc5d7dfp/dp_6.jpg",
                "https://s12.postimg.org/yvcvyvkql/dp_7.jpg",
                "https://s12.postimg.org/4f6z0qz7h/dp_8.jpg"};
        /*http://s13.postimg.org/prj11gcpj/dp_9.jpg*/
        String profileNameList[] = {"Akash Mondal", "Vishal Puri", "Dinesh Sir","Rob","Vishal","Abhishek Sharma","Ritu Sharma","Ritu Sharma"};
        String profileUsernameList[] = {"@dexter", "@vip", "@dinesh"," @ghonu","@vip","@abhionly","@Simpatico","@Simpatico"};
        String titleList[] = {"Cool kitchen gadgets to make like simpler!", "Morning Hike - Saguaro National Park",
                "I love the world at large","","Some myths busted!","Best plants for a rock garden",
                "Q  I'm forever burning my rice....Any tips on avoiding this daily disaster?",""};
        String descList[] = {"Juicer/Mixer Grinders \n" +
                "● opt for a juicer-mixer-grinder instead of a mixer-grinder \n" +
                "● Most models nowadays come with 3 speeds. Ensure that you buy one with a pulse mode too , for chopping delicate foods,whipping milkshake \n" +
                "● An overload protection function is a must for times when we tip in more food than recommended \n" +
                "● Pick a model with at least 3 jars,these ensure that the machine is versatile \n" +
                "● A locking system that ensures that the machine doesn't start till the jar is correctly locked is an essential safety feature -especially with little kids at home",
                "Saguaro cacti backlit by eastern sunrise - Tucson Mountain District. Monsoon rains make for a lush desert!",
                "I love the golden sand \n" +
                        "To go through my hand \n" +
                        "I love the flying birds in the sky \n" +
                        "Gliding or soaring very high \n" +
                        "I love, I love the world at large!! \n" +
                        "I love the flowers pretty and gay \n" +
                        "They get up early every day \n" +
                        "I love to dance, skip & hop \n" +
                        "Or climb right up a hilltop \n" +
                        "I love, I love the world at large!! \n" +
                        "I love to dance in a meadow green \n" +
                        "Strolling like a happy king \n" +
                        "Oh, I wouldn't lie \n" +
                        "I would do anything but die \n" +
                        "Coz \n" +
                        "I love, I love the world at large! \uD83D\uDC9F\uD83D\uDC9F\uD83D\uDC9F","Jane Eyre; for her " +
                "strengthof character and morals. She loved with all her heart,but didn't become a slave of her desires.She fought " +
                "against the wrongs done by her evil aunt and cousins bravely. She is an inspiration for all the young ladies of today.",
                "Myth : Sipping hot water will help you lose weight \n" +
                        "Fact : There's no evidence of this. However, sipping water throughout the day may eventually help you in losing weight ,as you will feel full and will tend to eat less. \n" +
                        "Myth : All carbohydrates make you fat \n" +
                        "Fact : Avoid refined -carbohydrate -rich foods and sugars like pasta, white bread and doughnuts, but if you banish the \" good - carb \" foods like whole grains, fruits , vegetables and beans- you'll miss out on the main source of fuel as well as vital nutrients and fibre \n" +
                        "Myth : You crave certain foods because your body is deficient in one of the nutrients that the food may provide \n" +
                        "Fact : Untrue. Food cravings are more emotional rather than a result of nutritional deficiency",
                "Alpine plants , found in hilly areas are most suitable for your rock garden, as they are used to UV light, rapid drainage, extreme temperatures and a strong sun. \n" +
                        "Pick jade,kalanchoe,portulaca & chlorophytum.","","We all grew up with the adorable bunch from the Hundred Acre Woods. Who can stay away from the charms of Pooh, Piglet, Tigger and the rest of the cutesy gang? \n" +
                "However, whatever quotes they mouthed , make so much more sense now. After growing up I realised just how simple but touching and true they are! \n" +
                "Here's a few of them which make me smile no matter how glum I may be. . \n" +
                "Love is taking a few steps backward maybe even more.... To give way to the happiness of the person you love \n" +
                "You are braver than you believe, stronger than you seem & smarter, than you think.. \n" +
                "Some people care too much. I think it's called Love.."};

        String imageUrlList[] = {"https://s17.postimg.org/kzvpqs3gv/sample_1.jpg",
                "https://s9.postimg.org/kvqk58lxb/sample_2.jpg",
                "https://s12.postimg.org/c8uzawhct/sample_3.jpg",
                "none","https://s15.postimg.org/3omz97m7v/sample_5.jpg",
                "https://s10.postimg.org/cdv5ksrbd/sample_4.jpg",
                "none","https://s15.postimg.org/ujn1s8zor/dp_4.jpg"};
        /*http://s11.postimg.org/50s2zrtxv/sample_6.png
        http://s13.postimg.org/jf7ewzi5z/sample_7.jpg
        http://s12.postimg.org/7tpfe9pwt/sample_8.jpg
        http://s18.postimg.org/4cdtbjf1l/sample_9.jpg
        http://s15.postimg.org/43lnqtq5n/sample_10.jpg
        http://s13.postimg.org/6myypau13/sample_11.jpg*/
        int post_quesList[] = {0, 1, 0,0,0,0,1,0};
        Float ratingList[] = {4.45f, 3.2f, 1.134f,0f,1f,2.3f,5f,1f};
        int totalCommentsList[] = {101, 266, 10,0,0,0,3,5};
        int totalSharesList[] = {29, 32, 298,1,0,2,2,1};
        int user_rating[] = {3, 4, 5,1,1,2,4,4};
        int number_of_images[] = {1,2,3,0,4,1,0,1};
        String hint_comm_profile_name[] = {"Akash Mondal", "Vishal Puri", "Dinesh Sir","Rob","Vishal","Abhishek Sharma","Ritu Sharma","Ritu Sharma"};
        String hint_comm_profile_image[] = {"http://s9.postimg.org/7hbv6xicf/dp_1.png",
                "http://s15.postimg.org/fdf7jpld7/dp_2.png",
                "http://s21.postimg.org/9qx9kmyvb/dp_3.jpg",
                "http://s15.postimg.org/ujn1s8zor/dp_4.jpg",
                "http://s14.postimg.org/k384wb3ep/dp_5.jpg",
                "http://s16.postimg.org/tlc5d7dfp/dp_6.jpg",
                "http://s12.postimg.org/yvcvyvkql/dp_7.jpg",
                "http://s12.postimg.org/4f6z0qz7h/dp_8.jpg"};
        String hint_comm_title[] = {"Juicer/Mixer Grinders \n" +
                "● opt for a juicer-mixer-grinder instead of a mixer-grinder \n" +
                "● Most models nowadays come with 3 speeds. Ensure that you buy one with a pulse mode too , for chopping delicate foods,whipping milkshake \n" +
                "● An overload protection function is a must for times when we tip in more food than recommended \n" +
                "● Pick a model with at least 3 jars,these ensure that the machine is versatile \n" +
                "● A locking system that ensures that the machine doesn't start till the jar is correctly locked is an essential safety feature -especially with little kids at home",
                "Saguaro cacti backlit by eastern sunrise - Tucson Mountain District. Monsoon rains make for a lush desert!",
                "I love the golden sand \n" +
                        "To go through my hand \n" +
                        "I love the flying birds in the sky \n" +
                        "Gliding or soaring very high \n" +
                        "I love, I love the world at large!! \n" +
                        "I love the flowers pretty and gay \n" +
                        "They get up early every day \n" +
                        "I love to dance, skip & hop \n" +
                        "Or climb right up a hilltop \n" +
                        "I love, I love the world at large!! \n" +
                        "I love to dance in a meadow green \n" +
                        "Strolling like a happy king \n" +
                        "Oh, I wouldn't lie \n" +
                        "I would do anything but die \n" +
                        "Coz \n" +
                        "I love, I love the world at large! \uD83D\uDC9F\uD83D\uDC9F\uD83D\uDC9F","Jane Eyre; for her " +
                "strengthof character and morals. She loved with all her heart,but didn't become a slave of her desires.She fought " +
                "against the wrongs done by her evil aunt and cousins bravely. She is an inspiration for all the young ladies of today.",
                "Myth : Sipping hot water will help you lose weight \n" +
                        "Fact : There's no evidence of this. However, sipping water throughout the day may eventually help you in losing weight ,as you will feel full and will tend to eat less. \n" +
                        "Myth : All carbohydrates make you fat \n" +
                        "Fact : Avoid refined -carbohydrate -rich foods and sugars like pasta, white bread and doughnuts, but if you banish the \" good - carb \" foods like whole grains, fruits , vegetables and beans- you'll miss out on the main source of fuel as well as vital nutrients and fibre \n" +
                        "Myth : You crave certain foods because your body is deficient in one of the nutrients that the food may provide \n" +
                        "Fact : Untrue. Food cravings are more emotional rather than a result of nutritional deficiency",
                "Alpine plants , found in hilly areas are most suitable for your rock garden, as they are used to UV light, rapid drainage, extreme temperatures and a strong sun. \n" +
                        "Pick jade,kalanchoe,portulaca & chlorophytum.","","We all grew up with the adorable bunch from the Hundred Acre Woods. Who can stay away from the charms of Pooh, Piglet, Tigger and the rest of the cutesy gang? \n" +
                "However, whatever quotes they mouthed , make so much more sense now. After growing up I realised just how simple but touching and true they are! \n" +
                "Here's a few of them which make me smile no matter how glum I may be. . \n" +
                "Love is taking a few steps backward maybe even more.... To give way to the happiness of the person you love \n" +
                "You are braver than you believe, stronger than you seem & smarter, than you think.. \n" +
                "Some people care too much. I think it's called Love.."};

        for (int i = 0; i < houseNameList.length && i < profileNameList.length; i++) {
            MainFeedObject current = new MainFeedObject();
            current.setHouseImageUrl(houseImageUrlList[i]);
            current.setHouseName(houseNameList[i]);
            current.setProfileImageUrl(profileImageUrlList[i]);
            current.setProfileName(profileNameList[i]);
            current.setProfileUsername(profileUsernameList[i]);
            current.setTitle(titleList[i]);
            current.setDesc(descList[i]);
            current.setImageUrl(imageUrlList[i]);
            current.setPost_ques(post_quesList[i]);
            current.setRating(ratingList[i]);
            current.setTotalComments(totalCommentsList[i]);
            current.setTotalShares(totalSharesList[i]);
            current.setUser_rate(user_rating[i]);
            current.setNum_of_images(number_of_images[i]);
            current.setHint_comm_profile_name(hint_comm_profile_name[i]);
            current.setHint_comm_profile_image(hint_comm_profile_image[i]);
            current.setHint_comm_title(hint_comm_title[i]);
            data.add(current);
        }
        return data;
    }

}