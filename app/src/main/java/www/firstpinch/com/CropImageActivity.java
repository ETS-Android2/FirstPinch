package www.firstpinch.com.firstpinch2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Rianaa Admin on 05-10-2016.
 */

public class CropImageActivity extends AppCompatActivity {

    Button btn_done;
    ImageView cropping_image;
    Bitmap bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image_activity);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        btn_done = (Button) findViewById(R.id.button_done);
        cropping_image = (ImageView) findViewById(R.id.crop_image);
        bt = getIntent().getParcelableExtra("bitmap");

        cropping_image.setImageBitmap(bt);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent data = new Intent();
                /*launchHomeScreen();*/// Add the required data to be returned to the MainActivity
                data.putExtra("data", bt);

                // Set the resultCode to Activity.RESULT_OK to
                // indicate a success and attach the Intent
                // which contains our result data
                setResult(Activity.RESULT_OK, data);

                // With finish() we close the DetailActivity to
                // return to MainActivity
                finish();
                //finish();
            }
        });

    }

    private Bitmap cropBitmap1()
    {
        Bitmap bmp2 = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher);
        Bitmap bmOverlay = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Canvas c = new Canvas(bmOverlay);
        c.drawBitmap(bmp2, 0, 0, null);
        c.drawRect(30, 30, 100, 100, p);

        return bmOverlay;
    }
    private Bitmap cropAndGivePointedShape(Bitmap originalBitmap)
    {
        Bitmap bmOverlay = Bitmap.createBitmap(originalBitmap.getWidth(),
                originalBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        canvas.drawRect(0, 0, 20, 20, p);

        Point a = new Point(0, 20);
        Point b = new Point(20, 20);
        Point c = new Point(0, 40);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();

        canvas.drawPath(path, p);

        a = new Point(0, 40);
        b = new Point(0, 60);
        c = new Point(20, 60);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();

        canvas.drawPath(path, p);

        canvas.drawRect(0, 60, 20, originalBitmap.getHeight(), p);

        return bmOverlay;
    }

}
