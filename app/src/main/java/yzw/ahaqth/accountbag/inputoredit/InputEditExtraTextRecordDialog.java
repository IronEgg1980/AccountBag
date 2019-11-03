package yzw.ahaqth.accountbag.inputoredit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.DialogDismissListener;

public class InputEditExtraTextRecordDialog extends DialogFragment {
    private String key,content;
    private boolean isConfirm;
    private DialogDismissListener onDismiss;
    private EditText keyET,contentET;

    public void setOnDismiss(DialogDismissListener onDismiss) {
        this.onDismiss = onDismiss;
    }

    public static InputEditExtraTextRecordDialog getInstance(String key, String content){
        InputEditExtraTextRecordDialog inputEditExtraTextRecordDialog = new InputEditExtraTextRecordDialog();
        Bundle bundle = new Bundle();
        bundle.putString("key",key);
        bundle.putString("content",content);
        inputEditExtraTextRecordDialog.setArguments(bundle);
        return inputEditExtraTextRecordDialog;
    }

    private void confirm(){
        if(TextUtils.isEmpty(keyET.getText())){
            keyET.setError("标题不能为空");
            keyET.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(contentET.getText())){
            contentET.setError("内容不能为空");
            contentET.requestFocus();
            return;
        }
        key = keyET.getText().toString().trim();
        content = contentET.getText().toString().trim();
        isConfirm = true;
        dismiss();
    }

    private void cancel(){
        isConfirm=false;
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(keyET.getWindowToken(),1);
        super.onDismiss(dialog);
        onDismiss.onDismiss(isConfirm,key,content);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        key = "";
        content = "";
        isConfirm = false;
        if(bundle != null){
            key = bundle.getString("key");
            content = bundle.getString("content");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_extra_text_dialog_layout,container,false);
        keyET = view.findViewById(R.id.extra_text_key_ET);
        if(!TextUtils.isEmpty(key))
            keyET.setText(key);
        contentET = view.findViewById(R.id.extra_text_content_ET);
        if(!TextUtils.isEmpty(content))
            contentET.setText(content);
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = (int) (dm.widthPixels * 0.8);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.setCanceledOnTouchOutside(false);
            Window window = dialog.getWindow();
            if (window!=null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setLayout(width, height);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
        keyET.requestFocus();
    }

}
