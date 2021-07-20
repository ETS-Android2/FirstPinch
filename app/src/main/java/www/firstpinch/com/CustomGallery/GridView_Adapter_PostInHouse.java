package www.firstpinch.com.firstpinch2.CustomGallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.PinchAPost;
import www.firstpinch.com.firstpinch2.PinchAPostInHouse;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 28-11-2016.
 */

//adapter for pinching a post from house page
public class GridView_Adapter_PostInHouse extends BaseAdapter {
    private Context context;
    private ArrayList<ImageObject> imageUrls;
    private SparseBooleanArray mSparseBooleanArray;//Variable to store selected Images
    private DisplayImageOptions options;
    private boolean isCustomGalleryActivity;//Variable to check if gridview is to setup for Custom Gallery or not
    String randomCode, UPLOAD_IMAGES_URL = "http://54.169.84.123/api/story/story_image", base64Image = "",
            DELETE_IMAGE = "http://54.169.84.123/api//story/contentdelete/1";
    Boolean status;
    int check_interger;
    int image_count=0;
    List<ImageObject> selectedImagesss = new ArrayList<ImageObject>();

    public GridView_Adapter_PostInHouse(Context context, ArrayList<ImageObject> imageUrls, boolean isCustomGalleryActivity, int check_integer) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.isCustomGalleryActivity = isCustomGalleryActivity;
        this.check_interger = check_integer;
        mSparseBooleanArray = new SparseBooleanArray();
        if (!isCustomGalleryActivity) {
            randomCode = getRandomString();
        }


        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .resetViewBeforeLoading(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    //Method to return selected Images
    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for (int i = 0; i < imageUrls.size(); i++) {
            if (mSparseBooleanArray.get(i)) {
                //ImageObject current = new ImageObject();
                //current.setImage_url(imageUrls.get(i).getImage_url());
                mTempArry.add(imageUrls.get(i).getImage_url());
            }
        }

        return mTempArry;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int i) {
        return imageUrls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            view = inflater.inflate(R.layout.customgridview_selected_item, viewGroup, false);//Inflate layout

        } else {
            view = convertView;
        }


        final CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.selectCheckBox);
        ImageView dark_back = (ImageView) view.findViewById(R.id.de_selected_image_dark);
        final ImageView close_btn_image = (ImageView) view.findViewById(R.id.de_selected_image_close_btn);
        ImageView image = (ImageView) view.findViewById(R.id.galleryImageView);
        final TextView tv_uploading = (TextView) view.findViewById(R.id.tv_uploading);
        final ImageView imageView = (ImageView) view.findViewById(R.id.galleryImageView);
        //If Context is MainActivity then hide checkbox
        if (!isCustomGalleryActivity) {
            mCheckBox.setVisibility(View.GONE);
            close_btn_image.setVisibility(View.VISIBLE);
            dark_back.setVisibility(View.VISIBLE);
            /*Bitmap bm = BitmapFactory.decodeFile(imageUrls.get(position));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            base64Image = Base64.encodeToString(b, Base64.DEFAULT);
            base64Image = "data:image/jpeg;base64," + base64Image;*/
            //upload_image(base64Image,position);
        } else {
            mCheckBox.setVisibility(View.VISIBLE);
            close_btn_image.setVisibility(View.GONE);
            dark_back.setVisibility(View.GONE);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckBox.setChecked(mSparseBooleanArray.get(position));
                    mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
                }
            });
        }

        close_btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_image(imageUrls.get(position).getImage_id(), position);
            }
        });

        if (check_interger == 1) {
            ImageLoader.getInstance().displayImage("file://" + imageUrls.get(position).getImage_url(), imageView, options);//Load Images over ImageView
        } else {
            tv_uploading.setVisibility(View.VISIBLE);
            tv_uploading.setText("Uploading");
            close_btn_image.setVisibility(View.GONE);
            Picasso.with(context)
                    .load(imageUrls.get(position).getImage_url())
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageView, new Callback() {

                        @Override
                        public void onSuccess() {
                            tv_uploading.setVisibility(View.GONE);
                            close_btn_image.setVisibility(View.VISIBLE);
                            tv_uploading.setText("Uploading");
                        }

                        @Override
                        public void onError() {
                            tv_uploading.setVisibility(View.VISIBLE);
                            close_btn_image.setVisibility(View.GONE);
                            tv_uploading.setText("Uploading");
                        }
                    });

        }

        mCheckBox.setTag(position);//Set Tag for CheckBox
        mCheckBox.setChecked(mSparseBooleanArray.get(position));
        mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        return view;
    }

    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);//Insert selected checkbox value inside boolean array
            ((CustomGallery_Activity_Post) context).showSelectButton();//call custom gallery activity method
        }
    };

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

    private void delete_image(final String id, final int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                DELETE_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("delt image", "data from server - " + response);
                        if (response != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                String status = jsonObj.getString("result");
                                if (status.contentEquals("true")) {
                                    try{
                                    Log.e("delted image", "" + imageUrls.get(position).getImage_id());
                                    imageUrls.remove(position);
                                    selectedImagesss = ((PinchAPostInHouse) context).getSelectedImages();
                                    selectedImagesss.remove(position);
                                    ((PinchAPostInHouse) context).setSelectedImages(selectedImagesss);
                                    image_count = ((PinchAPostInHouse) context).getImages_count();
                                    image_count--;
                                    if(selectedImagesss.size()==image_count){
                                        ((PinchAPostInHouse) context).setVisibilityVisible();
                                    }else{
                                        ((PinchAPostInHouse) context).setVisibilityGone();
                                    }
                                    ((PinchAPostInHouse) context).setImages_count(image_count);
                                    notifyDataSetChanged();
                                    }catch(Exception e){
                                        Log.e("GridViewAdapterPost", "InHouse" + e.toString());
                                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                                        error_global.initializeVar(context, e.toString());
                                    }
                                } else {
                                    Toast.makeText(context, "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                                }

                            } catch (final JSONException e) {
                                Log.e("YourHousesProfileAdpter", "Json parsing error: " + e.getMessage());
                                /*Toast.makeText(context,
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();*/
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(context, e.toString());
                            }
                        } else {
                            Log.e("YourHousesProfileAdpter", "Couldn't get json from server.");
                            Toast.makeText(context,
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
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Id", id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void upload_image(final String base64Image, final int position) {

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_IMAGES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("upload image response", "" + response);
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(response);
                            status = jsonObj.getBoolean("result");
                            if (status) {
                                Toast.makeText(context, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                //imageUrls.get(pos)= "image url";
                                //notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(context, e.toString());
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
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

}