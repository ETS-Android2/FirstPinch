package www.firstpinch.com.firstpinch2;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import www.firstpinch.com.firstpinch2.MainFeed.RoundedCornersTransform;

/**
 * Created by Rianaa Admin on 31-10-2016.
 */

public class ImageViewActivity extends AppCompatActivity {

    ImageView im, im_profile_img, im_back_button;
    TextView tv_heading_text, tv_title, tv_responses, tv_score, tv_appreciations,
            text_responses, text_score, text_appreciations;
    String imageUrl = "", profile_imageUrl = "", responses, score, appreciations, isMainFeedAdapter;
    ViewPager viewPager;
    ArrayList<String> arr_images = new ArrayList<String>();
    ViewFlipper viewFlipper;
    private float lastX;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageview_activity);
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");

        //im = (ImageView) findViewById(R.id.imageView);
        im_profile_img = (ImageView) findViewById(R.id.im_profile_img);
        tv_heading_text = (TextView) findViewById(R.id.im_heading_text);
        tv_title = (TextView) findViewById(R.id.im_title);
        im_back_button = (ImageView) findViewById(R.id.post_cross_img);
        tv_responses = (TextView) findViewById(R.id.tv_responses);
        tv_score = (TextView) findViewById(R.id.tv_score);
        tv_appreciations = (TextView) findViewById(R.id.tv_appreciations);
        text_responses = (TextView) findViewById(R.id.text_responses);
        text_score = (TextView) findViewById(R.id.text_score);
        text_appreciations = (TextView) findViewById(R.id.text_appreciations);
        text_responses.setTypeface(custom_font1);
        text_score.setTypeface(custom_font1);
        text_appreciations.setTypeface(custom_font1);
        tv_score.setTypeface(custom_font1);
        tv_responses.setTypeface(custom_font1);
        tv_appreciations.setTypeface(custom_font1);

        imageUrl = getIntent().getStringExtra("imageurl");
        profile_imageUrl = getIntent().getStringExtra("profileimage");
        tv_heading_text.setText(getIntent().getStringExtra("profilename"));
        tv_title.setText(Html.fromHtml(getIntent().getStringExtra("title")));
        arr_images = getIntent().getStringArrayListExtra("arr_images");
        tv_responses.setText(getIntent().getStringExtra("responses"));
        tv_score.setText(getIntent().getStringExtra("score"));
        tv_appreciations.setText(getIntent().getStringExtra("appreciations"));
        isMainFeedAdapter = getIntent().getStringExtra("isMainFeedAdapter");
        position = getIntent().getIntExtra("position", 0);

        if (isMainFeedAdapter.contentEquals("true")) {
            text_responses.setVisibility(View.VISIBLE);
            text_score.setVisibility(View.GONE);
            text_appreciations.setVisibility(View.VISIBLE);
            tv_responses.setVisibility(View.VISIBLE);
            tv_score.setVisibility(View.GONE);
            Picasso.with(this)
                    .load(profile_imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .transform(new RoundedCornersTransform())
                    .into(im_profile_img);
        } else if (isMainFeedAdapter.contentEquals("profileActivity")) {
            text_responses.setVisibility(View.GONE);
            text_score.setVisibility(View.GONE);
            text_appreciations.setVisibility(View.GONE);
            tv_responses.setVisibility(View.GONE);
            tv_score.setVisibility(View.GONE);
        } else {
            text_responses.setVisibility(View.GONE);
            text_score.setVisibility(View.GONE);
            text_appreciations.setVisibility(View.VISIBLE);
            tv_responses.setVisibility(View.GONE);
            tv_score.setVisibility(View.GONE);
            Picasso.with(this)
                    .load(profile_imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .transform(new RoundedCornersTransform())
                    .into(im_profile_img);
        }
 /*Picasso.with(this)
                .load(imageUrl)
                .into(im);*/
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        /*if (arr_images.size() == 1) {

        } else {*/
        for (int i = 0; i < arr_images.size(); i++) {
            //  This will create dynamic image view and add them to ViewFlipper
            setFlipperImage(arr_images.get(i));
        }
        viewFlipper.setDisplayedChild(position);
        //}
        im_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setFlipperImage(String image_url) {
        Log.i("Set Filpper Called", image_url + "");
        ImageView image = new ImageView(getApplicationContext());
        Picasso.with(this)
                .load(image_url)
                .placeholder(R.drawable.placeholder_image)
                .into(image);
        if(isMainFeedAdapter.contentEquals("profileActivity")){

        } else {
            image.setAdjustViewBounds(true);
        }
        viewFlipper.addView(image);
    }

    // Method to handle touch event like left to right swap and right to left swap
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            // when user first touches the screen to swap
            case MotionEvent.ACTION_DOWN: {
                lastX = touchevent.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float currentX = touchevent.getX();

                // if left to right swipe on screen
                if (lastX < currentX) {
                    // If no more View/Child to flip
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;

                    // set the required Animation type to ViewFlipper
                    // The Next screen will come in form Left and current Screen will go OUT from Right
                    viewFlipper.setInAnimation(this, R.anim.in_from_left);
                    viewFlipper.setOutAnimation(this, R.anim.out_to_right);
                    // Show the next Screen
                    viewFlipper.showPrevious();
                }

                // if right to left swipe on screen
                if (lastX > currentX) {
                    if (arr_images.size() == 1) {
                        break;
                    }
                    if (viewFlipper.getDisplayedChild() == arr_images.size() - 1)
                        break;
                    // set the required Animation type to ViewFlipper
                    // The Next screen will come in form Right and current Screen will go OUT from Left
                    viewFlipper.setInAnimation(this, R.anim.in_from_right);
                    viewFlipper.setOutAnimation(this, R.anim.out_to_left);
                    // Show The Previous Screen
                    viewFlipper.showNext();
                }
                break;
            }
        }
        return false;
    }


}