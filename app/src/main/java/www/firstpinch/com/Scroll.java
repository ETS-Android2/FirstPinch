package www.firstpinch.com.firstpinch2;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Rianaa Admin on 12-09-2016.
 */
public class Scroll extends FloatingActionButton.Behavior {

    Context c;
    private Animation rotate_backward;
    Home hm = new Home();

    public Scroll(Context context, AttributeSet attributeSet) {
        super();
        this.c = context;
        rotate_backward = AnimationUtils.loadAnimation(c, R.anim.rotate_backward);

    }


    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide();
            coordinatorLayout.getChildAt(5).setVisibility(View.GONE); //fab
            coordinatorLayout.getChildAt(3).setVisibility(View.GONE);  //post ll
            coordinatorLayout.getChildAt(4).setVisibility(View.GONE);  //question ll
            coordinatorLayout.getChildAt(2).setVisibility(View.INVISIBLE);  //framelayout
            coordinatorLayout.getChildAt(5).setClickable(false); //fab
            hm.setFabOpen(true);
            coordinatorLayout.getChildAt(5).startAnimation(rotate_backward);
        } else if (dyConsumed < 0 && child.getVisibility() == View.GONE) {
            child.show();
            coordinatorLayout.getChildAt(5).setClickable(true);
            hm.setFabOpen(false);

            if (coordinatorLayout.getChildAt(3).getVisibility() == View.VISIBLE) {
                coordinatorLayout.getChildAt(3).setVisibility(View.GONE);
                coordinatorLayout.getChildAt(4).setVisibility(View.GONE);
                coordinatorLayout.getChildAt(2).setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

}