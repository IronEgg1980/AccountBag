package yzw.ahaqth.accountbag.details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.tools.ToastFactory;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class ShowLargeImageActivity extends AppCompatActivity {
    private String TAG = "ShowLargeImageActivity";
    private Bitmap bitmap;
    private ImageViewTouch imageView;
    private String name,path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_large_image);
        name =System.currentTimeMillis() + ".jpg";
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        name = intent.getStringExtra("name");
        bitmap = BitmapFactory.decodeFile(path);
        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap,null,1f,5f);
        imageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_WIDTH);
        imageView.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {
                onBackPressed();
            }
        });
        findViewById(R.id.closeActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToAlbum();
            }
        });
    }

    @Override
    protected void onDestroy() {
        bitmap.recycle();
        super.onDestroy();
    }

    private void exportToAlbum(){
        ToastFactory toastFactory = new ToastFactory(this);
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,name, "导出图片："+name);
            toastFactory.showCenterToast("已保存");
        } catch (Exception e) {
            e.printStackTrace();
            toastFactory.showCenterToast("保存失败！\n"+e.getMessage());
        }
        // 通知图库更新
        MediaScannerConnection.scanFile(this, new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.setData(uri);
                        sendBroadcast(mediaScanIntent);
                    }
                });
        // 目标版本号低于23时用下面的代码
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            MediaScannerConnection.scanFile(this, new String[]{path}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        @Override
//                        public void onScanCompleted(String path, Uri uri) {
//                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                            mediaScanIntent.setData(uri);
//                            sendBroadcast(mediaScanIntent);
//                        }
//                    });
//        } else {
//            String relationDir = new File(path).getParent();
//            File file1 = new File(relationDir);
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
//        }
    }
}
