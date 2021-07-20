package www.firstpinch.com.firstpinch2.HouseProfilePages;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

import www.firstpinch.com.firstpinch2.MainFeed.RoundedCornersTransform;
import www.firstpinch.com.firstpinch2.NotificationPages.YourNotificatioinsObject;
import www.firstpinch.com.firstpinch2.NotificationPages.YourNotificationsRecyclerViewAdapter;
import www.firstpinch.com.firstpinch2.ProfilePages.ProfileActivity;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 27-09-2016.
 */

//adapter for house member in house pages
public class HouseMembreRecyclerAdapter extends RecyclerView.Adapter<HouseMembreRecyclerAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<HouseMemberRecyclerObject> data = Collections.emptyList();
    Context c;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    public HouseMembreRecyclerAdapter(Context context, List<HouseMemberRecyclerObject> data) {
        inflator = LayoutInflater.from(context);
        this.data = data;
        c = context;
    }

    @Override
    public HouseMembreRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.member_fragment_recyclerview_item, parent, false);
        HouseMembreRecyclerAdapter.MyViewHolder holder = new HouseMembreRecyclerAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HouseMembreRecyclerAdapter.MyViewHolder holder, int position) {


        final HouseMemberRecyclerObject current = data.get(position);
        holder.tv_member_name.setText(current.getMemberName());
        holder.tv_member_username.setText("@"+current.getMemberUserName());

        Picasso.with(c)
                .load(current.getMemberProfileImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(holder.im_member_profile_image);

        //setFadeAnimation(holder.itemView);
        //setAnimation(holder.itemView, position);
        //animate(holder);
        //Log.e("text=",""+current.getHouseName());


    }

    ///get dataList from any other class
    public List<HouseMemberRecyclerObject> getList() {
        return data;
    }

    //get size of data in the list
    @Override
    public int getItemCount() {
        return data.size();
    }

    //for animation(currently not used)
    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(c, R.anim.anticipateovershoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    //for animation(currently not used)
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

    //for animation(currently not used)
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView im_member_profile_image;
        TextView tv_member_name,tv_member_username;
        CardView member_card;

        public MyViewHolder(final View itemView) {
            super(itemView);

            tv_member_name = (TextView) itemView.findViewById(R.id.member_profile_name);
            tv_member_username = (TextView) itemView.findViewById(R.id.member_profile_username);
            im_member_profile_image = (ImageView) itemView.findViewById(R.id.member_profile_image);
            member_card = (CardView) itemView.findViewById(R.id.member_pin_cardview);

            //on click on member card to open profileActivity
            member_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HouseMemberRecyclerObject current = data.get(getAdapterPosition());
                    Intent intent;
                    intent = new Intent(itemView.getContext(), ProfileActivity.class);
                    intent.putExtra("profilename", current.getMemberName());
                    intent.putExtra("username", current.getMemberUserName());
                    intent.putExtra("imageurl", current.getMemberProfileImageUrl());
                    intent.putExtra("profile_id", "" + current.getMemberId());
                    intent.putExtra("bio", current.getMemberBio());
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
