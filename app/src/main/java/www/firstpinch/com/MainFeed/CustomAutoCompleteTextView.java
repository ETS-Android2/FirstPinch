package www.firstpinch.com.firstpinch2.MainFeed;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by Rianaa Admin on 27-10-2016.
 */

//custom AutoCompleteTextVIew for tagging in MainFeedDetailedActivity
public class CustomAutoCompleteTextView extends AutoCompleteTextView {
    public CustomAutoCompleteTextView(Context context) {
        super(context);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void replaceText(CharSequence text) {
        // do nothing so that the text stays the same
    }
}