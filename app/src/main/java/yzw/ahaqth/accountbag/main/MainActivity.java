package yzw.ahaqth.accountbag.main;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;

import yzw.ahaqth.accountbag.BaseActivity;
import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.operators.RecordOperator;
import yzw.ahaqth.accountbag.operators.SetupOperator;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class MainActivity extends BaseActivity {
    private ScrollView scrollView;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.scrollview);
        int mode = SetupOperator.getInputPassWordMode();
        fragmentManager = getSupportFragmentManager();
        if(TextUtils.isEmpty(SetupOperator.getUserName())) {
            fragmentManager.beginTransaction().replace(R.id.fragment_group, new FirstRunFragment()).commit();
        }else {
            if(mode == 1)
                changeToInputMode();
            else if(mode == 2)
                changeToGestureMode();
        }
        update();
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
//        scrollView.fullScroll(View.FOCUS_DOWN); // 底部
//        scrollView.fullScroll(View.FOCUS_UP); // 顶部
    }

    public void update(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RecordOperator.clearOldDeletedRecord(); // 清除删除30天以上的项目
            }
        }).start();
        if(SetupOperator.getLastAppVersion() < 2){
            for(AccountRecord accountRecord: RecordOperator.findAll()){
                accountRecord.setDeleted(false);
                RecordOperator.save(accountRecord);
            }
        }
        long version = ToolUtils.getAppVersionCode(this);
        SetupOperator.setLastAppVersion(version);
    }
}
