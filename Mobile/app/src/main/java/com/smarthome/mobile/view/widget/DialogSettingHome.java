package com.smarthome.mobile.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.smarthome.mobile.R;
import com.smarthome.mobile.util.LogoutEvent;

import java.util.Objects;

public class DialogSettingHome {
    private final Dialog dialog;

    public DialogSettingHome(Context context, LogoutEvent event) {
        dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_home_setting, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        View logoutBtn = view.findViewById(R.id.logout_btn);
        View exitBtn = view.findViewById(R.id.exit_btn);

        logoutBtn.setOnClickListener(v -> event.logout());
        exitBtn.setOnClickListener(v -> dismiss());
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
