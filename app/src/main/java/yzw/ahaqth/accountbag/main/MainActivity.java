package yzw.ahaqth.accountbag.main;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.details.ShowDetailsActivity;
import yzw.ahaqth.accountbag.inputoredit.InputOrEditRecordActivity;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.modules.ImageRecord;
import yzw.ahaqth.accountbag.operators.FileOperator;
import yzw.ahaqth.accountbag.operators.ImageOperator;
import yzw.ahaqth.accountbag.operators.RecordOperator;
import yzw.ahaqth.accountbag.tools.DialogFactory;

public class MainActivity extends AppCompatActivity {
    String TAG = "殷宗旺";
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private RecyclerView recyclerview;
    private RecordAdapter adapter;
    private List<AccountRecord> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialView();
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.app_name));
        FileOperator.initialAppDir(this);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        readData();
    }

    private void initialView() {
        list = new ArrayList<>();
        adapter = new RecordAdapter(list);
        adapter.setClickListener(new ItemClickListener() {
            @Override
            public void click(int position, @Nullable Object... values) {
                AccountRecord accountRecord = list.get(position);
                Pair<View, String> pair1 = new Pair<>((View) values[0], "mainRecordItem");
                Intent intent = new Intent(MainActivity.this, ShowDetailsActivity.class);
                intent.putExtra("id", accountRecord.getId());
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pair1).toBundle());
            }
        });
        adapter.setDelClick(new ItemClickListener() {
            @Override
            public void click(int position, @Nullable Object... values) {
                deleRecord(position);
            }
        });
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputOrEditRecordActivity.class);
                startActivity(intent);
            }
        });
        toolbar = findViewById(R.id.toolbar);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                }
                if (dy < -3 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
    }

    private void readData() {
        list.clear();
        list.addAll(RecordOperator.findAll());
        adapter.notifyDataSetChanged();
        if (fab.getVisibility() != View.VISIBLE)
            fab.show();
    }

    private void deleRecord(final int position) {
        final AccountRecord record = list.get(position);
        if (record != null) {
            String message = "是否要删除【"+record.getRecordName()+"】？";
            new DialogFactory(this).showDefaultConfirmDialog(message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (ImageRecord imageRecord : record.getImageRecords()) {
                        ImageOperator.deleImageFile(imageRecord);
                    }
                    record.delete();
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            });
        }
    }

}
