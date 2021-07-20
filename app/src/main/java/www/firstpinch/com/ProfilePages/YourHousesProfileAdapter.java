package www.firstpinch.com.firstpinch2.ProfilePages;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.HouseProfilePages.HouseProfileActivity;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedDetailedCommentsAdapter;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedDetailedCommentsObject;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 16-09-2016.
 */
//adapter for yourHousesProfile Fragment recyclerview
public class YourHousesProfileAdapter extends RecyclerView.Adapter<YourHousesProfileAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<YourHousesProfileObject> data = Collections.emptyList();
    Context c;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;
    int user_id;
    public static String POST_JOIN_HOUSE_URL = "http://54.169.84.123/api/user_clubs",
            POST_EXIT_HOUSE_URL = "http://54.169.84.123/api/user_clubs/1";
    ProgressDialog progressDialog;
    Boolean status = false;
    MyViewHolder holder;

    public YourHousesProfileAdapter(Context context, List<YourHousesProfileObject> data) {
        inflator = LayoutInflater.from(context);
        this.data = data;
        c = context;
        SharedPreferences sp = c.getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.your_houses_item2, parent, false);
        holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        final YourHousesProfileObject current = data.get(position);
        Typeface custom_font = Typeface.createFromAsset(c.getAssets(), "fonts/Cabin-Regular.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(c.getAssets(), "fonts/Lato-Regular.ttf");
        holder.tv_yourhouseName.setTypeface(custom_font);
        holder.tv_yourhousedesc.setTypeface(custom_font1);
        holder.tv_yourhouseName.setText(current.getYourHouseName().toString());
        holder.tv_yourhousedesc.setText(current.getYourHouseDesc().toString());

        if (current.getJoin_exit() == 0) {
            holder.join_exit.setText("Join");
            holder.join_exit.setTextColor(0xff148305);
        } else {
            holder.join_exit.setTextColor(Color.BLACK);
            holder.join_exit.setText("Leave");
        }

        /*holder.join_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.follow.getText().toString().contentEquals("EXIT")) {
                    holder.follow.setText("JOIN");
                    holder.follow.setTextColor(Color.GREEN);
                    current.setHouse_check(false);
                } else if(holder.follow.getText().toString().contentEquals("JOIN")) {
                    holder.follow.setText("EXIT");
                    holder.follow.setTextColor(Color.GRAY);
                    current.setHouse_check(true);
                }
            }
        });*/

        Picasso.with(c)
                .load(current.getYourHouseImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.im_yourhouseImage);


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
            final YourHousesProfileObject current = data.get(position);
            if (current.getJoin_exit() == 0) {
                holder.join_exit.setText("Join");
                holder.join_exit.setTextColor(0xff148305);
            } else {
                holder.join_exit.setTextColor(Color.BLACK);
                holder.join_exit.setText("Leave");
            }
        }

    }

    //join request for house API request
    public void joinRequest(final String house_id, final int position) {

        StringRequest myReq = new StringRequest(Request.Method.POST,
                POST_JOIN_HOUSE_URL,
                createMyReqSuccessListener(position),
                createMyReqErrorListener()) {

            @Override
            public byte[] getBody() {
                String str = "{\"user\":{\"user_id\":\"" + user_id + "\"" +
                        ",\"club_id\":\"" + house_id + "\"}}";
                Log.e("data", "" + str);
                return str.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        RequestQueue queue = Volley.newRequestQueue(c);
        queue.add(myReq);
        /*progressDialog = new ProgressDialog(c);
        progressDialog.setMessage("Joining...");
        progressDialog.show();*/

    }

    //response for join request for house API request
    private Response.Listener<String> createMyReqSuccessListener(final int position) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("SelectInterests", "data from server - " + response);
                if (response != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        status = jsonObj.getBoolean("status");
                        if (status) {
                            /*YourHousesProfileObject current = data.get(position);
                            current.setJoin_exit(1);
                            //holder.progressBar.setVisibility(View.GONE);
                            notifyItemChanged(position);*/
                            /*Toast.makeText(c,
                                    "" + response,
                                    Toast.LENGTH_LONG)
                                    .show();*/
                        /*startActivity(new Intent(HouseRecyclerView.this, HouseRecyclerView.class));
                        finish();*/
                        } else {
                            /*Toast.makeText(c,
                                    "" + response,
                                    Toast.LENGTH_LONG)
                                    .show();*/
                        }

                    } catch (final JSONException e) {
                        Log.e("SelectInterests", "Json parsing error: " + e.getMessage());
                        /*Toast.makeText(c,
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();*/
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(c, e.toString());
                    }
                } else {
                    Log.e("SelectInterests", "Couldn't get json from server.");
                    Toast.makeText(c,
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG)
                            .show();
                }
                //progressDialog.dismiss();
            }
        };
    }

    //error response join request for house API request
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SignUp", "error connect - " + error);
                //progressDialog.dismiss();
            }
        };
    }

    //exit request for house API request
    private void exitRequest(final String house_id, final int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                POST_EXIT_HOUSE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("YourHousesProfileAdpter", "data from server - " + response);
                        if (response != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                status = jsonObj.getBoolean("status");
                                if (status) {
                                    /*YourHousesProfileObject current = data.get(position);
                                    current.setJoin_exit(0);
                                    //holder.progressBar.setVisibility(View.GONE);
                                    notifyItemChanged(position);*/
                                    /*Toast.makeText(c,
                                            "" + response,
                                            Toast.LENGTH_LONG)
                                            .show();*/
                                } else {
                                    /*Toast.makeText(c,
                                            ""+response,
                                            Toast.LENGTH_LONG)
                                            .show();*/
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
                headers.put("User-Id", String.valueOf(user_id));
                headers.put("Club-Id", house_id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);

    }

    public List<YourHousesProfileObject> getList() {
        return data;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView im_yourhouseImage;
        TextView tv_yourhouseName, tv_yourhousedesc;
        Button join_exit;
        ProgressBar progressBar;

        public MyViewHolder(final View itemView) {
            super(itemView);

            tv_yourhouseName = (TextView) itemView.findViewById(R.id.house_card_housename);
            tv_yourhousedesc = (TextView) itemView.findViewById(R.id.house_card_housedesc);
            im_yourhouseImage = (ImageView) itemView.findViewById(R.id.house_card_image);
            join_exit = (Button) itemView.findViewById(R.id.house_card_enter_exit);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

            //all onClick listeners in the view below
            join_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (join_exit.getText() == "Leave") {
                        final YourHousesProfileObject current = data.get(getAdapterPosition());
                        AlertDialog.Builder builder = new AlertDialog.Builder(c);
                        builder.setIcon(R.drawable.ic_dialog_alert);
                                builder.setTitle("Leave?");
                                builder.setMessage("Are you sure you want to leave this club?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //progressBar.setVisibility(View.VISIBLE);
                                        current.setJoin_exit(0);
                                        notifyItemChanged(getAdapterPosition(),join_exit);
                                        exitRequest(current.getId(), getAdapterPosition());
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
                        YourHousesProfileObject current = data.get(getAdapterPosition());
                        //progressBar.setVisibility(View.VISIBLE);
                        current.setJoin_exit(1);
                        notifyItemChanged(getAdapterPosition(),join_exit);
                        joinRequest(current.getId(),getAdapterPosition());

                    }
                }
            });

            im_yourhouseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        YourHousesProfileObject current = data.get(getAdapterPosition());
                        final Intent intent;
                        intent = new Intent(itemView.getContext(), HouseProfileActivity.class);
                        intent.putExtra("housename", current.getYourHouseName());
                        intent.putExtra("housedesc", current.getYourHouseDesc());
                        intent.putExtra("imageurl", current.getYourHouseImageUrl());
                        intent.putExtra("house_id", "" + current.getId());
                        itemView.getContext().startActivity(intent);
                    }catch (Exception e){
                        Log.e("YourHouseProflAdaptr","im_yourhouseimage on click listener"+e.toString());
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(c, e.toString());
                    }
                }
            });

            tv_yourhousedesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        YourHousesProfileObject current = data.get(getAdapterPosition());
                        final Intent intent;
                        intent = new Intent(itemView.getContext(), HouseProfileActivity.class);
                        intent.putExtra("housename", current.getYourHouseName());
                        intent.putExtra("housedesc", current.getYourHouseDesc());
                        intent.putExtra("imageurl", current.getYourHouseImageUrl());
                        intent.putExtra("house_id", "" + current.getId());
                        itemView.getContext().startActivity(intent);
                    }catch (Exception e){
                        Log.e("YourHouseProflAdaptr","im_yourhouseimage on click listener"+e.toString());
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(c, e.toString());
                    }
                }
            });

            tv_yourhouseName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        YourHousesProfileObject current = data.get(getAdapterPosition());
                        final Intent intent;
                        intent = new Intent(itemView.getContext(), HouseProfileActivity.class);
                        intent.putExtra("housename", current.getYourHouseName());
                        intent.putExtra("housedesc", current.getYourHouseDesc());
                        intent.putExtra("imageurl", current.getYourHouseImageUrl());
                        intent.putExtra("house_id", "" + current.getId());
                        itemView.getContext().startActivity(intent);
                    }catch (Exception e){
                        Log.e("YourHouseProflAdaptr","im_yourhouseimage on click listener"+e.toString());
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(c, e.toString());
                    }
                }
            });

        }
    }
}
