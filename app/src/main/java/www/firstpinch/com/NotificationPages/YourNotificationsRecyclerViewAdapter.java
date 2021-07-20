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

//adapter for yourNotifications recyclerView
public class YourNotificationsRecyclerViewAdapter extends RecyclerView.Adapter<YourNotificationsRecyclerViewAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<YourNotificatioinsObject> data = Collections.emptyList();
    Context c;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;
    Typeface iconFont;

    public YourNotificationsRecyclerViewAdapter(Context context, List<YourNotificatioinsObject> data) {
        inflator = LayoutInflater.from(context);
        this.data = data;
        c = context;
        iconFont = FontManager.getTypeface(c, FontManager.FONTAWESOME);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.your_notifications_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        final YourNotificatioinsObject current = data.get(position);
        holder.tv_notifText.setText(current.getNotifText());

        //String str = current.getNotifTitle();
        /*String notif_title = "";
        if (str != null) {
            notif_title = str.replaceAll("\\<[^>]*>","");
            //notif_title.replaceAll("n", " ");
        }*/
        String str = Html.fromHtml(current.getNotifTitle()).toString();
        String test = str.replaceAll("\\n", "");
        holder.tv_notifUserName.setText(current.getNotifUserName());
        holder.tv_notifPost_question_response.setText(current.getNotifpost_question_response());
        holder.tv_notif_title.setText(test);
        String user_rate = current.getUser_react();

        //select the textView for different type of yourNotifications
        if (current.getNotifType().contentEquals("storyRate")) {
            holder.im_notifIndicatorImageUrl.setText("");
            if (user_rate.contentEquals("1")) {
                holder.im_notifIndicatorImageUrl.setBackgroundResource(R.drawable.ic_1_filled);
            } else if (user_rate.contentEquals("2")) {
                holder.im_notifIndicatorImageUrl.setBackgroundResource(R.drawable.ic_2_filled);
            } else if (user_rate.contentEquals("3")) {
                holder.im_notifIndicatorImageUrl.setBackgroundResource(R.drawable.ic_3_filled);
            }
        } else if (current.getNotifType().contentEquals("questionRate")) {
            holder.im_notifIndicatorImageUrl.setText("");
            if (user_rate.contentEquals("1")) {
                holder.im_notifIndicatorImageUrl.setBackgroundResource(R.drawable.ic_1_filled);
            } else if (user_rate.contentEquals("2")) {
                holder.im_notifIndicatorImageUrl.setBackgroundResource(R.drawable.ic_2_filled);
            } else if (user_rate.contentEquals("3")) {
                holder.im_notifIndicatorImageUrl.setBackgroundResource(R.drawable.ic_3_filled);
            }
        } else if (current.getNotifType().contentEquals("comment")) {
            holder.im_notifIndicatorImageUrl.setText(c.getResources()
                    .getString(R.string.fa_icon_respond));
            holder.im_notifIndicatorImageUrl.setTextColor(0Xfff900e2);
            holder.im_notifIndicatorImageUrl.setBackgroundResource(0);
        } else if (current.getNotifType().contentEquals("answer")) {
            holder.im_notifIndicatorImageUrl.setText(c.getResources()
                    .getString(R.string.fa_icon_answer));
            holder.im_notifIndicatorImageUrl.setTextColor(0XFF38ab38);
            holder.im_notifIndicatorImageUrl.setBackgroundResource(0);
        } else if (current.getNotifType().contentEquals("commenttag")) {
            holder.im_notifIndicatorImageUrl.setText(c.getResources()
                    .getString(R.string.fa_icon_tagging));
            holder.im_notifIndicatorImageUrl.setTextColor(0XFF000ff9);
            holder.im_notifIndicatorImageUrl.setBackgroundResource(0);
        } else if (current.getNotifType().contentEquals("answertag")) {
            holder.im_notifIndicatorImageUrl.setText(c.getResources()
                    .getString(R.string.fa_icon_tagging));
            holder.im_notifIndicatorImageUrl.setTextColor(0XFF000ff9);
            holder.im_notifIndicatorImageUrl.setBackgroundResource(0);
        } else if (current.getNotifType().contentEquals("commentvote")) {
            holder.im_notifIndicatorImageUrl.setText(c.getResources()
                    .getString(R.string.fa_icon_star));
            holder.im_notifIndicatorImageUrl.setTextColor(0XFFfdae31);
            holder.im_notifIndicatorImageUrl.setBackgroundResource(0);
        } else if (current.getNotifType().contentEquals("answervote")) {
            holder.im_notifIndicatorImageUrl.setText(c.getResources()
                    .getString(R.string.fa_icon_star));
            holder.im_notifIndicatorImageUrl.setTextColor(0XFFfdae31);
            holder.im_notifIndicatorImageUrl.setBackgroundResource(0);
        } else {
            holder.im_notifIndicatorImageUrl.setText("@");
            holder.im_notifIndicatorImageUrl.setTextColor(0XFFffffff);
            holder.im_notifIndicatorImageUrl.setBackgroundResource(0);
        }

        Picasso.with(c)
                .load(current.getNotifProfileImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(holder.im_notifProfileImageUrl);

    }

    public List<YourNotificatioinsObject> getList() {

        return data;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    //for animations (currenty not used)
    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(c, R.anim.anticipateovershoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    //for animations (currenty not used)
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

    //for animations (currenty not used)
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView im_notifProfileImageUrl;
        TextView tv_notifUserName, tv_notifText, tv_notifPost_question_response, tv_notif_title, im_notifIndicatorImageUrl;

        public MyViewHolder(final View itemView) {
            super(itemView);

            Typeface custom_font = Typeface.createFromAsset(c.getAssets(), "fonts/Cabin-Regular.ttf");
            Typeface custom_font1 = Typeface.createFromAsset(c.getAssets(), "fonts/Lato-Regular.ttf");


            tv_notifUserName = (TextView) itemView.findViewById(R.id.notif_username);
            tv_notifText = (TextView) itemView.findViewById(R.id.notif_text);
            tv_notifPost_question_response = (TextView) itemView.findViewById(R.id.notif_type);
            tv_notif_title = (TextView) itemView.findViewById(R.id.notif_title);
            im_notifIndicatorImageUrl = (TextView) itemView.findViewById(R.id.notification_indicator);
            im_notifProfileImageUrl = (ImageView) itemView.findViewById(R.id.notif_profile_image);
            FontManager.markAsIconContainer(im_notifIndicatorImageUrl, iconFont);

            tv_notif_title.setTypeface(custom_font1);

            tv_notifText.setTypeface(custom_font1);

            //on CLick on yourNotification item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final YourNotificatioinsObject current = data.get(getAdapterPosition());
                    final Intent intent;
                    intent = new Intent(itemView.getContext(), MainFeedDetailedActivity.class);
                    intent.putExtra("post_ques", current.getNotifPost_Question());
                    intent.putExtra("question_id", current.getNotifId());
                    itemView.getContext().startActivity(intent);
                }
            });
        }

    }
}
