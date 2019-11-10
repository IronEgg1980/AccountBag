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
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import yzw.ahaqth.accountbag.BaseActivity;
import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.details.ShowDetailsActivity;
import yzw.ahaqth.accountbag.inputoredit.InputOrEditRecordActivity;
import yzw.ahaqth.accountbag.interfaces.DialogDismissListener;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.modules.RecordGroup;
import yzw.ahaqth.accountbag.operators.FileOperator;
import yzw.ahaqth.accountbag.operators.GroupOperator;
import yzw.ahaqth.accountbag.operators.RecordOperator;
import yzw.ahaqth.accountbag.tools.DialogFactory;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class ShowAllRecordActivity extends BaseActivity {
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private RecyclerView recyclerview;
    private RecordAdapter adapter;
    private List<AccountRecord> list;
    private boolean isAddRecord;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView titleIV;
    private Random random;
    private int[] imagesId = {R.mipmap.bg1, R.mipmap.bg2, R.mipmap.bg3, R.mipmap.bg4, R.mipmap.bg5};
    private Spinner recordGroupSpinner, sortSpinner;
    private List<String> sortList;
    private List<RecordGroup> recordGroupList;
    private long recordGroupId = -1;
    private int sortMode = 0;
    //    private Comparator<AccountRecord> nameAscComparator,nameDscComparator,timeAscComparator,timeDscComparator;
    private boolean readDataFlag = false;

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
                        if (isConfirm) {
                            int id = (int) objects[0];
                            if (id > 0) {
                                if (id == 3)
                                    deleResume();
                                else
                                    showInputPWDDialog(id);
                            }

                        }
                    }
                });
                dialogFragment.show(getSupportFragmentManager(), "setup");
            }
        });
        FileOperator.initialAppDir(this);
        isAddRecord = false;
        initialSortSpinner();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        initialRecordGroupSpinner();
        readData();
    }

    private void initialSortSpinner() {
        sortList = new ArrayList<>();
        sortList.add("日期升序");
        sortList.add("日期降序");
        sortList.add("名称升序");
        sortList.add("名称降序");
        sortSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, sortList));
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortMode = position;
                if (readDataFlag)
                    readData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initialRecordGroupSpinner() {
        recordGroupList = new ArrayList<>();
        recordGroupList.add(new RecordGroup("所有分组"));
        recordGroupList.add(new RecordGroup("测试"));
        recordGroupList.addAll(GroupOperator.findAll(true));
        recordGroupList.add(new RecordGroup("管理分组..."));
        recordGroupSpinner.setAdapter(new ArrayAdapter<RecordGroup>(this, R.layout.spinner_item, recordGroupList));
        recordGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    recordGroupId = -1;
                    if(readDataFlag)
                        readData();
                } else if (position == recordGroupList.size() - 1) {
                    parent.setSelection(0);
                    startActivity(new Intent(ShowAllRecordActivity.this,RecordGroupActivity.class));
                } else {
                    if(readDataFlag)
                        readData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showInputPWDDialog(final int mode) {
        InputPWDDialog dialog = new InputPWDDialog();
        dialog.setOnDismiss(new DialogDismissListener() {
            @Override
            public void onDismiss(boolean isConfirm, Object... objects) {
                if (isConfirm) {
                    switch (mode) {
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
        dialog.show(getSupportFragmentManager(), "showInputPWDDialog");
    }

    private void setUserNamePWD() {
        startActivity(new Intent(ShowAllRecordActivity.this, SetUserNamePWDActivity.class));
    }

    private void setGesturePWD() {
        startActivity(new Intent(ShowAllRecordActivity.this, SetGesturePWDActivity.class));
    }

    private void deleResume() {
        startActivity(new Intent(ShowAllRecordActivity.this, DeleResumeActivity.class));
    }

    private void setImage() {
        int index = random.nextInt(5);
        titleIV.setImageResource(imagesId[index]);
    }

    private void initialView() {
        list = new ArrayList<>();
        random = new Random();
        adapter = new RecordAdapter(list);
        adapter.setClickListener(new ItemClickListener() {
            @Override
            public void click(int position, @Nullable Object... values) {
                AccountRecord accountRecord = list.get(position);
                Intent intent = new Intent(ShowAllRecordActivity.this, ShowDetailsActivity.class);
                intent.putExtra("id", accountRecord.getId());
                View view = (View) values[1];
                int startX = 0, startY = view.getMeasuredHeight() / 2;
                startActivity(intent, ActivityOptions.makeScaleUpAnimation(view, startX, startY, view.getMeasuredWidth(), 0).toBundle());
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
        toolbar.setNavigationIcon(R.drawable.ic_menu_24dp);

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);
        recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
        toolbarLayout.setExpandedTitleGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        titleIV = findViewById(R.id.titleIV);
        setImage();
        appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                int offset = Math.abs(i);
                int range = appBarLayout.getTotalScrollRange();
                if (offset > range - 1) {
                    if (titleIV.getVisibility() == View.VISIBLE) {
                        titleIV.setVisibility(View.INVISIBLE);
                        setImage();
                    }
                    toolbarLayout.setTitle(getString(R.string.app_name));
                    toolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                } else if (offset < range) {
                    if (titleIV.getVisibility() != View.VISIBLE)
                        titleIV.setVisibility(View.VISIBLE);
                    toolbarLayout.setTitle("");
                    toolbar.setNavigationIcon(null);
                    if (offset < 1) {
                        toolbarLayout.setTitle(ToolUtils.getHelloString());
                    }
                }
            }
        });
        recordGroupSpinner = findViewById(R.id.recordGroupSpinner);
        sortSpinner = findViewById(R.id.sortSpinner);
    }

    private void readData() {
        readDataFlag = true;
        list.clear();
        list.addAll(RecordOperator.findAll(sortMode, recordGroupId));
        adapter.notifyDataSetChanged();
        if (isAddRecord) {
            recyclerview.scrollToPosition(adapter.getItemCount() - 1);
            isAddRecord = false;
        }
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
