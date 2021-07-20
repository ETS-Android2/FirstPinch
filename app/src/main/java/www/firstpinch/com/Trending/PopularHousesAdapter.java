package www.firstpinch.com.firstpinch2.Trending;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import www.firstpinch.com.firstpinch2.HouseProfilePages.HouseProfileActivity;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedObject;
import www.firstpinch.com.firstpinch2.R;
import www.firstpinch.com.firstpinch2.Search.SearchActivityHouseAdapter;
import www.firstpinch.com.firstpinch2.Search.SearchActivityObject;

/**
 * Created by Rianaa Admin on 01-11-2016.
 */

public class PopularHousesAdapter extends RecyclerView.Adapter<PopularHousesAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<SearchActivityObject> data = Collections.emptyList();
    Context c;

    public PopularHousesAdapter(Context context, List<SearchActivityObject> data){
        inflator = LayoutInflater.from(context);
        this.data = data;
        c=context;
    }

    @Override
    public PopularHousesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.search_activty_item, parent, false);
        PopularHousesAdapter.MyViewHolder holder = new PopularHousesAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PopularHousesAdapter.MyViewHolder holder, int position) {

        final SearchActivityObject current = data.get(position);
        holder.tv.setText(current.getSearch_name());
        Picasso.with(c)
                .load(current.getSearch_image())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.im);

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


        public MyViewHolder(final View itemView) {
            super(itemView);

            tv = (TextView) itemView.findViewById(R.id.search_textview);
            im = (ImageView) itemView.findViewById(R.id.search_image);
            ll_item = (LinearLayout) itemView.findViewById(R.id.ll_search_item);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchActivityObject current = data.get(getAdapterPosition());
                    Intent intent;
                    intent = new Intent(itemView.getContext(), HouseProfileActivity.class);
                    intent.putExtra("housename", current.getSearch_name());
                    intent.putExtra("housedesc", current.getSearch_desc());
                    intent.putExtra("imageurl", current.getSearch_image());
                    intent.putExtra("house_id", "" + current.getId());
                    itemView.getContext().startActivity(intent);
                }
            });
            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchActivityObject current = data.get(getAdapterPosition());
                    Intent intent;
                    intent = new Intent(itemView.getContext(), HouseProfileActivity.class);
                    intent.putExtra("housename", current.getSearch_name());
                    intent.putExtra("housedesc", current.getSearch_desc());
                    intent.putExtra("imageurl", current.getSearch_image());
                    intent.putExtra("house_id", "" + current.getId());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}