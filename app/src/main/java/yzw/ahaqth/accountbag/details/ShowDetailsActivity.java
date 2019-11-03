package yzw.ahaqth.accountbag.details;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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

public class ShowDetailsActivity extends AppCompatActivity {
    private String TAG = "殷宗旺";

    private TextView accountNameTV;
    private TextView accountPwdTV;
    private TextView accountDiscribeTV;
    private RecyclerView extraTextRLV;
    private TextView recordTimeTV,modifyTimeTV;
    private RecyclerView extraImageRLV;
    private Toolbar toolbar;
    private CardView accountNameCardView,accountPWDCardView,extraTextCardView,extraImageCardView;
    private ImageButton copyName,copyPWD,seePWD;
    private TextView recordNameTV;

    private long id;
    private SimpleDateFormat format;
    private List<TextRecord> textRecords;
    private List<ImageRecord> imageRecords;
    private TextRecordAdapter textRecordAdapter;
    private ImageRecordAdapter imageRecordAdapter;
    private LinearLayoutManager manager;
    private DividerItemDecoration dividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.temp);
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

    private void deleRecord(){
        AccountRecord record = RecordOperator.findOne(id);
        if (record!=null) {
//            for (ImageRecord imageRecord : record.getImageRecords()) {
//                ImageOperator.deleImageFile(imageRecord);
//            }
//            record.delete();
            RecordOperator.dele(record);
            new ToastFactory(this).showCenterToast("已删除");
            finish();
        }
    }

    private void initialView(){
//        accountNameGroup = findViewById(R.id.accountNameGroup);
//        accountPWDGroup = findViewById(R.id.accountPWDGroup);
//        line = findViewById(R.id.line);
//        contentCardView = findViewById(R.id.contentCardView);
        accountNameCardView = findViewById(R.id.accountNameCardView);
        accountPWDCardView = findViewById(R.id.accountPWDCardView);
        extraTextCardView = findViewById(R.id.extraTextCardView);
        extraImageCardView  = findViewById(R.id.extraImageCardView);
//        noteGroup = findViewById(R.id.noteGroup);
        accountNameTV = findViewById(R.id.account_name_TV);
        accountPwdTV = findViewById(R.id.account_pwd_TV);
        accountDiscribeTV = findViewById(R.id.account_discribe_TV);
        extraTextRLV = findViewById(R.id.extra_text_RLV);
        extraTextRLV.setLayoutManager(new LinearLayoutManager(this));
        recordTimeTV = findViewById(R.id.record_time_TV);
        modifyTimeTV = findViewById(R.id.modify_time_TV);
        extraImageRLV = findViewById(R.id.extra_image_RLV);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        copyName = findViewById(R.id.copy_account_name);
        copyPWD = findViewById(R.id.copy_account_pwd);
        seePWD = findViewById(R.id.show_account_pwd);
        recordNameTV = findViewById(R.id.record_name_TV);
        manager = new LinearLayoutManager(this);
    }

    private void readData(){
        boolean b2 = false;
        final AccountRecord record = RecordOperator.findOne(id);
        if(record == null)
            return;
//        setTitle(record.getRecordName());
        recordNameTV.setText(record.getRecordName());
        String createTimeString = "创建时间："+format.format(record.getRecordTime());
        recordTimeTV.setText(createTimeString);
        if(record.getModifyTime() > 1000){
            String modifyTimeString = "最近修改："+format.format(record.getModifyTime());
            modifyTimeTV.setText(modifyTimeString);
        }else{
            modifyTimeTV.setText("");
        }
        if(TextUtils.isEmpty(record.getAccountName())){
            b2 = true;
//            accountNameGroup.setVisibility(View.GONE);
            accountNameCardView.setVisibility(View.GONE);
        }else {
//            accountNameGroup.setVisibility(View.VISIBLE);
            accountNameCardView.setVisibility(View.VISIBLE);
//            b = false;
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
            b2 = true;
//            accountPWDGroup.setVisibility(View.GONE);
            accountPWDCardView.setVisibility(View.GONE);
        }else {
            accountPWDCardView.setVisibility(View.VISIBLE);
//            accountPWDGroup.setVisibility(View.VISIBLE);
//            b = false;
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
        if(TextUtils.isEmpty(record.getDescribe())){
            accountDiscribeTV.setVisibility(View.GONE);
        }else {
            accountDiscribeTV.setVisibility(View.VISIBLE);
            accountDiscribeTV.setText(record.getDescribe());
        }
//        if(b)
//            contentCardView.setVisibility(View.GONE);
//        else
//            contentCardView.setVisibility(View.VISIBLE);
//        if(b2){
//            line.setVisibility(View.GONE);
//        }else{
//            line.setVisibility(View.VISIBLE);
//        }
        textRecords = record.getTextRecords();
        if(textRecords.size() == 0){
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
            extraImageCardView.setMinimumHeight(800);
            extraTextCardView.setVisibility(View.GONE);
        }else{
            extraTextCardView.setVisibility(View.VISIBLE);
            textRecordAdapter = new TextRecordAdapter(textRecords);
            extraTextRLV.setAdapter(textRecordAdapter);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL);
        }
        imageRecords = record.getImageRecords();
        if(imageRecords.size() == 0){
            extraImageCardView.setVisibility(View.GONE);
        }else{
            extraImageCardView.setVisibility(View.VISIBLE);
            imageRecordAdapter = new ImageRecordAdapter(imageRecords);
            imageRecordAdapter.setClickListener(new ItemClickListener() {
                @Override
                public void click(int position, @Nullable Object... values) {
                    ImageRecord imageRecord = imageRecords.get(position);
                    Intent intent = new Intent(ShowDetailsActivity.this,ShowLargeImageActivity.class);
                    intent.putExtra("path",ImageOperator.getRealImagePath(imageRecord));
                    String s = record.getRecordName()+"_图片"+(position+1)+".jpg";
                    intent.putExtra("name",s);
                    Pair<View,String> pair = new Pair<>((View) values[0],"showLargeImage");
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ShowDetailsActivity.this,pair).toBundle());
                }
            });
            extraImageRLV.setLayoutManager(manager);
            extraImageRLV.setAdapter(imageRecordAdapter);
            extraImageRLV.addItemDecoration(dividerItemDecoration);
        }
    }
}
