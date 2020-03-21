package yzw.ahaqth.accountbag.inputoredit;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import yzw.ahaqth.accountbag.BaseActivity;
import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.allrecords.RecordGroupActivity;
import yzw.ahaqth.accountbag.interfaces.DialogDismissListener;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.modules.ImageRecord;
import yzw.ahaqth.accountbag.modules.RecordGroup;
import yzw.ahaqth.accountbag.modules.TextRecord;
import yzw.ahaqth.accountbag.operators.FileOperator;
import yzw.ahaqth.accountbag.operators.GroupOperator;
import yzw.ahaqth.accountbag.operators.ImageOperator;
import yzw.ahaqth.accountbag.operators.RecordOperator;
import yzw.ahaqth.accountbag.tools.ToastFactory;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class InputOrEditRecordActivity extends BaseActivity {
    String TAG = "殷宗旺";

    private final int OPEN_ALBUM = 100;
    private final int TAKE_PHOTO = 101;
    private final int CROP_IMAGE = 102;

    private NestedScrollView root;
    private MaterialEditText recordNameET;
    private EditText accountNameTV;
    private EditText accountPwdTV;
    private ImageButton randomPwdBT;
    private EditText accountDiscribeTV;
    private Button addExtraTextBT;
    private RecyclerView extraTextRLV;
    private Button addExtraImageBT;
    private RecyclerView extraImageRLV;
    private Toolbar toolbar;

    private AccountRecord accountRecord;
    private List<TextRecord> textRecords;
    private List<ImageRecord> imageRecords;
    private String recordName, accountName, accountPWD, describe;
    private int mode;
    private EditImagRecordAdapter imagRecordAdapter;
    private EditTextRecordAdapter textRecordAdapter;
    private ToastFactory toastFactory;
    private ImagePicker imagePicker;
    private ImagePicker.Callback imagePickerCallback;
    private MaterialCardView cardView;
    private Spinner spinner;
    private int mHeight,toolBarHeight;
    private long recordGroupId = -1;
    private boolean isSpinnerInitialed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_or_edit_record);
        initialView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOperator.clearExternalCacheDir();
                finish();
            }
        });
        prepareData(getIntent());
        float scale=getResources().getDisplayMetrics().density;
        toolBarHeight = (int)(56*scale+0.5f);
        imagePickerCallback = new ImagePicker.Callback() {
            // 选择图片回调
            @Override public void onPickImage(Uri imageUri) {

            }

            // 裁剪图片回调
            @Override public void onCropImage(Uri imageUri) {
                addExtraImage(imageUri);
            }

            // 自定义裁剪配置
            @Override
            public void cropConfig(CropImage.ActivityBuilder builder) {
                builder
                        // 是否启动多点触摸
                        .setMultiTouchEnabled(true)
                        // 设置网格显示模式
                        .setGuidelines(CropImageView.Guidelines.ON)
                        // 圆形/矩形
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        // 调整裁剪后的图片最终大小
                        .setRequestedSize(900, 600)
                        // 宽高比
                        .setAspectRatio(3, 2);
            }

            // 用户拒绝授权回调
            @Override public void onPermissionDenied(int requestCode, String[] permissions,
                                                     int[] grantResults) {
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mHeight = dm.heightPixels;
        initialSpinner();
        showRcordData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_edit_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        save();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(this,requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void prepareData(Intent intent) {
        long id = intent.getLongExtra("id", -1L);
        imagePicker = new ImagePicker();
//        toastFactory.showCenterToast(id + "");
        accountRecord = RecordOperator.findOne(id);
        if (accountRecord == null) {
            setTitle("新建");
            accountRecord = new AccountRecord();
            mode = 1; // add record
            recordName = "";
            accountName = "";
            accountPWD = "";
            describe = "";
            recordGroupId = -1;
        } else {
            setTitle("修改");
            mode = 2;// edit record
            recordName = accountRecord.getRecordName();
            accountName = accountRecord.getAccountName();
            accountPWD = accountRecord.getAccountPWD();
            describe = accountRecord.getDescribe();
            recordGroupId = accountRecord.getGroupId();
        }
        textRecords = accountRecord.getTextRecords();
        imageRecords = accountRecord.getImageRecords();
        textRecordAdapter = new EditTextRecordAdapter(this,textRecords);
        textRecordAdapter.setEditClickListener(new ItemClickListener() {
            @Override
            public void click(int position, @Nullable Object... values) {
                editExtraTextRecord(position);
            }
        });
        imagRecordAdapter = new EditImagRecordAdapter(this,imageRecords);
        extraTextRLV.setAdapter(textRecordAdapter);
        extraImageRLV.setAdapter(imagRecordAdapter);
    }

    private void initialView() {
        toastFactory = new ToastFactory(this);
        root = findViewById(R.id.root);
        cardView = findViewById(R.id.text_cardView);
        recordNameET = findViewById(R.id.record_name_ET);
        accountNameTV = findViewById(R.id.account_name_TV);
        accountPwdTV = findViewById(R.id.account_pwd_TV);
        randomPwdBT = findViewById(R.id.random_pwd_BT);
        randomPwdBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRandomPWD();
            }
        });
        accountDiscribeTV = findViewById(R.id.account_discribe_TV);
        addExtraTextBT = findViewById(R.id.add_extra_text_BT);
        addExtraTextBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExtraTextRecord();
            }
        });
        extraTextRLV = findViewById(R.id.extra_text_RLV);
        extraTextRLV.setLayoutManager(new LinearLayoutManager(this));
        addExtraImageBT = findViewById(R.id.add_extra_image_BT);
        addExtraImageBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });
        extraImageRLV = findViewById(R.id.extra_image_RLV);
        extraImageRLV.setLayoutManager(new LinearLayoutManager(this));
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        spinner = findViewById(R.id.recordGroupSpinner);
    }

    private void initialSpinner(){
        isSpinnerInitialed = false;
        final List<RecordGroup> list = new ArrayList<>();
        list.add(new RecordGroup("未分组"));
        list.addAll(GroupOperator.findAll(true));
        list.add(new RecordGroup("管理分组..."));
        spinner.setAdapter(new ArrayAdapter<RecordGroup>(this, R.layout.spinner_item, list));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnerInitialed) {
                    if (position == 0) {
                        recordGroupId = -1L;
                    } else if (position == list.size() - 1) {
                        startActivity(new Intent(InputOrEditRecordActivity.this, RecordGroupActivity.class));
                        spinner.setSelection(0);
                        recordGroupId = -1;
                    } else {
                        recordGroupId = list.get(position).getId();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(recordGroupId != -1L) {
            for (int i = 1; i < list.size() - 1; i++) {
                RecordGroup r = list.get(i);
                if (r.getId() == recordGroupId) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
        isSpinnerInitialed = true;
    }

    private void showRcordData() {
        if (mode == 2) {
            recordNameET.setText(recordName);
            accountNameTV.setText(accountName);
            accountPwdTV.setText(accountPWD);
            accountDiscribeTV.setText(describe);
        }
        recordNameET.requestFocus();
    }

    private void save() {
        if (TextUtils.isEmpty(recordNameET.getText())) {
            recordNameET.setError("请输入名称");
            recordNameET.requestFocus();
            return;
        }
        recordName = recordNameET.getText().toString().trim();
        if(mode == 1 && RecordOperator.isExist(recordName)){
            recordNameET.setError("名称重复");
            recordNameET.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(accountNameTV.getText())) {
            accountName = "";
        } else {
            accountName = accountNameTV.getText().toString().trim();
        }
        if (TextUtils.isEmpty(accountPwdTV.getText())) {
            accountPWD = "";
        } else
            accountPWD = accountPwdTV.getText().toString().trim();
        if (TextUtils.isEmpty(accountDiscribeTV.getText())) {
            describe = "";
        } else
            describe = accountDiscribeTV.getText().toString().trim();
        accountRecord.setRecordName(recordName);
        accountRecord.setAccountName(accountName);
        accountRecord.setAccountPWD(accountPWD);
        accountRecord.setDescribe(describe);
        accountRecord.setGroupId(recordGroupId);
        if(mode == 1) {
            accountRecord.setRecordTime(System.currentTimeMillis());
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("AddRecord"));
        }else{
            accountRecord.setModifyTime(System.currentTimeMillis());
        }
        accountRecord.save();
        toastFactory.showCenterToast("保存成功！");
        finish();
    }

    private void getRandomPWD() {
        accountPWD = ToolUtils.getRandomPassword();
        accountPwdTV.setText(accountPWD);
    }

    private void addExtraTextRecord() {
        final TextRecord record = new TextRecord();
        InputEditExtraTextRecordDialog fragment = new InputEditExtraTextRecordDialog();
        fragment.setOnDismiss(new DialogDismissListener() {
            @Override
            public void onDismiss(boolean isConfirm, Object... objects) {
                if (isConfirm) {
                    String key = (String) objects[0];
                    String content = (String) objects[1];
                    record.setKey(key);
                    record.setContent(content);
                    accountRecord.addTextRecord(record);
                    int c = textRecordAdapter.getItemCount();
                    textRecordAdapter.notifyItemInserted(c);
                    root.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            extraTextRLV.requestFocus();
                            int y = cardView.getBottom() + toolBarHeight + 100;
                            if(y > mHeight) {
                                root.smoothScrollTo(0, y - mHeight);
                            }
                        }
                    },300);
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "AddTextRecord");
    }

    private void editExtraTextRecord(final int position) {
        final TextRecord record = textRecords.get(position);
        InputEditExtraTextRecordDialog fragment = InputEditExtraTextRecordDialog.getInstance(record.getKey(), record.getContent());
        fragment.setOnDismiss(new DialogDismissListener() {
            @Override
            public void onDismiss(boolean isConfirm, Object... objects) {
                if (isConfirm) {
                    String key = (String) objects[0];
                    String content = (String) objects[1];
                    record.setKey(key);
                    record.setContent(content);
                    textRecordAdapter.notifyItemChanged(position);
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "EditTextRecord");
    }

    private void openAlbum(){
        imagePicker.setTitle("相册");
        imagePicker.setCropImage(true);
        imagePicker.startGallery(this, imagePickerCallback);
    }

    private void openCamera(){
        imagePicker.setTitle("相机");
        imagePicker.setCropImage(true);
        imagePicker.startCamera(this, imagePickerCallback);
    }

    private void choosePhoto() {
//        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, uri);
//        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        startActivityForResult(intentToPickPic, OPEN_ALBUM);
        imagePicker.setTitle("选择图片");
        // 设置是否裁剪图片
        imagePicker.setCropImage(true);
        // 启动图片选择器
        imagePicker.startChooser(this, imagePickerCallback);
    }

    private void addExtraImage(Uri uri) {
        File image = null;
        try {
            image = ImageOperator.imageCopyToCache(this,uri);
//            Log.d(TAG, "addExtraImage: 裁剪成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image != null) {
            Luban.with(this)
                    .load(image)
                    .ignoreBy(100)
                    .setTargetDir(FileOperator.compressedImageDir.getAbsolutePath())
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
//                                toastFactory.showCenterToast("压缩图片中...");
                        }

                        @Override
                        public void onSuccess(File file) {
                            ImageRecord imageRecord = new ImageRecord();
                            imageRecord.setPath(file.getParent());
                            imageRecord.setImageFileName(file.getName());
                            accountRecord.addImageRecord(imageRecord);
                            int c = imagRecordAdapter.getItemCount();
                            imagRecordAdapter.notifyItemInserted(c);
                            root.post(new Runnable() {
                                @Override
                                public void run() {
                                    root.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable e) {
                            toastFactory.showCenterToast("图片压缩失败");
                        }
                    }).launch();
        } else {
            toastFactory.showCenterToast("打开图片失败");
        }
    }



//    private void takePhotoPrepare() {
//        if (XXPermissions.isHasPermission(this, Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE, Permission.CAMERA)) {
//            takePhoto();
//        } else {
//            XXPermissions.with(this)
//                    .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE, Permission.CAMERA)
//                    .request(new OnPermission() {
//                        @Override
//                        public void hasPermission(List<String> granted, boolean isAll) {
//                            if (isAll) {
//                                takePhoto();
//                            }
//                        }
//
//                        @Override
//                        public void noPermission(List<String> denied, boolean quick) {
//                            if (quick) {
//                                toastFactory.showCenterToast("已永久拒绝权限，请手动授予权限");
//                                XXPermissions.gotoPermissionSettings(InputOrEditRecordActivity.this);
//                            }
//                        }
//                    });
//        }
//    }
//
//    private void takePhoto() {
//        // 跳转到系统的拍照界面
//        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // 这里设置为固定名字 这样就只会只有一张temp图 如果要所有中间图片都保存可以通过时间或者加其他东西设置图片的名称
//        // File.separator为系统自带的分隔符 是一个固定的常量
//        String mTempPhotoPath = FileOperator.imageCacheDir+File.separator+System.currentTimeMillis() + ".jpg";
//        Uri imageUri;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            imageUri = FileProvider.getUriForFile(this, "yzw.ahaqth.accountbag.imagePicker.provider", new File(mTempPhotoPath));
//            intentToTakePhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        } else {
//            imageUri = Uri.fromFile(new File(mTempPhotoPath));
//        }
//        //下面这句指定调用相机拍照后的照片存储的路径
//        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        startActivityForResult(intentToTakePhoto, TAKE_PHOTO);
//    }
}
