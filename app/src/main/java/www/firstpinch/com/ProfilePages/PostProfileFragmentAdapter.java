package www.firstpinch.com.firstpinch2.ProfilePages;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
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
import java.util.Timer;
import java.util.TimerTask;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.HouseProfilePages.HouseProfileActivity;
import www.firstpinch.com.firstpinch2.HouseProfilePages.PostHouseFragmentAdapter;
import www.firstpinch.com.firstpinch2.ImageViewActivity;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedDetailedActivity;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedObject;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedRecyclerViewAdapter;
import www.firstpinch.com.firstpinch2.MainFeed.RoundedCornersTransform;
import www.firstpinch.com.firstpinch2.R;
import www.firstpinch.com.firstpinch2.WebView_Common;

/**
 * Created by Rianaa Admin on 20-10-2016.
 */

//adapter for postProfileFragment recyclerView
public class PostProfileFragmentAdapter extends RecyclerView.Adapter<PostProfileFragmentAdapter.MyViewHolder> {

    String QUESTION_DELETE_URL = "http://54.169.84.123/api/club_questions/1",
            POST_DELETE_URL = "http://54.169.84.123/api/club_stories/1",
            PUT_QUESTION_BOOKMARK_URL = "http://54.169.84.123/api/club_questions/1/favourite",
            DELETE_QUESTION_BOOKMARK = "http://54.169.84.123/api/club_questions/1/unfavourite",
            PUT_POST_BOOKMARK_URL = "http://54.169.84.123/api/club_stories/1/favourite",
            DELETE_POST_BOOKMARK = "http://54.169.84.123/api/club_stories/1/unfavourite",
            PATCH_RATING_QUESTION_URL = "http://54.169.84.123/api/story_ratings/questionrate",
            PATCH_RATING_POST_URL = "http://54.169.84.123/api/story_ratings/rate";
    private LayoutInflater inflator;
    List<MainFeedObject> data = Collections.emptyList();
    Context c;
    DisplayMetrics metrics;
    int pixels;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;
    private Timer timer;
    private TimerTask timerTask;
    int pos = 0, user_id,POST_FEED_DELETE=1;
    PostProfileFragmentAdapter.MyViewHolder global_holder;


    public PostProfileFragmentAdapter(Context context, final List<MainFeedObject> data) {
        inflator = LayoutInflater.from(context);
        this.data = data;
        c = context;
        metrics = c.getResources().getDisplayMetrics();
        float dp = 250f;
        float fpixels = metrics.density * dp;
        pixels = (int) (fpixels + 0.5f);
        timer = new Timer();
        SharedPreferences sp = c.getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        //MainFeedRecyclerView mfrv = new MainFeedRecyclerView();
        /*timerTask = new TimerTask() {
            @Override
            public void run() {
                if (pos < data.size()) {
                    Log.e("pos=", "" + pos);
                    //changeCommData(pos);
                    pos++;
                } else {
                    pos = 0;
                }
            }
            //refresh your textview
        };
        timer.schedule(timerTask, 0, 10000);*/
    }

