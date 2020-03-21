package yzw.ahaqth.accountbag.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Objects;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.DialogClickListener;
import yzw.ahaqth.accountbag.interfaces.DialogDismissListener;
import yzw.ahaqth.accountbag.interfaces.NoDoubleClicker;

public final class DialogFactory extends DialogFragment {
    private String message = "";
    private boolean isConfirmMode;
    private View cancelView, confirmView;
    private TextView messageTextView;
    private DialogDismissListener dismissListener;

    public DialogFactory setDismissListener(DialogDismissListener dismissListener) {
        this.dismissListener = dismissListener;
        return this;
    }

    public static DialogFactory getConfirmDialog(String message) {
        DialogFactory dialogFactory = new DialogFactory();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isConfirmMode", true);
        bundle.putString("message", message);
        dialogFactory.setArguments(bundle);
        return dialogFactory;
    }

    public static DialogFactory getTipsDialog(String message) {
        DialogFactory dialogFactory = new DialogFactory();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isConfirmMode", false);
        bundle.putString("message", message);
        dialogFactory.setArguments(bundle);
        return dialogFactory;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.isConfirmMode = bundle.getBoolean("isConfirmMode");
            this.message = bundle.getString("message");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_or_tips_view, container, true);
        cancelView = view.findViewById(R.id.cancelBT);
        cancelView.setOnClickListener(new NoDoubleClicker() {
            @Override
            public void noDoubleClick(View v) {
                if (dismissListener != null)
                    dismissListener.onDismiss(false);
                dismiss();
            }
        });
        confirmView = view.findViewById(R.id.confirmBT);
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dismissListener != null)
                    dismissListener.onDismiss(true);
                dismiss();
            }
        });
        confirmView.setVisibility(isConfirmMode ? View.VISIBLE : View.GONE);
        messageTextView = view.findViewById(R.id.messageTV);
        messageTextView.setText(message);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            if (window != null) {
                DisplayMetrics dm = new DisplayMetrics();
                Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(dm);
                int width = (int) (dm.widthPixels * 0.75);
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setLayout(width, height);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }
}
