package www.firstpinch.com.firstpinch2.MainFeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import www.firstpinch.com.firstpinch2.EditProfile;
import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.HouseProfilePages.HouseProfileActivity;
import www.firstpinch.com.firstpinch2.ImageViewActivity;
import www.firstpinch.com.firstpinch2.ProfilePages.ProfileActivity;
import www.firstpinch.com.firstpinch2.R;
import www.firstpinch.com.firstpinch2.WebView_Common;

/**
 * Created by Rianaa Admin on 13-09-2016.
 */

//detailed view activity for any post/question
public class MainFeedDetailedActivity extends AppCompatActivity {

    String GET_STORY_DETAILS = "http://54.169.84.123/api/getStoryDetails",
            GET_QUESTION_DETAILS = "http://54.169.84.123/api/getQuestionDetails",
            GET_QUESTIONS_TAG_USERS_URL = "http://54.169.84.123/api/getTagUserQuestion",
            GET_STORY_TAG_USERS_URL = "http://54.169.84.123/api/getTagUserStory",
            GET_ANSWERS_URL = "http://54.169.84.123/api/getQuestionDetails",
            GET_RESPONSES_URL = "http://54.169.84.123/api/getStoryDetails",
            POST_ANSWER_URL = "http://54.169.84.123/api/answers",
            POST_RESPONSE_URL = "http://54.169.84.123/api/story_comments",
            PATCH_RATING_QUESTION_URL = "http://54.169.84.123/api/story_ratings/questionrate",
            PATCH_RATING_POST_URL = "http://54.169.84.123/api/story_ratings/rate",
            PUT_QUESTION_BOOKMARK_URL = "http://54.169.84.123/api/club_questions/1/favourite",
            DELETE_QUESTION_BOOKMARK = "http://54.169.84.123/api/club_questions/1/unfavourite",
            PUT_POST_BOOKMARK_URL = "http://54.169.84.123/api/club_stories/1/favourite",
            DELETE_POST_BOOKMARK = "http://54.169.84.123/api/club_stories/1/unfavourite",
            QUESTION_DELETE_URL = "http://54.169.84.123/api/club_questions/1",
            POST_DELETE_URL = "http://54.169.84.123/api/club_stories/1";
    List<String> mStrings = new ArrayList<String>();
    public static String house = "";
    int user_id, page_index = 1;
    Boolean loading = false;
    ImageView im_d_houseImageUrl, im_d_profileImageUrl, im_upload_in_comment, im_d_imageUrl, add_comment_image, d_share,
            im_comm_img_close, im_add_comm_image, im_menu_button, im_preview_image;
    CardView preview_cardview;
    LinearLayout fm_comm_image, ll_deleted_post;
    RelativeLayout rl_edittext;
    ScrollView sv_detailed;
    TextView tv_d_houseName, tv_d_profileName, tv_d_profileUsername, tv_d_title, tv_d_desc, tv_d_post_ques,
            tv_d_appreciation, tv_d_score, text_d_score, tv_d_responses, text_responses_answers, responseTextStyle,
            tv_preview_title, tv_preview_description;
    Button d_r1, d_r2, d_r3, /*d_r4, d_r5,*/
            d_comment, bt_post_comment;
    String houseImageUrl, profileImageUrl, houseName, profileName, profileUsername, title, desc, sec_image,
            feed_add_comment = "", question_id = "", current_comment = "", appreciation_count, responses_count,
            bookmark_status = "0", bookmark_id, house_id, profile_id = "", story_check = "", starting_activity = "",
            preview_title = "", preview_desc = "", preview_link = "", preview_image = "";
    Double score_count;
    Integer post_ques;
    CustomAutoCompleteTextView ac_tv_add_comm;
    int num_of_images;
    ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private MainFeedDetailedCommentsAdapter recyclerAdapter;
    List<MainFeedDetailedCommentsObject> data = new ArrayList<MainFeedDetailedCommentsObject>();
    ArrayList<Tag_userObject> arr_list_tag_users = new ArrayList<Tag_userObject>();
    View view;
    String userChoosenTask;
    int SELECT_FILE = 10, REQUEST_CAMERA = 20, RESULT_CROP = 30, SELECT_FILE_COVER = 11, REQUEST_CAMERA_COVER = 12, CROP_CAMERA_COVER = 13,
            CROP_GALLERY_COVER = 14, CROP_CAMERA_PROFILE = 15, CROP_GALLERY_PROFILE = 16;
    ArrayList<String> arr_images = new ArrayList<String>();
    String[] stockArr;
    String base64Image = "";
    int user_rate;
    LinearLayout layout;
    LinearLayout ll;
    HorizontalScrollView horizon_scroll;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_feed_detailed_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.overridePendingTransition(R.anim.right_to_left,
                R.anim.do_nothing);
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        view = (EditText) findViewById(R.id.et_comment);
        recyclerView = (RecyclerView) findViewById(R.id.d_comments_recyclerview);

        //get data from previous intent on onClick
        houseName = getIntent().getStringExtra("housename");
        profileName = getIntent().getStringExtra("profilename");
        profileUsername = getIntent().getStringExtra("profileusername");
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        appreciation_count = getIntent().getStringExtra("appreciation_count");
        score_count = getIntent().getDoubleExtra("score_count", 0.0);
        responses_count = getIntent().getStringExtra("responses_count");
        houseImageUrl = getIntent().getStringExtra("houseimageurl");
        profileImageUrl = getIntent().getStringExtra("profileimageurl");
        post_ques = getIntent().getIntExtra("post_ques", 0);
        num_of_images = getIntent().getIntExtra("num_of_images", 0);
        feed_add_comment = getIntent().getStringExtra("comment");
        question_id = getIntent().getStringExtra("question_id");
        user_rate = getIntent().getIntExtra("rate", 0);
        arr_images = getIntent().getStringArrayListExtra("arr_images");
        bookmark_status = getIntent().getStringExtra("bookmark_status");
        starting_activity = getIntent().getStringExtra("activity_name");
        bookmark_id = getIntent().getStringExtra("bookmark_id");
        //sec_image = getIntent().getStringExtra("second_image");

        tv_d_houseName = (TextView) findViewById(R.id.d_feed_house_name);
        tv_d_profileName = (TextView) findViewById(R.id.d_feed_profile_name);
        tv_d_profileUsername = (TextView) findViewById(R.id.d_feed_profile_username);
        tv_d_title = (TextView) findViewById(R.id.d_feed_title);
        tv_d_desc = (TextView) findViewById(R.id.d_feed_desc);
        tv_d_post_ques = (TextView) findViewById(R.id.d_feed_post_or_ques);
        tv_d_appreciation = (TextView) findViewById(R.id.tv_appreciations);
        tv_d_score = (TextView) findViewById(R.id.tv_score);
        text_d_score = (TextView) findViewById(R.id.text_score);
        tv_d_responses = (TextView) findViewById(R.id.tv_responses);
        im_d_houseImageUrl = (ImageView) findViewById(R.id.d_feed_house_pic);
        im_d_profileImageUrl = (ImageView) findViewById(R.id.d_feed_profile_pic);
        im_d_imageUrl = (ImageView) findViewById(R.id.d_feed_image);
        add_comment_image = (ImageView) findViewById(R.id.add_comm_image);
        d_share = (ImageView) findViewById(R.id.d_feed_share);
        tv_preview_title = (TextView) findViewById(R.id.preview_title);
        tv_preview_description = (TextView) findViewById(R.id.preview_desc);
        preview_cardview = (CardView) findViewById(R.id.preview_cardview);
        im_preview_image = (ImageView) findViewById(R.id.preview_image);
        //text_responses_answers = (TextView) findViewById(R.id.d_text_comments);
        //im_bookmark = (ImageView) findViewById(R.id.d_feed_bookmark);
        im_menu_button = (ImageView) findViewById(R.id.menu_button);
        fm_comm_image = (LinearLayout) findViewById(R.id.fm_comm_image);
        im_comm_img_close = (ImageView) findViewById(R.id.de_selected_comm_image);
        im_add_comm_image = (ImageView) findViewById(R.id.add_comm_image);
        d_r1 = (Button) findViewById(R.id.d_rate1);
        d_r2 = (Button) findViewById(R.id.d_rate2);
        d_r3 = (Button) findViewById(R.id.d_rate3);
        /*d_r4 = (Button) findViewById(R.id.d_rate4);
        d_r5 = (Button) findViewById(R.id.d_rate5);*/

