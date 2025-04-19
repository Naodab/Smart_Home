package com.smarthome.mobile.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.smarthome.mobile.R;
import com.smarthome.mobile.viewmodel.AuthViewModel;

import java.util.Objects;

public class DialogSetting {
    private final Dialog dialog;

    public DialogSetting(Context context, AuthViewModel authViewModel, DialogChangePassword dialogChangePassword) {
        dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_setting, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        LinearLayout changePasswordBtn = view.findViewById(R.id.change_password_btn);
        LinearLayout logoutBtn = view.findViewById(R.id.logout_btn);
        LinearLayout cancelBtn = view.findViewById(R.id.exit_btn);

        changePasswordBtn.setOnClickListener(v -> {
            dismiss();
            dialogChangePassword.show();
        });

        logoutBtn.setOnClickListener(v -> authViewModel.logout());
        cancelBtn.setOnClickListener(v -> dismiss());
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
