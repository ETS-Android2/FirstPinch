package www.firstpinch.com.firstpinch2;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Rianaa Admin on 21-12-2016.
 */

public class FourFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.welcome_slide4, container, false);

        Typeface custom_font2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");
        TextView tv_intro_screen_title = (TextView) view.findViewById(R.id.intro_screen_title4);
        tv_intro_screen_title.setTypeface(custom_font2);

        return view;
    }
}