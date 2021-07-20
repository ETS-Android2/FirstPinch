package www.firstpinch.com.firstpinch2;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/*Created by Rianaa Admin on 06-09-2016.*/


public class HouseRecyclerViewAdapter extends RecyclerView.Adapter<HouseRecyclerViewAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<HouseObject> data = Collections.emptyList();
    Context c;

    public  HouseRecyclerViewAdapter (Context context, List<HouseObject> data){
        inflator = LayoutInflater.from(context);
        this.data = data;
        c=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.house_recyclerview_item2, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final HouseObject current = data.get(position);
        holder.tv.setText(current.getHousename());
        Picasso.with(c)
                .load(current.house_imageurl)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.im);
        if(!current.house_check) {
            holder.follow.setTextColor(Color.GREEN);
            holder.follow.setText("JOIN");
        }
        else {
            holder.follow.setTextColor(Color.BLACK);
            holder.follow.setText("EXIT");
        }





        //animate(holder);
        //Log.e("text=",""+current.getHouseName());


    }

    public List<HouseObject> getList(){

        return data;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        ImageView im;
        Button follow;

        public MyViewHolder(View itemView) {
            super(itemView);

            final HouseObject current = data.get(getAdapterPosition());
            tv = (TextView) itemView.findViewById(R.id.housename);
            im = (ImageView) itemView.findViewById(R.id.fe_img);
            follow = (Button) itemView.findViewById(R.id.house_follow);
            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (follow.getText().toString().contentEquals("EXIT")) {
                        follow.setText("JOIN");
                        follow.setTextColor(Color.GREEN);
                        current.setHouse_check(false);
                    } else if(follow.getText().toString().contentEquals("JOIN")) {
                        follow.setText("EXIT");
                        follow.setTextColor(Color.BLACK);
                        current.setHouse_check(true);
                    }
                }
            });
        }
    }
}
