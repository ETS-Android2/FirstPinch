package www.firstpinch.com.firstpinch2.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import www.firstpinch.com.firstpinch2.ChooseSignUp;
import www.firstpinch.com.firstpinch2.ForgotPassword;
import www.firstpinch.com.firstpinch2.GCM.GCMRegistrationIntentService;
import www.firstpinch.com.firstpinch2.R;
import www.firstpinch.com.firstpinch2.Search.SearchActivity;
import www.firstpinch.com.firstpinch2.Search.SearchActivityObject;
import www.firstpinch.com.firstpinch2.Search.SearchActivityOptionsAdapter;
import www.firstpinch.com.firstpinch2.SignUp;

/**
 * Created by Rianaa Admin on 03-10-2016.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {

    private LayoutInflater inflator;
    List<SettingsObject> data = Collections.emptyList();
    Context c;

    public  SettingsAdapter (Context context, List<SettingsObject> data){
        inflator = LayoutInflater.from(context);
        this.data = data;
        c=context;
    }

    @Override
    public SettingsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.settings_recyclerview_item, parent, false);
        final SettingsAdapter.MyViewHolder holder = new SettingsAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SettingsAdapter.MyViewHolder holder, int position) {

        final SettingsObject current = data.get(position);
        holder.tv.setText(current.getSetting_title());
        /*Picasso.with(c)
                .load(current.getSearch_image())
                .into(holder.im);*/
    }

    public List<SettingsObject> getList(){

        return data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        String login_type;

        public MyViewHolder(final View itemView) {
            super(itemView);

            SharedPreferences sp = c.getSharedPreferences("logintype",
                    Activity.MODE_PRIVATE);
            login_type = sp.getString("type","");
            tv = (TextView) itemView.findViewById(R.id.tv_setting_title);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(itemView.getContext(),""+tv.getText().toString(),Toast.LENGTH_SHORT).show();
                    if(tv.getText().toString().contentEquals("Change Password")) {
                        itemView.getContext().startActivity(new Intent(itemView.getContext(), UpdatePassword.class));
                    }else if(tv.getText().toString().contentEquals("Change Username")){
                        if(login_type.contentEquals("social")){
                            itemView.getContext().startActivity(new Intent(itemView.getContext(), UpdateUsernameSocial.class));
                        } else {
                            itemView.getContext().startActivity(new Intent(itemView.getContext(), UpdateUsername.class));
                        }
                    }
                }
            });

        }
    }
}