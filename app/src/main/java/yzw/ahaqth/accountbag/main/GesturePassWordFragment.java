package yzw.ahaqth.accountbag.main;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.allrecords.ShowAllRecordActivity;
import yzw.ahaqth.accountbag.custom_views.GestureView;
import yzw.ahaqth.accountbag.interfaces.OnGestureViewValidateListener;
import yzw.ahaqth.accountbag.operators.SetupOperator;
import yzw.ahaqth.accountbag.tools.ToastFactory;

public class GesturePassWordFragment extends Fragment {
    private GestureView gestureView;
    private TextView textview1,forgetGesturePWD;
    private Vibrator vibrator;
    private String gesturePWD;
    private MainActivity activity;

    private void initialView(View view) {
        forgetGesturePWD = view.findViewById(R.id.goto_input_password_TV);
        forgetGesturePWD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ToastFactory(getContext()).showCenterToast("请输入文字密码");
                activity.changeToInputMode();
            }
        });
        textview1 = view.findViewById(R.id.gesture_fragment_info_TV);
        gestureView = view.findViewById(R.id.gestureView);
        gestureView.setAnswer(gesturePWD);
        gestureView.setValidateListener(new OnGestureViewValidateListener() {
            @Override
            public void onBlockSelected(int cId) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    VibrationEffect effect = VibrationEffect.createOneShot(30, 50);
                    vibrator.vibrate(effect);
                } else {
                    vibrator.vibrate(30);
                }
            }

            @Override
            public void onGestureEvent(boolean matched) {
                if(TextUtils.isEmpty(gestureView.getAnswer())){
                    textview1.setText("最少连接4个点");
                    return;
                }
                if(matched){
                    textview1.setText("手势验证成功");
                    Intent intent = new Intent(getActivity(), ShowAllRecordActivity.class);
                    startActivity(intent);
                    activity.finish();
                }else{
                    int times = gestureView.getRemainTime();
                    if(times > 0){
                        String s = "手势验证失败，还剩 " + times + " 次机会";
                        textview1.setText(s);
                    }else{
                        SetupOperator.setInputPassWordMode(1);
                        SetupOperator.saveGesturePassWord("-1");
                        textview1.setText("请使用文字密码登录");
                    }
                }
            }

            @Override
            public void onUnmatchedExceedBoundary() {
                activity.changeToInputMode();
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) Objects.requireNonNull(getContext()).getSystemService(Context.VIBRATOR_SERVICE);
        gesturePWD = SetupOperator.getGesturePassWord();
        activity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gesture_password_fragment_layout,container,false);
        initialView(view);
        return view;
    }
}
