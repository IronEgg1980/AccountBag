package yzw.ahaqth.accountbag.allrecords;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.DialogDismissListener;
import yzw.ahaqth.accountbag.operators.SetupOperator;

public class InputPWDDialog extends DialogFragment {
    private MaterialEditText edittext;
    private MaterialButton button;
    private DialogDismissListener onDismiss;
    private boolean flag = false;

    public void setOnDismiss(DialogDismissListener onDismiss) {
        this.onDismiss = onDismiss;
    }

    private void confirmClick(){
        if(TextUtils.isEmpty(edittext.getText())){
            edittext.setError("请输入密码");
            edittext.requestFocus();
            return;
        }
        String s = edittext.getText().toString().trim();
        if (s.length() < 6){
            edittext.setError("密码长度至少6位");
            edittext.requestFocus();
            return;
        }
        if(TextUtils.equals(s, SetupOperator.getPassWord())){
            flag = true;
            dismiss();
        }else{
            flag = false;
            edittext.setError("密码错误");
            edittext.requestFocus();
        }
    }

    private void initialView(View view){
        edittext = view.findViewById(R.id.edittext);
        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClick();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_edittext_layout,container,false);
        initialView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null){
            DisplayMetrics dm = new DisplayMetrics();
            Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = (int) (dm.widthPixels * 0.75);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            Window window = dialog.getWindow();
            if(window!=null){
                window.setLayout(width,height);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            edittext.requestFocus();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(),0);
        super.onDismiss(dialog);
        onDismiss.onDismiss(flag);
    }
}
