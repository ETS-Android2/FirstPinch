package www.firstpinch.com.firstpinch2.CustomGallery;

/**
 * Created by Rianaa Admin on 20-09-2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import www.firstpinch.com.firstpinch2.PinchAPost;
import www.firstpinch.com.firstpinch2.PinchAQues;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by SONU on 31/10/15.
 */
//adapter for pinching a question from home and profile pages
public class GridView_Adapter_Question extends BaseAdapter {
    private Context context;
    private ArrayList<String> imageUrls;
    private SparseBooleanArray mSparseBooleanArray;//Variable to store selected Images
    private DisplayImageOptions options;
    private boolean isCustomGalleryActivity;//Variable to check if gridview is to setup for Custom Gallery or not

    public GridView_Adapter_Question(Context context, ArrayList<String> imageUrls, boolean isCustomGalleryActivity) {
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

            ImageLoader.getInstance().displayImage("file://" + imageUrls.get(position), imageView, options);//Load Images over ImageView
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
            ImageLoader.getInstance().displayImage("file://" + imageUrls.get(position), imageView, options);//Load Images over ImageView
        }

        close_btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUrls.remove(position);
                ((PinchAQues) context).setBase64Image("");
                notifyDataSetChanged();
            }
        });

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
