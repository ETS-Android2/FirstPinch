package www.firstpinch.com.firstpinch2.Search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 28-09-2016.
 */

public class SearchActivityOptionsAdapter extends RecyclerView.Adapter<SearchActivityOptionsAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<SearchActivityObject> data = Collections.emptyList();
    Context c;

    public  SearchActivityOptionsAdapter (Context context, List<SearchActivityObject> data){
        inflator = LayoutInflater.from(context);
        this.data = data;
        c=context;
    }

    @Override
    public SearchActivityOptionsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.search_activty_options_item, parent, false);
        final SearchActivityOptionsAdapter.MyViewHolder holder = new SearchActivityOptionsAdapter.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final SearchActivityOptionsAdapter.MyViewHolder holder, int position) {

        final SearchActivityObject current = data.get(position);
        holder.tv.setText(current.getSearch_name());
        Picasso.with(c)
                .load(R.drawable.search_icon)
                .into(holder.im);

        //animate(holder);
        //Log.e("text=",""+current.getHouseName());


    }

    public List<SearchActivityObject> getList(){

        return data;
    }



    @Override
    public int getItemCount() {
        return data.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        ImageView im;
        LinearLayout ll_item;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv = (TextView) itemView.findViewById(R.id.search_textview);
            im = (ImageView) itemView.findViewById(R.id.search_image);
            ll_item = (LinearLayout) itemView.findViewById(R.id.ll_search_item);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SearchActivity) c).onClickCalled();
                }
            });
            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SearchActivity) c).onClickCalled();
                }
            });
        }
    }
}