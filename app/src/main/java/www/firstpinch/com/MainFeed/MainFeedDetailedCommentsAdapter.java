package www.firstpinch.com.firstpinch2.MainFeed;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.ImageViewActivity;
import www.firstpinch.com.firstpinch2.ProfilePages.ProfileActivity;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 14-09-2016.
 */

//adapter for responses/answers in detailedActivity
public class MainFeedDetailedCommentsAdapter extends RecyclerView.Adapter<MainFeedDetailedCommentsAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<MainFeedDetailedCommentsObject> data = Collections.emptyList();
    String POST_ANSWER_DELETE_COMMENT_URL = "http://54.169.84.123/api/answers/1",
            POST_RESPONSE_DELETE_COMMENT_URL = "http://54.169.84.123/api/story_comments/1",
            PUT_QUESTION_APPRECIATE_URL = "http://54.169.84.123/api/answers/1/like",
            PUT_QUESTION_NOT_APPRECIATE_URL = "http://54.169.84.123/api/answers/1/dislike",
            PUT_RESPONSE_APPRECIATE_URL = "http://54.169.84.123/api/story_comments/1/like",
            PUT_RESPONSE_NOT_APPRECIATE_URL = "http://54.169.84.123/api/story_comments/1/dislike";
    Context c;
    String uname;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1, user_id;

    public MainFeedDetailedCommentsAdapter(Context context, List<MainFeedDetailedCommentsObject> data) {
        inflator = LayoutInflater.from(context);
        this.data = data;
        c = context;
        SharedPreferences sp = c.getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        uname = sp.getString("uname", "");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.mainfeed_detailed_comments_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final MainFeedDetailedCommentsObject current = data.get(position);
        holder.comm_tv_profileName.setText(current.getComment_profileName());
        holder.comm_tv_profileUsername.setText("@"+current.getComment_profileUsername());

        //Log.e("comment", current.getComment_title());
        CharSequence sequence = Html.fromHtml(current.getComment_title());
        //make tags and links clickable
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        holder.comm_tv_title.setText(strBuilder);
        holder.comm_tv_title.setMovementMethod(LinkMovementMethod.getInstance());

        //holder.comm_tv_rating.setText("" + current.getComment_rating());
        holder.comm_tv_appreciation_count.setText(("" + current.getComment_rating()));

        if (uname.contentEquals(current.getComment_profileUsername())) {
            holder.comm_tv_delete.setVisibility(View.VISIBLE);
        } else {
            holder.comm_tv_delete.setVisibility(View.GONE);
        }

        if (current.getComment_imageUrl().contentEquals("/default_club.jpg")) {
            holder.comm_im_imageUrl.setVisibility(View.GONE);
        } else {
            holder.comm_im_imageUrl.setVisibility(View.VISIBLE);
            Picasso.with(c)
                    .load(current.getComment_imageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.comm_im_imageUrl);
        }
        Picasso.with(c)
                .load(current.getComment_profileImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(holder.comm_im_profileImageUrl);

        /*if (position == data.size() - 1) {
            holder.itemView.setPadding(0, 0, 0, 40);
        }*/

        if (current.getComment_rating_status() == 0) {
            holder.bt_comm_tv_appreciate.setBackgroundResource(R.drawable.star_unfilled);
        } else {
            holder.bt_comm_tv_appreciate.setBackgroundResource(R.drawable.star_filled);
        }


        //setFadeAnimation(holder.itemView);
        //setAnimation(holder.itemView, position);
        //animate(holder);
        //Log.e("text=",""+current.getHouseName());


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position, List<Object> payloads) {

        if (payloads.isEmpty()) {
            // Perform a full update
            onBindViewHolder(holder, position);
        } else {
            // Perform a partial update
            final MainFeedDetailedCommentsObject current = data.get(position);
            if (current.getComment_rating_status() == 0) {
                holder.bt_comm_tv_appreciate.setBackgroundResource(R.drawable.star_unfilled);
            } else {
                holder.bt_comm_tv_appreciate.setBackgroundResource(R.drawable.star_filled);
            }
            holder.comm_tv_appreciation_count.setText(("" + current.getComment_rating()));

        }

    }

    public List<MainFeedDetailedCommentsObject> getList() {

        return data;
    }

    //make links in response/answer clickable
    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                List<String> arr = new ArrayList<String>(Arrays.asList(span.getURL().substring(1, span.getURL().length() - 1).split("/")));
                String str = arr.get(1);
                Log.e("on click username", str);
                Intent intent;
                intent = new Intent(c, ProfileActivity.class);
                intent.putExtra("username", str);
                c.startActivity(intent);
                // Do something with span.getURL() to handle the link click...
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //to animate item(currently not used)
    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(c, R.anim.anticipateovershoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    //to animate item(currently not used)
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition && position != 0) {
            ScaleAnimation anim = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.2f, Animation.ZORDER_TOP, 1.0f, Animation.ZORDER_TOP, 1.0f);
            //anim.setDuration(new Random().nextInt(1000));//to make duration random number between [0,501)
            anim.setDuration(500);
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

    //to animate item(currently not used)
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);

    }

    //delete answer API request
    private void deleteAnswer(final String answer_id, final int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                POST_ANSWER_DELETE_COMMENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("delete comment", "" + response);
                        if (response != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                int status = jsonObj.getInt("status");
                                if (status == 1) {
                                    notifyItemRemoved(position);
                                    data.remove(position);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(c,
                                            "" + response,
                                            Toast.LENGTH_LONG)
                                            .show();
                                }

                            } catch (final JSONException e) {
                                Log.e("YourHousesProfileAdpter", "Json parsing error: " + e.getMessage());
                                /*Toast.makeText(c,
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();*/
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(c, e.toString());
                            }
                        } else {
                            Log.e("YourHousesProfileAdpter", "Couldn't get json from server.");
                            Toast.makeText(c,
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
                        Toast.makeText(c, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Answer-Id", answer_id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);

    }

    //delete response API request
    private void deleteResponse(final String response_id, final int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                POST_RESPONSE_DELETE_COMMENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("delete comment", "" + response);
                        if (response != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                int status = jsonObj.getInt("status");
                                if (status == 1) {
                                    notifyItemRemoved(position);
                                    data.remove(position);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(c,
                                            "" + response,
                                            Toast.LENGTH_LONG)
                                            .show();
                                }

                            } catch (final JSONException e) {
                                Log.e("delete comment", "Json parsing error: " + e.getMessage());
                                /*Toast.makeText(c,
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();*/
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(c, e.toString());
                            }
                        } else {
                            Log.e("YourHousesProfileAdpter", "Couldn't get json from server.");
                            Toast.makeText(c,
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
                        Toast.makeText(c, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Comment-Id", response_id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);

    }

    //appreciate answer API request
    private void answer_appreciate(final int position) {

        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PUT_QUESTION_APPRECIATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("put ans appreciate", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("1")) {
                                //MainFeedDetailedCommentsObject current = data.get(position);
                                /*current.setComment_rating_status(1);
                                current.setComment_rating(current.getComment_rating() + 1);*/
                                //notifyItemChanged(position);
                            } else {
                                Toast.makeText(c, "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(c, e.toString());
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
                MainFeedDetailedCommentsObject current = data.get(position);
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("answer_id", current.getId());
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("likes", String.valueOf(new JSONObject(questionHash)));
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

    //remove appreciated answer API request
    private void answer_not_appreciate(final int position) {

        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PUT_QUESTION_NOT_APPRECIATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("put ques bkmrk response", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("1")) {
                                //MainFeedDetailedCommentsObject current = data.get(position);
                                /*current.setComment_rating_status(0);
                                current.setComment_rating(current.getComment_rating() - 1);*/
                                //notifyItemChanged(position);
                            } else {
                                Toast.makeText(c, "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(c, e.toString());
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
                MainFeedDetailedCommentsObject current = data.get(position);
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("answer_id", current.getId());
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("likes", String.valueOf(new JSONObject(questionHash)));
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

    //appreciate response API request
    private void response_appreciate(final int position) {

        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PUT_RESPONSE_APPRECIATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("put ans appreciate", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("1")) {
                                //MainFeedDetailedCommentsObject current = data.get(position);
                                /*current.setComment_rating_status(1);
                                current.setComment_rating(current.getComment_rating() + 1);*/
                                //notifyItemChanged(position);
                            } else {
                                Toast.makeText(c, "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(c, e.toString());
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
                MainFeedDetailedCommentsObject current = data.get(position);
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("comment_id", current.getId());
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("likes", String.valueOf(new JSONObject(questionHash)));
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

    //remove appreciate response API request
    private void response_not_appreciate(final int position) {

        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PUT_RESPONSE_NOT_APPRECIATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("put ques bkmrk response", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("1")) {
                                //MainFeedDetailedCommentsObject current = data.get(position);
                                /*current.setComment_rating_status(0);
                                current.setComment_rating(current.getComment_rating() - 1);*/
                                //notifyItemChanged(position);
                            } else {
                                Toast.makeText(c, "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(c, e.toString());
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
                MainFeedDetailedCommentsObject current = data.get(position);
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("comment_id", current.getId());
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("likes", String.valueOf(new JSONObject(questionHash)));
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView comm_im_profileImageUrl, comm_im_imageUrl,comm_tv_delete;
        TextView comm_tv_profileName, comm_tv_profileUsername, comm_tv_title,
                comm_tv_rating, comm_tv_appreciation_count;
        Button bt_comm_tv_appreciate;

        public MyViewHolder(final View itemView) {
            super(itemView);

            comm_tv_profileName = (TextView) itemView.findViewById(R.id.comm_profile_name);
            comm_tv_profileUsername = (TextView) itemView.findViewById(R.id.comm_username);
            comm_tv_title = (TextView) itemView.findViewById(R.id.comm_title);
            //comm_tv_rating = (TextView) itemView.findViewById(R.id.comm_rate_text);
            comm_im_profileImageUrl = (ImageView) itemView.findViewById(R.id.comm_profile_image);
            bt_comm_tv_appreciate = (Button) itemView.findViewById(R.id.bt_comm_appreciate);
            comm_tv_delete = (ImageView) itemView.findViewById(R.id.comm_delete2);
            comm_tv_appreciation_count = (TextView) itemView.findViewById(R.id.comm_appreciations_count);
            comm_im_imageUrl = (ImageView) itemView.findViewById(R.id.comm_image);
            Typeface custom_font1 = Typeface.createFromAsset(c.getAssets(), "fonts/Lato-Regular.ttf");
            comm_tv_title.setTypeface(custom_font1);

            //all onclicks on response/answers recyclerview item
            bt_comm_tv_appreciate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedDetailedCommentsObject current = data.get(getAdapterPosition());
                    if (current.getAnswer_response() == 0) {
                        if (current.getComment_rating_status() == 0) {
                            current.setComment_rating_status(1);
                            current.setComment_rating(current.getComment_rating() + 1);
                            answer_appreciate(getAdapterPosition());
                        } else {
                            current.setComment_rating_status(0);
                            current.setComment_rating(current.getComment_rating() - 1);
                            answer_not_appreciate(getAdapterPosition());
                        }
                    } else {
                        if (current.getComment_rating_status() == 0) {
                            current.setComment_rating_status(1);
                            current.setComment_rating(current.getComment_rating() + 1);
                            response_appreciate(getAdapterPosition());
                        } else {
                            current.setComment_rating_status(0);
                            current.setComment_rating(current.getComment_rating() - 1);
                            response_not_appreciate(getAdapterPosition());
                        }
                    }
                    notifyItemChanged(getAdapterPosition(), bt_comm_tv_appreciate);

                }
            });

            comm_tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MainFeedDetailedCommentsObject current = data.get(getAdapterPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setIcon(R.drawable.ic_dialog_alert);
                            builder.setTitle("Delete?");
                            builder.setMessage("Are you sure you want to delete this?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //finish();
                                    if (current.getAnswer_response() == 0) {
                                        deleteAnswer(current.getId(), getAdapterPosition());
                                    } else {
                                        deleteResponse(current.getId(), getAdapterPosition());
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
                }
            });

            comm_im_profileImageUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedDetailedCommentsObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ProfileActivity.class);
                    intent.putExtra("profilename", current.getComment_profileName());
                    intent.putExtra("username", current.getComment_profileUsername());
                    intent.putExtra("imageurl", current.getComment_profileImageUrl());
                    intent.putExtra("profile_id", "" + current.getComment_profile_user_id());
                    itemView.getContext().startActivity(intent);
                }
            });
            comm_tv_profileName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedDetailedCommentsObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ProfileActivity.class);
                    intent.putExtra("profilename", current.getComment_profileName());
                    intent.putExtra("username", current.getComment_profileUsername());
                    intent.putExtra("imageurl", current.getComment_profileImageUrl());
                    intent.putExtra("profile_id", "" + current.getComment_profile_user_id());
                    itemView.getContext().startActivity(intent);
                }
            });
            comm_tv_profileUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedDetailedCommentsObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ProfileActivity.class);
                    intent.putExtra("profilename", current.getComment_profileName());
                    intent.putExtra("username", current.getComment_profileUsername());
                    intent.putExtra("imageurl", current.getComment_profileImageUrl());
                    intent.putExtra("profile_id", "" + current.getComment_profile_user_id());
                    itemView.getContext().startActivity(intent);
                }
            });
            comm_im_imageUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedDetailedCommentsObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("profileimage", current.getComment_profileImageUrl());
                    intent.putExtra("title", current.getComment_title());
                    intent.putExtra("profilename", current.getComment_profileName());
                    intent.putExtra("imageurl", current.getComment_imageUrl());
                    ArrayList<String> arr_images = new ArrayList<String>();
                    arr_images.add(current.getComment_imageUrl());
                    intent.putStringArrayListExtra("arr_images", arr_images);
                    intent.putExtra("appreciations", current.getComment_rating() + "");
                    intent.putExtra("isMainFeedAdapter", "false");
                    intent.putExtra("position", 0);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}