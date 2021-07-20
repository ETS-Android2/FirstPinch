package www.firstpinch.com.firstpinch2;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Rianaa Admin on 08-12-2016.
 */

public class WrapContentLinearLayoutManager extends LinearLayoutManager {
    public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout)    {
        super(context, orientation, reverseLayout);
    }

    //... constructor
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("Exception", "WrapContentLinearLayoutManager"+e.toString());
        }
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

}