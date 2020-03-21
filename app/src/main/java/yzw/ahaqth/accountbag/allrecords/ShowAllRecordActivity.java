package yzw.ahaqth.accountbag.allrecords;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
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
import yzw.ahaqth.accountbag.operators.SetupOperator;
import yzw.ahaqth.accountbag.tools.DialogFactory;
import yzw.ahaqth.accountbag.tools.ToastFactory;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class ShowAllRecordActivity extends BaseActivity {
    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private RecyclerView recyclerview;
    private RecordAdapter adapter;
    private GroupAdapter groupAdapter;
    private List<RecordGroup> groupList;
    private List<AccountRecord> list;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView titleIV;
    private Random random;
    private int[] imagesId = {R.mipmap.bg1, R.mipmap.bg2, R.mipmap.bg3, R.mipmap.bg4, R.mipmap.bg5};
    private long recordGroupId;
    private String title;
    private RecyclerView.ItemDecoration dividerItemDecoration;
    private BroadcastReceiver groupFlush, addRecordBR;
    private LocalBroadcastManager lbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_record_activity);
        FileOperator.initialAppDir(this);
        recordGroupId = -1L;
        title = "全部记录";
        initialView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
        lbm = LocalBroadcastManager.getInstance(this);
        groupFlush = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                groupList.clear();
                groupList.addAll(GroupOperator.findAll(true));
                groupAdapter.notifyDataSetChanged();
            }
        };
        addRecordBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                recordGroupId = -1L;
                title = "全部记录";
                readData();
                recyclerview.scrollToPosition(list.size() - 1);
            }
        };
        lbm.registerReceiver(addRecordBR, new IntentFilter("AddRecord"));
        lbm.registerReceiver(groupFlush, new IntentFilter("GroupFlush"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lbm.unregisterReceiver(addRecordBR);
        lbm.unregisterReceiver(groupFlush);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        readData();
    }

    private void setupRecordGroup() {
        startActivity(new Intent(ShowAllRecordActivity.this, RecordGroupActivity.class));
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
        groupList = new ArrayList<>();
        groupList.addAll(GroupOperator.findAll(true));
        random = new Random();
        groupAdapter = new GroupAdapter(this, groupList);
        adapter = new RecordAdapter(list);
        groupAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void click(int position, @Nullable Object... values) {
                drawerLayout.closeDrawers();
                appBarLayout.setExpanded(false);
                RecordGroup recordGroup = groupList.get(position);
                recordGroupId = recordGroup.getId();
                title = recordGroup.getGroupName();
                readData();
                recyclerview.scrollToPosition(0);
            }
        });
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
            }
        });
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        drawerLayout = findViewById(R.id.drawerLayout);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);
        dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
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
                } else if (offset < range) {
                    if (titleIV.getVisibility() != View.VISIBLE)
                        titleIV.setVisibility(View.VISIBLE);
                }
            }
        });
        findViewById(R.id.slide_menu_accountname_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputPWDDialog(1);
                drawerLayout.closeDrawers();
            }
        });
        findViewById(R.id.slide_menu_gesture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputPWDDialog(2);
                drawerLayout.closeDrawers();
            }
        });
        findViewById(R.id.slide_menu_textpwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupOperator.setInputPassWordMode(1);
                new ToastFactory(ShowAllRecordActivity.this).showCenterToast("已启用文字密码");
                drawerLayout.closeDrawers();
            }
        });
        findViewById(R.id.slide_menu_groupmanage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupRecordGroup();
                drawerLayout.closeDrawers();
            }
        });
        findViewById(R.id.slide_menu_resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleResume();
                drawerLayout.closeDrawers();
            }
        });
        findViewById(R.id.slide_menu_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ToastFactory(ShowAllRecordActivity.this).showCenterToast("谢谢使用！本页面在建设中...");
                drawerLayout.closeDrawers();
            }
        });
        findViewById(R.id.slide_menu_allgroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                recordGroupId = -1L;
                title = "全部记录";
                readData();
                appBarLayout.setExpanded(false);
            }
        });
        TextView slideMenuTitleTV = findViewById(R.id.slidemenu_title);
        slideMenuTitleTV.setText(ToolUtils.getHelloString());
        RecyclerView recyclerView = findViewById(R.id.slide_menu_groupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(groupAdapter);
    }

    private void readData() {
        toolbarLayout.setTitle(title);
        list.clear();
        list.addAll(RecordOperator.findAll(recordGroupId));
        recyclerview.removeItemDecoration(dividerItemDecoration);
        if (list.isEmpty()) {
            AccountRecord accountRecord = new AccountRecord();
            accountRecord.setSortIndex(999999);
            list.add(accountRecord);
        } else {
            recyclerview.addItemDecoration(dividerItemDecoration);
        }
        adapter.notifyDataSetChanged();
    }

    private void deleRecord(final int position, final RecordAdapter.RecordVH recordVH) {
        final AccountRecord record = list.get(position);
        if (record != null) {
            String message = "是否要删除【" + record.getRecordName() + "】？";
            DialogFactory.getConfirmDialog(message)
                    .setDismissListener(new DialogDismissListener() {
                        @Override
                        public void onDismiss(boolean isConfirm, Object... valus) {
                            if (isConfirm) {
//                            for (ImageRecord imageRecord : record.getImageRecords()) {
//                                ImageOperator.deleImageFile(imageRecord);
//                            }
//                            record.delete();
                                RecordOperator.dele(record);
                                list.remove(position);
                                adapter.notifyItemRemoved(position);
                            }else{
                                recordVH.closeMenu();
                            }
                        }
                    })
                    .show(getSupportFragmentManager(),"confirmDele");
        }
    }
}
