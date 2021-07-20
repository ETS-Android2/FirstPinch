package www.firstpinch.com.firstpinch2;

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
import java.util.Random;
import java.util.regex.Pattern;

import www.firstpinch.com.firstpinch2.CustomGallery.CustomGallery_Activity_Post;
import www.firstpinch.com.firstpinch2.CustomGallery.GridView_Adapter_Post;
import www.firstpinch.com.firstpinch2.CustomGallery.GridView_Adapter_PostInHouse;
import www.firstpinch.com.firstpinch2.CustomGallery.ImageObject;
import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.MainFeed.RoundedCornersTransform;
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileObject;
import www.firstpinch.com.firstpinch2.SQLite.Draft_DatabaseHandler;

/**
 * Created by Rianaa Admin on 25-10-2016.
 */

public class PinchAPostInHouse extends AppCompatActivity implements View.OnClickListener {

    EditText et_question, et_house_id;
    MonitoringEditTextPostInHouse et_content;
    TextView tv_char_left,tv_house_name_inhouse,preview_title, preview_desc;
    CardView preview_cardview;
    LinearLayout ll_house_name_inhouse;
    String base64Image = "",url = "";
    String text = "";
    String desc = "";
    String imageUrl = "";
    String POST_STORY_URL = "http://54.169.84.123/api/club_stories";
    String GET_HOUSES_URL = "http://54.169.84.123/api/getJoinHouses",
            UPLOAD_IMAGES_URL = "http://54.169.84.123/api/story/story_image",
            DELETE_UPLOADING_IMAGE = "http://54.169.84.123/api//story/contentdelete/1",
            content="", question="", isHouseProfileActivity = "";
    String status, house_id="", user_image,house_name="";
    public ImageView image_close,preview_close, preview_image;
    private Button btnAddPhots, bt_ask_ques;
    private static GridView selectedImageGridView;
    int user_id, post_success = 0;
    private static final int CustomGallerySelectId = 1;//Set Intent Id
    public static final String CustomGalleryIntentKey = "ImageArray";//Set Intent Key Value
    View parent;
    ProgressDialog progressDialog;
    AutoCompleteTextView houses;
    String unidata, unislug;
    int unil;
    //for slug
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    List<YourHousesProfileObject> data = new ArrayList<YourHousesProfileObject>();
    List<String> mStrings = new ArrayList<String>();

    public List<ImageObject> getSelectedImages() {
        return selectedImages;
    }

    public void setSelectedImages(List<ImageObject> selectedImages) {
        this.selectedImages = selectedImages;
    }

    public List<ImageObject> selectedImages = new ArrayList<ImageObject>();
    String randomCode;
    LinearLayout ll_select_house;
    RequestQueue uploadrequestQueue;
    ImageView im_profile_image;
    Draft_DatabaseHandler db;
    int images_count = 0;
    public int getImages_count() {
        return images_count;
    }

    public void setImages_count(int images_count) {
        this.images_count = images_count;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinchapost_inhouse);
        uploadrequestQueue = Volley.newRequestQueue(getApplicationContext());
        randomCode = getRandomString();
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
        house_name = getIntent().getStringExtra("housename");
        tv_char_left = (TextView) findViewById(R.id.tv_char_left);
        et_question = (EditText) findViewById(R.id.et_ques);
        et_house_id = (EditText) findViewById(R.id.et_choose_house);
        et_content = (MonitoringEditTextPostInHouse) findViewById(R.id.et_ques_desc);
        image_close = (ImageView) findViewById(R.id.post_cross_img);
        btnAddPhots = (Button) findViewById(R.id.btnAddPhots);
        bt_ask_ques = (Button) findViewById(R.id.button_ask_now);
        im_profile_image = (ImageView) findViewById(R.id.post_acc_img);
        ll_house_name_inhouse = (LinearLayout) findViewById(R.id.ll_house_name_inhouse);
        tv_house_name_inhouse = (TextView) findViewById(R.id.tv_house_name_inhouse);
        houses = (AutoCompleteTextView) findViewById(R.id.et_choose_house);
        ll_select_house = (LinearLayout) findViewById(R.id.ll_select_house);
        preview_title = (TextView) findViewById(R.id.preview_title);
        preview_desc = (TextView) findViewById(R.id.preview_desc);
        preview_image = (ImageView) findViewById(R.id.preview_image);
        preview_close = (ImageView) findViewById(R.id.preview_close);
        preview_cardview = (CardView) findViewById(R.id.preview_cardview);
        preview_cardview.setVisibility(View.GONE);
        View grayline5 = (View) findViewById(R.id.grayline5);

