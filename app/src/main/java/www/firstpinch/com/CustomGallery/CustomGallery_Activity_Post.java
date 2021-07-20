package www.firstpinch.com.firstpinch2.CustomGallery;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import www.firstpinch.com.firstpinch2.PinchAPost;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 25-10-2016.
 */

//custom gallery activity for pinching a post
public class CustomGallery_Activity_Post extends AppCompatActivity implements View.OnClickListener {
    private static Button selectImages;
    private static GridView galleryImagesGridView;
    private static ArrayList<ImageObject> galleryImageUrls;
    private static GridView_Adapter_Post imagesAdapter;
    final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};//get all columns of type images
    final String orderBy = MediaStore.Images.Media.DATE_MODIFIED;//order data by date
    View parent;
    Toolbar toolbar;
    int alreadySelectedImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customgallery_activity);
        parent = findViewById(R.id.custom_gallery_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        alreadySelectedImages = getIntent().getIntExtra("selectedimagescount", 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recent Pictures to Post");
        initViews();
        setListeners();
        fetchGalleryImages();
        setUpGridView();
    }


    //Init all views
    private void initViews() {
        selectImages = (Button) findViewById(R.id.selectImagesBtn);
        galleryImagesGridView = (GridView) findViewById(R.id.galleryImagesGridView);
    }

    //fetch all images from gallery
    private void fetchGalleryImages() {


        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");//get all data in Cursor by sorting in DESC order

        galleryImageUrls = new ArrayList<ImageObject>();//Init array


        //Loop to cursor count
        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);//get column index
            ImageObject current = new ImageObject();
            current.setImage_url(imagecursor.getString(dataColumnIndex));
            galleryImageUrls.add(current);//get Image from column index
            System.out.println("Array path" + galleryImageUrls.get(i));
        }


    }

    //Set Up GridView method
    private void setUpGridView() {
        imagesAdapter = new GridView_Adapter_Post(CustomGallery_Activity_Post.this, galleryImageUrls, true, 1);
        galleryImagesGridView.setAdapter(imagesAdapter);
    }

    //Set Listeners method
    private void setListeners() {
        selectImages.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //Show hide select button if images are selected or deselected
    public void showSelectButton() {
        ArrayList<String> selectedItems = imagesAdapter.getCheckedItems();
        if (alreadySelectedImages + selectedItems.size() > 0 && alreadySelectedImages + selectedItems.size() < 5) {

            selectImages.setText((alreadySelectedImages + selectedItems.size()) + " - Images Selected - Let's Go");
            selectImages.setVisibility(View.VISIBLE);
        } else {
            selectImages.setVisibility(View.GONE);
            showSnackBar();
        }

    }

    public void showSnackBar() {

        Snackbar snack = Snackbar.make(parent, "Cannot select more than 4 images", Snackbar.LENGTH_LONG);
        snack.setAction("CLOSE", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        })
                .setActionTextColor(getResources().getColor(android.R.color.holo_purple
                )).show();

        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectImagesBtn:

                //When button is clicked then fill array with selected images
                ArrayList<String> selectedItems = imagesAdapter.getCheckedItems();

                //Send back result to MainActivity with selected images
                Intent intent = new Intent();
                intent.putExtra(PinchAPost.CustomGalleryIntentKey, selectedItems.toString());
                //Convert Array into string to pass data
                setResult(RESULT_OK, intent);//Set result OK


                finish();//finish activity
                break;

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finish();
    }

}