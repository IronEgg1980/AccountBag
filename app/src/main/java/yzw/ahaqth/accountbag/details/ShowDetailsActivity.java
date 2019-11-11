package yzw.ahaqth.accountbag.details;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import yzw.ahaqth.accountbag.BaseActivity;
import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.custom_views.ViewPagerPointIndicator;
import yzw.ahaqth.accountbag.inputoredit.InputOrEditRecordActivity;
import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.modules.ImageRecord;
import yzw.ahaqth.accountbag.modules.RecordGroup;
import yzw.ahaqth.accountbag.modules.TextRecord;
import yzw.ahaqth.accountbag.operators.FileOperator;
import yzw.ahaqth.accountbag.operators.GroupOperator;
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
    private ViewPagerPointIndicator indicator;
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    private LinearLayout accountNameGroup,accPWDGroup;
    private ImageButton copyName,copyPWD,seePWD;
    private Spinner spinner;

    private long id;
    private SimpleDateFormat format;
    private List<TextRecord> textRecords;
    private List<ImageRecord> imageRecords;
    private TextRecordAdapter textRecordAdapter;
    private int currentImageIndex = 0;
    private boolean isShowPWD = true;

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
        overridePendingTransition(-1,R.anim.activity_r_l);
    }

    private void deleRecord(){
        AccountRecord accountRecord = RecordOperator.findOne(id);
        if (accountRecord!=null) {
            RecordOperator.dele(accountRecord);
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
        indicator = findViewById(R.id.viewpagerIndicator);
        frameLayout = findViewById(R.id.framelayout);
        spinner = findViewById(R.id.spinner);
    }

    private void initialSpinner(){
        final AccountRecord accountRecord = RecordOperator.findOne(id);
        final List<RecordGroup> list = new ArrayList<>();
        list.add(new RecordGroup("未分组"));
        list.addAll(GroupOperator.findAll(true));
        spinner.setAdapter(new ArrayAdapter<RecordGroup>(this, R.layout.spinner_item, list));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    accountRecord.setGroupId(-1);
                    accountRecord.save();

                }else{
                    accountRecord.setGroupId(list.get(position).getId());
                    accountRecord.save();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for (int i = 1; i < list.size() - 1; i++) {
            RecordGroup r = list.get(i);
            if (r.getId() == accountRecord.getGroupId()) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void readData(){
        final AccountRecord accountRecord = RecordOperator.findOne(id);
        if(accountRecord == null)
            return;
        setTitle(accountRecord.getRecordName());
        String createTimeString = "创建时间："+format.format(accountRecord.getRecordTime());
        recordTimeTV.setText(createTimeString);
        if(accountRecord.getModifyTime() > 1000){
            String modifyTimeString = "最近修改："+format.format(accountRecord.getModifyTime());
            modifyTimeTV.setText(modifyTimeString);
        }else{
            modifyTimeTV.setText("");
        }
        if(TextUtils.isEmpty(accountRecord.getAccountName())){
            accountNameGroup.setVisibility(View.GONE);
        }else {
            accountNameGroup.setVisibility(View.VISIBLE);
            accountNameTV.setText(accountRecord.getAccountName());
            copyName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToolUtils.copy(v.getContext(),accountRecord.getAccountName());
                    new ToastFactory(v.getContext()).showCenterToast("用户名已复制");
                }
            });
        }
        if(TextUtils.isEmpty(accountRecord.getAccountPWD())){
            accPWDGroup.setVisibility(View.GONE);
        }else {
            accPWDGroup.setVisibility(View.VISIBLE);
            accountPwdTV.setText("********");
            copyPWD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToolUtils.copy(v.getContext(),accountRecord.getAccountPWD());
                    new ToastFactory(v.getContext()).showCenterToast("密码已复制");
                }
            });
            seePWD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isShowPWD) {
                        isShowPWD = false;
                        accountPwdTV.setText(accountRecord.getAccountPWD());
                        seePWD.setImageResource(R.drawable.view_off);
                    }else{
                        isShowPWD = true;
                        accountPwdTV.setText("********");
                        seePWD.setImageResource(R.drawable.view);
                    }
                }
            });
        }
        String s = "备注：" + accountRecord.getDescribe();
        accountDiscribeTV.setText(s);
        textRecords = accountRecord.getTextRecords();
        if(textRecords.size() == 0){
            extraTextRLV.setVisibility(View.GONE);
        }else{
            extraTextRLV.setVisibility(View.VISIBLE);
            textRecordAdapter = new TextRecordAdapter(textRecords);
            extraTextRLV.setAdapter(textRecordAdapter);
        }
        imageRecords = accountRecord.getImageRecords();
        if(imageRecords.size() == 0){
            frameLayout.setVisibility(View.GONE);
        }else {
            frameLayout.setVisibility(View.VISIBLE);
            extraImageVP.setAdapter(new ImageAdapter());
            if (currentImageIndex > imageRecords.size() - 1) {
                currentImageIndex = 0;
            }
            extraImageVP.setCurrentItem(currentImageIndex);
            if(imageRecords.size() > 1){
                indicator.setBgColor(Color.parseColor("#38000000")).attachToViewPager(extraImageVP);
            }
        }
        initialSpinner();
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
