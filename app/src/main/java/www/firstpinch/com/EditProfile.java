package www.firstpinch.com.firstpinch2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.MainFeed.RoundedCornersTransform;

/**
 * Created by Rianaa Admin on 04-10-2016.
 */

public class EditProfile extends AppCompatActivity {

    String POST_EDIT_PROFILE = "http://54.169.84.123/api//user_profile/profileSave";
    Toolbar toolbar;
    String name, username, image_url, cover_image_url, userChoosenTask, user_bio;
    String picturePath, type;
    File finalFile;
    EditText et_profileName, et_profileBio;
    int SELECT_FILE = 10, REQUEST_CAMERA = 20, RESULT_CROP = 30, SELECT_FILE_COVER = 11, REQUEST_CAMERA_COVER = 12, CROP_CAMERA_COVER = 13,
            CROP_GALLERY_COVER = 14, CROP_CAMERA_PROFILE = 15, CROP_GALLERY_PROFILE = 16;
    ImageView im_profile_image, im_cover_image, image_close, cover_bg, profile_bg, cover_bg_icon, profile_bg_icon, im_update_check;
    Button bt_save, bt_cancel;
    TextView tv_char_left;
    int user_id;
    ProgressDialog progressDialog;
    Bitmap bmm;

    String saved_name = "", saved_bio = "", saved_base64image = "";
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);
        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");*/
        user_id = getIntent().getIntExtra("user_id", 0);
        name = getIntent().getStringExtra("profilename");
        username = getIntent().getStringExtra("username");
        image_url = getIntent().getStringExtra("imageurl");
        cover_image_url = getIntent().getStringExtra("coverimg");
        user_bio = getIntent().getStringExtra("bio");

        im_profile_image = (ImageView) findViewById(R.id.edit_profile_img);
        im_cover_image = (ImageView) findViewById(R.id.edit_cover_image);
        image_close = (ImageView) findViewById(R.id.post_cross_img);
        et_profileName = (EditText) findViewById(R.id.edit_profilename);
        et_profileBio = (EditText) findViewById(R.id.edit_profile_bio);
        cover_bg = (ImageView) findViewById(R.id.cover_bg);
        profile_bg = (ImageView) findViewById(R.id.profile_bg);
        cover_bg_icon = (ImageView) findViewById(R.id.cover_change_icon);
        profile_bg_icon = (ImageView) findViewById(R.id.profile_bg_change_icon);
        im_update_check = (ImageView) findViewById(R.id.edit_update_img);
        bt_save = (Button) findViewById(R.id.bt_save);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        tv_char_left = (TextView) findViewById(R.id.tv_char_left);
        et_profileName.setText(name);
        if (user_bio.contentEquals("null")) {
            et_profileBio.setHint("Add your bio here...");
            et_profileBio.setHintTextColor(0xFF9E9E9E);
            et_profileBio.setTextColor(0xFF212121);
        } else {
            et_profileBio.setText(user_bio);
            et_profileBio.setTextColor(0xFF212121);
        }
        image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Picasso.with(this)
                .load(image_url)
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(im_profile_image);
        Picasso.with(this)
                .load(cover_image_url)
                .placeholder(R.drawable.placeholder_image)
                .into(im_cover_image);
        im_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "profile";
                selectImage();
            }
        });
        /*im_cover_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "cover";
                selectCoverImage();
            }
        });*/
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        im_update_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saved_name = et_profileName.getText().toString();
                saved_bio = et_profileBio.getText().toString();
                if (et_profileBio.length() > 150) {
                    tv_char_left.setText("limit Exceeded!");
                    tv_char_left.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Text Limit Exceeded in Bio", Toast.LENGTH_SHORT).show();
                } else {
                    edit_profile_post();
                }
                //finish();
            }
        });
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            }
        });
        et_profileBio.addTextChangedListener(new TextWatcher() {

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

                int len = et_profileBio.length();
                tv_char_left.setText(150 - len + " char. left");
                if (150 - len < 0) {
                    tv_char_left.setText("limit Exceeded!");
                    tv_char_left.setTextColor(Color.RED);
                } else
                    tv_char_left.setTextColor(Color.GRAY);
            }
        });
    }

    private void edit_profile_post() {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_EDIT_PROFILE,
                new Response.Listener<String>() {
                    SharedPreferences pref3 = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref3.edit();

                    @Override
                    public void onResponse(String response) {
                        Log.e("POST STORY RESPONSE", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            JSONObject obj = jsonObj.getJSONObject("profile");
                            String status = obj.getString("status");
                            int id = obj.getInt("id");
                            String uname = obj.getString("uname");
                            String nam = obj.getString("name");
                            String image = obj.getString("image");
                            editor.putInt("id", id);
                            editor.putString("uname", uname);
                            editor.putString("name", nam);
                            editor.putString("image", image);
                            editor.commit();
                            if (status.contentEquals("1")) {
                                //Toast.makeText(getApplicationContext(), "Post Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(getApplicationContext(), "Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(EditProfile.this, e.toString());
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
                questionHash.put("name", saved_name);
                questionHash.put("bio", saved_bio);
                questionHash.put("profile_pic", saved_base64image);
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("user_details", String.valueOf(new JSONObject(questionHash)));

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
        progressDialog.setMessage("Updating Profile...");
        progressDialog.show();


    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(EditProfile.this);
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
        builder.show();
    }

    private void selectCoverImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(EditProfile.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraCoverIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryCoverIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraCoverIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA_COVER);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void galleryCoverIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE_COVER);
    }

    public static class Utility {
        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static boolean checkPermission(final Context context) {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == SELECT_FILE_COVER) {
                onSelectFromGalleryResultCover(data);
            } else if (requestCode == REQUEST_CAMERA) {

                Bitmap thumbnail = null;
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(EditProfile.this, e.toString());
                }
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(getRealPathFromURI(imageUri));
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
                    thumbnail = bmRotated;
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(EditProfile.this, e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(EditProfile.this, e.toString());
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(EditProfile.this, e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(EditProfile.this, e.toString());
                }
                Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", destination);
                Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(uri, outputUri).asSquare().start(this);
                    /*base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    base64Image = "data:image/jpeg;base64," + base64Image;
                    Log.e("imageUrl","-"+getRealPathFromURI(imageUri));*/
                //onCaptureImageResult(data);
            } else if (requestCode == REQUEST_CAMERA_COVER) {
                onCaptureImageResultCover(data);
            } else if (requestCode == CROP_CAMERA_COVER) {
                //Bitmap bt = getIntent().getParcelableExtra("data");
                /*Bundle extras = data.getExtras();
                Bitmap bt = (Bitmap) extras.get("data");*/
                Uri selectedImage = data.getData();
                /*bt_save.setVisibility(View.VISIBLE);
                bt_cancel.setVisibility(View.VISIBLE);*/
                cover_bg.setVisibility(View.GONE);
                cover_bg_icon.setVisibility(View.GONE);
                im_cover_image.setImageURI(selectedImage);

            } else if (requestCode == CROP_GALLERY_COVER) {

                Uri selectedImage = data.getData();
                /*bt_save.setVisibility(View.VISIBLE);
                bt_cancel.setVisibility(View.VISIBLE);*/
                cover_bg.setVisibility(View.GONE);
                cover_bg_icon.setVisibility(View.GONE);
                im_cover_image.setImageURI(selectedImage);

            } else if (requestCode == CROP_CAMERA_PROFILE) {

                Uri selectedImage = data.getData();
                /*bt_save.setVisibility(View.VISIBLE);
                bt_cancel.setVisibility(View.VISIBLE);*/
                profile_bg.setVisibility(View.GONE);
                profile_bg_icon.setVisibility(View.GONE);
                im_profile_image.setImageURI(selectedImage);

            } else if (requestCode == CROP_GALLERY_PROFILE) {

                Uri selectedImage = data.getData();
                /*bt_save.setVisibility(View.VISIBLE);
                bt_cancel.setVisibility(View.VISIBLE);*/
                profile_bg.setVisibility(View.GONE);
                profile_bg_icon.setVisibility(View.GONE);
                im_profile_image.setImageURI(selectedImage);

            } else if (requestCode == Crop.REQUEST_CROP) {
                if (resultCode == RESULT_OK) {
                    profile_bg.setVisibility(View.GONE);
                    profile_bg_icon.setVisibility(View.GONE);
                    Uri testuri = Crop.getOutput(data);
                    try {
                        bmm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), testuri);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(EditProfile.this, e.toString());
                    }
                    im_profile_image.setImageBitmap(bmm);
                    Bitmap resizedBitmap = null;
                    int scaleSize = 800;
                    int originalWidth = bmm.getWidth();
                    int originalHeight = bmm.getHeight();

                    double newWidth = 1;
                    double newHeight = 1;
                    if (originalWidth > scaleSize) {

                        newWidth = scaleSize;
                        newHeight = (newWidth * originalHeight / originalWidth);
                    } else {

                        newWidth = originalWidth;
                        newHeight = originalHeight;
                    }

                    resizedBitmap = Bitmap.createScaledBitmap(bmm, (int) newWidth, (int) newHeight, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();


                    if (resizedBitmap != null)
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    saved_base64image = Base64.encodeToString(b, Base64.DEFAULT);
                    saved_base64image = "data:image/jpeg;base64," + saved_base64image;
                    im_profile_image.setClickable(false);
                } else if (resultCode == Crop.RESULT_ERROR) {
                    Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

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
                e.printStackTrace();
                Error_GlobalVariables error_global = new Error_GlobalVariables();
                error_global.initializeVar(EditProfile.this, e.toString());
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


            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            Bitmap resizedBitmap = null;
            int scaleSize = 800;
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

            if (resizedBitmap != null)
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes); //bm is the bitmap object
            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");

            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Error_GlobalVariables error_global = new Error_GlobalVariables();
                error_global.initializeVar(EditProfile.this, e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Error_GlobalVariables error_global = new Error_GlobalVariables();
                error_global.initializeVar(EditProfile.this, e.toString());
            }

            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", destination);
            Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(uri, outputUri).asSquare().start(this);


        /*Intent cropIntent = new Intent("com.android.camera.action.CROP");
        // indicate image type and Uri
        cropIntent.setDataAndType(uri, "image*//*");
        // set crop properties
        cropIntent.putExtra("crop", "true");
        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        // indicate output X and Y
        cropIntent.putExtra("outputX", 128);
        cropIntent.putExtra("outputY", 128);

        // retrieve data on return
        cropIntent.putExtra("return-data", true);
        // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_GALLERY_PROFILE);*/
            //im_profile_image.setImageBitmap(bm);
        }

    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bt = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bt.compress(Bitmap.CompressFormat.PNG, 100, bytes);


        /*Intent intent1 = new Intent(this, CropImageActivity.class);
        intent1.putExtra("bitmap", thumbnail);
        intent1.putExtra("crop", "true");
        intent1.putExtra("aspectX", 0);
        intent1.putExtra("aspectY", 0);
        intent1.putExtra("outputX", 200);
        intent1.putExtra("outputY", 150);
        startActivityForResult(intent1, 1);*/

        /*Intent cropIntent = new Intent("com.android.camera.action.CROP");
        // indicate image type and Uri
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", destination);
        cropIntent.setDataAndType(uri, "image*//*");
        // set crop properties
        cropIntent.putExtra("crop", "true");
        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        // indicate output X and Y
        cropIntent.putExtra("outputX", 128);
        cropIntent.putExtra("outputY", 128);

        // retrieve data on return
        cropIntent.putExtra("return-data", true);
        // start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, CROP_CAMERA_PROFILE);*/

    }

    private void onCaptureImageResultCover(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(EditProfile.this, e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(EditProfile.this, e.toString());
        }
        /*Intent intent1 = new Intent(this, CropImageActivity.class);
        intent1.putExtra("bitmap", thumbnail);
        intent1.putExtra("crop", "true");
        intent1.putExtra("aspectX", 0);
        intent1.putExtra("aspectY", 0);
        intent1.putExtra("outputX", 200);
        intent1.putExtra("outputY", 150);
        startActivityForResult(intent1, 1);*/
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        // indicate image type and Uri
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", destination);
        cropIntent.setDataAndType(uri, "image/*");
        // set crop properties
        cropIntent.putExtra("crop", "true");
        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        // indicate output X and Y
        cropIntent.putExtra("outputX", 128);
        cropIntent.putExtra("outputY", 128);

        // retrieve data on return
        cropIntent.putExtra("return-data", true);
        // start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, CROP_CAMERA_COVER);

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResultCover(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                //picturePath = data.getStringExtra("picturePath");
                /*String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, null, null, null);
                int column_index_data = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToLast();

                picturePath = cursor.getString(column_index_data);*/

            } catch (IOException e) {
                e.printStackTrace();
                Error_GlobalVariables error_global = new Error_GlobalVariables();
                error_global.initializeVar(EditProfile.this, e.toString());
            }
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(EditProfile.this, e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(EditProfile.this, e.toString());
        }
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        // indicate image type and Uri
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", destination);
        cropIntent.setDataAndType(uri, "image/*");
        // set crop properties
        cropIntent.putExtra("crop", "true");
        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        // indicate output X and Y
        cropIntent.putExtra("outputX", 128);
        cropIntent.putExtra("outputY", 128);

        // retrieve data on return
        cropIntent.putExtra("return-data", true);
        // start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, CROP_GALLERY_COVER);
        //im_profile_image.setImageBitmap(bm);
    }


}
