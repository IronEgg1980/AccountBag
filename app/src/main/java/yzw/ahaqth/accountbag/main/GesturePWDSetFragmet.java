package yzw.ahaqth.accountbag.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.custom_views.GestureView;
import yzw.ahaqth.accountbag.interfaces.OnGestureViewValidateListener;
import yzw.ahaqth.accountbag.operators.SetupOperator;
import yzw.ahaqth.accountbag.tools.ToastFactory;

public class GesturePWDSetFragmet extends Fragment {
    private MainActivity activity;
    private TextView setGesturePWDInfoTV;
    private GestureView setGesturePWDGestureView;
    private boolean isFirsTouch = true;
    private void initialView(View view){
        setGesturePWDInfoTV = view.findViewById(R.id.set_gesturePWD_info_TV);
        setGesturePWDGestureView = view.findViewById(R.id.set_gesturePWD_gestureView);
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
                    new ToastFactory(getContext()).showCenterToast("设置成功");
                    /* 重启应用的代码 */
                    PackageManager pm = getContext().getPackageManager();
                    if(pm !=null){
                        Intent intent = pm.getLaunchIntentForPackage(getContext().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(intent);
                    }else{
                        activity.changeToGestureMode();
                    }

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gesture_pwd_set_fragment,container,false);
        activity = (MainActivity) getActivity();
        initialView(view);
        return view;
    }
}