        ac_tv_add_comm = (CustomAutoCompleteTextView) findViewById(R.id.et_comment);
        im_upload_in_comment = (ImageView) findViewById(R.id.im_comment_image_select);
        bt_post_comment = (Button) findViewById(R.id.bt_post);
        ll_deleted_post = (LinearLayout) findViewById(R.id.ll_detaied_activity_error);
        sv_detailed = (ScrollView) findViewById(R.id.comm_scrollview);
        rl_edittext = (RelativeLayout) findViewById(R.id.rl_detaied_activity);

        //code_added
        final Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/KeepCalm-Medium.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");

        tv_d_houseName.setTypeface(custom_font1);
        tv_d_profileName.setTypeface(custom_font1);
        tv_d_profileUsername.setTypeface(custom_font1);
        tv_d_title.setTypeface(custom_font);
        tv_d_desc.setTypeface(custom_font1);
        tv_d_post_ques.setTypeface(custom_font1);
        text_d_score.setTypeface(custom_font1);

        TextView appreciationTextStyle = (TextView) findViewById(R.id.text_appreciations);
        appreciationTextStyle.setTypeface(custom_font1);
        responseTextStyle = (TextView) findViewById(R.id.text_responses);
        responseTextStyle.setTypeface(custom_font1);

        /*if (bookmark_status != null) {
            if (bookmark_status.contentEquals("0")) {
                im_bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_48dp);
            } else {
                im_bookmark.setBackgroundResource(R.drawable.ic_bookmark_black_48dp);
            }
        }*/

