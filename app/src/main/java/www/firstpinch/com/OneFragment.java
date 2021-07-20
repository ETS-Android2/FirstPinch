package www.firstpinch.com.firstpinch2;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Rianaa Admin on 06-09-2016.
 */
public class  OneFragment extends Fragment{

    //ImageView im_ws1;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.welcome_slide1, container, false);

        //im_ws1 = (ImageView) view.findViewById(R.id.im_ws1);
        /*Ion.with(im_ws1)
                .error(R.drawable.ws1)
                .animateGif(AnimateGifMode.ANIMATE)
                .load("file:///android_asset/firstgif.gif");*/
        Typeface custom_font2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");
        TextView tv_intro_screen_title = (TextView) view.findViewById(R.id.intro_screen_title1);
        tv_intro_screen_title.setTypeface(custom_font2);

        return view;
    }
}