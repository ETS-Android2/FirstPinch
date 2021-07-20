package www.firstpinch.com.firstpinch2;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

/**
 * Created by Rianaa Admin on 28-12-2016.
 */

public class OnTheWay extends AppCompatActivity {

    ImageView im_animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ontheway);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/KeepCalm-Medium.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");

        ImageView animal = (ImageView) findViewById(R.id.image_animal);
        TextView tv = (TextView) findViewById(R.id.tv_text);
        TextView tvc = (TextView) findViewById(R.id.tv_text_content);
        im_animal = (ImageView) findViewById(R.id.image_animal);
        Ion.with(im_animal)
                        .error(R.drawable.ws1)
                        .animateGif(AnimateGifMode.ANIMATE)
                        .load("file:///android_asset/animal.gif");


        TextView tv1 = (TextView) findViewById(R.id.tv_text1);
        TextView tvc1 = (TextView) findViewById(R.id.tv_text_content1);


        TextView tv2 = (TextView) findViewById(R.id.tv_text2);
        TextView tvc2 = (TextView) findViewById(R.id.tv_text_content2);


        TextView tv3 = (TextView) findViewById(R.id.tv_text3);
        TextView tvc3 = (TextView) findViewById(R.id.tv_text_content3);

        TextView tvc4 = (TextView) findViewById(R.id.tv_text4);


        tv.setTypeface(custom_font1);
        tvc.setTypeface(custom_font1);

        tv1.setTypeface(custom_font1);
        tvc1.setTypeface(custom_font1);

        tv2.setTypeface(custom_font1);
        tvc2.setTypeface(custom_font1);

        tv3.setTypeface(custom_font1);
        tvc3.setTypeface(custom_font1);

        tvc4.setTypeface(custom_font);


    }
}