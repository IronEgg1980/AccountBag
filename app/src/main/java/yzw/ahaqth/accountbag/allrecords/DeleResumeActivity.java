package yzw.ahaqth.accountbag.allrecords;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.tools.DialogFactory;

public class DeleResumeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout buttonGroup;
    private LinearLayout infoGroup;
    private DeleResumeAdapter adapter;
    private boolean isMultiMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dele_resume);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter = new DeleResumeAdapter(this);
        adapter.setLongClick(new ItemClickListener() {
            @Override
            public void click(int position, @Nullable Object... values) {
                isMultiMode = true;
                infoGroup.setVisibility(View.GONE);
                buttonGroup.setVisibility(View.VISIBLE);
            }
        });
        recyclerView.setAdapter(adapter);
        buttonGroup = findViewById(R.id.button_group);
        buttonGroup.setVisibility(View.GONE);
        infoGroup = findViewById(R.id.info_Group);
        findViewById(R.id.resume_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeAll();
                cancelMultiMode();
            }
        });
        findViewById(R.id.clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogFactory(DeleResumeActivity.this).showWarningDialog("删除确认", "是否彻底清除选中的项目？\n注意：清除后无法恢复！",
                        "清除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.clearAll();
                                cancelMultiMode();
                            }
                        },
                        "取消", null);
            }
        });
        findViewById(R.id.cancel_multiMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelMultiMode();
            }
        });
        findViewById(R.id.close_info_Group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoGroup.setVisibility(View.GONE);
            }
        });
    }

    private void cancelMultiMode(){
        adapter.cancelMultiMode();
        buttonGroup.setVisibility(View.GONE);
        isMultiMode = false;
    }

    @Override
    public void onBackPressed() {
        if(isMultiMode){
            cancelMultiMode();
        }else{
            super.onBackPressed();
        }
    }
}
