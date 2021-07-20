package www.firstpinch.com.firstpinch2.Search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import www.firstpinch.com.firstpinch2.HouseProfilePages.HouseMemberRecyclerObject;
import www.firstpinch.com.firstpinch2.HouseProfilePages.HouseMembreRecyclerAdapter;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedDetailedActivity;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedObject;
import www.firstpinch.com.firstpinch2.MainFeed.RoundedCornersTransform;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 03-10-2016.
 */
public class SearchQuestionsFragmentAdapter extends RecyclerView.Adapter<SearchQuestionsFragmentAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<SearchQuestionsFragmentObject> data = Collections.emptyList();
    Context c;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    public SearchQuestionsFragmentAdapter(Context context, List<SearchQuestionsFragmentObject> data) {
        inflator = LayoutInflater.from(context);
        this.data = data;
        c = context;
    }

    @Override
    public SearchQuestionsFragmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.search_ques_recyclerview_item, parent, false);
        SearchQuestionsFragmentAdapter.MyViewHolder holder = new SearchQuestionsFragmentAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SearchQuestionsFragmentAdapter.MyViewHolder holder, int position) {


        final SearchQuestionsFragmentObject current = data.get(position);
        holder.tv_ques_title.setText(current.getQuestionTitle());
        Picasso.with(c)
                .load(current.getProfileImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(holder.profile_image);

    }

    public List<SearchQuestionsFragmentObject> getList() {

        return data;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(c, R.anim.anticipateovershoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

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

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView tv_ques_title;

        public MyViewHolder(final View itemView) {
            super(itemView);

            tv_ques_title = (TextView) itemView.findViewById(R.id.search_ques_title);




            profile_image = (ImageView) itemView.findViewById(R.id.profile_image);
            tv_ques_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchQuestionsFragmentObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), MainFeedDetailedActivity.class);
                    intent.putExtra("profileimageurl", current.getProfileImageUrl());
                    intent.putExtra("title", current.getQuestionTitle());
                    intent.putExtra("question_id", current.getId());
                    intent.putExtra("comment", " ");
                    intent.putExtra("post_ques", 1);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}