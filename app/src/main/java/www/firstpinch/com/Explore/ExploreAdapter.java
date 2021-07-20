package www.firstpinch.com.firstpinch2.Explore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.util.Collections;
import java.util.List;

import www.firstpinch.com.firstpinch2.HouseProfilePages.HouseProfileActivity;
import www.firstpinch.com.firstpinch2.HouseRecyclerView;
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileObject;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 16-09-2016.
 */

//adapter for explore interests
public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<ExploreObject> data = Collections.emptyList();
    Context c;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    public ExploreAdapter(Context context, List<ExploreObject> data) {
        inflator = LayoutInflater.from(context);
        this.data = data;
        c = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.explore_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        final ExploreObject current = data.get(position);
        holder.tv_exp_interest_name.setText(current.getExp_interest_name());

        Picasso.with(c)
                .load(current.getExp_interest_image())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.im_exp_interest_image);

        //onClick on the interest image to open its houses
        holder.im_exp_interest_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                intent = new Intent(holder.itemView.getContext(), ExploreHousesRecyclerView.class);
                intent.putExtra("interestname", current.getExp_interest_name());
                intent.putExtra("interest_id", current.getInterest_id());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        //setFadeAnimation(holder.itemView);
        //setAnimation(holder.itemView, position);
        //animate(holder);
        //Log.e("text=",""+current.getHouseName());


    }

    public List<ExploreObject> getList() {

        return data;
    }


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
        ImageView im_exp_interest_image;
        TextView tv_exp_interest_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_exp_interest_name = (TextView) itemView.findViewById(R.id.exp_interest_text);
            im_exp_interest_image = (ImageView) itemView.findViewById(R.id.exp_interest_image);

        }
    }
}