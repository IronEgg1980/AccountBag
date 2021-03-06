package yzw.ahaqth.accountbag.allrecords;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import yzw.ahaqth.accountbag.BaseActivity;
import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.DialogDismissListener;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.interfaces.NoDoubleClicker;
import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.operators.RecordOperator;
import yzw.ahaqth.accountbag.tools.DialogFactory;

public class DeleResumeActivity extends BaseActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout buttonGroup;
    private AppCompatTextView infoTV;
//    private LinearLayout infoGroup;
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
        infoTV = findViewById(R.id.info_TV);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);

        List<AccountRecord> list = RecordOperator.findAllDeleted();
        recyclerView.removeItemDecoration(divider);
        if(list.isEmpty()){
            AccountRecord accountRecord = new AccountRecord();
            accountRecord.setSortIndex(999999);
            list.add(accountRecord);
        }else{
            recyclerView.addItemDecoration(divider);
        }
        adapter = new DeleResumeAdapter(this,list);
        adapter.setLongClick(new ItemClickListener() {
            @Override
            public void click(int position, @Nullable Object... values) {
                isMultiMode = true;
                infoTV.setVisibility(View.GONE);
                buttonGroup.setVisibility(View.VISIBLE);
            }
        });
        recyclerView.setAdapter(adapter);
        buttonGroup = findViewById(R.id.button_group);
        buttonGroup.setVisibility(View.GONE);
//        infoGroup = findViewById(R.id.info_Group);
        findViewById(R.id.resume_button).setOnClickListener(new NoDoubleClicker() {
            @Override
            public void noDoubleClick(View v) {
                adapter.resumeAll();
                cancelMultiMode();
            }
        });
        findViewById(R.id.clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFactory.getConfirmDialog( "是否彻底清除选中的项目？\n注意：清除后无法恢复！")
                        .setDismissListener(new DialogDismissListener() {
                            @Override
                            public void onDismiss(boolean isConfirm, Object... valus) {
                                if(isConfirm) {
                                    adapter.clearAll();
                                    cancelMultiMode();
                                }
                            }
                        })
                        .show(getSupportFragmentManager(),"confirm");
            }
        });
        findViewById(R.id.cancel_multiMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelMultiMode();
            }
        });
//        findViewById(R.id.close_info_Group).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                infoGroup.setVisibility(View.GONE);
//            }
//        });
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
