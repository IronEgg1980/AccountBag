package yzw.ahaqth.accountbag.main;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.ScrollView;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import yzw.ahaqth.accountbag.BaseActivity;
import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.operators.RecordOperator;
import yzw.ahaqth.accountbag.operators.SetupOperator;
import yzw.ahaqth.accountbag.tools.ToastFactory;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class MainActivity extends BaseActivity {
    private ScrollView scrollView;
    private FragmentManager fragmentManager;
    private ToastFactory toastFactory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.scrollview);
        fragmentManager = getSupportFragmentManager();
        toastFactory = new ToastFactory(this);
        update();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        int mode = SetupOperator.getInputPassWordMode();
        if(TextUtils.isEmpty(SetupOperator.getUserName())) {
            initial();
        }else {
            if(mode == 1)
                changeToInputMode();
            else if(mode == 2)
                changeToGestureMode();
        }
    }

    private void initial(){
        if(XXPermissions.isHasPermission(this,Permission.CAMERA,Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE)) {
            fragmentManager.beginTransaction().replace(R.id.fragment_group, new FirstRunFragment()).commit();
        }else{
            requestPermission();
        }
    }

    private void requestPermission(){
        XXPermissions.with(this)
                .permission(Permission.CAMERA,Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll){
                            fragmentManager.beginTransaction().replace(R.id.fragment_group, new FirstRunFragment()).commit();
                        }else{
                            toastFactory.showCenterToast("已拒绝授权，APP终止运行！");
                            finish();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if(quick){
                            XXPermissions.gotoPermissionSettings(MainActivity.this,true);
                        }else{
                            toastFactory.showCenterToast("已拒绝授权，APP终止运行！");
                            finish();
                        }
                    }
                });
    }

    public void changeToGestureMode(){
        fragmentManager.beginTransaction().replace(R.id.fragment_group,new GesturePassWordFragment()).commit();
    }

    public void changeToInputMode(){
        fragmentManager.beginTransaction().replace(R.id.fragment_group,new InputPassWordFragment()).commit();
    }

    public void changeToSetGesturePWD(){
        fragmentManager.beginTransaction().replace(R.id.fragment_group,new GesturePWDSetFragmet()).commit();
    }

    public void scrollToBottom(){
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollBy(0,250);
            }
        },100);
    }

    public void update(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RecordOperator.clearOldDeletedRecord();// 清除删除30天以上的项目
                if(SetupOperator.getLastAppVersion() < 2){
                    for(AccountRecord accountRecord: RecordOperator.findAll()){
                        accountRecord.setDeleted(false);
                        RecordOperator.save(accountRecord);
                    }
                }
                long version = ToolUtils.getAppVersionCode(MainActivity.this);
                SetupOperator.setLastAppVersion(version);
            }
        }).start();
    }
}