        im_comm_img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                base64Image = "";
                fm_comm_image.setVisibility(View.GONE);
                add_comment_image.setVisibility(View.GONE);
            }
        });
        im_add_comm_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        d_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                //sharingIntent.setType("image/*");
                String shareBody = "";
                String title = "Download FirstPinch App to know more - " + "";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
                //sharingIntent.putExtra(Intent.EXTRA_STREAM, current.getTitle());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sent from First Pinch App\n" + shareBody + "\nDownload FirstPinch App to know more - LINK");
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        /*im_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post_ques == 0) {
                    if (bookmark_status.contentEquals("0")) {
                        put_post_bookmark();
                    } else {
                        delete_post_bookmark();
                    }
                } else {
                    if (bookmark_status.contentEquals("0")) {
                        put_question_bookmark();
                    } else {
                        delete_question_bookmark();
                    }
                }
            }
        });*/

        //top right arrow button onCLick to bookmark/delete post/question
        im_menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //itemView.showContextMenu();
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                if (bookmark_status.contentEquals("0") && profile_id.contentEquals("" + user_id)) {
                    popup.inflate(R.menu.menu_button_delete_bookmark);
                } else if (bookmark_status.contentEquals("1") && profile_id.contentEquals("" + user_id)) {
                    popup.inflate(R.menu.menu_button_delete_bookmarked);
                } else if (bookmark_status.contentEquals("0") && !profile_id.contentEquals("" + user_id)) {
                    popup.inflate(R.menu.menu_button_bookmark);
                } else if (bookmark_status.contentEquals("1") && !profile_id.contentEquals("" + user_id)) {
                    popup.inflate(R.menu.menu_button_bookmarked);
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        //noinspection SimplifiableIfStatement
                        if (id == R.id.bookmark) {
                            try {
                                if (post_ques == 0) {
                                    if (bookmark_status.contentEquals("0")) {
                                        put_post_bookmark();
                                        im_menu_button.setClickable(false);
                                        Toast.makeText(getApplicationContext(), "Bookmarked", Toast.LENGTH_SHORT).show();
                                    } else {
                                        delete_post_bookmark();
                                        im_menu_button.setClickable(false);
                                        Toast.makeText(getApplicationContext(), "Removed from bookmarks", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    if (bookmark_status.contentEquals("0")) {
                                        put_question_bookmark();
                                        im_menu_button.setClickable(false);
                                        Toast.makeText(getApplicationContext(), "Bookmarked", Toast.LENGTH_SHORT).show();
                                    } else {
                                        delete_question_bookmark();
                                        im_menu_button.setClickable(false);
                                        Toast.makeText(getApplicationContext(), "Removed from bookmarks", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                                Log.e("MainFeedDetailedActvty", "menu button bookmark" + e.toString());
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                            }
                            //notifyItemChanged(getAdapterPosition(), feed_bookmark);
                            return true;
                        } else if (id == R.id.delete) {
                            try {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainFeedDetailedActivity.this);
                                builder.setIcon(R.drawable.ic_dialog_alert);
                                builder.setTitle("Delete?");
                                builder.setMessage("Are you sure you want to delete this?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                        if (post_ques == 0) {
                                            deletePost(question_id);
                                        } else {
                                            deleteQuestion(question_id);
                                        }
                                    }

                                });
                                builder.setNegativeButton("No", null);
                                AlertDialog alert = builder.create();
                                alert.show();
                                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                nbutton.setTextColor(Color.GREEN);
                                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                pbutton.setTextColor(Color.RED);
                            } catch (Exception e) {
                                Log.e("MainFeedAdapter", "feed_bookmark" + e.toString());
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                            }
                            return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        preview_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences prefs = getSharedPreferences("webviewurl", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("url", preview_link);
                    editor.commit();
                    startActivity(new Intent(MainFeedDetailedActivity.this, WebView_Common.class).putExtra("url", preview_link));
                } catch (Exception e) {
                    Log.e("MainFeedDetailedAtivty", "feed_bookmark" + e.toString());
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                }
            }
        });

        tv_d_houseName.setText(houseName);
        tv_d_profileName.setText(profileName);
        tv_d_profileUsername.setText(profileUsername);
        tv_d_title.setText(title);
        tv_d_desc.setText(desc);
        tv_d_appreciation.setText(appreciation_count);
        tv_d_score.setText(score_count + "");
        tv_d_responses.setText(responses_count);
        tv_d_appreciation.setTypeface(custom_font1);
        tv_d_score.setTypeface(custom_font1);
        tv_d_responses.setTypeface(custom_font1);

        switch (user_rate) {
            case 1:
                d_r1.setBackgroundResource(R.drawable.ic_1_filled);
                d_r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                d_r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*d_r4.setBackgroundResource(R.drawable.star_unfilled);
                d_r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
            case 2:
                d_r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                d_r2.setBackgroundResource(R.drawable.ic_2_filled);
                d_r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*d_r4.setBackgroundResource(R.drawable.star_unfilled);
                d_r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
            case 3:
                d_r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                d_r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                d_r3.setBackgroundResource(R.drawable.ic_3_filled);
                /*d_r4.setBackgroundResource(R.drawable.star_unfilled);
                d_r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
            /*case 4:
                d_r1.setBackgroundResource(R.drawable.star_filled);
                d_r2.setBackgroundResource(R.drawable.star_filled);
                d_r3.setBackgroundResource(R.drawable.star_filled);
                *//*d_r4.setBackgroundResource(R.drawable.star_filled);
                d_r5.setBackgroundResource(R.drawable.star_unfilled);*//*
                break;
            case 5:
                d_r1.setBackgroundResource(R.drawable.star_filled);
                d_r2.setBackgroundResource(R.drawable.star_filled);
                d_r3.setBackgroundResource(R.drawable.star_filled);
                *//*d_r4.setBackgroundResource(R.drawable.star_filled);
                d_r5.setBackgroundResource(R.drawable.star_filled);*//*
                break;*/
            default:
                d_r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                d_r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                d_r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*d_r4.setBackgroundResource(R.drawable.star_unfilled);
                d_r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
        }

        Picasso.with(this)
                .load(houseImageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(im_d_houseImageUrl);
        Picasso.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(im_d_profileImageUrl);
        if (post_ques == 0) {
            tv_d_post_ques.setText("post");
            //text_responses_answers.setText("Responses");
            responseTextStyle.setText("Responses");
            get_responses(1);
            get_story_details(question_id);
            ac_tv_add_comm.setHint("Type Your Response Here");
            tv_d_title.setTextColor(0xffef197d);
        } else {
            tv_d_post_ques.setText("question");
            //text_responses_answers.setText("Answers");
            responseTextStyle.setText("Answers");
            get_answers(1);
            get_question_details(question_id);
            ac_tv_add_comm.setHint("Type Your Answer Here");
            tv_d_title.setTextColor(0xff06aba5);
        }

        recyclerAdapter = new MainFeedDetailedCommentsAdapter(this, data);
        recyclerView.setAdapter(recyclerAdapter);
        final LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        //onScroll Listener to load responses/answers from API request again
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (llm.findLastVisibleItemPosition() == recyclerAdapter.getItemCount() - 3 && !loading) {
                    //bottom of list!
                    //Log.e("position", "" + llm.findLastCompletelyVisibleItemPosition());
                    Log.e("position", "" + llm.findLastVisibleItemPosition());
                    page_index++;
                    get_answers(page_index);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        layout = (LinearLayout) findViewById(R.id.images_container);
        ll = (LinearLayout) findViewById(R.id.ll);
        horizon_scroll = (HorizontalScrollView) findViewById(R.id.horizontaL_scroll);

        tv_d_houseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainFeedDetailedActivity.this, HouseProfileActivity.class);
                intent.putExtra("housename", houseName);
                intent.putExtra("imageurl", houseImageUrl);
                intent.putExtra("house_id", house_id);
                startActivity(intent);
            }
        });

        im_d_houseImageUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainFeedDetailedActivity.this, HouseProfileActivity.class);
                intent.putExtra("housename", houseName);
                intent.putExtra("imageurl", houseImageUrl);
                intent.putExtra("house_id", house_id);
                startActivity(intent);
            }
        });

        im_d_profileImageUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainFeedDetailedActivity.this, ProfileActivity.class);
                intent.putExtra("profilename", profileName);
                intent.putExtra("username", profileUsername);
                intent.putExtra("imageurl", profileImageUrl);
                intent.putExtra("profile_id", profile_id);
                startActivity(intent);
            }
        });

        tv_d_profileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainFeedDetailedActivity.this, ProfileActivity.class);
                intent.putExtra("profilename", profileName);
                intent.putExtra("username", profileUsername);
                intent.putExtra("imageurl", profileImageUrl);
                intent.putExtra("profile_id", profile_id);
                startActivity(intent);
            }
        });
        bt_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ac_tv_add_comm.getText().toString().length() != 0 && ac_tv_add_comm.getText().toString().length() <= 2000) {
                    //MainFeedDetailedCommentsObject newObj = new MainFeedDetailedCommentsObject();
                    // Check if no view has focus:
                    ac_tv_add_comm.clearFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    ArrayList<String> allMatches = new ArrayList<String>();
                    Pattern p = Pattern.compile("href=\\\"([^\\\"]*)\\\"", Pattern.DOTALL);
                    Matcher m = p.matcher(current_comment);
                    while (m.find()) {
                        String groupStr = m.group(1);
                        String str = groupStr.split("/")[2];
                        allMatches.add(str);
                    }
                    stockArr = new String[allMatches.size()];
                    stockArr = allMatches.toArray(stockArr);
                    if (post_ques == 0) {
                        post_response(current_comment);
                    } else {
                        post_answer(current_comment);
                    }
                    ac_tv_add_comm.setText("");
                    add_comment_image.setVisibility(View.GONE);
                    fm_comm_image.setVisibility(View.GONE);
                    scrollToView((ScrollView) findViewById(R.id.comm_scrollview), findViewById(R.id.view));
                    //im_upload_in_comment.setVisibility(View.GONE);
                    //bt_post_comment.setVisibility(View.GONE);
                    /*View targetView = findViewById(R.id.rl_with_rating);
                    targetView.getParent().requestChildFocus(targetView, targetView);*/

                } else if (ac_tv_add_comm.getText().toString().length() > 2000) {
                    if (post_ques == 0) {
                        Toast.makeText(getApplicationContext(), "Your comment length is " + (ac_tv_add_comm.getText().toString().length() - 2000) + " characters long", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Your answer length is " + (ac_tv_add_comm.getText().toString().length() - 2000) + " characters long", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ac_tv_add_comm.setError("Write Something");
                }
                //Toast.makeText(getApplicationContext(), "Blank comment not allowed", Toast.LENGTH_SHORT).show();

            }
        });

        im_upload_in_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_SHORT).show();

            }
        });

        d_r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d_r1.setBackgroundResource(R.drawable.ic_1_filled);
                d_r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                d_r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                if (post_ques == 0) {
                    patch_post_rating(1);
                } else {
                    patch_question_rating(1);
                }

            }
        });
        d_r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d_r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                d_r2.setBackgroundResource(R.drawable.ic_2_filled);
                d_r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                if (post_ques == 0) {
                    patch_post_rating(2);
                } else {
                    patch_question_rating(2);
                }
            }
        });
        d_r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d_r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                d_r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                d_r3.setBackgroundResource(R.drawable.ic_3_filled);
                if (post_ques == 0) {
                    patch_post_rating(3);
                } else {
                    patch_question_rating(3);
                }
            }
        });
        /*d_r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post_ques == 0) {
                    patch_post_rating(4);
                } else {
                    patch_question_rating(4);
                }
            }
        });
        d_r5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post_ques == 0) {
                    patch_post_rating(5);
                } else {
                    patch_question_rating(5);
                }
            }
        });*/

        if (feed_add_comment != null && !feed_add_comment.contentEquals(" ")) {
            MainFeedDetailedCommentsObject newObj = new MainFeedDetailedCommentsObject();
            newObj.setComment_title(feed_add_comment);
            newObj.setComment_rating_status(1);
            newObj.setComment_rating(0);
            newObj.setComment_profileImageUrl("http://s13.postimg.org/prj11gcpj/dp_9.jpg");
            newObj.setComment_profileName("Vishal Puri");
            newObj.setComment_profileUsername("@vip");
            data.add(newObj);
            recyclerAdapter.notifyDataSetChanged();
            scrollToView((ScrollView) findViewById(R.id.comm_scrollview), findViewById(R.id.view));
        }

        //called when any change occurs in text while responding/answering
        ac_tv_add_comm.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                /*if (ac_tv_add_comm.getText().length() == 0) {
                    //im_upload_in_comment.setVisibility(View.GONE);
                    bt_post_comment.setVisibility(View.GONE);
                }
                //im_upload_in_comment.setVisibility(View.VISIBLE);
                bt_post_comment.setVisibility(View.VISIBLE);*/
                Log.e("on text change", s.toString());

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (s.length() > 0) {

                }
                Log.e("before text change", s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                int index = 0;
                String subString = "";
                Spanned test1 = Html.fromHtml(s.toString());
                if (test1.toString().contains("@")) {
                    index = test1.toString().indexOf('@');
                    subString = test1.toString().substring(index, test1.length());
                    if (post_ques == 0) {
                        get_story_tag_users(question_id, subString);
                    } else {
                        get_question_tag_users(question_id, subString);
                    }
                }
                Log.e("after text change", s.toString());
                Log.e("subString", "" + subString);
                current_comment = Html.toHtml(s);
                Log.e("current comment", current_comment);

            }
        });

        //called when a tag user is selected from the list
        ac_tv_add_comm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tag_userObject obj_tag_user = (Tag_userObject) parent.getItemAtPosition(position);
                current_comment = current_comment.substring(0, current_comment.indexOf('@'));
                current_comment = current_comment.concat("<a href='/users/" + obj_tag_user.getUname() + "/profile' attruname='" + obj_tag_user.getUname() + "'>" + obj_tag_user.getName() + "</a>");
                Spanned htmlText = Html.fromHtml(current_comment);
                ac_tv_add_comm.setText(htmlText);
                Spanned htmltextt = Html.fromHtml(htmlText.toString());
                ac_tv_add_comm.setSelection(htmlText.length() - 2);
            }
        });
    }

    //onCLick on link in description in detailed view
    @SuppressLint("ParcelCreator")
    private class LinkSpan extends URLSpan {
        private LinkSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View view) {
            String url = getURL();
            if (url != null) {

                SharedPreferences prefs = getSharedPreferences("webviewurl", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("url", url);
                editor.commit();
                startActivity(new Intent(MainFeedDetailedActivity.this, WebView_Common.class).putExtra("url", url));

            }
        }
    }

    //delete question API request
    private void deleteQuestion(final String question_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                QUESTION_DELETE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("delete comment", "" + response);
                        if (response != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                int status = jsonObj.getInt("status");
                                if (status == 1) {
                                    /*notifyItemRemoved(position);
                                    data.remove(position);
                                    notifyDataSetChanged();*/
                                    Intent intent = new Intent();
                                    intent.putExtra("id", question_id);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "" + response,
                                            Toast.LENGTH_LONG)
                                            .show();
                                }

                            } catch (final JSONException e) {
                                Log.e("YourHousesProfileAdpter", "Json parsing error: " + e.getMessage());
                                /*Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();*/
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                            }
                        } else {
                            Log.e("YourHousesProfileAdpter", "Couldn't get json from server.");
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }

                        //progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Question-Id", question_id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    //delete post API request
    private void deletePost(final String post_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                POST_DELETE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("delete comment", "" + response);
                        if (response != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                int status = jsonObj.getInt("status");
                                if (status == 1) {
                                    /*notifyItemRemoved(position);
                                    data.remove(position);
                                    notifyDataSetChanged();*/
                                    Intent intent = new Intent();
                                    intent.putExtra("id", question_id);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "" + response,
                                            Toast.LENGTH_LONG)
                                            .show();
                                }

                            } catch (final JSONException e) {
                                Log.e("YourHousesProfileAdpter", "Json parsing error: " + e.getMessage());
                                /*Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();*/
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                            }
                        } else {
                            Log.e("YourHousesProfileAdpter", "Couldn't get json from server.");
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }

                        //progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Story-Id", post_id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    //get responses API request
    private void get_responses(final int page_index) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_RESPONSES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response comments", "" + response);
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONObject feed = jsonObj.getJSONObject("feed");
                            JSONArray block = feed.getJSONArray("comment");
                            for (int i = 0; i < block.length(); i++) {
                                //YourHousesProfileObject current = new YourHousesProfileObject();
                                MainFeedDetailedCommentsObject current = new MainFeedDetailedCommentsObject();
                                JSONObject block2 = block.getJSONObject(i);
                                current.setId(block2.getString("id"));
                                current.setAnswer_response(1);
                                current.setComment_profileImageUrl(block2.getString("user_image"));
                                current.setComment_profileName(block2.getString("user_name"));
                                current.setComment_profile_user_id(block2.getString("user_id"));
                                current.setComment_profileUsername(block2.getString("user_uname"));
                                current.setComment_rating_status(block2.getInt("rate_comment_status"));
                                current.setComment_rating(block2.getInt("rate_count"));
                                //current.setComment_rating(comm_ratingList[i]);
                                current.setComment_title(block2.getString("comment"));
                                current.setComment_imageUrl(block2.getString("comment_image"));
                                data.add(current);
                                //data.add(current);

                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        if (page_index == 1)
                            progressDialog.dismiss();
                        loading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                        if (page_index == 1)
                            progressDialog.dismiss();
                        loading = false;
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Page-Index", String.valueOf(page_index));
                headers.put("User-Id", String.valueOf(user_id));
                headers.put("Story-Id", question_id);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        if (page_index == 1) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    //get answers API request
    private void get_answers(final int page_index) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_ANSWERS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response answers users", "" + response);
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                JSONObject feed = jsonObj.getJSONObject("feed");
                                JSONObject jsonObj2 = new JSONObject(String.valueOf(feed));
                                JSONObject house = jsonObj2.getJSONObject("club");
                                houseName = house.getString("club_name");
                                houseImageUrl = house.getString("club_image");

                                JSONObject user = jsonObj2.getJSONObject("user");
                                profileName = user.getString("user_name");
                                profileImageUrl = user.getString("user_image");

                                JSONObject question = jsonObj2.getJSONObject("question");
                                title = question.getString("title");
                                desc = question.getString("content");
                                appreciation_count = question.getString("appreciation_count");
                                responses_count = question.getString("answer_count");
                                user_rate = question.getInt("apprection_star");
                                post_ques = 1;
                                if (question.getString("image").contentEquals("/default_interset.jpg")) {
                                    num_of_images = 0;
                                } else {
                                    arr_images = new ArrayList<String>();
                                    arr_images.add(question.getString("image"));
                                    // arr_images.add();
                                    num_of_images = 1;
                                }

                                JSONArray block = feed.getJSONArray("answer");
                                for (int i = 0; i < block.length(); i++) {

                                    MainFeedDetailedCommentsObject current = new MainFeedDetailedCommentsObject();
                                    JSONObject block2 = block.getJSONObject(i);
                                    current.setId(block2.getString("id"));
                                    current.setAnswer_response(0);
                                    current.setComment_profileImageUrl(block2.getString("answer_user_image"));
                                    current.setComment_profileName(block2.getString("answer_user_name"));
                                    current.setComment_profileUsername(block2.getString("answer_user_uname"));
                                    current.setComment_rating_status(block2.getInt("rate_answer_status"));
                                    current.setComment_profile_user_id(block2.getString("answer_user_id"));
                                    current.setComment_rating(block2.getInt("rate_count"));
                                    current.setAnswer_response(0);
                                    current.setComment_title(block2.getString("answer"));
                                    current.setComment_imageUrl(block2.getString("answer_image"));
                                    data.add(current);

                                }

                                setAllData();

                            } catch (JSONException e) {
                                Log.e("LOG", "" + e.toString());
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                            }
                            recyclerAdapter.notifyDataSetChanged();
                            if (page_index == 1)
                                progressDialog.dismiss();
                            loading = false;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("check", "check" + error);
                            if (page_index == 1)
                                progressDialog.dismiss();
                            loading = false;
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {

                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Page-Index", String.valueOf(page_index));
                    headers.put("User-Id", String.valueOf(user_id));
                    headers.put("Question-Id", question_id);
                    return headers;
                }

            };
            requestQueue.add(stringRequest);
            if (page_index == 1) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        } catch (Exception e) {
            Log.e("MainFeedDetaildActvty", "getAnswers" + e.toString());
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
        }
    }

    //called to load answers after answering the question
    private void get_answers_after_post(final int page_index) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_ANSWERS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response answers users", "" + response);
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                JSONObject feed = jsonObj.getJSONObject("feed");
                                JSONObject jsonObj2 = new JSONObject(String.valueOf(feed));
                                JSONObject house = jsonObj2.getJSONObject("club");
                                houseName = house.getString("club_name");
                                houseImageUrl = house.getString("club_image");

                                JSONObject user = jsonObj2.getJSONObject("user");
                                profileName = user.getString("user_name");
                                profileImageUrl = user.getString("user_image");

                                JSONObject question = jsonObj2.getJSONObject("question");
                                title = question.getString("title");
                                desc = question.getString("content");
                                appreciation_count = question.getString("appreciation_count");
                                responses_count = question.getString("answer_count");
                                user_rate = question.getInt("apprection_star");
                                post_ques = 1;
                                if (question.getString("image").contentEquals("/default_interset.jpg")) {
                                    num_of_images = 0;
                                } else {
                                    arr_images = new ArrayList<String>();
                                    arr_images.add(question.getString("image"));
                                    // arr_images.add();
                                    num_of_images = 1;
                                }

                                JSONArray block = feed.getJSONArray("answer");
                                for (int i = 0; i < block.length(); i++) {

                                    MainFeedDetailedCommentsObject current = new MainFeedDetailedCommentsObject();
                                    JSONObject block2 = block.getJSONObject(i);
                                    current.setId(block2.getString("id"));
                                    current.setAnswer_response(0);
                                    current.setComment_profileImageUrl(block2.getString("answer_user_image"));
                                    current.setComment_profileName(block2.getString("answer_user_name"));
                                    current.setComment_profileUsername(block2.getString("answer_user_uname"));
                                    current.setComment_rating_status(block2.getInt("rate_answer_status"));
                                    current.setComment_profile_user_id(block2.getString("answer_user_id"));
                                    current.setComment_rating(block2.getInt("rate_count"));
                                    current.setAnswer_response(0);
                                    current.setComment_title(block2.getString("answer"));
                                    current.setComment_imageUrl(block2.getString("answer_image"));
                                    data.add(current);

                                }

                                setAllData();

                            } catch (JSONException e) {
                                Log.e("LOG", "" + e.toString());
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                            }
                            recyclerAdapter.notifyDataSetChanged();
                            /*if (page_index == 1)
                                progressDialog.dismiss();*/
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
                    headers.put("User-Id", String.valueOf(user_id));
                    headers.put("Question-Id", question_id);
                    return headers;
                }

            };
            requestQueue.add(stringRequest);
            /*if (page_index == 1) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading Feed...");
                progressDialog.show();
            }*/
        } catch (Exception e) {
            Log.e("MainFeedDetaildActvty", "getAnswers" + e.toString());
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
        }
    }

    //called to load responses after responding to the post
    private void get_responses_after_post(final int page_index) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_RESPONSES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response comments", "" + response);
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONObject feed = jsonObj.getJSONObject("feed");
                            JSONArray block = feed.getJSONArray("comment");
                            for (int i = 0; i < block.length(); i++) {
                                //YourHousesProfileObject current = new YourHousesProfileObject();
                                MainFeedDetailedCommentsObject current = new MainFeedDetailedCommentsObject();
                                JSONObject block2 = block.getJSONObject(i);
                                current.setId(block2.getString("id"));
                                current.setAnswer_response(1);
                                current.setComment_profileImageUrl(block2.getString("user_image"));
                                current.setComment_profileName(block2.getString("user_name"));
                                current.setComment_profile_user_id(block2.getString("user_id"));
                                current.setComment_profileUsername(block2.getString("user_uname"));
                                current.setComment_rating_status(block2.getInt("rate_comment_status"));
                                current.setComment_rating(block2.getInt("rate_count"));
                                //current.setComment_rating(comm_ratingList[i]);
                                current.setComment_title(block2.getString("comment"));
                                current.setComment_imageUrl(block2.getString("comment_image"));
                                data.add(current);
                                //data.add(current);

                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        /*if (page_index == 1)
                            progressDialog.dismiss();*/
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
                headers.put("User-Id", String.valueOf(user_id));
                headers.put("Story-Id", question_id);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        /*if (page_index == 1) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading Feed...");
            progressDialog.show();
        }*/
    }

    //get tag users for a question API request
    private void get_question_tag_users(final String question_id, final String key_search) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_QUESTIONS_TAG_USERS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response tag users", "" + response);
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONArray feed = jsonObj.getJSONArray("users");
                            mStrings.clear();
                            arr_list_tag_users.clear();
                            for (int i = 0; i < feed.length(); i++) {
                                //YourHousesProfileObject current = new YourHousesProfileObject();
                                Tag_userObject obj_tag_user = new Tag_userObject();
                                JSONObject block = feed.getJSONObject(i);
                                //current.setId(block.getInt("id"));
                                //current.setYourHouseName(block.getString("name"));
                                obj_tag_user.setId(block.getString("id"));
                                obj_tag_user.setName(block.getString("name"));
                                obj_tag_user.setUname(block.getString("uname"));
                                obj_tag_user.setImageUrl(block.getString("image"));
                                arr_list_tag_users.add(obj_tag_user);
                                //data.add(current);

                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                        }
                        String[] strings = new String[mStrings.size()];
                        strings = mStrings.toArray(strings);
                        CustomerAdapter ar_obj1 = new CustomerAdapter(getApplicationContext(), R.layout.member_fragment_recyclerview_item, arr_list_tag_users);
                        ac_tv_add_comm.setAdapter(ar_obj1);
                        ac_tv_add_comm.showDropDown();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Key-Search", key_search);
                headers.put("Question-Id", String.valueOf(question_id));
                return headers;
            }

        };
        requestQueue.add(stringRequest);

    }

    //get tag users for a post API request
    private void get_story_tag_users(final String question_id, final String key_search) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_STORY_TAG_USERS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response tag users", "" + response);
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONArray feed = jsonObj.getJSONArray("users");
                            mStrings.clear();
                            arr_list_tag_users.clear();
                            for (int i = 0; i < feed.length(); i++) {
                                //YourHousesProfileObject current = new YourHousesProfileObject();
                                Tag_userObject obj_tag_user = new Tag_userObject();
                                JSONObject block = feed.getJSONObject(i);
                                //current.setId(block.getInt("id"));
                                //current.setYourHouseName(block.getString("name"));
                                obj_tag_user.setId(block.getString("id"));
                                obj_tag_user.setName(block.getString("name"));
                                obj_tag_user.setUname(block.getString("uname"));
                                obj_tag_user.setImageUrl(block.getString("image"));
                                arr_list_tag_users.add(obj_tag_user);
                                //data.add(current);

                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                        }
                        String[] strings = new String[mStrings.size()];
                        strings = mStrings.toArray(strings);
                        CustomerAdapter ar_obj1 = new CustomerAdapter(getApplicationContext(), R.layout.member_fragment_recyclerview_item, arr_list_tag_users);
                        ac_tv_add_comm.setAdapter(ar_obj1);
                        ac_tv_add_comm.showDropDown();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Key-Search", key_search);
                headers.put("Story-Id", String.valueOf(question_id));
                return headers;
            }

        };
        requestQueue.add(stringRequest);

    }

    //rate a question API request
    private void patch_question_rating(final int rate) {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PATCH_RATING_QUESTION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("patch ques rate", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("1")) {

                            } else {
                                //Toast.makeText(getApplicationContext(), "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                        }

                        // looping through All Contacts
                        //progressDialog.dismiss();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "" + error);
                        //progressDialog.dismiss();
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("question_id", question_id);
                questionHash.put("rate", String.valueOf(rate));
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("ratings", String.valueOf(new JSONObject(questionHash)));
                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }


            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

        /*progressDialog = new ProgressDialog(c);
        progressDialog.setMessage("Adding Bookmark...");
        progressDialog.show();*/


    }

    //rate a post API request
    private void patch_post_rating(final int rate) {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PATCH_RATING_POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("patch ques rate", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("1")) {

                            } else {
                                //Toast.makeText(getApplicationContext(), "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                        }

                        // looping through All Contacts
                        //progressDialog.dismiss();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "" + error);
                        //progressDialog.dismiss();
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("story_id", question_id);
                questionHash.put("rate", String.valueOf(rate));
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("ratings", String.valueOf(new JSONObject(questionHash)));
                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }


            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

        /*progressDialog = new ProgressDialog(c);
        progressDialog.setMessage("Adding Bookmark...");
        progressDialog.show();*/


    }

    //post an answer to the question API request
    private void post_answer(final String answer) {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_ANSWER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("post answer", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            //status = jsonObj.getBoolean("result");
                            //if (status) {
                            base64Image = "";
                            //Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();
                            //onBackPressed();
                            //} else {
                            //    Toast.makeText(getApplicationContext(), "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            //}
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                        }

                        // looping through All Contacts
                        progressDialog.dismiss();
                        page_index = 1;
                        data.clear();
                        get_answers_after_post(page_index);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "" + error);
                        Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {

                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("answer", answer);
                questionHash.put("answer_pic", base64Image);
                questionHash.put("club_question_id", question_id);
                questionHash.put("uids", Arrays.toString(stockArr).replace("[", "").replace("]", ""));
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("answer", String.valueOf(new JSONObject(questionHash)));
                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }


            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sending Answer...");
        progressDialog.show();


    }

    //post a response to the post API request
    private void post_response(final String response) {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_RESPONSE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("post response", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            //status = jsonObj.getBoolean("result");
                            //if (status) {
                            base64Image = "";
                            //Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();
                            //onBackPressed();
                            //} else {
                            //    Toast.makeText(getApplicationContext(), "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            //}
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                        }

                        // looping through All Contacts

                        progressDialog.dismiss();
                        progressDialog.dismiss();
                        page_index = 1;
                        data.clear();
                        get_responses_after_post(page_index);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_LONG).show();
                        Log.e("check", "" + error);
                        progressDialog.dismiss();
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {

                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("comment", response);
                questionHash.put("comment_pic", base64Image);
                questionHash.put("story_id", question_id);
                questionHash.put("uids", Arrays.toString(stockArr).replace("[", "").replace("]", ""));
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("story_comment", String.valueOf(new JSONObject(questionHash)));
                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }


            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Response...");
        progressDialog.setCancelable(false);
        progressDialog.show();


    }

    //add question to the bookmarks API request
    private void put_question_bookmark() {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PUT_QUESTION_BOOKMARK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("put ques bkmrk response", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("1")) {
                                bookmark_status = "1";
                                String bkmrk_id = jsonObj.getString("favourte_id");
                                bookmark_id = bkmrk_id;
                                //im_bookmark.setBackgroundResource(R.drawable.ic_bookmark_black_48dp);
                            } else {
                                Toast.makeText(getApplicationContext(), "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                        }
                        im_menu_button.setClickable(true);

                        // looping through All Contacts
                        //progressDialog.dismiss();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        im_menu_button.setClickable(true);
                        Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_LONG).show();
                        Log.e("check", "" + error);
                        //progressDialog.dismiss();
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("question_id", question_id);
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("question_fav", String.valueOf(new JSONObject(questionHash)));
                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }


            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

        /*progressDialog = new ProgressDialog(c);
        progressDialog.setMessage("Adding Bookmark...");
        progressDialog.show();*/


    }

    //remove question from the bookmarks API request
    private void delete_question_bookmark() {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                DELETE_QUESTION_BOOKMARK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("delt ques bkmrk respnse", "data from server - " + response);
                        if (response != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                String status = jsonObj.getString("status");
                                if (status.contentEquals("1")) {
                                    bookmark_status = "0";
                                    //im_bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_48dp);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                                }

                            } catch (final JSONException e) {
                                Log.e("YourHousesProfileAdpter", "Json parsing error: " + e.getMessage());
                                /*Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();*/
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                            }
                        } else {
                            Log.e("YourHousesProfileAdpter", "Couldn't get json from server.");
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                        //progressDialog.dismiss();
                        im_menu_button.setClickable(true);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        im_menu_button.setClickable(true);
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Favourite-Id", bookmark_id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    //add post to the bookmarks API request
    private void put_post_bookmark() {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PUT_POST_BOOKMARK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("put ques bkmrk response", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("1")) {
                                String bkmrk_id = jsonObj.getString("favourte_id");
                                bookmark_id = bkmrk_id;
                                //im_bookmark.setBackgroundResource(R.drawable.ic_bookmark_black_48dp);
                                bookmark_status = "1";
                            } else {
                                Toast.makeText(getApplicationContext(), "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                        }
                        im_menu_button.setClickable(true);
                        // looping through All Contacts
                        //progressDialog.dismiss();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "" + error);
                        //progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_LONG).show();
                        im_menu_button.setClickable(true);
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("story_id", question_id);
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("story_fav", String.valueOf(new JSONObject(questionHash)));
                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }


            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

        /*progressDialog = new ProgressDialog(c);
        progressDialog.setMessage("Adding Bookmark...");
        progressDialog.show();*/


    }

    //remove post from the bookmarks API request
    private void delete_post_bookmark() {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                DELETE_POST_BOOKMARK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("delt ques bkmrk respnse", "data from server - " + response);
                        if (response != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                String status = jsonObj.getString("status");
                                if (status.contentEquals("1")) {
                                    bookmark_status = "0";
                                    //im_bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_48dp);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                                }

                            } catch (final JSONException e) {
                                Log.e("YourHousesProfileAdpter", "Json parsing error: " + e.getMessage());
                                /*Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();*/
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                            }
                        } else {
                            Log.e("YourHousesProfileAdpter", "Couldn't get json from server.");
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                        //progressDialog.dismiss();
                        im_menu_button.setClickable(true);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        im_menu_button.setClickable(true);
                        Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_LONG).show();
                        Log.e("deletebookmark", error.toString());
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Favourite-Id", bookmark_id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    //get all the detailes of a post API request
    private void get_story_details(final String id) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_STORY_DETAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);

                                JSONObject block = jsonObj.getJSONObject("feed");
                                if (block.getInt("story_check") == 0) {
                                    rl_edittext.setVisibility(View.GONE);
                                    sv_detailed.setVisibility(View.GONE);
                                    ll_deleted_post.setVisibility(View.VISIBLE);
                                } else {
                                    rl_edittext.setVisibility(View.VISIBLE);
                                    sv_detailed.setVisibility(View.VISIBLE);
                                    ll_deleted_post.setVisibility(View.GONE);
                                    String type = block.getString("type");
                                    story_check = block.getString("story_check");
                                    JSONObject obj_club = block.getJSONObject("club");
                                    house_id = obj_club.getString("club_id");
                                    houseName = obj_club.getString("club_name");
                                    //current.setHouseDesc(obj_club.getString("club_descrition"));
                                    houseImageUrl = obj_club.getString("club_image");
                                    JSONObject obj_user = block.getJSONObject("user");
                                    profile_id = obj_user.getString("user_id");
                                    profileName = obj_user.getString("user_name");
                                    profileImageUrl = obj_user.getString("user_image");
                                    if (type.contentEquals("story")) {
                                        post_ques = 0;
                                        JSONObject obj_story = block.getJSONObject("story");
                                        JSONArray arr_contents = obj_story.getJSONArray("contents");
                                        arr_images = new ArrayList<String>();
                                        arr_images.clear();
                                        for (int j = 0; j < arr_contents.length(); j++) {
                                            JSONObject obj_images = arr_contents.getJSONObject(j);
                                            arr_images.add(obj_images.getString("content"));
                                        }
                                        num_of_images = arr_images.size();
                                        //Log.e("arr_contents=", "" + arr_contents);
                                        //current.setMainfeed_question_id(obj_story.getString("id"));
                                        title = obj_story.getString("title");
                                        desc = obj_story.getString("description");
                                        appreciation_count = obj_story.getString("appreciation_count");
                                        score_count = obj_story.getDouble("score");
                                        user_rate = obj_story.getInt("apprection_star");
                                        responses_count = obj_story.getString("comment_count");
                                        bookmark_id = obj_story.getString("bookmark_id");
                                        bookmark_status = obj_story.getString("bookmark_status");
                                        preview_title = obj_story.getString("preview_title");
                                        preview_desc = obj_story.getString("preview_description");
                                        preview_image = obj_story.getString("preview_image");
                                        preview_link = obj_story.getString("preview_link");

                                    } else {
                                        JSONObject obj_story = block.getJSONObject("question");
                                        post_ques = 1;
                                        //current.setMainfeed_question_id(obj_story.getString("id"));
                                        title = obj_story.getString("title");
                                        desc = obj_story.getString("content");
                                        responses_count = obj_story.getString("answer_count");
                                        user_rate = obj_story.getInt("apprection_star");
                                        appreciation_count = obj_story.getString("appreciation_count");
                                        bookmark_id = obj_story.getString("bookmark_id");
                                        bookmark_status = obj_story.getString("bookmark_status");
                                        preview_title = obj_story.getString("preview_title");
                                        preview_desc = obj_story.getString("preview_description");
                                        preview_image = obj_story.getString("preview_image");
                                        preview_link = obj_story.getString("preview_link");
                                    /*JSONArray arr_comments = obj_story.getJSONArray("answers");
                                    for (int j = 0; j < arr_comments.length(); j++) {
                                        JSONObject obj_comments = arr_comments.getJSONObject(j);
                                        current.setHint_comm_profile_name(obj_comments.getString("user_name"));
                                        current.setHint_comm_profile_image(obj_comments.getString("user_image"));
                                        current.setHint_comm_title(obj_comments.getString("answer"));
                                    }*/
                                        if (obj_story.getString("image").contentEquals("/default_interset.jpg")) {
                                            num_of_images = 0;
                                        } else {
                                            num_of_images = 1;
                                            arr_images.clear();
                                            arr_images.add(obj_story.getString("image"));
                                        }
                                        //current.setScore(obj_story.getDouble("score"));
                                        //current.setResponses(obj_story.getInt("comment_count"));

                                    }
                                    setAllData();
                                }

                            } catch (JSONException e) {
                                Log.e("LOG", "" + e.toString());
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_LONG).show();
                            Log.e("check", "check" + error);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {

                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Story-Id", id);
                    headers.put("User-Id", String.valueOf(user_id));
                    return headers;
                }

            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("MainFeedDetledActvity", "getStoryDetails" + e.toString());
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
        }
    }

    //get all the detailes of a question API request
    private void get_question_details(final String id) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_QUESTION_DETAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);

                                JSONObject block = jsonObj.getJSONObject("feed");
                                if (block.getInt("question_check") == 0) {
                                    rl_edittext.setVisibility(View.GONE);
                                    sv_detailed.setVisibility(View.GONE);
                                    ll_deleted_post.setVisibility(View.VISIBLE);
                                } else {
                                    rl_edittext.setVisibility(View.VISIBLE);
                                    sv_detailed.setVisibility(View.VISIBLE);
                                    ll_deleted_post.setVisibility(View.GONE);
                                    JSONObject obj_club = block.getJSONObject("club");
                                    house_id = obj_club.getString("club_id");
                                    houseName = obj_club.getString("club_name");
                                    //current.setHouseDesc(obj_club.getString("club_descrition"));
                                    houseImageUrl = obj_club.getString("club_image");
                                    JSONObject obj_user = block.getJSONObject("user");
                                    profile_id = obj_user.getString("user_id");
                                    profileName = obj_user.getString("user_name");
                                    profileImageUrl = obj_user.getString("user_image");
                                    JSONObject obj_story = block.getJSONObject("question");
                                    post_ques = 1;
                                    //current.setMainfeed_question_id(obj_story.getString("id"));
                                    title = obj_story.getString("title");
                                    desc = obj_story.getString("content");
                                    responses_count = obj_story.getString("answer_count");
                                    user_rate = obj_story.getInt("apprection_star");
                                    score_count = obj_story.getDouble("score");
                                    appreciation_count = obj_story.getString("appreciation_count");
                                    bookmark_id = obj_story.getString("bookmark_id");
                                    bookmark_status = obj_story.getString("bookmark_status");
                                    /*JSONArray arr_comments = obj_story.getJSONArray("answers");
                                    for (int j = 0; j < arr_comments.length(); j++) {
                                        JSONObject obj_comments = arr_comments.getJSONObject(j);
                                        current.setHint_comm_profile_name(obj_comments.getString("user_name"));
                                        current.setHint_comm_profile_image(obj_comments.getString("user_image"));
                                        current.setHint_comm_title(obj_comments.getString("answer"));
                                    }*/
                                    if (obj_story.getString("image").contentEquals("/default_interset.jpg")) {
                                        num_of_images = 0;
                                    } else {
                                        num_of_images = 1;
                                        arr_images.clear();
                                        arr_images.add(obj_story.getString("image"));
                                        //current.setScore(obj_story.getDouble("score"));
                                        //current.setResponses(obj_story.getInt("comment_count"));
                                    }
                                    setAllData();
                                }

                            } catch (Exception e) {
                                Log.e("LOG", "" + e.toString());
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_LONG).show();
                            Log.e("check", "check" + error);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {

                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Question-Id", id);
                    headers.put("User-Id", String.valueOf(user_id));
                    return headers;
                }

            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("MainFeedDetledActvity", "getQuesDetails" + e.toString());
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
        }
    }

    //make the view changes and loading data after getting post/question details from API request
    public void setAllData() {
        tv_d_houseName.setText(houseName);
        tv_d_profileName.setText(profileName);
        tv_d_profileUsername.setText(profileUsername);
        tv_d_title.setText(title);
        tv_d_desc.setText(desc);
        Spannable spannable = new SpannableString(desc);
        Linkify.addLinks(spannable, Linkify.WEB_URLS);
        URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (URLSpan urlSpan : spans) {
            LinkSpan linkSpan = new LinkSpan(urlSpan.getURL());
            int spanStart = spannable.getSpanStart(urlSpan);
            int spanEnd = spannable.getSpanEnd(urlSpan);
            spannable.setSpan(linkSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.removeSpan(urlSpan);
        }
        tv_d_desc.setMovementMethod(LinkMovementMethod.getInstance());
        tv_d_desc.setText(spannable, TextView.BufferType.SPANNABLE);
        tv_d_appreciation.setText(appreciation_count);
        tv_d_score.setText("" + score_count);
        tv_d_responses.setText(responses_count);
        /*Picasso.with(this)
                .load(imageUrl)
                .into(im_d_imageUrl);*/

        if (preview_link.contentEquals("null") || preview_link.contentEquals("") || preview_link.isEmpty()) {
            preview_cardview.setVisibility(View.GONE);
        } else {
            preview_cardview.setVisibility(View.VISIBLE);
            tv_preview_title.setText(preview_title);
            tv_preview_description.setText(preview_desc);

            if (!preview_image.contentEquals("")) {
                Picasso.with(getApplicationContext())
                        .load(preview_image)
                        .placeholder(R.drawable.placeholder_image)
                        .transform(new RoundedCornersTransform())
                        .into(im_preview_image);
            } else {
                Picasso.with(getApplicationContext())
                        .load("http://s15.postimg.org/3omz97m7v/sample_5.jpg")
                        .placeholder(R.drawable.placeholder_image)
                        .transform(new RoundedCornersTransform())
                        .into(im_preview_image);
            }

        }


        switch (user_rate) {
            case 1:
                d_r1.setBackgroundResource(R.drawable.ic_1_filled);
                d_r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                d_r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*d_r4.setBackgroundResource(R.drawable.star_unfilled);
                d_r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
            case 2:
                d_r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                d_r2.setBackgroundResource(R.drawable.ic_2_filled);
                d_r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*d_r4.setBackgroundResource(R.drawable.star_unfilled);
                d_r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
            case 3:
                d_r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                d_r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                d_r3.setBackgroundResource(R.drawable.ic_3_filled);
                /*d_r4.setBackgroundResource(R.drawable.star_unfilled);
                d_r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
            /*case 4:
                d_r1.setBackgroundResource(R.drawable.star_filled);
                d_r2.setBackgroundResource(R.drawable.star_filled);
                d_r3.setBackgroundResource(R.drawable.star_filled);
                *//*d_r4.setBackgroundResource(R.drawable.star_filled);
                d_r5.setBackgroundResource(R.drawable.star_unfilled);*//*
                break;
            case 5:
                d_r1.setBackgroundResource(R.drawable.star_filled);
                d_r2.setBackgroundResource(R.drawable.star_filled);
                d_r3.setBackgroundResource(R.drawable.star_filled);
                *//*d_r4.setBackgroundResource(R.drawable.star_filled);
                d_r5.setBackgroundResource(R.drawable.star_filled);*//*
                break;*/
            default:
                d_r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                d_r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                d_r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*d_r4.setBackgroundResource(R.drawable.star_unfilled);
                d_r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
        }

        Picasso.with(this)
                .load(houseImageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(im_d_houseImageUrl);
        Picasso.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(im_d_profileImageUrl);
        if (post_ques == 0) {
            tv_d_post_ques.setText("post");
            /*tv_d_score.setVisibility(View.VISIBLE);
            text_d_score.setVisibility(View.VISIBLE);*/
        } else {
            tv_d_post_ques.setText("question");
            /*tv_d_score.setVisibility(View.GONE);
            text_d_score.setVisibility(View.GONE);*/
        }
        if (num_of_images == 0) {
            ll.setVisibility(View.GONE);
        } else if (num_of_images == 1) {
            ll.setVisibility(View.VISIBLE);
            horizon_scroll.setVisibility(View.GONE);
            im_d_imageUrl.setVisibility(View.VISIBLE);
            im_d_imageUrl.setOnClickListener(onclicklistener);
            Picasso.with(this)
                    .load(arr_images.get(0))
                    .placeholder(R.drawable.placeholder_image)
                    .into(im_d_imageUrl);
        } else {
            ll.setVisibility(View.VISIBLE);
            horizon_scroll.setVisibility(View.VISIBLE);
            im_d_imageUrl.setVisibility(View.GONE);
            for (int i = 0; i < num_of_images; i++) {
                ImageView image = new ImageView(this);
                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                image.setAdjustViewBounds(true);
                image.setPadding(5, 0, 0, 0);
                image.setId(i);
                Picasso.with(this)
                        .load(arr_images.get(i))
                        .placeholder(R.drawable.placeholder_image)
                        .into(image);
                layout.addView(image);
                image.setOnClickListener(onclicklistener);
            }
        }
        /*if (bookmark_status != null) {
            if (bookmark_status.contentEquals("0")) {
                im_bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_48dp);
            } else {
                im_bookmark.setBackgroundResource(R.drawable.ic_bookmark_black_48dp);
            }
        }*/

    }

    //onCLickListeners on images in detailes view. 0 for first image, 1 for second and so on.
    View.OnClickListener onclicklistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            Intent intent;
            switch (id) {
                case 0:
                    //Log.e("image", "-" + id);
                    intent = new Intent(MainFeedDetailedActivity.this, ImageViewActivity.class);
                    intent.putExtra("profileimage", profileImageUrl);
                    intent.putExtra("profilename", profileName);
                    intent.putExtra("title", title);
                    intent.putExtra("imageurl", arr_images.get(0));
                    intent.putStringArrayListExtra("arr_images", arr_images);
                    intent.putExtra("responses", "" + responses_count);
                    intent.putExtra("score", "" + score_count);
                    intent.putExtra("appreciations", "" + appreciation_count);
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 0);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(MainFeedDetailedActivity.this, ImageViewActivity.class);
                    intent.putExtra("profileimage", profileImageUrl);
                    intent.putExtra("title", title);
                    intent.putExtra("profilename", profileName);
                    intent.putExtra("imageurl", arr_images.get(1));
                    intent.putStringArrayListExtra("arr_images", arr_images);
                    intent.putExtra("responses", "" + responses_count);
                    intent.putExtra("score", "" + score_count);
                    intent.putExtra("appreciations", "" + appreciation_count);
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 1);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(MainFeedDetailedActivity.this, ImageViewActivity.class);
                    intent.putExtra("profileimage", profileImageUrl);
                    intent.putExtra("title", title);
                    intent.putExtra("profilename", profileName);
                    intent.putExtra("imageurl", arr_images.get(2));
                    intent.putStringArrayListExtra("arr_images", arr_images);
                    intent.putExtra("responses", "" + responses_count);
                    intent.putExtra("score", "" + score_count);
                    intent.putExtra("appreciations", "" + appreciation_count);
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 2);
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(MainFeedDetailedActivity.this, ImageViewActivity.class);
                    intent.putExtra("profileimage", profileImageUrl);
                    intent.putExtra("title", title);
                    intent.putExtra("profilename", profileName);
                    intent.putExtra("imageurl", arr_images.get(3));
                    intent.putStringArrayListExtra("arr_images", arr_images);
                    intent.putExtra("responses", "" + responses_count);
                    intent.putExtra("score", "" + score_count);
                    intent.putExtra("appreciations", "" + appreciation_count);
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 3);
                    startActivity(intent);
                    break;
                case R.id.d_feed_image:
                    intent = new Intent(MainFeedDetailedActivity.this, ImageViewActivity.class);
                    intent.putExtra("profileimage", profileImageUrl);
                    intent.putExtra("title", title);
                    intent.putExtra("profilename", profileName);
                    intent.putExtra("imageurl", arr_images.get(0));
                    intent.putStringArrayListExtra("arr_images", arr_images);
                    intent.putExtra("responses", "" + responses_count);
                    intent.putExtra("score", "" + score_count);
                    intent.putExtra("appreciations", "" + appreciation_count);
                    intent.putExtra("isMainFeedAdapter", "detailedActivity");
                    intent.putExtra("position", 0);
                    startActivity(intent);
                    break;
            }
        }
    };

    //to scroll to a particular view
    private void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    /**
     * Used to get deep child offset.
     * <p/>
     * 1. We need to scroll to child in scrollview, but the child may not the direct child to scrollview.
     * 2. So to get correct child position to scroll, we need to iterate through all of its parent views till the main parent.
     *
     * @param mainParent        Main Top parent.
     * @param parent            Parent.
     * @param child             Child.
     * @param accumulatedOffset Accumalated Offset.
     */
    private void getDeepChildOffset(final ViewGroup mainParent,
                                    final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    //loads a menu to select image chooser options
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = EditProfile.Utility.checkPermission(MainFeedDetailedActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.GREEN);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.RED);
    }

    //open camera to take a photo
    private void cameraIntent() {
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    //open gallery to view all device photos
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //when image from gallery selected
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
            //when image from camera selected
            else if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = null;
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                }
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(getRealPathFromURI(imageUri));
                } catch (IOException e) {
                    e.printStackTrace();
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
                }

                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                Matrix matrix = new Matrix();
                switch (exifOrientation) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        matrix.setRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                        matrix.setScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.setRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                        matrix.setRotate(180);
                        matrix.postScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_TRANSPOSE:
                        matrix.setRotate(90);
                        matrix.postScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.setRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_TRANSVERSE:
                        matrix.setRotate(-90);
                        matrix.postScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.setRotate(-90);
                        break;
                    default:
                }

                Bitmap bmRotated = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
                //bm.recycle();
                //thumbnail = bmRotated;

                Bitmap resizedBitmap = null;
                int scaleSize = 600;
                int originalWidth = bmRotated.getWidth();
                int originalHeight = bmRotated.getHeight();

                double newWidth = 1;
                double newHeight = 1;
                if (originalWidth > scaleSize) {

                    newWidth = scaleSize;
                    newHeight = (newWidth * originalHeight / originalWidth);
                } else {

                    newWidth = originalWidth;
                    newHeight = originalHeight;
                }

                resizedBitmap = Bitmap.createScaledBitmap(bmRotated, (int) newWidth, (int) newHeight, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                fm_comm_image.setVisibility(View.VISIBLE);
                add_comment_image.setImageBitmap(resizedBitmap);
                add_comment_image.setVisibility(View.VISIBLE);
                add_comment_image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

                if (resizedBitmap != null)
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
                byte[] byteArray = baos.toByteArray();

                base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
                base64Image = "data:image/jpeg;base64," + base64Image;
                Log.e("imageUrl", "-" + getRealPathFromURI(imageUri));

            }

        }
    }

    //convert a URI into file path
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //image from gallery is selected
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        ExifInterface exif = null;
        int exifOrientation = 0;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                exif = new ExifInterface(getRealPathFromURI(data.getData()));
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
            } catch (Exception e) {
                Log.e("error", e + "");
                Error_GlobalVariables error_global = new Error_GlobalVariables();
                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
            }
            Matrix matrix = new Matrix();
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:

            }
            Bitmap bmRotated = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            //bm.recycle();
            bm = bmRotated;

            Bitmap resizedBitmap = null;
            int scaleSize = 600;
            int originalWidth = bm.getWidth();
            int originalHeight = bm.getHeight();

            double newWidth = 1;
            double newHeight = 1;
            if (originalWidth > scaleSize) {

                newWidth = scaleSize;
                newHeight = (newWidth * originalHeight / originalWidth);
            } else {

                newWidth = originalWidth;
                newHeight = originalHeight;
            }

            resizedBitmap = Bitmap.createScaledBitmap(bm, (int) newWidth, (int) newHeight, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();


            if (resizedBitmap != null)
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 90, baos); //bm is the bitmap object
            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");

            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(baos.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Error_GlobalVariables error_global = new Error_GlobalVariables();
                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Error_GlobalVariables error_global = new Error_GlobalVariables();
                error_global.initializeVar(MainFeedDetailedActivity.this, e.toString());
            }

            add_comment_image.setVisibility(View.VISIBLE);
            fm_comm_image.setVisibility(View.VISIBLE);
            add_comment_image.setImageBitmap(bm);
            add_comment_image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            byte[] b = baos.toByteArray();
            base64Image = Base64.encodeToString(b, Base64.DEFAULT);
            base64Image = "data:image/jpeg;base64," + base64Image;

        }
    }


    //called when back button is pressed
    @Override
    public void onBackPressed() {
        //show alert when response/answer lenght is not zero or image is selected
        if (current_comment.length() != 0 || base64Image.length() != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_dialog_alert);
            builder.setTitle("Closing Activity");
            builder.setMessage("Are you sure you want to close this activity?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    overridePendingTransition(R.anim.do_nothing,
                            R.anim.left_to_right);
                    finish();
                }

            });
            builder.setNegativeButton("No", null);
            AlertDialog alert = builder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(Color.GREEN);
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(Color.RED);
        } else {
            super.onBackPressed();
            //animation to close detailed activity
            overridePendingTransition(R.anim.do_nothing,
                    R.anim.left_to_right);
        }
    }

}