    @Override
    public PostProfileFragmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.post_profile_recyclerview_item, parent, false);
        PostProfileFragmentAdapter.MyViewHolder holder = new PostProfileFragmentAdapter.MyViewHolder(view);
        //global_holder = holder;
        return holder;
    }

    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }


    @Override
    public void onBindViewHolder(final PostProfileFragmentAdapter.MyViewHolder holder, final int position) {

        final MainFeedObject current = data.get(position);
        holder.tv_houseName.setText(current.getHouseName());
        //Log.e("images_count=", "" + current.arr_images.size());
        holder.tv_title.setTextColor(0xffef197d);

        if (current.getPreview_link().contentEquals("null") || current.getPreview_link().contentEquals("") || current.getPreview_link() == null) {
            holder.preview_cardview.setVisibility(View.GONE);
        } else {
            holder.preview_cardview.setVisibility(View.VISIBLE);
            holder.preview_title.setText(current.getPreview_title());
            holder.preview_description.setText(current.getPreview_description());
            if (!current.getPreview_image().contentEquals("")) {
                Picasso.with(c)
                        .load(current.getPreview_image())
                        .placeholder(R.drawable.placeholder_image)
                        .transform(new RoundedCornersTransform())
                        .into(holder.preview_image);
            } else {
                Picasso.with(c)
                        .load("http://s15.postimg.org/3omz97m7v/sample_5.jpg")
                        .placeholder(R.drawable.placeholder_image)
                        .transform(new RoundedCornersTransform())
                        .into(holder.preview_image);
            }
        }

        /*if (current.getBookmark_status().contentEquals("0")) {
            holder.feed_bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_48dp);
        } else {
            holder.feed_bookmark.setBackgroundResource(R.drawable.ic_bookmark_black_48dp);
        }*/
        /*if (user_id == current.getMainfeed_user_id()) {
            holder.feed_delete.setVisibility(View.VISIBLE);
        } else {
            holder.feed_delete.setVisibility(View.INVISIBLE);
        }*/
        if (current.getTitle().contentEquals("")) {
            holder.tv_title.setVisibility(View.GONE);
        } else {
            holder.tv_title.setVisibility(View.VISIBLE);
            holder.tv_title.setText(current.getTitle());
        }
        if (current.getResponses() < 3) {
            holder.tv_see_all_comments.setVisibility(View.GONE);
        } else {
            holder.tv_see_all_comments.setVisibility(View.VISIBLE);
        }
        holder.tv_comment.setText("Respond Now");
        holder.text_responses.setText("All Responses");

        holder.tv_desc.setText(current.getDesc());
        Spannable spannable = new SpannableString(current.getDesc());
        Linkify.addLinks(spannable, Linkify.WEB_URLS);
        URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (URLSpan urlSpan : spans) {
            LinkSpan linkSpan = new LinkSpan(urlSpan.getURL());
            int spanStart = spannable.getSpanStart(urlSpan);
            int spanEnd = spannable.getSpanEnd(urlSpan);
            spannable.setSpan(linkSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.removeSpan(urlSpan);
        }
        holder.tv_desc.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tv_desc.setText(spannable, TextView.BufferType.SPANNABLE);

        holder.tv_appreciations_count.setText("" + current.getAppreciations_count());
        holder.tv_score.setText("" + current.getScore());
        holder.tv_responses.setText("" + current.getResponses());
        //holder.tv_totalShares.setText("" + current.getTotalShares());
        //holder.tv_totalComments.setText("" + current.getTotalComments());
        //holder.tv_rating.setText(current.getRating().toString());

        String str = current.getHint_comm_title();
        if (str != null) {
            CharSequence sequence = Html.fromHtml(current.getHint_comm_title());
            SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
            URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
            for (URLSpan span : urls) {
                makeLinkClickable(strBuilder, span);
            }
            holder.hint_comm_title.setText(strBuilder);
            holder.hint_comm_title.setMovementMethod(LinkMovementMethod.getInstance());
        }

        holder.hint_comm_profile_name.setText(data.get(position).getHint_comm_profile_name());
        Picasso.with(c)
                .load(data.get(position).getHint_comm_profile_image())
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(holder.hint_comm_profile_image);

        String str2 = current.getHint_comm_title2();
        //Spanned comm = null;
        if (str2 != null) {
            //comm = Html.fromHtml(str);
            //comm.replaceAll("\\n","");
            CharSequence sequence = Html.fromHtml(current.getHint_comm_title2());
            SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
            URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
            for (URLSpan span : urls) {
                makeLinkClickable(strBuilder, span);
            }
            holder.hint_comm_title2.setText(strBuilder);
            holder.hint_comm_title2.setMovementMethod(LinkMovementMethod.getInstance());
        }
        //holder.hint_comm_title.setText(comm);

        holder.hint_comm_profile_name2.setText(current.getHint_comm_profile_name2());
        Picasso.with(c)
                .load(current.getHint_comm_profile_image2())
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(holder.hint_comm_profile_image2);


        if (current.getResponses() == 0) {
            holder.rl_comment_hint.setVisibility(View.GONE);
            holder.rl_comment_hint2.setVisibility(View.GONE);
        } else if(current.getResponses() == 1){
            holder.rl_comment_hint.setVisibility(View.VISIBLE);
            holder.rl_comment_hint2.setVisibility(View.GONE);
        } else {
            holder.rl_comment_hint.setVisibility(View.VISIBLE);
            holder.rl_comment_hint2.setVisibility(View.VISIBLE);
        }

        Picasso.with(c)
                .load(current.getHouseImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.im_houseImageUrl);
        /*Picasso.with(c)
                .load(current.getProfileImageUrl())
                .transform(new RoundedCornersTransform())
                .into(holder.add_comment_profile_image);*/

        if (current.getNum_of_images() == 0) {
            holder.ll_container.setVisibility(View.GONE);
        } else if (current.getNum_of_images() == 1) {
            holder.ll_container.setVisibility(View.VISIBLE);
            holder.l1.setVisibility(View.VISIBLE);
            holder.l2.setVisibility(View.GONE);
            holder.l3.setVisibility(View.GONE);
            holder.l4.setVisibility(View.GONE);
            Picasso.with(c)
                    .load(current.arr_images.get(0))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.i1);
        } else if (current.getNum_of_images() == 2) {
            holder.ll_container.setVisibility(View.VISIBLE);
            holder.l1.setVisibility(View.GONE);
            holder.l2.setVisibility(View.VISIBLE);
            holder.l3.setVisibility(View.GONE);
            holder.l4.setVisibility(View.GONE);
            Picasso.with(c)
                    .load(current.arr_images.get(0))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.i2_1);
            Picasso.with(c)
                    .load(current.arr_images.get(1))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.i2_2);
        } else if (current.getNum_of_images() == 3) {
            holder.ll_container.setVisibility(View.VISIBLE);
            holder.l1.setVisibility(View.GONE);
            holder.l2.setVisibility(View.GONE);
            holder.l3.setVisibility(View.VISIBLE);
            holder.l4.setVisibility(View.GONE);
            Picasso.with(c)
                    .load(current.arr_images.get(0))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.i3_1);
            Picasso.with(c)
                    .load(current.arr_images.get(1))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.i3_2);
            Picasso.with(c)
                    .load(current.arr_images.get(2))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.i3_3);
        } else if (current.getNum_of_images() == 4) {
            holder.ll_container.setVisibility(View.VISIBLE);
            holder.l1.setVisibility(View.GONE);
            holder.l2.setVisibility(View.GONE);
            holder.l3.setVisibility(View.GONE);
            holder.l4.setVisibility(View.VISIBLE);
            Picasso.with(c)
                    .load(current.arr_images.get(0))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.i4_1);
            Picasso.with(c)
                    .load(current.arr_images.get(1))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.i4_2);
            Picasso.with(c)
                    .load(current.arr_images.get(2))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.i4_3);
            Picasso.with(c)
                    .load(current.arr_images.get(3))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.i4_4);
        }

        switch (current.getUser_rate()) {
            case 1:
                holder.r1.setBackgroundResource(R.drawable.ic_1_filled);
                holder.r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                holder.r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*holder.r4.setBackgroundResource(R.drawable.star_unfilled);
                holder.r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
            case 2:
                holder.r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                holder.r2.setBackgroundResource(R.drawable.ic_2_filled);
                holder.r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*holder.r4.setBackgroundResource(R.drawable.star_unfilled);
                holder.r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
            case 3:
                holder.r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                holder.r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                holder.r3.setBackgroundResource(R.drawable.ic_3_filled);
                /*holder.r4.setBackgroundResource(R.drawable.star_unfilled);
                holder.r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
            /*case 4:
                holder.r1.setBackgroundResource(R.drawable.star_filled);
                holder.r2.setBackgroundResource(R.drawable.star_filled);
                holder.r3.setBackgroundResource(R.drawable.star_filled);
                holder.r4.setBackgroundResource(R.drawable.star_filled);
                holder.r5.setBackgroundResource(R.drawable.star_unfilled);
                break;
            case 5:
                holder.r1.setBackgroundResource(R.drawable.star_filled);
                holder.r2.setBackgroundResource(R.drawable.star_filled);
                holder.r3.setBackgroundResource(R.drawable.star_filled);
                holder.r4.setBackgroundResource(R.drawable.star_filled);
                holder.r5.setBackgroundResource(R.drawable.star_filled);
                break;*/
            default:
                holder.r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                holder.r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                holder.r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*holder.r4.setBackgroundResource(R.drawable.star_unfilled);
                holder.r5.setBackgroundResource(R.drawable.star_unfilled);*/
                break;
        }

        holder.rl_house_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                intent = new Intent(holder.itemView.getContext(), HouseProfileActivity.class);
                intent.putExtra("housename", current.getHouseName());
                intent.putExtra("imageurl", current.getHouseImageUrl());
                intent.putExtra("house_id", "" + current.getMainfeed_house_id());
                holder.itemView.getContext().startActivity(intent);
            }
        });


        /*holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.et_add_comm.getText().toString().contentEquals("")) {
                    Toast.makeText(c, "Blank comment not allowed",
                            Toast.LENGTH_LONG).show();
                } else {
                    *//*final Intent intent;
                    intent = new Intent(holder.itemView.getContext(), MainFeedDetailedActivity.class);
                    intent.putExtra("housename", current.getHouseName());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("profileusername", current.getProfileUsername());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("desc", current.getDesc());
                    intent.putExtra("totalshares", current.getTotalShares());
                    intent.putExtra("totalcomments", current.getTotalComments());
                    intent.putExtra("rating", current.getUser_rate());
                    intent.putExtra("houseimageurl", current.getHouseImageUrl());
                    intent.putExtra("profileimageurl", current.getProfileImageUrl());
                    intent.putExtra("imageurl", current.getImageUrl());
                    intent.putExtra("post_ques", current.getPost_ques());
                    intent.putExtra("num_of_images", current.getNum_of_images());
                    intent.putExtra("comment", holder.et_add_comm.getText().toString());
                    holder.itemView.getContext().startActivity(intent);*//*
                    if (holder.et_add_comm != null) {
                        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(holder.et_add_comm.getWindowToken(), 0);
                        holder.hint_comm_title.setText(holder.et_add_comm.getText().toString());
                        holder.hint_comm_profile_name.setText("Vishal Puri");
                        Picasso.with(c)
                                .load("https://s3.amazonaws.com/cdn.firstpinch.com/spree/user_profile_pic/26/original/fbdp.jpg?1473159927")
                                .transform(new RoundedCornersTransform())
                                .into(holder.hint_comm_profile_image);
                    }
                    holder.et_add_comm.setText("");

                    Toast.makeText(c, "Comment Added",
                            Toast.LENGTH_LONG).show();
                }
            }
        });*/

    }

    @Override
    public void onBindViewHolder(final PostProfileFragmentAdapter.MyViewHolder holder, final int position, List<Object> payloads) {

        if (payloads.isEmpty()) {
            // Perform a full update
            onBindViewHolder(holder, position);
        } else {
            // Perform a partial update
            final MainFeedObject current = data.get(position);
            /*if (current.getBookmark_status().contentEquals("0")) {
                holder.feed_bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_48dp);
            } else {
                holder.feed_bookmark.setBackgroundResource(R.drawable.ic_bookmark_black_48dp);
            }*/
            switch (current.getUser_rate()) {
                case 1:
                    holder.r1.setBackgroundResource(R.drawable.ic_1_filled);
                    holder.r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                    holder.r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*holder.r4.setBackgroundResource(R.drawable.star_unfilled);
                holder.r5.setBackgroundResource(R.drawable.star_unfilled);*/
                    break;
                case 2:
                    holder.r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                    holder.r2.setBackgroundResource(R.drawable.ic_2_filled);
                    holder.r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*holder.r4.setBackgroundResource(R.drawable.star_unfilled);
                holder.r5.setBackgroundResource(R.drawable.star_unfilled);*/
                    break;
                case 3:
                    holder.r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                    holder.r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                    holder.r3.setBackgroundResource(R.drawable.ic_3_filled);
                /*holder.r4.setBackgroundResource(R.drawable.star_unfilled);
                holder.r5.setBackgroundResource(R.drawable.star_unfilled);*/
                    break;
            /*case 4:
                holder.r1.setBackgroundResource(R.drawable.star_filled);
                holder.r2.setBackgroundResource(R.drawable.star_filled);
                holder.r3.setBackgroundResource(R.drawable.star_filled);
                holder.r4.setBackgroundResource(R.drawable.star_filled);
                holder.r5.setBackgroundResource(R.drawable.star_unfilled);
                break;
            case 5:
                holder.r1.setBackgroundResource(R.drawable.star_filled);
                holder.r2.setBackgroundResource(R.drawable.star_filled);
                holder.r3.setBackgroundResource(R.drawable.star_filled);
                holder.r4.setBackgroundResource(R.drawable.star_filled);
                holder.r5.setBackgroundResource(R.drawable.star_filled);
                break;*/
                default:
                    holder.r1.setBackgroundResource(R.drawable.ic_1_unfilled);
                    holder.r2.setBackgroundResource(R.drawable.ic_2_unfilled);
                    holder.r3.setBackgroundResource(R.drawable.ic_3_unfilled);
                /*holder.r4.setBackgroundResource(R.drawable.star_unfilled);
                holder.r5.setBackgroundResource(R.drawable.star_unfilled);*/
                    break;
            }
        }

    }

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


    private class LinkSpan extends URLSpan {
        private LinkSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View view) {
            String url = getURL();
            if (url != null) {

                SharedPreferences prefs = c.getSharedPreferences("webviewurl", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("url", url);
                editor.commit();
                c.startActivity(new Intent(c,WebView_Common.class).putExtra("url",url));

            }
        }
    }

    public List<MainFeedObject> getList() {
        return data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //for animation(not used currently)
    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(c, R.anim.anticipateovershoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    //for animation(not used currently)
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

    //for animation(not used currently)
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);

    }

    //delete question API request
    private void deleteQuestion(final String question_id, final int position) {
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
                headers.put("Question-Id", question_id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);

    }

    //delete post API request
    private void deletePost(final String post_id, final int position) {
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
                headers.put("Story-Id", post_id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);

    }

    //add question to bookmarks API request
    private void put_question_bookmark(final int position) {

        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PUT_QUESTION_BOOKMARK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("put ques bkmrk response", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            MainFeedObject current = data.get(position);
                            if (status.contentEquals("1")) {
                                String bookmark_id = jsonObj.getString("favourte_id");
                                current.setBookmark_id(bookmark_id);
                                current.setBookmark_status("1");
                                notifyItemChanged(position);
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
                MainFeedObject current = data.get(position);
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("question_id", current.getMainfeed_question_id());
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

    //remove question from bookmarks API request
    private void delete_question_bookmark(final int position) {
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
                                MainFeedObject current = data.get(position);
                                if (status.contentEquals("1")) {
                                    current.setBookmark_id("0");
                                    current.setBookmark_status("0");
                                    notifyItemChanged(position);
                                } else {
                                    Toast.makeText(c, "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
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
                MainFeedObject current = data.get(position);
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Favourite-Id", current.getBookmark_id());
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);

    }

    //add post to bookmarks API request
    private void put_post_bookmark(final int position) {

        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PUT_POST_BOOKMARK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("put ques bkmrk response", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            MainFeedObject current = data.get(position);
                            if (status.contentEquals("1")) {
                                String bookmark_id = jsonObj.getString("favourte_id");
                                current.setBookmark_id(bookmark_id);
                                current.setBookmark_status("1");
                                notifyItemChanged(position);
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
                MainFeedObject current = data.get(position);
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("story_id", current.getMainfeed_question_id());
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

    //remove post from bookmarks API request
    private void delete_post_bookmark(final int position) {
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
                                MainFeedObject current = data.get(position);
                                if (status.contentEquals("1")) {
                                    current.setBookmark_id("0");
                                    current.setBookmark_status("0");
                                    notifyItemChanged(position);
                                } else {
                                    Toast.makeText(c, "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
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
                MainFeedObject current = data.get(position);
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Favourite-Id", current.getBookmark_id());
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);

    }

    //rate question API request
    private void patch_question_rating(final int position,final int rate) {

        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PATCH_RATING_QUESTION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("patch ques rate", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            MainFeedObject current = data.get(position);
                            if (status.contentEquals("1")) {
                                current.setUser_rate(rate);
                                notifyItemChanged(position);
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
                MainFeedObject current = data.get(position);
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("question_id", current.getMainfeed_question_id());
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

    //rate post API request
    private void patch_post_rating(final int position,final int rate) {

        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PATCH_RATING_POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("patch ques rate", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            MainFeedObject current = data.get(position);
                            if (status.contentEquals("1")) {
                                current.setUser_rate(rate);
                                notifyItemChanged(position);
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
                MainFeedObject current = data.get(position);
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("story_id", current.getMainfeed_question_id());
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


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView im_houseImageUrl, preview_image;
        CardView preview_cardview;
        FrameLayout fl_images;
        TextView tv_houseName, tv_title, tv_desc, tv_post_ques,
                tv_rating, tv_totalComments, tv_totalShares, tv_appreciations_count, tv_score, tv_responses,
                text_score, preview_title, preview_description;
        Button r1, r2, r3, /*r4, r5,*/ comment;
        RelativeLayout rl, rl_house_head, rl_comment_hint,rl_comment_hint2;
        LinearLayout ll_container, l1, l2, l3, l4;
        ImageView i1, i2_1, i2_2, i3_1, i3_2, i3_3, i4_1, i4_2, i4_3, i4_4;
        EditText et_add_comm;
        ImageView hint_comm_profile_image,hint_comm_profile_image2, add_comment_profile_image, share,im_menu_button;
        TextView hint_comm_profile_name, hint_comm_title,hint_comm_profile_name2, hint_comm_title2, tv_see_all_comments, text_responses, tv_comment;


        public MyViewHolder(final View itemView) {
            super(itemView);

            Typeface custom_font = Typeface.createFromAsset(c.getAssets(), "fonts/KeepCalm-Medium.ttf");
            Typeface custom_font1 = Typeface.createFromAsset(c.getAssets(), "fonts/Lato-Regular.ttf");

            tv_houseName = (TextView) itemView.findViewById(R.id.feed_house_name);
            tv_title = (TextView) itemView.findViewById(R.id.feed_title);
            tv_desc = (TextView) itemView.findViewById(R.id.feed_desc);
            tv_post_ques = (TextView) itemView.findViewById(R.id.feed_post_or_ques);
            preview_title = (TextView) itemView.findViewById(R.id.preview_title);
            preview_description = (TextView) itemView.findViewById(R.id.preview_desc);
            preview_cardview = (CardView) itemView.findViewById(R.id.preview_cardview);
            TextView appreciationTextStyle1 = (TextView) itemView.findViewById(R.id.text_appreciations);

            TextView responsesTextStyle1 = (TextView) itemView.findViewById(R.id.text_responses);
            //TextView commentTextStyle1 = (TextView)itemView.findViewById(R.id.main_feed_comment_text);

            appreciationTextStyle1.setTypeface(custom_font1);

            responsesTextStyle1.setTypeface(custom_font1);
            //commentTextStyle1.setTypeface(custom_font1);

            tv_houseName.setTypeface(custom_font);
            tv_title.setTypeface(custom_font);
            tv_desc.setTypeface(custom_font1);
            preview_title.setTypeface(custom_font1);
            preview_description.setTypeface(custom_font1);

            //tv_rating = (TextView) itemView.findViewById(R.id.feed_rate);
            //tv_totalComments = (TextView) itemView.findViewById(R.id.totalcomments);
            //tv_totalShares = (TextView) itemView.findViewById(R.id.totalshares);
            im_houseImageUrl = (ImageView) itemView.findViewById(R.id.feed_house_pic);
            preview_image = (ImageView) itemView.findViewById(R.id.preview_image);
            //add_comment_profile_image = (ImageView) itemView.findViewById(R.id.feed_add_comm_profile_image);
            //im_image = (ImageView) itemView.findViewById(R.id.feed_image);
            //fl_images = (FrameLayout) itemView.findViewById(R.id.feed_image);
            share = (ImageView) itemView.findViewById(R.id.feed_share);
            r1 = (Button) itemView.findViewById(R.id.rate1);
            r2 = (Button) itemView.findViewById(R.id.rate2);
            r3 = (Button) itemView.findViewById(R.id.rate3);
            /*r4 = (Button) itemView.findViewById(R.id.rate4);
            r5 = (Button) itemView.findViewById(R.id.rate5);*/
            rl = (RelativeLayout) itemView.findViewById(R.id.rl_main_feed_without_house_layout);
            rl_house_head = (RelativeLayout) itemView.findViewById(R.id.house_head_layout);
            rl_comment_hint = (RelativeLayout) itemView.findViewById(R.id.rl_comment_hint);
            rl_comment_hint2 = (RelativeLayout) itemView.findViewById(R.id.rl_comment_hint2);
            //comment = (Button) itemView.findViewById(R.id.feed_bt_add_comment);
            //feed_bookmark = (ImageView) itemView.findViewById(R.id.feed_bookmark);
            //feed_delete = (ImageView) itemView.findViewById(R.id.feed_delete);
            im_menu_button = (ImageView) itemView.findViewById(R.id.menu_button);
            tv_comment = (TextView) itemView.findViewById(R.id.main_feed_comment_text);
            text_responses = (TextView) itemView.findViewById(R.id.text_responses);

            //et_add_comm = (EditText) itemView.findViewById(R.id.feed_add_comment_et);

            ll_container = (LinearLayout) itemView.findViewById(R.id.ll_containing_images);
            l1 = (LinearLayout) itemView.findViewById(R.id.ll_for_1_img);
            l2 = (LinearLayout) itemView.findViewById(R.id.ll_for_2_img);
            l3 = (LinearLayout) itemView.findViewById(R.id.ll_for_3_img);
            l4 = (LinearLayout) itemView.findViewById(R.id.ll_for_4_img);
            i1 = (ImageView) itemView.findViewById(R.id.feed_image1_1);
            i2_1 = (ImageView) itemView.findViewById(R.id.feed_image_for2_1);
            i2_2 = (ImageView) itemView.findViewById(R.id.feed_image_for2_2);
            i3_1 = (ImageView) itemView.findViewById(R.id.feed_image_for3_1);
            i3_2 = (ImageView) itemView.findViewById(R.id.feed_image_for3_2);
            i3_3 = (ImageView) itemView.findViewById(R.id.feed_image_for3_3);
            i4_1 = (ImageView) itemView.findViewById(R.id.feed_image_for4_1);
            i4_2 = (ImageView) itemView.findViewById(R.id.feed_image_for4_2);
            i4_3 = (ImageView) itemView.findViewById(R.id.feed_image_for4_3);
            i4_4 = (ImageView) itemView.findViewById(R.id.feed_image_for4_4);

            hint_comm_profile_image = (ImageView) itemView.findViewById(R.id.feed_hint_comm_profile_image);
            hint_comm_profile_name = (TextView) itemView.findViewById(R.id.feed_hint_comm_profile_name);
            hint_comm_title = (TextView) itemView.findViewById(R.id.feed_hint_comm_title);
            hint_comm_profile_image2 = (ImageView) itemView.findViewById(R.id.feed_hint_comm_profile_image2);
            hint_comm_profile_name2 = (TextView) itemView.findViewById(R.id.feed_hint_comm_profile_name2);
            hint_comm_title2 = (TextView) itemView.findViewById(R.id.feed_hint_comm_title2);
            tv_appreciations_count = (TextView) itemView.findViewById(R.id.tv_appreciations);
            tv_score = (TextView) itemView.findViewById(R.id.tv_score);
            tv_responses = (TextView) itemView.findViewById(R.id.tv_responses);
            text_score = (TextView) itemView.findViewById(R.id.text_score);

            text_score.setTypeface(custom_font1);
            tv_responses.setTypeface(custom_font1);
            tv_score.setTypeface(custom_font1);
            tv_appreciations_count.setTypeface(custom_font1);

            hint_comm_profile_name.setTypeface(custom_font1);
            hint_comm_title.setTypeface(custom_font1);
            hint_comm_title2.setTypeface(custom_font1);
            hint_comm_profile_name2.setTypeface(custom_font1);
            tv_see_all_comments = (TextView) itemView.findViewById(R.id.tv_see_all_comments);

            //onClicks on item view below
            im_menu_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //itemView.showContextMenu();
                    final MainFeedObject current = data.get(getAdapterPosition());
                    PopupMenu popup = new PopupMenu(v.getContext(), v);
                    if (current.getBookmark_status().contentEquals("0")&&user_id == current.getMainfeed_user_id()) {
                        popup.inflate(R.menu.menu_button_delete_bookmark);
                    } else if (current.getBookmark_status().contentEquals("0")&&user_id != current.getMainfeed_user_id()){
                        popup.inflate(R.menu.menu_button_bookmark);
                    } else if (current.getBookmark_status().contentEquals("1")&&user_id == current.getMainfeed_user_id()){
                        popup.inflate(R.menu.menu_button_delete_bookmarked);
                    } else if (current.getBookmark_status().contentEquals("1")&&user_id != current.getMainfeed_user_id()){
                        popup.inflate(R.menu.menu_button_bookmarked);
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();

                            //noinspection SimplifiableIfStatement
                            if (id == R.id.delete) {
                                try {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                                    builder.setIcon(R.drawable.ic_dialog_alert);
                                    builder.setTitle("Delete?");
                                    builder.setMessage("Are you sure you want to delete this?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //finish();
                                            if (current.getPost_ques() == 0) {
                                                deletePost(current.getMainfeed_question_id(), getAdapterPosition());
                                            } else {
                                                deleteQuestion(current.getMainfeed_question_id(), getAdapterPosition());
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
                                    error_global.initializeVar(c, e.toString());
                                }
                                return true;
                            } else if (id == R.id.bookmark) {
                                try {
                                    MainFeedObject current = data.get(getAdapterPosition());
                                    if (current.getPost_ques() == 0) {
                                        if (current.getBookmark_status().contentEquals("0")) {
                                            current.setBookmark_status("1");
                                            put_post_bookmark(getAdapterPosition());
                                            Toast.makeText(c, "Bookmarked",Toast.LENGTH_SHORT).show();
                                        } else {
                                            delete_post_bookmark(getAdapterPosition());
                                            current.setBookmark_status("0");
                                            Toast.makeText(c, "Removed from bookmarks",Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        if (current.getBookmark_status().contentEquals("0")) {
                                            put_question_bookmark(getAdapterPosition());
                                            current.setBookmark_status("1");
                                            Toast.makeText(c, "Bookmarked",Toast.LENGTH_SHORT).show();
                                        } else {
                                            delete_question_bookmark(getAdapterPosition());
                                            current.setBookmark_status("0");
                                            Toast.makeText(c, "Removed from bookmarks",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } catch (Exception e) {
                                    Log.e("MainFeedAdapter", "feed_bookmark" + e.toString());
                                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                                    error_global.initializeVar(c, e.toString());
                                }
                                //notifyItemChanged(getAdapterPosition(), feed_bookmark);
                                return true;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });

            r1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    current.setUser_rate(1);
                    notifyItemChanged(getAdapterPosition(), r1);
                    if (current.getPost_ques() == 0) {
                        Toast.makeText(c, "Booo",
                                Toast.LENGTH_LONG).show();
                        patch_post_rating(getAdapterPosition(), 1);
                    } else {
                        Toast.makeText(c, "Booo",
                                Toast.LENGTH_LONG).show();
                        patch_question_rating(getAdapterPosition(), 1);
                    }

                }
            });
            r2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    current.setUser_rate(2);
                    notifyItemChanged(getAdapterPosition(), r2);
                    if (current.getPost_ques() == 0) {
                        Toast.makeText(c, "I like it",
                                Toast.LENGTH_LONG).show();
                        patch_post_rating(getAdapterPosition(), 2);
                    } else {
                        Toast.makeText(c, "I like it",
                                Toast.LENGTH_LONG).show();
                        patch_question_rating(getAdapterPosition(), 2);
                    }
                }
            });
            r3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    current.setUser_rate(3);
                    notifyItemChanged(getAdapterPosition(), r3);
                    if (current.getPost_ques() == 0) {
                        Toast.makeText(c, "Marvellous",
                                Toast.LENGTH_LONG).show();
                        patch_post_rating(getAdapterPosition(), 3);
                    } else {
                        Toast.makeText(c, "Marvellous",
                                Toast.LENGTH_LONG).show();
                        patch_question_rating(getAdapterPosition(), 3);
                    }
                }
            });
            /*r4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    current.setUser_rate(4);
                    notifyItemChanged(getAdapterPosition(), r4);
                    if (current.getPost_ques() == 0) {
                        *//*Toast.makeText(c, "You rated " + current.getProfileName() + "'s Post 1",
                                Toast.LENGTH_LONG).show();*//*
                        patch_post_rating(getAdapterPosition(), 4);
                    } else {
                        *//*Toast.makeText(c, "You rated " + current.getProfileName() + "'s Question 1",
                                Toast.LENGTH_LONG).show();*//*
                        patch_question_rating(getAdapterPosition(), 4);
                    }
                }
            });
            r5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    current.setUser_rate(5);
                    notifyItemChanged(getAdapterPosition(), r5);
                    if (current.getPost_ques() == 0) {
                        *//*Toast.makeText(c, "You rated " + current.getProfileName() + "'s Post 1",
                                Toast.LENGTH_LONG).show();*//*
                        patch_post_rating(getAdapterPosition(), 5);
                    } else {
                        *//*Toast.makeText(c, "You rated " + current.getProfileName() + "'s Question 1",
                                Toast.LENGTH_LONG).show();*//*
                        patch_question_rating(getAdapterPosition(), 5);
                    }
                }
            });*/
            /*feed_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    *//*Toast.makeText(c, "Bookmark Added",
                            Toast.LENGTH_SHORT).show();*//*
                    try {
                        MainFeedObject current = data.get(getAdapterPosition());
                        if (current.getPost_ques() == 0) {
                            if (current.getBookmark_status().contentEquals("0")) {
                                current.setBookmark_status("1");
                                put_post_bookmark(getAdapterPosition());
                            } else {
                                delete_post_bookmark(getAdapterPosition());
                                current.setBookmark_status("0");
                            }
                        } else {
                            if (current.getBookmark_status().contentEquals("0")) {
                                put_question_bookmark(getAdapterPosition());
                                current.setBookmark_status("1");
                            } else {
                                delete_question_bookmark(getAdapterPosition());
                                current.setBookmark_status("0");
                            }
                        }

                    } catch (Exception e) {
                        Log.e("MainFeedAdapter", "feed_bookmark" + e.toString());
                    }
                    notifyItemChanged(getAdapterPosition(), feed_bookmark);

                }
            });*/


            /*feed_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MainFeedObject current = data.get(getAdapterPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                            builder.setTitle("Delete?");
                            builder.setMessage("Are you sure you want to delete this?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (current.getPost_ques() == 0) {
                                        deletePost(current.getMainfeed_question_id(), getAdapterPosition());
                                    } else {
                                        deleteQuestion(current.getMainfeed_question_id(), getAdapterPosition());
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
            });*/

            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), MainFeedDetailedActivity.class);
                    intent.putExtra("housename", current.getHouseName());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("profileusername", current.getProfileUsername());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("desc", current.getDesc());
                    intent.putExtra("totalshares", current.getTotalShares());
                    intent.putExtra("totalcomments", current.getTotalComments());
                    intent.putExtra("appreciation_count", "" + current.getAppreciations_count());
                    intent.putExtra("score_count", current.getScore());
                    intent.putExtra("responses_count", "" + current.getResponses());
                    intent.putExtra("rating", current.getUser_rate());
                    intent.putExtra("houseimageurl", current.getHouseImageUrl());
                    intent.putExtra("profileimageurl", current.getProfileImageUrl());
                    intent.putExtra("imageurl", current.getImageUrl());
                    intent.putExtra("post_ques", current.getPost_ques());
                    intent.putExtra("num_of_images", current.getNum_of_images());
                    intent.putExtra("comment", " ");
                    intent.putExtra("bookmark_status", current.getBookmark_status());
                    intent.putExtra("bookmark_id", current.getBookmark_id());
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("question_id", String.valueOf(current.getMainfeed_question_id()));
                    //if(position>0)
                    //intent.putExtra("second_image",data.get(position).getImageUrl());
                    intent.putExtra("activity_name", "profileactivity");
                    ((ProfileActivity) c).startActivityForResult(intent,POST_FEED_DELETE);
                }
            });
            tv_desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), MainFeedDetailedActivity.class);
                    intent.putExtra("housename", current.getHouseName());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("profileusername", current.getProfileUsername());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("desc", current.getDesc());
                    intent.putExtra("totalshares", current.getTotalShares());
                    intent.putExtra("totalcomments", current.getTotalComments());
                    intent.putExtra("appreciation_count", "" + current.getAppreciations_count());
                    intent.putExtra("score_count", current.getScore());
                    intent.putExtra("responses_count", "" + current.getResponses());
                    intent.putExtra("rating", current.getUser_rate());
                    intent.putExtra("houseimageurl", current.getHouseImageUrl());
                    intent.putExtra("profileimageurl", current.getProfileImageUrl());
                    intent.putExtra("imageurl", current.getImageUrl());
                    intent.putExtra("post_ques", current.getPost_ques());
                    intent.putExtra("num_of_images", current.getNum_of_images());
                    intent.putExtra("comment", " ");
                    intent.putExtra("bookmark_status", current.getBookmark_status());
                    intent.putExtra("bookmark_id", current.getBookmark_id());
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("question_id", String.valueOf(current.getMainfeed_question_id()));
                    intent.putExtra("activity_name", "profileactivity");
                    ((ProfileActivity) c).startActivityForResult(intent,POST_FEED_DELETE);
                }
            });

            text_responses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), MainFeedDetailedActivity.class);
                    intent.putExtra("housename", current.getHouseName());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("profileusername", current.getProfileUsername());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("desc", current.getDesc());
                    intent.putExtra("totalshares", current.getTotalShares());
                    intent.putExtra("totalcomments", current.getTotalComments());
                    intent.putExtra("appreciation_count", "" + current.getAppreciations_count());
                    intent.putExtra("score_count", current.getScore());
                    intent.putExtra("responses_count", "" + current.getResponses());
                    intent.putExtra("rating", current.getUser_rate());
                    intent.putExtra("houseimageurl", current.getHouseImageUrl());
                    intent.putExtra("profileimageurl", current.getProfileImageUrl());
                    intent.putExtra("imageurl", current.getImageUrl());
                    intent.putExtra("post_ques", current.getPost_ques());
                    intent.putExtra("num_of_images", current.getNum_of_images());
                    intent.putExtra("comment", " ");
                    intent.putExtra("bookmark_status", current.getBookmark_status());
                    intent.putExtra("bookmark_id", current.getBookmark_id());
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("question_id", String.valueOf(current.getMainfeed_question_id()));
                    intent.putExtra("activity_name", "profileactivity");
                    ((ProfileActivity) c).startActivityForResult(intent,POST_FEED_DELETE);
                }
            });
            /*ll_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), MainFeedDetailedActivity.class);
                    intent.putExtra("housename", current.getHouseName());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("profileusername", current.getProfileUsername());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("desc", current.getDesc());
                    intent.putExtra("totalshares", current.getTotalShares());
                    intent.putExtra("totalcomments", current.getTotalComments());
                    intent.putExtra("rating", current.getUser_rate());
                    intent.putExtra("appreciation_count", "" + current.getAppreciations_count());
                    intent.putExtra("score_count", "" + current.getScore());
                    intent.putExtra("responses_count", "" + current.getResponses());
                    intent.putExtra("houseimageurl", current.getHouseImageUrl());
                    intent.putExtra("profileimageurl", current.getProfileImageUrl());
                    intent.putExtra("imageurl", current.getImageUrl());
                    intent.putExtra("post_ques", current.getPost_ques());
                    intent.putExtra("num_of_images", current.getNum_of_images());
                    intent.putExtra("comment", " ");
                    intent.putExtra("bookmark_status", current.getBookmark_status());
                    intent.putExtra("bookmark_id", current.getBookmark_id());
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    itemView.getContext().startActivity(intent);
                }
            });*/

            i1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("profileimage", current.getProfileImageUrl());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("imageurl", current.arr_images.get(0));
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("responses", current.getResponses()+"");
                    intent.putExtra("score", current.getScore()+"");
                    intent.putExtra("appreciations", current.getAppreciations_count()+"");
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 0);
                    itemView.getContext().startActivity(intent);
                }
            });

            i2_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("profileimage", current.getProfileImageUrl());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("imageurl", current.arr_images.get(0));
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("responses", current.getResponses()+"");
                    intent.putExtra("score", current.getScore()+"");
                    intent.putExtra("appreciations", current.getAppreciations_count()+"");
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 0);
                    itemView.getContext().startActivity(intent);
                }
            });
            i2_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("profileimage", current.getProfileImageUrl());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("imageurl", current.arr_images.get(1));
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("responses", current.getResponses()+"");
                    intent.putExtra("score", current.getScore()+"");
                    intent.putExtra("appreciations", current.getAppreciations_count()+"");
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 1);
                    itemView.getContext().startActivity(intent);
                }
            });
            i3_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("profileimage", current.getProfileImageUrl());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("imageurl", current.arr_images.get(0));
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("responses", current.getResponses()+"");
                    intent.putExtra("score", current.getScore()+"");
                    intent.putExtra("appreciations", current.getAppreciations_count()+"");
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 0);
                    itemView.getContext().startActivity(intent);
                }
            });
            i3_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("profileimage", current.getProfileImageUrl());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("imageurl", current.arr_images.get(1));
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("responses", current.getResponses()+"");
                    intent.putExtra("score", current.getScore()+"");
                    intent.putExtra("appreciations", current.getAppreciations_count()+"");
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 1);
                    itemView.getContext().startActivity(intent);
                }
            });
            i3_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("profileimage", current.getProfileImageUrl());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("imageurl", current.arr_images.get(2));
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("responses", current.getResponses()+"");
                    intent.putExtra("score", current.getScore()+"");
                    intent.putExtra("appreciations", current.getAppreciations_count()+"");
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 2);
                    itemView.getContext().startActivity(intent);
                }
            });
            i4_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("profileimage", current.getProfileImageUrl());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("imageurl", current.arr_images.get(0));
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("responses", current.getResponses()+"");
                    intent.putExtra("score", current.getScore()+"");
                    intent.putExtra("appreciations", current.getAppreciations_count()+"");
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 0);
                    itemView.getContext().startActivity(intent);
                }
            });
            i4_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("profileimage", current.getProfileImageUrl());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("imageurl", current.arr_images.get(1));
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("responses", current.getResponses()+"");
                    intent.putExtra("score", current.getScore()+"");
                    intent.putExtra("appreciations", current.getAppreciations_count()+"");
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 1);
                    itemView.getContext().startActivity(intent);
                }
            });
            i4_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("profileimage", current.getProfileImageUrl());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("imageurl", current.arr_images.get(2));
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("responses", current.getResponses()+"");
                    intent.putExtra("score", current.getScore()+"");
                    intent.putExtra("appreciations", current.getAppreciations_count()+"");
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 2);
                    itemView.getContext().startActivity(intent);
                }
            });
            i4_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("profileimage", current.getProfileImageUrl());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("imageurl", current.arr_images.get(3));
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("responses", current.getResponses()+"");
                    intent.putExtra("score", current.getScore()+"");
                    intent.putExtra("appreciations", current.getAppreciations_count()+"");
                    intent.putExtra("isMainFeedAdapter", "true");
                    intent.putExtra("position", 3);
                    itemView.getContext().startActivity(intent);
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    //sharingIntent.setType("image/*");
                    String link="";
                    String shareBody = current.getDesc();
                    if(current.getPost_ques()==0) {
                        link = "https://www.firstpinch.com/club/" + current.getMainfeed_house_id() +
                                "/" + current.getHouseName().replaceAll(" ","-") + "/story/" +
                                current.getMainfeed_question_id() + "/"+current.getTitle().replaceAll(" ","-");
                        Log.e("Shared link", "" + link);
                    }else {
                        link = "https://www.firstpinch.com/club/" + current.getMainfeed_house_id() +
                                "/" + current.getHouseName().replaceAll(" ","-") + "/question/" +
                                current.getMainfeed_question_id() + "/"+current.getTitle().replaceAll(" ","-");
                        Log.e("Shared link", "" + link);
                    }
                    String title = "Download FirstPinch App to know more - " + current.getTitle() + "";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
                    //sharingIntent.putExtra(Intent.EXTRA_STREAM, current.getTitle());
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sent from First Pinch App\n" + shareBody + "\nDownload FirstPinch App to know more - "+link);
                    itemView.getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            });

            tv_see_all_comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), MainFeedDetailedActivity.class);
                    intent.putExtra("housename", current.getHouseName());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("profileusername", current.getProfileUsername());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("desc", current.getDesc());
                    intent.putExtra("totalshares", current.getTotalShares());
                    intent.putExtra("totalcomments", current.getTotalComments());
                    intent.putExtra("appreciation_count", "" + current.getAppreciations_count());
                    intent.putExtra("score_count", current.getScore());
                    intent.putExtra("responses_count", "" + current.getResponses());
                    intent.putExtra("rating", current.getUser_rate());
                    intent.putExtra("houseimageurl", current.getHouseImageUrl());
                    intent.putExtra("profileimageurl", current.getProfileImageUrl());
                    intent.putExtra("post_ques", current.getPost_ques());
                    intent.putExtra("num_of_images", current.getNum_of_images());
                    intent.putExtra("comment", " ");
                    intent.putExtra("question_id", String.valueOf(current.getMainfeed_question_id()));
                    intent.putExtra("rate", current.getUser_rate());
                    intent.putExtra("bookmark_status", current.getBookmark_status());
                    intent.putExtra("bookmark_id", current.getBookmark_id());
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("activity_name", "profileactivity");
                    ((ProfileActivity) c).startActivityForResult(intent,POST_FEED_DELETE);
                }
            });

            tv_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), MainFeedDetailedActivity.class);
                    intent.putExtra("housename", current.getHouseName());
                    intent.putExtra("profilename", current.getProfileName());
                    intent.putExtra("profileusername", current.getProfileUsername());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("desc", current.getDesc());
                    intent.putExtra("totalshares", current.getTotalShares());
                    intent.putExtra("totalcomments", current.getTotalComments());
                    intent.putExtra("appreciation_count", "" + current.getAppreciations_count());
                    intent.putExtra("score_count", current.getScore());
                    intent.putExtra("responses_count", "" + current.getResponses());
                    intent.putExtra("rating", current.getUser_rate());
                    intent.putExtra("houseimageurl", current.getHouseImageUrl());
                    intent.putExtra("profileimageurl", current.getProfileImageUrl());
                    intent.putExtra("post_ques", current.getPost_ques());
                    intent.putExtra("num_of_images", current.getNum_of_images());
                    intent.putExtra("comment", " ");
                    intent.putExtra("question_id", String.valueOf(current.getMainfeed_question_id()));
                    intent.putExtra("rate", current.getUser_rate());
                    intent.putExtra("bookmark_status", current.getBookmark_status());
                    intent.putExtra("bookmark_id", current.getBookmark_id());
                    intent.putStringArrayListExtra("arr_images", current.arr_images);
                    intent.putExtra("activity_name", "profileactivity");
                    ((ProfileActivity) c).startActivityForResult(intent,POST_FEED_DELETE);
                }
            });

            preview_cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFeedObject current = data.get(getAdapterPosition());
                    SharedPreferences prefs = c.getSharedPreferences("webviewurl", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("url", current.getPreview_link());
                    editor.commit();
                    c.startActivity(new Intent(c, WebView_Common.class).putExtra("url", current.getPreview_link()));
                }
            });

            hint_comm_profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        MainFeedObject current = data.get(getAdapterPosition());
                        final Intent intent;
                        intent = new Intent(itemView.getContext(), ProfileActivity.class);
                        intent.putExtra("profile_id", "" + current.getHint_comm_profile_id());
                        intent.putExtra("username", current.getHint_comm_profile_username());
                        itemView.getContext().startActivity(intent);
                    } catch (Exception e) {
                        Log.e("MainFeedAdapter", "feed_bookmark" + e.toString());
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(c, e.toString());
                    }
                }
            });

            hint_comm_profile_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        MainFeedObject current = data.get(getAdapterPosition());
                        final Intent intent;
                        intent = new Intent(itemView.getContext(), ProfileActivity.class);
                        intent.putExtra("profile_id", "" + current.getHint_comm_profile_id());
                        intent.putExtra("username", current.getHint_comm_profile_username());
                        itemView.getContext().startActivity(intent);
                    } catch (Exception e) {
                        Log.e("MainFeedAdapter", "feed_bookmark" + e.toString());
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(c, e.toString());
                    }
                }
            });

            hint_comm_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        MainFeedObject current = data.get(getAdapterPosition());
                        final Intent intent;
                        intent = new Intent(itemView.getContext(), MainFeedDetailedActivity.class);
                        intent.putExtra("housename", current.getHouseName());
                        intent.putExtra("profilename", current.getProfileName());
                        intent.putExtra("profileusername", current.getProfileUsername());
                        intent.putExtra("title", current.getTitle());
                        intent.putExtra("desc", current.getDesc());
                        intent.putExtra("appreciation_count", "" + current.getAppreciations_count());
                        intent.putExtra("score_count", current.getScore());
                        intent.putExtra("responses_count", "" + current.getResponses());
                        intent.putExtra("houseimageurl", current.getHouseImageUrl());
                        intent.putExtra("profileimageurl", current.getProfileImageUrl());
                        intent.putExtra("post_ques", current.getPost_ques());
                        intent.putExtra("num_of_images", current.getNum_of_images());
                        intent.putExtra("comment", " ");
                        intent.putExtra("question_id", current.getMainfeed_question_id());
                        intent.putExtra("rate", current.getUser_rate());
                        intent.putExtra("bookmark_status", current.getBookmark_status());
                        intent.putExtra("bookmark_id", current.getBookmark_id());
                        intent.putStringArrayListExtra("arr_images", current.arr_images);
                        intent.putExtra("activity_name", "profileactivity");
                        //if(position>0)
                        //intent.putExtra("second_image",data.get(position).getImageUrl());
                        ((ProfileActivity) c).startActivityForResult(intent,POST_FEED_DELETE);
                    } catch (Exception e) {
                        Log.e("MainFeedAdapter", "feed_bookmark" + e.toString());
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(c, e.toString());
                    }
                }
            });

            hint_comm_profile_image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        MainFeedObject current = data.get(getAdapterPosition());
                        final Intent intent;
                        intent = new Intent(itemView.getContext(), ProfileActivity.class);
                        intent.putExtra("profile_id", "" + current.getHint_comm_profile_id2());
                        intent.putExtra("username", current.getHint_comm_profile_username2());
                        itemView.getContext().startActivity(intent);
                    } catch (Exception e) {
                        Log.e("MainFeedAdapter", "feed_bookmark" + e.toString());
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(c, e.toString());
                    }
                }
            });

            hint_comm_profile_name2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        MainFeedObject current = data.get(getAdapterPosition());
                        final Intent intent;
                        intent = new Intent(itemView.getContext(), ProfileActivity.class);
                        intent.putExtra("profile_id", "" + current.getHint_comm_profile_id2());
                        intent.putExtra("username", current.getHint_comm_profile_username2());
                        itemView.getContext().startActivity(intent);
                    } catch (Exception e) {
                        Log.e("MainFeedAdapter", "feed_bookmark" + e.toString());
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(c, e.toString());
                    }
                }
            });

            hint_comm_title2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        MainFeedObject current = data.get(getAdapterPosition());
                        final Intent intent;
                        intent = new Intent(itemView.getContext(), MainFeedDetailedActivity.class);
                        intent.putExtra("housename", current.getHouseName());
                        intent.putExtra("profilename", current.getProfileName());
                        intent.putExtra("profileusername", current.getProfileUsername());
                        intent.putExtra("title", current.getTitle());
                        intent.putExtra("desc", current.getDesc());
                        intent.putExtra("appreciation_count", "" + current.getAppreciations_count());
                        intent.putExtra("score_count", current.getScore());
                        intent.putExtra("responses_count", "" + current.getResponses());
                        intent.putExtra("houseimageurl", current.getHouseImageUrl());
                        intent.putExtra("profileimageurl", current.getProfileImageUrl());
                        intent.putExtra("post_ques", current.getPost_ques());
                        intent.putExtra("num_of_images", current.getNum_of_images());
                        intent.putExtra("comment", " ");
                        intent.putExtra("question_id", current.getMainfeed_question_id());
                        intent.putExtra("rate", current.getUser_rate());
                        intent.putExtra("bookmark_status", current.getBookmark_status());
                        intent.putExtra("bookmark_id", current.getBookmark_id());
                        intent.putExtra("activity_name", "profileactivity");
                        intent.putStringArrayListExtra("arr_images", current.arr_images);
                        //if(position>0)
                        //intent.putExtra("second_image",data.get(position).getImageUrl());
                        ((ProfileActivity) c).startActivityForResult(intent,POST_FEED_DELETE);
                    } catch (Exception e) {
                        Log.e("MainFeedAdapter", "feed_bookmark" + e.toString());
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(c, e.toString());
                    }
                }
            });

        }
    }
}