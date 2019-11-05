package yzw.ahaqth.accountbag.details;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import yzw.ahaqth.accountbag.BaseActivity;
import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.inputoredit.InputOrEditRecordActivity;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.modules.ImageRecord;
import yzw.ahaqth.accountbag.modules.TextRecord;
import yzw.ahaqth.accountbag.operators.FileOperator;
import yzw.ahaqth.accountbag.operators.ImageOperator;
import yzw.ahaqth.accountbag.operators.RecordOperator;
import yzw.ahaqth.accountbag.tools.DialogFactory;
import yzw.ahaqth.accountbag.tools.ToastFactory;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class ShowDetailsActivity extends BaseActivity {
    private String TAG = "殷宗旺";

    private TextView accountNameTV;
    private TextView accountPwdTV;
    private TextView accountDiscribeTV;
    private RecyclerView extraTextRLV;
    private TextView recordTimeTV,modifyTimeTV;
    private ViewPager extraImageVP;
    private Toolbar toolbar;
    private LinearLayout accountNameGroup,accPWDGroup;
    private ImageButton copyName,copyPWD,seePWD;

    private long id;
    private SimpleDateFormat format;
    private List<TextRecord> textRecords;
    private List<ImageRecord> imageRecords;
    private TextRecordAdapter textRecordAdapter;
    private int currentImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_show_details);
        Intent intent = getIntent();
        if(intent != null){
            id = intent.getLongExtra("id",-1L);
        }
        initialView();
        setSupportActionBar(toolbar);
        setTitle("详细信息");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        format = new SimpleDateFormat("yyyy年M月d日 HH:mm:ss", Locale.CHINA);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        readData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.del:
                new DialogFactory(this).showDefaultConfirmDialog("是否删除该项？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleRecord();
                    }
                });
                break;
            case R.id.edit:
                Intent intent = new Intent(ShowDetailsActivity.this, InputOrEditRecordActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        FileOperator.clearExternalCacheDir();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    private void deleRecord(){
        AccountRecord record = RecordOperator.findOne(id);
        if (record!=null) {
            RecordOperator.dele(record);
            new ToastFactory(this).showCenterToast("已删除");
            finish();
        }
    }

    private void initialView(){
        accountNameGroup = findViewById(R.id.account_name_Group);
        accPWDGroup = findViewById(R.id.account_PWD_Group);
        accountNameTV = findViewById(R.id.account_name_TV);
        accountPwdTV = findViewById(R.id.account_pwd_TV);
        accountDiscribeTV = findViewById(R.id.account_discribe_TV);
        extraTextRLV = findViewById(R.id.extra_text_RLV);
        extraTextRLV.setLayoutManager(new LinearLayoutManager(this));
        recordTimeTV = findViewById(R.id.record_time_TV);
        modifyTimeTV = findViewById(R.id.modify_time_TV);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        copyName = findViewById(R.id.copy_account_name);
        copyPWD = findViewById(R.id.copy_account_pwd);
        seePWD = findViewById(R.id.show_account_pwd);
        extraImageVP = findViewById(R.id.extra_image_VP);
    }

    private void readData(){
        final AccountRecord record = RecordOperator.findOne(id);
        if(record == null)
            return;
        setTitle(record.getRecordName());
        String createTimeString = "创建时间："+format.format(record.getRecordTime());
        recordTimeTV.setText(createTimeString);
        if(record.getModifyTime() > 1000){
            String modifyTimeString = "最近修改："+format.format(record.getModifyTime());
            modifyTimeTV.setText(modifyTimeString);
        }else{
            modifyTimeTV.setText("");
        }
        if(TextUtils.isEmpty(record.getAccountName())){
            accountNameGroup.setVisibility(View.GONE);
        }else {
            accountNameGroup.setVisibility(View.VISIBLE);
            accountNameTV.setText(record.getAccountName());
            copyName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToolUtils.copy(v.getContext(),record.getAccountName());
                    new ToastFactory(v.getContext()).showCenterToast("用户名已复制");
                }
            });
        }
        if(TextUtils.isEmpty(record.getAccountPWD())){
            accPWDGroup.setVisibility(View.GONE);
        }else {
            accPWDGroup.setVisibility(View.VISIBLE);
            accountPwdTV.setText("********");
            copyPWD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToolUtils.copy(v.getContext(),record.getAccountPWD());
                    new ToastFactory(v.getContext()).showCenterToast("密码已复制");
                }
            });
            seePWD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountPwdTV.setText(record.getAccountPWD());
                }
            });
        }
        String s = "备注：" + record.getDescribe();
        accountDiscribeTV.setText(s);
        textRecords = record.getTextRecords();
        if(textRecords.size() == 0){
            extraTextRLV.setVisibility(View.GONE);
        }else{
            extraTextRLV.setVisibility(View.VISIBLE);
            textRecordAdapter = new TextRecordAdapter(textRecords);
            extraTextRLV.setAdapter(textRecordAdapter);
        }
        imageRecords = record.getImageRecords();
        if(imageRecords.size() == 0){
            extraImageVP.setVisibility(View.GONE);
        }else {
            extraImageVP.setAdapter(new ImageAdapter());
            if (currentImageIndex > imageRecords.size() - 1) {
                currentImageIndex = 0;
            }
            extraImageVP.setCurrentItem(currentImageIndex);
        }
    }
    protected class ImageAdapter extends PagerAdapter{
        List<View> views = new ArrayList<>();

        public ImageAdapter() {
            super();
            for(int i = 0;i<imageRecords.size();i++){
                final ImageRecord imageRecord = imageRecords.get(i);
                final View view = getLayoutInflater().inflate(R.layout.extra_image_item,null);
                final ImageView imageView = view.findViewById(R.id.extra_image_IV);
                imageView.setImageBitmap(ImageOperator.getRealImage(imageRecord));
                final int finalI = i;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentImageIndex = finalI;
                        Intent intent = new Intent(ShowDetailsActivity.this,ShowLargeImageActivity.class);
                        intent.putExtra("path",ImageOperator.getRealImagePath(imageRecord));
                        Pair<View,String> pair = new Pair<>((View) imageView,"showLargeImage");
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ShowDetailsActivity.this,pair).toBundle());
                    }
                });
                views.add(view);
            }

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public int getCount() {
            return imageRecords.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }
    }
}
