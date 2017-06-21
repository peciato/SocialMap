package pecia.socialmap;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Image_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_);
        Bitmap bitmap = null;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            bitmap = null;
        }
        else {
            bitmap = (Bitmap) extras.getParcelable("image");
        }

        ImageView imageView = (ImageView) findViewById(R.id.activity_image);
        imageView.setImageBitmap(bitmap);

    }

    @Override
    public void onBackPressed()
    {
        finish();
    }


}
