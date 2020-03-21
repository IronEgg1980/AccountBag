package yzw.ahaqth.accountbag.main;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.DialogDismissListener;
import yzw.ahaqth.accountbag.operators.SetupOperator;
import yzw.ahaqth.accountbag.tools.DialogFactory;
import yzw.ahaqth.accountbag.tools.ToastFactory;

public class FirstRunFragment extends Fragment {
    private MaterialEditText appUsernameET;
    private MaterialEditText appPwdET;
    private MaterialEditText appPwdConfirmET;
    private MaterialButton setUser;
    private MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.setup_username_pwd_layout,container,false);
        initialView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appUsernameET.setShowSoftInputOnFocus(true);
        appUsernameET.requestFocus();
    }

    private void initialView(View view){
        appUsernameET = view.findViewById(R.id.app_username_ET);
        appPwdET = view.findViewById(R.id.app_pwd_ET);
        appPwdConfirmET = view.findViewById(R.id.app_pwd_confirm_ET);
        appPwdConfirmET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    activity.scrollToBottom();
                }
            }
        });
        setUser = view.findViewById(R.id.set_user);
        setUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserName();
            }
        });
    }

    private void setUserName() {
        if (TextUtils.isEmpty(appUsernameET.getText())) {
            appUsernameET.setError("请设置用户名");
            appUsernameET.requestFocus();
            return;
        }
        String userName = appUsernameET.getText().toString().trim();
        if (TextUtils.isEmpty(appPwdET.getText())) {
            appPwdET.setError("请设置密码");
            appPwdET.requestFocus();
            return;
        }
        String s1 = appPwdET.getText().toString().trim();
        if (s1.length() < 6 || s1.length() > 20) {
            appPwdET.setError("密码长度6-20位");
            appPwdET.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(appPwdConfirmET.getText())) {
            appPwdConfirmET.setError("请确认密码");
            appPwdConfirmET.requestFocus();
            return;
        }
        String s2 = appPwdConfirmET.getText().toString().trim();
        if (!TextUtils.equals(s1, s2)) {
            appPwdConfirmET.setError("两次输入的密码不一致！");
            appPwdConfirmET.requestFocus();
            return;
        }
        SetupOperator.saveUserName(userName);
        SetupOperator.savePassWord(s1);
//        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        manager.hideSoftInputFromWindow(appPwdConfirmET.getWindowToken(),0);
//        activity.changeToSetGesturePWD();
        DialogFactory.getConfirmDialog("用户名和密码设置成功！是否现在设置手势密码？")
                .setDismissListener(new DialogDismissListener() {
                    @Override
                    public void onDismiss(boolean isConfirm, Object... valus) {
                        if(isConfirm){
                            InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(appPwdConfirmET.getWindowToken(),0);
                            activity.changeToSetGesturePWD();
                        }else{
                            activity.changeToInputMode();
                        }
                    }
                })
                .show(activity.getSupportFragmentManager(),"confirm");
    }
}
