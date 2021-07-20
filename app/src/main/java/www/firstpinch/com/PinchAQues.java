package www.firstpinch.com.firstpinch2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import www.firstpinch.com.firstpinch2.CustomGallery.CustomGallery_Activity_Question;
import www.firstpinch.com.firstpinch2.CustomGallery.GridView_Adapter_Question;
import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.MainFeed.RoundedCornersTransform;
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileObject;
import www.firstpinch.com.firstpinch2.SQLite.Draft_DatabaseHandler;

/**
 * Created by Rianaa Admin on 07-09-2016.
 */
public class PinchAQues extends AppCompatActivity implements View.OnClickListener {

    EditText et_question, et_house_id;
    MonitoringEditTextQuestion et_content;
    TextView tv_char_left, tv_house_name, preview_title, preview_desc;
    ImageView im_house_name_close, image_close, preview_close, preview_image;
    CardView preview_cardview;
    LinearLayout ll_house_name, ll_house_name_inhouse;
    View grayline2;

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    String base64Image = "", url = "";
    String text = "";
    String desc = "";
    String imageUrl = "";
    String POST_QUESTION_HOUSES_URL = "http://54.169.84.123/api/club_questions";
    String GET_HOUSES_URL = "http://54.169.84.123/api/getJoinHouses", content = "", question = "", isHouseProfileActivity = "";
    Boolean status;
    String house_id = "";
    private Button btnAddPhots, bt_ask_ques;
    /*private LinearLayout lnrImages;
    private Button btnAddPhots;
    private Button btnSaveImages;
    private ArrayList<String> imagesPathList;
    private Bitmap yourbitmap;
    private Bitmap resized;
    private final int PICK_IMAGE_MULTIPLE = 1;*/
    private static Button openCustomGallery;
    private static GridView selectedImageGridView;
    int user_id, post_success = 0;
    private static final int CustomGallerySelectId = 1;//Set Intent Id
    public static final String CustomGalleryIntentKey = "ImageArray";//Set Intent Key Value
    View parent;
    ProgressDialog progressDialog;
    AutoCompleteTextView houses;
    String house_name = "", unislug, user_image;
    int unil;
    //for slug
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    List<YourHousesProfileObject> data = new ArrayList<YourHousesProfileObject>();
    List<String> mStrings = new ArrayList<String>();
    LinearLayout ll_select_house;
    ImageView im_profile_image;
    Draft_DatabaseHandler db;
    RequestQueue uploadrequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinch_a_ques);
        uploadrequestQueue = Volley.newRequestQueue(getApplicationContext());
        db = new Draft_DatabaseHandler(this);
        db.onOpen(db.getWritableDatabase());
        parent = findViewById(R.id.view_pinch_a_ques);
        this.overridePendingTransition(R.anim.bottom_to_top,
                R.anim.do_nothing);
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        user_image = sp.getString("image", " ");
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Cabin-Regular.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");

        isHouseProfileActivity = getIntent().getStringExtra("isHouseProfileActivity");
        house_id = getIntent().getStringExtra("house_id");

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        tv_char_left = (TextView) findViewById(R.id.tv_char_left);
        et_question = (EditText) findViewById(R.id.et_ques);
        et_house_id = (EditText) findViewById(R.id.et_choose_house);
        et_content = (MonitoringEditTextQuestion) findViewById(R.id.et_ques_desc);
        image_close = (ImageView) findViewById(R.id.post_cross_img);
        btnAddPhots = (Button) findViewById(R.id.btnAddPhots);
        bt_ask_ques = (Button) findViewById(R.id.button_ask_now);
        ll_select_house = (LinearLayout) findViewById(R.id.ll_select_house);
        btnAddPhots.setOnClickListener(this);
        houses = (AutoCompleteTextView) findViewById(R.id.et_choose_house);
        im_profile_image = (ImageView) findViewById(R.id.post_acc_img);
        tv_house_name = (TextView) findViewById(R.id.tv_house_name);
        im_house_name_close = (ImageView) findViewById(R.id.im_house_name_close);
        ll_house_name = (LinearLayout) findViewById(R.id.ll_house_name);
        ll_house_name_inhouse = (LinearLayout) findViewById(R.id.ll_house_name_inhouse);
        TextView pinchTextStyle = (TextView) findViewById(R.id.pinchInText);
        preview_title = (TextView) findViewById(R.id.preview_title);
        preview_desc = (TextView) findViewById(R.id.preview_desc);
        preview_image = (ImageView) findViewById(R.id.preview_image);
        preview_close = (ImageView) findViewById(R.id.preview_close);
        preview_cardview = (CardView) findViewById(R.id.preview_cardview);
        preview_cardview.setVisibility(View.GONE);

        ll_select_house.setVisibility(View.VISIBLE);
        ll_house_name_inhouse.setVisibility(View.GONE);
        Picasso.with(this)
                .load(user_image)
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(im_profile_image);
        pinchTextStyle.setTypeface(custom_font1);

        et_question.setTypeface(custom_font);
        et_content.setTypeface(custom_font1);
        et_house_id.setTypeface(custom_font1);

        preview_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview_cardview.setVisibility(View.GONE);
                text = "";
                desc = "";
                imageUrl = "";
                url = "";
            }
        });

        /*if (isHouseProfileActivity.contentEquals("true")) {
            ll_select_house.setVisibility(View.GONE);
        } else {*/
        get_houses();
        SharedPreferences sp1 = getSharedPreferences("draft_question",
                Activity.MODE_PRIVATE);
        if (sp1.getBoolean("draft", false)) {
            List<String> data = db.getAllDraftQuestionData();
            if (data.size() > 0) {
                try {
                    et_question.setText(data.get(0));
                    et_content.setText(data.get(3));
                    if (!data.get(2).contentEquals("")) {
                        house_name = data.get(2);
                    /*houses.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            houses.showDropDown();
                        }
                    }, 500);*/
                        houses.setVisibility(View.GONE);
                        ll_house_name.setVisibility(View.VISIBLE);
                        tv_house_name.setText(data.get(2));
                        houses.setText(data.get(2));
                    }
                    houses.setSelection(houses.getText().length());
                    house_id = data.get(1);
                } catch (Exception e) {
                    Log.e("Exception - ", e.toString());
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(PinchAQues.this, e.toString());
                }
            }
        }
        //}
        bt_ask_ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Unit Testing", "Pinch a Post button on click");
                content = et_content.getText().toString();
                question = et_question.getText().toString();
                if (check_pref()) {
                    post_success = 2;
                    post_question();
                }
            }
        });

        houses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                house_name = houses.getText().toString();
                houses.setVisibility(View.GONE);
                ll_house_name.setVisibility(View.VISIBLE);
                tv_house_name.setText(house_name);
                Log.e("universitydata", "" + house_name);
                for (int i = 0; i < data.size(); i++) {
                    YourHousesProfileObject current = data.get(i);
                    if (current.getYourHouseName().contentEquals(house_name))
                        house_id = current.getId();
                }
                /*unislug = toSlug(unidata);
                Log.e("unislug", "" + unislug);
                unil = unidata.length();*/
            }
        });

        ll_house_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_house_name.setVisibility(View.GONE);
                houses.setVisibility(View.VISIBLE);
                house_name = "";
                house_id = "";
                /*houses.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        houses.showDropDown();
                    }
                }, 500);*/
                houses.setText(house_name);
            }
        });

        /*lnrImages = (LinearLayout) findViewById(R.id.lnrImages);
        btnAddPhots = (Button) findViewById(R.id.btnAddPhots);
        btnSaveImages = (Button) findViewById(R.id.button_ask_now);
        btnAddPhots.setOnClickListener(this);
        btnSaveImages.setOnClickListener(this);*/
        initViews();
        setListeners();
        getSharedImages();

        houses.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                houses.showDropDown();
            }
        });


        et_question.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                int len = et_question.length();
                tv_char_left.setText(100 - len + " char. left");
                if (100 - len < 0) {
                    tv_char_left.setText("limit Exceeded!");
                    tv_char_left.setTextColor(Color.RED);
                } else
                    tv_char_left.setTextColor(Color.GRAY);
            }
        });

        image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public Boolean check_pref() {

        if (/*!isHouseProfileActivity.contentEquals("true") && */houses.getText().toString().trim().length() == 0) {
            houses.setError("*Choose House");
            return false;
        }
        if (et_question.getText().toString().trim().length() == 0) {
            et_question.setError("*Write Something");
            return false;
        }
        return true;
    }

    public void setPreviewQuestion(final String url) {
        /*Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

            }
        });
        thread.start();*/
        this.url = url;
        if (preview_cardview.getVisibility() != View.VISIBLE) {
            new Preview().execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                post_success = 10;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void get_houses() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_HOUSES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONArray feed = jsonObj.getJSONArray("clubs");
                            for (int i = 0; i < feed.length(); i++) {
                                YourHousesProfileObject current = new YourHousesProfileObject();
                                JSONObject block = feed.getJSONObject(i);
                                current.setId(block.getString("id"));
                                current.setYourHouseName(block.getString("name"));
                                mStrings.add(block.getString("name"));
                                data.add(current);

                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(PinchAQues.this, e.toString());
                        }
                        String[] strings = new String[mStrings.size()];
                        strings = mStrings.toArray(strings);
                        Log.e("get houses", "" + strings);
                        ArrayAdapter ar_obj1 = new ArrayAdapter<String>(PinchAQues.this, android.R.layout.select_dialog_item, strings);
                        houses.setAdapter(ar_obj1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "AuthFailureError - Try Again", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Sorry! We couldn't connect with the server - Try Again", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "NetworkError - Try Again", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(), "ParseError - Try Again", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-Id", String.valueOf(user_id));
                return headers;
            }

        };
        requestQueue.add(stringRequest);

    }

    public static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    private void initViews() {
        selectedImageGridView = (GridView) findViewById(R.id.selectedImagesGridView);
    }

    //set Listeners
    private void setListeners() {
        //openCustomGallery.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        switch (requestcode) {
            case CustomGallerySelectId:
                if (resultcode == RESULT_OK) {
                    String imagesArray = imagereturnintent.getStringExtra(CustomGalleryIntentKey);//get Intent data
                    //Convert string array into List by splitting by ',' and substring after '[' and before ']'
                    List<String> selectedImages = Arrays.asList(imagesArray.substring(1, imagesArray.length() - 1).split(", "));
                    for (int i = 0; i < selectedImages.size(); i++) {
                        //Bitmap bm = BitmapFactory.decodeFile("file://" + selectedImages.get(i));
                        Bitmap bm = BitmapFactory.decodeFile(selectedImages.get(i));
                        Bitmap bmRotated = null;
                        ExifInterface exif = null;
                        try {
                            exif = new ExifInterface(selectedImages.get(i));
                            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_NORMAL);

                            Log.e("exif", exifOrientation + "");


                            Matrix matrix = new Matrix();
                            switch (exifOrientation) {
                                case ExifInterface.ORIENTATION_NORMAL:
                                    //matrix.setRotate(90);
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
                                    //bm.recycle();
                                    //bm = bmRotated;
                            }
                            bmRotated = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                            bm = bmRotated; //final image after rotation

                            Bitmap resizedBitmap = null;

                            int scaleSize = 800;
                            int originalWidth = bmRotated.getWidth();
                            int originalHeight = bmRotated.getHeight();
                            Log.e("width", originalWidth + "");
                            Log.e("height", originalHeight + "");


                            double newWidth = 1;
                            double newHeight = 1;
                            if (originalWidth > scaleSize) {

                                newWidth = scaleSize;
                                newHeight = (newWidth * originalHeight / originalWidth);
                            } else {

                                newWidth = originalWidth;
                                newHeight = originalHeight;
                            }
                            Log.e("nwidth", newWidth + "");
                            Log.e("nheight", newHeight + "");
                            resizedBitmap = Bitmap.createScaledBitmap(bmRotated, (int) newWidth, (int) newHeight, true);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();


                            if (resizedBitmap != null)
                                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();
                            base64Image = Base64.encodeToString(b, Base64.DEFAULT);
                            base64Image = "data:image/jpeg;base64," + base64Image;
                            //Log.e("Base64 image", base64Image);
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(PinchAQues.this, e.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(PinchAQues.this, e.toString());
                        }
                    }
                    loadGridView(new ArrayList<String>(selectedImages));//call load gridview method by passing converted list into arrayList
                    if (!selectedImages.isEmpty()) {
                        selectedImageGridView.setNumColumns(2);
                    }
                }
                break;

        }
    }

    private void post_question() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_QUESTION_HOUSES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("post question response", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            status = jsonObj.getBoolean("result");
                            if (status) {
                                post_success = 1;
                                /*MainFeedRecyclerView mfrv = new MainFeedRecyclerView();
                                List<MainFeedObject> mfData = mfrv.getMainFeedDataList();
                                MainFeedObject current = new MainFeedObject();
                                current.setMainfeed_house_id(34);
                                current.setHouseName("club_name");
                                current.setHouseDesc("club_descrition");
                                current.setHouseImageUrl("club_image");
                                current.setMainfeed_user_id(147);
                                current.setProfileName("user_name");
                                current.setProfileImageUrl("user_image");
                                current.setNum_of_images(0);
                                current.setMainfeed_question_id("id");
                                current.setTitle("title");
                                current.setDesc("description");
                                current.setAppreciations_count(0);
                                current.setScore(0.0);
                                current.setUser_rate(2);
                                current.setResponses(0);
                                current.setBookmark_id("71");
                                current.setBookmark_status("0");
                                current.setPost_ques(0);
                                mfData.add(current);
                                mfrv.setMainFeedDataList(mfData);*/
                                progressDialog.dismiss();
                                //mfrv.recyclerAdapter.notifyDataSetChanged();
                                //Toast.makeText(getApplicationContext(), "Question pinched successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                                Log.i("Unit Testing", "Pinch a Post data posted and activity back pressed");
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Oops! Failed to pinch your question!! Please try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "Image Not Uploaded - " + e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(PinchAQues.this, e.toString());
                        }

                        // looping through All Contacts

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_LONG).show();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "AuthFailureError - Try Again", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Sorry! We couldn't connect with the server - Try Again", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "NetworkError - Try Again", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(), "ParseError - Try Again", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_SHORT).show();
                        }
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
                questionHash.put("question_pic", base64Image);
                questionHash.put("title", question);
                questionHash.put("club_id", String.valueOf(house_id));
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("content", content);
                questionHash.put("preview_title", text);
                questionHash.put("preview_description", desc);
                questionHash.put("preview_image", imageUrl);
                questionHash.put("preview_link", url);
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("club_question", String.valueOf(new JSONObject(questionHash)));

                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }


            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag("question_request");
        uploadrequestQueue.add(stringRequest);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Pinching your Question...");
        progressDialog.setCancelable(false);
        progressDialog.show();


    }

    //Load GridView
    private void loadGridView(ArrayList<String> imagesArray) {
        GridView_Adapter_Question adapter = new GridView_Adapter_Question(this, imagesArray, false);
        selectedImageGridView.setAdapter(adapter);
    }

    //Read Shared Images
    private void getSharedImages() {

        //If Intent Action equals then proceed
        if (Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())
                && getIntent().hasExtra(Intent.EXTRA_STREAM)) {
            ArrayList<Parcelable> list =
                    getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);//get Parcelabe list
            ArrayList<String> selectedImages = new ArrayList<>();

            //Loop to all parcelable list
            for (Parcelable parcel : list) {
                Uri uri = (Uri) parcel;//get URI
                String sourcepath = getPath(uri);//Get Path of URI
                selectedImages.add(sourcepath);//add images to arraylist
            }
            loadGridView(selectedImages);//call load gridview
        }
    }


    //get actual path of uri
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddPhots:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // Explain to the user why we need to read the contacts
                        }
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                        // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                        // app-defined int constant
                        return;
                    } else {
                        Intent intent = new Intent(this, CustomGallery_Activity_Question.class);
                        startActivityForResult(intent, CustomGallerySelectId);
                    }
                } else {
                    Intent intent = new Intent(this, CustomGallery_Activity_Question.class);
                    startActivityForResult(intent, CustomGallerySelectId);
                }
                break;
            case R.id.button_ask_now:
                /*if (imagesPathList != null) {
                    if (imagesPathList.size() > 1) {
                        Toast.makeText(this, imagesPathList.size() + " no of images are selected", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, imagesPathList.size() + " no of image are selected", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, " no images are selected", Toast.LENGTH_SHORT).show();
                }*/
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent(this, CustomGallery_Activity_Question.class);
                    startActivityForResult(intent, CustomGallerySelectId);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(parent, "Allow to Upload Images", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_purple
                            )).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageview = (ImageView) findViewById(R.id.imageView);


        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    imageview.setImageURI(selectedImage);
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    imageview.setImageURI(selectedImage);
                }
                break;
        }

    }*/
    /*@TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_MULTIPLE){
                imagesPathList = new ArrayList<String>();
                String[] imagesPath = data.getStringExtra("data").split("\\|");
                try{
                    lnrImages.removeAllViews();
                }catch (Throwable e){
                    e.printStackTrace();
                }
                for (int i=0;i<imagesPath.length;i++){
                    imagesPathList.add(imagesPath[i]);
                    yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);
                    //yourbitmap.setConfig(Bitmap.Config.RGB_565);
                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(yourbitmap);
                    imageView.setMaxWidth(400);
                    imageView.setMaxHeight(400);
                    imageView.setPadding(10,10,10,10);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setElevation(20f);
                    }
                    imageView.setAdjustViewBounds(true);
                    lnrImages.addView(imageView);
                }
            }
        }

    }*/


    @Override
    public void onBackPressed() {
        if (post_success == 0) {
            if (et_content.getText().toString().length() != 0 || et_question.getText().toString().length() != 0
                    || houses.getText().toString().length() != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.ic_dialog_alert);
                builder.setTitle("Closing Activity");
                builder.setMessage("Choose Action");
                builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        overridePendingTransition(R.anim.do_nothing,
                                R.anim.top_to_bottom);
                        finish();
                        SharedPreferences prefs = getSharedPreferences("draft_question", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("draft", false);
                        editor.commit();
                        db.removeAllQuestion();
                    }

                });
                builder.setNegativeButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        overridePendingTransition(R.anim.do_nothing,
                                R.anim.top_to_bottom);
                        finish();
                        content = et_content.getText().toString();
                        question = et_question.getText().toString();
                        db.addQuestionData(question, content, house_id, house_name, "1");
                        SharedPreferences prefs = getSharedPreferences("draft_question", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("draft", true);
                        editor.commit();

                    }

                });
                AlertDialog alert = builder.create();
                alert.show();
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.GREEN);
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.RED);

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.ic_dialog_alert);
                builder.setTitle("Closing Activity");
                builder.setMessage("Are you sure you want to close this activity?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        overridePendingTransition(R.anim.do_nothing,
                                R.anim.top_to_bottom);
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
            }

        } else {
            overridePendingTransition(R.anim.do_nothing,
                    R.anim.top_to_bottom);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        uploadrequestQueue.cancelAll("question_request");
        if (post_success == 2) {
            finish();
        }
        super.onPause();
    }

    private class Preview extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla")
                        .get();
                Elements metaOgTitle = doc.select("meta[name=og:title]");
                Elements metaOgTitle2 = doc.select("meta[property=og:title]");
                Elements metaOgTitle3 = doc.select("meta[name=twitter:title]");
                if (metaOgTitle.size() != 0) {
                    text = metaOgTitle.attr("content");
                } else if (metaOgTitle2.size() != 0) {
                    text = metaOgTitle2.attr("content");
                } else if (metaOgTitle3.size() != 0) {
                    text = metaOgTitle3.attr("content");
                } else {
                    text = doc.title();
                }

                Elements metaOgTitle4 = doc.select("meta[name=og:description]");
                Elements metaOgTitle5 = doc.select("meta[property=og:description]");
                Elements metaOgTitle6 = doc.select("meta[name=description]");
                if (metaOgTitle4.size() != 0) {
                    desc = metaOgTitle4.attr("content");
                } else if (metaOgTitle5.size() != 0) {
                    desc = metaOgTitle5.attr("content");
                } else if (metaOgTitle6.size() != 0) {
                    desc = metaOgTitle6.attr("content");
                } else {
                    desc = doc.tag().getName();
                }

                Elements metaOgTitle7 = doc.select("meta[name=og:image]");
                Elements metaOgTitle8 = doc.select("meta[property=og:image]");
                Elements metaOgTitle9 = doc.select("meta[name=twitter:image]");
                if (metaOgTitle7.size() != 0) {
                    imageUrl = metaOgTitle7.attr("content");
                } else if (metaOgTitle8.size() != 0) {
                    imageUrl = metaOgTitle8.attr("content");
                } else if (metaOgTitle9.size() != 0) {
                    imageUrl = metaOgTitle9.attr("content");
                }


                Log.e("title", text);
                Log.e("desc", desc);
                Log.e("image ", imageUrl);

            } catch (Exception e) {
                e.printStackTrace();
                Error_GlobalVariables error_global = new Error_GlobalVariables();
                error_global.initializeVar(PinchAQues.this, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            preview_cardview.setVisibility(View.VISIBLE);
            preview_desc.setText(desc);
            preview_title.setText(text);
            if (imageUrl != "") {
                Picasso.with(getApplicationContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .transform(new RoundedCornersTransform())
                        .into(preview_image);
            }
            //super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
