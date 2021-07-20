package www.firstpinch.com.firstpinch2;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by vishallappy on 9/4/2016.
 */
public class SelectInterestsRecyclerAdapter extends RecyclerView.Adapter<SelectInterestsRecyclerAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<SelectedInterestsObject> data = Collections.emptyList();
    Context c;

    public  SelectInterestsRecyclerAdapter (Context context, List<SelectedInterestsObject> data){
        inflator = LayoutInflater.from(context);
        this.data = data;
        c=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.selectinterests_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final SelectedInterestsObject current = data.get(position);
        Typeface custom_font2 = Typeface.createFromAsset(c.getAssets(),  "fonts/Lato-Regular.ttf");


        holder.tv.setTypeface(custom_font2);
        holder.tv.setText(current.getHouseName());
        Picasso.with(c)
                .load(current.imageurl)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.im);
        if(current.check)
            holder.checkimg.setVisibility(View.VISIBLE);
        else
            holder.checkimg.setVisibility(View.GONE);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!current.getCheck()) {
                    current.setCheck(true);
                    holder.checkimg.setVisibility(View.VISIBLE);
                } else {
                    current.setCheck(false);
                    holder.checkimg.setVisibility(View.GONE);
                }
            }
        });

        //animate(holder);
        //Log.e("text=",""+current.getHouseName());


    }

    public List<SelectedInterestsObject> getList(){

        return data;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(c, R.anim.anticipateovershoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        ImageView im;
        ImageView checkimg;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv = (TextView) itemView.findViewById(R.id.textView1);
            im = (ImageView) itemView.findViewById(R.id.picture);
            checkimg = (ImageView) itemView.findViewById(R.id.check);
        }
    }
}
