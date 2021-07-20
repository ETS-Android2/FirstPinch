package www.firstpinch.com.firstpinch2.MainFeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 27-10-2016.
 */

//custom Adapter for tag users list in MainFeedDetailedActivity
public class CustomerAdapter extends ArrayAdapter<Tag_userObject> {
    private final String MY_DEBUG_TAG = "CustomerAdapter";
    private ArrayList<Tag_userObject> items;
    private ArrayList<Tag_userObject> itemsAll;
    private ArrayList<Tag_userObject> suggestions;
    private int viewResourceId;
    Context context;

    public CustomerAdapter(Context context, int viewResourceId, ArrayList<Tag_userObject> items) {
        super(context, viewResourceId, items);
        this.context = context;
        this.items = items;
        this.itemsAll = (ArrayList<Tag_userObject>) items.clone();
        this.suggestions = new ArrayList<Tag_userObject>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        Tag_userObject customer = items.get(position);
        if (customer != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.member_profile_name);
            TextView customerUsernameNameLabel = (TextView) v.findViewById(R.id.member_profile_username);
            ImageView customerImageView = (ImageView) v.findViewById(R.id.member_profile_image);
            if (customerNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView Customer Name:"+customer.getName());
                customerNameLabel.setText(customer.getName());
                customerUsernameNameLabel.setText(customer.getUname());
                Picasso.with(context)
                        .load(customer.getImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .transform(new RoundedCornersTransform())
                        .into(customerImageView);
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Tag_userObject)(resultValue)).getName();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (Tag_userObject customer : itemsAll) {
                    if(customer.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Tag_userObject> filteredList = (ArrayList<Tag_userObject>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (Tag_userObject c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

}