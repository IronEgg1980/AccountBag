package yzw.ahaqth.accountbag.details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import yzw.ahaqth.accountbag.R;

public class ShowLargeImageActivity extends AppCompatActivity {
    private Bitmap bitmap,bitmap1;
    private ImageViewTouch imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_large_image);
        imageView = findViewById(R.id.imageView);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap,null,1f,5f);
        imageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_HEIGHT);
        findViewById(R.id.closeActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        bitmap.recycle();
        super.onDestroy();
    }
}
