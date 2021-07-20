package www.firstpinch.com.firstpinch2.NotificationPages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import www.firstpinch.com.firstpinch2.FontManager;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedDetailedActivity;
import www.firstpinch.com.firstpinch2.MainFeed.RoundedCornersTransform;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 13-09-2016.
 */

//adapter for house notifications recyclerview
public class HouseNotificationsRecyclerViewAdapter extends RecyclerView.Adapter<HouseNotificationsRecyclerViewAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<HouseNotificationsObject> data = Collections.emptyList();
    Context c;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;
    Typeface iconFont;

    public HouseNotificationsRecyclerViewAdapter(Context context, List<HouseNotificationsObject> data) {
        inflator = LayoutInflater.from(context);
        this.data = data;
        c = context;
        iconFont = FontManager.getTypeface(c, FontManager.FONTAWESOME);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.house_notifications_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        final HouseNotificationsObject current = data.get(position);
        String str = "";
        str = Html.fromHtml(current.getNotifhText()).toString();
        String test = str.replaceAll("\\n", "");
        holder.tv_notifhText.setText(test);

        String str2 = Html.fromHtml(current.getNotifhTitle()).toString();
        String test2 = str2.replaceAll("\\n", "");
        holder.tv_notif_title.setText(test2);
        if(current.getNotifhTitle().contentEquals("\"\"")){
            holder.tv_notif_title.setVisibility(View.GONE);
        } else {
            holder.tv_notif_title.setVisibility(View.VISIBLE);
        }

        //set textView image according to the type of notification
        if (current.getNotifType().contentEquals("StoryPost")) {
            holder.im_notifhIndicatorImageUrl.setText(c.getResources()
                    .getString(R.string.fa_icon_post));
            holder.im_notifhIndicatorImageUrl.setTextColor(0XFFef1ae5);
        } else if (current.getNotifType().contentEquals("QuestionPost")) {
            holder.im_notifhIndicatorImageUrl.setText(c.getResources()
                    .getString(R.string.fa_icon_question));
            holder.im_notifhIndicatorImageUrl.setTextColor(0XFF23ace2);
        } else if (current.getNotifType().contentEquals("comment")) {
            holder.im_notifhIndicatorImageUrl.setText(c.getResources()
                    .getString(R.string.fa_icon_respond));
            holder.im_notifhIndicatorImageUrl.setTextColor(0Xfff900e2);
        } else if (current.getNotifType().contentEquals("answer")) {
            holder.im_notifhIndicatorImageUrl.setText(c.getResources()
                    .getString(R.string.fa_icon_answer));
            holder.im_notifhIndicatorImageUrl.setTextColor(0XFF38ab38);
        } else {
            holder.im_notifhIndicatorImageUrl.setText("@");
            holder.im_notifhIndicatorImageUrl.setTextColor(0XFFffffff);
        }

        Picasso.with(c)
                .load(current.getNotifhProfileImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(holder.im_notifhProfileImageUrl);

    }

    public List<HouseNotificationsObject> getList() {
        return data;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    //for animation (currently not used)
    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(c, R.anim.anticipateovershoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    //for animation (currently not used)
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

    //for animation (currently not used)
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView im_notifhProfileImageUrl;
        TextView tv_notifhText, im_notifhIndicatorImageUrl,tv_notif_title;
        Button r1, r2, r3, r4, r5;

        public MyViewHolder(final View itemView) {
            super(itemView);

            tv_notifhText = (TextView) itemView.findViewById(R.id.notifh_text);
            im_notifhIndicatorImageUrl = (TextView) itemView.findViewById(R.id.notificationh_indicator);
            im_notifhProfileImageUrl = (ImageView) itemView.findViewById(R.id.notifh_profile_image);
            tv_notif_title = (TextView) itemView.findViewById(R.id.notif_title);
            FontManager.markAsIconContainer(im_notifhIndicatorImageUrl, iconFont);


            Typeface custom_font = Typeface.createFromAsset(c.getAssets(), "fonts/Cabin-Regular.ttf");
            Typeface custom_font1 = Typeface.createFromAsset(c.getAssets(), "fonts/Lato-Regular.ttf");

            tv_notif_title.setTypeface(custom_font1);
            tv_notifhText.setTypeface(custom_font1);
            //onClick on item in house notification
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final HouseNotificationsObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), MainFeedDetailedActivity.class);
                    intent.putExtra("post_ques", current.getNotifhPost_Question());
                    intent.putExtra("question_id", current.getNotifhId());
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
