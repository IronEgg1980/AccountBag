package yzw.ahaqth.accountbag.allrecords;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.details.ShowDetailsActivity;
import yzw.ahaqth.accountbag.inputoredit.InputOrEditRecordActivity;
import yzw.ahaqth.accountbag.interfaces.DialogDismissListener;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.modules.ImageRecord;
import yzw.ahaqth.accountbag.operators.FileOperator;
import yzw.ahaqth.accountbag.operators.ImageOperator;
import yzw.ahaqth.accountbag.operators.RecordOperator;
import yzw.ahaqth.accountbag.operators.SetupOperator;
import yzw.ahaqth.accountbag.tools.DialogFactory;
import yzw.ahaqth.accountbag.tools.ToastFactory;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class ShowAllRecordActivity extends AppCompatActivity {
    String TAG = "殷宗旺";
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private RecyclerView recyclerview;
    private RecordAdapter adapter;
    private List<AccountRecord> list;
    private boolean isAddRecord;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_record_activity);
        initialView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupDialogFragment dialogFragment = new SetupDialogFragment();
                dialogFragment.setOnDismiss(new DialogDismissListener() {
                    @Override
                    public void onDismiss(boolean isConfirm, Object... objects) {
                        if(isConfirm) {
                            int id = (int) objects[0];
                            if (id > 0) {
                                if(id == 3)
                                    deleResume();
                                else
                                    showInputPWDDialog(id);
                            }

                        }
                    }
                });
                dialogFragment.show(getSupportFragmentManager(),"setup");
            }
        });
        FileOperator.initialAppDir(this);
        isAddRecord = false;
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        readData();
    }

    private void showInputPWDDialog(final int mode){
        InputPWDDialog dialog = new InputPWDDialog();
        dialog.setOnDismiss(new DialogDismissListener() {
            @Override
            public void onDismiss(boolean isConfirm, Object... objects) {
                if(isConfirm){
                    switch (mode){
                        case 1:
                            setUserNamePWD();// 设置用户名和密码
                            break;
                        case 2:
                            setGesturePWD();// 设置手势密码
                            break;
                    }
                }
            }
        });
        dialog.show(getSupportFragmentManager(),"showInputPWDDialog");
    }

    private void setUserNamePWD(){
        startActivity(new Intent(ShowAllRecordActivity.this,SetUserNamePWDActivity.class));
    }

    private void setGesturePWD(){
        startActivity(new Intent(ShowAllRecordActivity.this,SetGesturePWDActivity.class));
    }

    private void deleResume(){
        startActivity(new Intent(ShowAllRecordActivity.this,DeleResumeActivity.class));
    }

    private void initialView() {
        list = new ArrayList<>();
        adapter = new RecordAdapter(list);
        adapter.setClickListener(new ItemClickListener() {
            @Override
            public void click(int position, @Nullable Object... values) {
                AccountRecord accountRecord = list.get(position);
                Pair<View, String> pair1 = new Pair<>((View) values[0], "mainRecordItem");
                Intent intent = new Intent(ShowAllRecordActivity.this, ShowDetailsActivity.class);
                intent.putExtra("id", accountRecord.getId());
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ShowAllRecordActivity.this, pair1).toBundle());
            }
        });
        adapter.setDelClick(new ItemClickListener() {
            @Override
            public void click(int position, @Nullable Object... values) {
                deleRecord(position, (RecordAdapter.RecordVH) values[0]);
            }
        });
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowAllRecordActivity.this, InputOrEditRecordActivity.class);
                startActivity(intent);
                isAddRecord = true;
            }
        });
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_settings_white_24dp);

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0
                        && fab.getVisibility() == View.VISIBLE
                        && recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) { // 到达底部
                    fab.hide();
                }
                if (dy < -3 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
        toolbarLayout = findViewById(R.id.toolbar_layout);
        appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (Math.abs(i) < 300) {
                    toolbarLayout.setTitle(ToolUtils.getHelloString());
                    toolbar.setNavigationIcon(null);
                } else {
                    toolbarLayout.setTitle(getString(R.string.app_name));
                    toolbar.setNavigationIcon(R.drawable.ic_settings_white_24dp);
                }
            }
        });
    }

    private void readData() {
        list.clear();
        list.addAll(RecordOperator.findAllNotDeleted());
        adapter.notifyDataSetChanged();
        if (isAddRecord) {
            recyclerview.scrollToPosition(adapter.getItemCount() - 1);
        }
        if (fab.getVisibility() != View.VISIBLE)
            fab.show();
    }

    private void deleRecord(final int position, final RecordAdapter.RecordVH recordVH) {
        final AccountRecord record = list.get(position);
        if (record != null) {
            String message = "是否要删除【" + record.getRecordName() + "】？";
            new DialogFactory(this).showWarningDialog("删除确认", message,
                    "删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            for (ImageRecord imageRecord : record.getImageRecords()) {
//                                ImageOperator.deleImageFile(imageRecord);
//                            }
//                            record.delete();
                            RecordOperator.dele(record);
                            list.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                    },
                    "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            recordVH.closeMenu();
                        }
                    });
        }
    }

}
