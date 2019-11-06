package yzw.ahaqth.accountbag.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.rengwuxian.materialedittext.MaterialEditText;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.allrecords.ShowAllRecordActivity;
import yzw.ahaqth.accountbag.operators.SetupOperator;

public class InputPassWordFragment extends Fragment {
    private MaterialEditText appPwdET;
    private MaterialButton enterApp;
    private String userPWD;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPWD = SetupOperator.getPassWord();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_password_fragment_layout,container,false);
        initialView(view);
        return view;
    }

    private void initialView(View view){
        appPwdET = view.findViewById(R.id.app_pwd_ET);
        enterApp = view.findViewById(R.id.enter_app);
        enterApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmUserPWD();
            }
        });
    }
    private void confirmUserPWD(){
        if(TextUtils.isEmpty(appPwdET.getText())){
            appPwdET.setError("密码不能为空！");
            appPwdET.requestFocus();
            return;
        }
        String s1 = appPwdET.getText().toString().trim();
        if(s1.length() < 6){
            appPwdET.setError("密码最少6位！");
            appPwdET.requestFocus();
            return;
        }
        if(!TextUtils.equals(s1,userPWD)){
            appPwdET.setError("密码错误！");
            appPwdET.requestFocus();
            return;
        }
        enterApp();
    }
    @Override
    public void onStart() {
        super.onStart();
        appPwdET.setShowSoftInputOnFocus(true);
        appPwdET.requestFocus();
    }

    private void enterApp(){
        Intent intent = new Intent(getActivity(), ShowAllRecordActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
