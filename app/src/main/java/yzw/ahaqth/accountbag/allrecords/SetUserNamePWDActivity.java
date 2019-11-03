package yzw.ahaqth.accountbag.allrecords;

import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.rengwuxian.materialedittext.MaterialEditText;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.operators.SetupOperator;
import yzw.ahaqth.accountbag.tools.ToastFactory;

public class SetUserNamePWDActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialEditText appUsernameET;
    private MaterialEditText appPwdET;
    private MaterialEditText appPwdConfirmET;
    private MaterialButton setUser;

    private void initialView(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        appUsernameET = findViewById(R.id.app_username_ET);
        appUsernameET.setText(SetupOperator.getUserName());
        String pwd = SetupOperator.getPassWord();
        appPwdET = findViewById(R.id.app_pwd_ET);
        appPwdET.setText(pwd);
        appPwdConfirmET = findViewById(R.id.app_pwd_confirm_ET);
        appPwdConfirmET.setText(pwd);
        setUser = findViewById(R.id.set_user);
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
        new ToastFactory(this).showCenterToast("设置成功");
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_name_pwd);
        initialView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        appUsernameET.requestFocus();
    }
}
