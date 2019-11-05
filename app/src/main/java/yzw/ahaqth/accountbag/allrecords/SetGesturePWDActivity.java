package yzw.ahaqth.accountbag.allrecords;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import yzw.ahaqth.accountbag.BaseActivity;
import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.OnGestureViewValidateListener;
import yzw.ahaqth.accountbag.main.GestureView;
import yzw.ahaqth.accountbag.operators.SetupOperator;
import yzw.ahaqth.accountbag.tools.ToastFactory;

public class SetGesturePWDActivity extends BaseActivity {
    private Toolbar toolbar;
    private TextView setGesturePWDInfoTV;
    private GestureView setGesturePWDGestureView;
    private boolean isFirsTouch = true;
    private void initialView(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setGesturePWDInfoTV = findViewById(R.id.set_gesturePWD_info_TV);
        setGesturePWDGestureView = findViewById(R.id.set_gesturePWD_gestureView);
        setGesturePWDGestureView.setUnMatchExceedBoundary(9999);
        setGesturePWDGestureView.setAnswer("");
        setGesturePWDGestureView.setValidateListener(new OnGestureViewValidateListener() {
            @Override
            public void onBlockSelected(int cId) {
            }

            @Override
            public void onGestureEvent(boolean matched) {
                if(TextUtils.isEmpty(setGesturePWDGestureView.getAnswer())){
                    setGesturePWDInfoTV.setText("最少连接4个点");
                    return;
                }
                if(matched) {
                    SetupOperator.saveGesturePassWord(setGesturePWDGestureView.getAnswer());
                    SetupOperator.setInputPassWordMode(2);
                    new ToastFactory(SetGesturePWDActivity.this).showCenterToast("设置成功");
                    finish();
                }else if(isFirsTouch){
                    setGesturePWDInfoTV.setText("请确认手势");
                    setGesturePWDGestureView.setAnswer(setGesturePWDGestureView.getAnswer());
                    isFirsTouch = false;
                }else{
                    isFirsTouch = true;
                    setGesturePWDGestureView.setAnswer("");
                    setGesturePWDInfoTV.setText("两次输入的手势密码不一致，请重新设置！");
                }
            }

            @Override
            public void onUnmatchedExceedBoundary() {

            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_gesture_pwd);
        initialView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