        ll_select_house.setVisibility(View.GONE);
        ll_house_name_inhouse.setVisibility(View.VISIBLE);
        tv_house_name_inhouse.setText(house_name);
        Picasso.with(this)
                .load(user_image)
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(im_profile_image);
        TextView pinchTextStyle = (TextView) findViewById(R.id.pinchInText);
        pinchTextStyle.setTypeface(custom_font1);

        et_question.setTypeface(custom_font);
        et_content.setTypeface(custom_font1);
        et_house_id.setTypeface(custom_font1);
        preview_title.setTypeface(custom_font1);
        preview_desc.setTypeface(custom_font1);

        btnAddPhots.setOnClickListener(this);


        SharedPreferences sp1 = getSharedPreferences("draft_post",
                Activity.MODE_PRIVATE);
        if (sp1.getBoolean("draft", false)) {
            List<String> data = db.getAllDraftPostData();
            if (data.size() > 0) {
                try {
                    et_question.setText(data.get(0));
                    et_content.setText(data.get(3));
                }catch (Exception e){
                    Log.e("Exception","Pinch Post in house"+e.toString());
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(PinchAPostInHouse.this, e.toString());
                }
            }
        }

        preview_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview_cardview.setVisibility(View.GONE);
                text="";
                desc="";
                imageUrl="";
                url="";
            }
        });

        bt_ask_ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = et_content.getText().toString();
                question = et_question.getText().toString();
                if (check_pref()) {
                    pinch_a_post();
                }
            }
        });

        houses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                unidata = houses.getText().toString();
                Log.e("universitydata", "" + unidata);
                for (int i = 0; i < data.size(); i++) {
                    YourHousesProfileObject current = data.get(i);
                    if (current.getYourHouseName().contentEquals(unidata))
                        house_id = current.getId();
                }
                /*unislug = toSlug(unidata);
                Log.e("unislug", "" + unislug);
                unil = unidata.length();*/
            }
        });
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

    public void setPreviewPostInHouse(final String url) {
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
                            error_global.initializeVar(PinchAPostInHouse.this, e.toString());
                        }
                        String[] strings = new String[data.size()];
                        strings = mStrings.toArray(strings);
                        ArrayAdapter ar_obj1 = new ArrayAdapter<String>(PinchAPostInHouse.this, android.R.layout.select_dialog_item, strings);
                        houses.setAdapter(ar_obj1);
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
        if (resultcode != RESULT_CANCELED) {
            switch (requestcode) {
                case CustomGallerySelectId:
                    String imagesArray = imagereturnintent.getStringExtra(CustomGalleryIntentKey);//get Intent data
                    //Convert string array into List by splitting by ',' and substring after '[' and before ']'
                    List<String> images = new ArrayList<String>(Arrays.asList(imagesArray.substring(1, imagesArray.length() - 1).split(", ")));
                    List<ImageObject> newselectedImages = new ArrayList<ImageObject>();
                    for (int i = 0; i < images.size(); i++) {
                        ImageObject current = new ImageObject();
                        current.setImage_url(images.get(i));
                        current.setImage_id("0");
                        newselectedImages.add(current);
                    }
                    for (int i = 0; i < newselectedImages.size(); i++) {
                        //Bitmap bm = BitmapFactory.decodeFile("file://" + selectedImages.get(i));
                        Bitmap bm = BitmapFactory.decodeFile(newselectedImages.get(i).getImage_url());
                        ExifInterface exif = null;
                        try {
                            exif = new ExifInterface(newselectedImages.get(i).getImage_url());
                            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_NORMAL);
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

                            }
                            Bitmap bmRotated = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                            //bm.recycle();
                            bm = bmRotated;
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(PinchAPostInHouse.this, e.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(PinchAPostInHouse.this, e.toString());
                        }
                        Bitmap resizedBitmap = null;
                        int scaleSize = 800;
                        int originalWidth = bm.getWidth();
                        int originalHeight = bm.getHeight();

                        double newWidth = 1 ;
                        double newHeight =1 ;
                        if (originalWidth > scaleSize){

                            newWidth = scaleSize;
                            newHeight = (newWidth*originalHeight/originalWidth);
                        }
                        else{

                            newWidth = originalWidth;
                            newHeight = originalHeight;
                        }

                        resizedBitmap = Bitmap.createScaledBitmap(bm, (int)newWidth, (int)newHeight, true);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();


                        if (resizedBitmap != null)
                            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();
                        base64Image = Base64.encodeToString(b, Base64.DEFAULT);
                        base64Image = "data:image/jpeg;base64," + base64Image;
                        //Log.e("Base64 image Post", base64Image);
                        upload_image(base64Image, selectedImages.size() + i);
                    }
                    if (selectedImages.size() >= 0) {
                        setVisibilityGone();
                    } else {
                        setVisibilityVisible();
                    }
                    selectedImages.addAll(selectedImages.size(), newselectedImages);
                    loadGridView(new ArrayList<ImageObject>(selectedImages));//call load gridview method by passing converted list into arrayList
                    if (!selectedImages.isEmpty()) {
                        selectedImageGridView.setNumColumns(2);
                    }

                    break;

            }
        }
    }

    String ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    String getRandomString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(13);
        for (int i = 0; i < 6; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        sb.append('-');
        for (int i = 0; i < 6; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private void upload_image(final String base64Image, final int position) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_IMAGES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("upload image response", "" + response);
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(response);
                            int id = jsonObj.getInt("id");
                            String imageUrl = jsonObj.getString("image");
                            int position = jsonObj.getInt("position");
                            if (true) {
                                //selectedImages.set(position, imageUrl);
                                selectedImages.get(images_count).setImage_url(imageUrl);
                                selectedImages.get(images_count).setImage_id(id + "");
                                images_count++;
                                if (selectedImages.size() == images_count) {
                                    setVisibilityVisible();
                                } else {
                                    setVisibilityGone();
                                }
                                loadGridView(new ArrayList<ImageObject>(selectedImages));
                                Toast.makeText(getApplicationContext(), "Image " + (position + 1) + " Uploaded", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Image " + (position + 1) + " Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(PinchAPostInHouse.this, e.toString());
                        }


                        // looping through All Contacts

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("upload image response", "" + error);
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
                questionHash.put("story_file", base64Image);
                questionHash.put("tempid", randomCode);
                questionHash.put("position", String.valueOf(position));
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("upload", String.valueOf(new JSONObject(questionHash)));

                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }


            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                1200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag("upload_image");
        uploadrequestQueue.add(stringRequest);
    }

    public void setVisibilityVisible() {
        //bt_ask_ques.setVisibility(View.VISIBLE);
        bt_ask_ques.setBackgroundResource(R.drawable.pinchbuttonpost);
        bt_ask_ques.setClickable(true);
    }

    public void setVisibilityGone() {
        //bt_ask_ques.setVisibility(View.INVISIBLE);
        bt_ask_ques.setBackgroundResource(R.drawable.pinchbutton_faded);
        bt_ask_ques.setClickable(false);
    }

    private void delete_image(final String base64Image, final int position) {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, DELETE_UPLOADING_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("upload image response", "" + response);
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(response);
                            int id = jsonObj.getInt("id");
                            String imageUrl = jsonObj.getString("image");
                            int position = jsonObj.getInt("position");
                            if (true) {
                                //selectedImages.set(position, imageUrl);
                                selectedImages.get(position).setImage_url(imageUrl);
                                //loadGridView(new ArrayList<String>(selectedImages));
                                Toast.makeText(getApplicationContext(), "Image " + (position + 1) + " Uploaded", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Image " + (position + 1) + " Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(PinchAPostInHouse.this, e.toString());
                        }


                        // looping through All Contacts

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("upload image response", "" + error);
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Id", randomCode);
                return headers;
            }

            /*@Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {

                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("story_file", base64Image);
                questionHash.put("tempid", randomCode);
                questionHash.put("position", String.valueOf(position));
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("upload", String.valueOf(new JSONObject(questionHash)));

                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }*/


            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                1200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public Boolean check_pref() {

        if (!isHouseProfileActivity.contentEquals("true") && houses.getText().toString().trim().length() == 0) {
            houses.setError("*Choose Club");
            return false;
        }
        if (et_content.getText().toString().trim().length() == 0) {
            et_content.setError("*Write Something");
            return false;
        }
        return true;
    }

    private void pinch_a_post() {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_STORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("POST STORY RESPONSE", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            status = jsonObj.getString("status");
                            if (status.contentEquals("success")) {
                                post_success = 1;
                                //Toast.makeText(getApplicationContext(), "Post pinched successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(getApplicationContext(), "Post Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(PinchAPostInHouse.this, e.toString());
                        }

                        // looping through All Contacts


                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("POST STORY RESPONSE", "" + error);
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
                questionHash.put("title", question);
                questionHash.put("club_id", house_id);
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("temp_id", randomCode);
                questionHash.put("description", content);
                questionHash.put("preview_title", text);
                questionHash.put("preview_description", desc);
                questionHash.put("preview_image", imageUrl);
                questionHash.put("preview_link", url);
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("story", String.valueOf(new JSONObject(questionHash)));

                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }


            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                1200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Pinching your Post...");
        progressDialog.show();


    }

    //Load GridView
    private void loadGridView(ArrayList<ImageObject> imagesArray) {
        GridView_Adapter_PostInHouse adapter = new GridView_Adapter_PostInHouse(this, imagesArray, false, 0);
        selectedImageGridView.setAdapter(adapter);

    }

    //Read Shared Images
    private void getSharedImages() {

        //If Intent Action equals then proceed
        if (Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())
                && getIntent().hasExtra(Intent.EXTRA_STREAM)) {
            ArrayList<Parcelable> list =
                    getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);//get Parcelabe list
            ArrayList<ImageObject> selectedImages = new ArrayList<>();

            //Loop to all parcelable list
            for (Parcelable parcel : list) {
                Uri uri = (Uri) parcel;//get URI
                String sourcepath = getPath(uri);//Get Path of URI
                ImageObject current = new ImageObject();
                current.setImage_url(sourcepath);
                selectedImages.add(current);//add images to arraylist
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
                    if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (shouldShowRequestPermissionRationale(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // Explain to the user why we need to read the contacts
                        }
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                        // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                        // app-defined int constant
                        return;
                    } else {
                        Intent intent = new Intent(this, CustomGallery_Activity_Post.class);
                        intent.putExtra("selectedimagescount", selectedImages.size());
                        startActivityForResult(intent, CustomGallerySelectId);
                    }
                } else {
                    Intent intent = new Intent(this, CustomGallery_Activity_Post.class);
                    intent.putExtra("selectedimagescount", selectedImages.size());
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
                    Intent intent = new Intent(this, CustomGallery_Activity_Post.class);
                    intent.putExtra("selectedimagescount", selectedImages.size());
                    startActivityForResult(intent, CustomGallerySelectId);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar snack = Snackbar.make(parent, "Allow to Upload Images", Snackbar.LENGTH_LONG);
                    snack.setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_purple
                            )).show();

                    View view = snack.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


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
                                uploadrequestQueue.cancelAll("upload_image");
                                overridePendingTransition(R.anim.do_nothing,
                                        R.anim.top_to_bottom);
                                finish();
                                SharedPreferences prefs = getSharedPreferences("draft_post", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("draft", false);
                                editor.commit();
                                db.removeAllPost();
                            }

                        });
                        builder.setNegativeButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadrequestQueue.cancelAll("upload_image");
                                overridePendingTransition(R.anim.do_nothing,
                                        R.anim.top_to_bottom);
                                finish();
                                content = et_content.getText().toString();
                                question = et_question.getText().toString();
                                db.addPostData(question, content, "", "", "0");
                                SharedPreferences prefs = getSharedPreferences("draft_post", Context.MODE_PRIVATE);
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
                                uploadrequestQueue.cancelAll("upload_image");
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
            uploadrequestQueue.cancelAll("upload_image");
            overridePendingTransition(R.anim.do_nothing,
                    R.anim.top_to_bottom);

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);

            finish();
        }

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
                error_global.initializeVar(PinchAPostInHouse.this, e.toString());
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