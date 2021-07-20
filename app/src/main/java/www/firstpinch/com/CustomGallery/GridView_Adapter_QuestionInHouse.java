package www.firstpinch.com.firstpinch2.CustomGallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import www.firstpinch.com.firstpinch2.PinchAQues;
import www.firstpinch.com.firstpinch2.PinchAQuesInHouse;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 28-11-2016.
 */

//adapter for pinch a question from house page
public class GridView_Adapter_QuestionInHouse extends BaseAdapter {
    private Context context;
    private ArrayList<String> imageUrls;
    private SparseBooleanArray mSparseBooleanArray;//Variable to store selected Images
    private DisplayImageOptions options;
    private boolean isCustomGalleryActivity;//Variable to check if gridview is to setup for Custom Gallery or not

    public GridView_Adapter_QuestionInHouse(Context context, ArrayList<String> imageUrls, boolean isCustomGalleryActivity) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.isCustomGalleryActivity = isCustomGalleryActivity;
        mSparseBooleanArray = new SparseBooleanArray();


        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .resetViewBeforeLoading(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    //Method to return selected Images
    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for (int i = 0; i < imageUrls.size(); i++) {
            if (mSparseBooleanArray.get(i)) {
                mTempArry.add(imageUrls.get(i));
            }
        }

        return mTempArry;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int i) {
        return imageUrls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            view = inflater.inflate(R.layout.customgridview_selected_item, viewGroup, false);//Inflate layout

        } else {
            view = convertView;
        }


        final CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.selectCheckBox);
        ImageView dark_back = (ImageView) view.findViewById(R.id.de_selected_image_dark);
        ImageView close_btn_image = (ImageView) view.findViewById(R.id.de_selected_image_close_btn);
        ImageView image = (ImageView) view.findViewById(R.id.galleryImageView);
        final ImageView imageView = (ImageView) view.findViewById(R.id.galleryImageView);
        //If Context is MainActivity then hide checkbox
        if (!isCustomGalleryActivity) {
            mCheckBox.setVisibility(View.GONE);
            close_btn_image.setVisibility(View.VISIBLE);
            dark_back.setVisibility(View.VISIBLE);
        }else{
            mCheckBox.setVisibility(View.VISIBLE);
            close_btn_image.setVisibility(View.GONE);
            dark_back.setVisibility(View.GONE);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckBox.setChecked(mSparseBooleanArray.get(position));
                    mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
                }
            });
        }

        close_btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUrls.remove(position);
                ((PinchAQuesInHouse) context).setBase64Image("");
                notifyDataSetChanged();
            }
        });
        ImageLoader.getInstance().displayImage("file://" + imageUrls.get(position), imageView, options);//Load Images over ImageView

        mCheckBox.setTag(position);//Set Tag for CheckBox
        mCheckBox.setChecked(mSparseBooleanArray.get(position));
        mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        return view;
    }

    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);//Insert selected checkbox value inside boolean array
            ((CustomGallery_Activity_Question) context).showSelectButton();//call custom gallery activity method
        }
    };
}