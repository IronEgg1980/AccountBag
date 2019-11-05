package yzw.ahaqth.accountbag.details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import yzw.ahaqth.accountbag.BaseActivity;
import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.tools.ToastFactory;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class ShowLargeImageActivity extends BaseActivity {
    private Bitmap bitmap;
    private String path = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_large_image);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        bitmap = BitmapFactory.decodeFile(path);
        ImageViewTouch imageView = findViewById(R.id.imageView);
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
        final ToastFactory toastFactory = new ToastFactory(this);
        XXPermissions.with(this)
                .permission(Permission.Group.STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if(isAll){
                            try {
                                MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"", "");
                                toastFactory.showCenterToast("已保存");
                            } catch (Exception e) {
                                e.printStackTrace();
                                toastFactory.showCenterToast("保存失败！\n"+e.getMessage());
                            }
                            // 通知图库更新
                            MediaScannerConnection.scanFile(ShowLargeImageActivity.this, new String[]{path}, null,
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

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        toastFactory.showCenterToast("已拒绝授权，不能保存");
                    }
                });
    }
}
