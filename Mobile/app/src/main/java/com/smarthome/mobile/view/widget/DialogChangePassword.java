package com.smarthome.mobile.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.smarthome.mobile.R;
import com.smarthome.mobile.viewmodel.AuthViewModel;

import java.util.Objects;

public class DialogChangePassword {
    private final Dialog dialog;
    private final Context context;

    public DialogChangePassword(Context context, AuthViewModel authViewModel) {
        this.context = context;
        this.dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        EditText oldPassText = view.findViewById(R.id.edtOldPassword);
        EditText newPassText = view.findViewById(R.id.edtNewPassword);
        EditText confPassText = view.findViewById(R.id.edtConfirmPassword);

        view.findViewById(R.id.exit_btn).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            String oldPass = oldPassText.getText().toString();
            String newPass = newPassText.getText().toString();
            String confNewPass = confPassText.getText().toString();

            if (oldPass.isEmpty() || newPass.isEmpty() || confNewPass.isEmpty()) {
                CustomToast.showInfo(context, "Vui lòng điền đầy đủ");
            } else if (!newPass.equals(confNewPass)) {
                CustomToast.showInfo(context, "Mật khẩu nhập lại không chính xác");
            } else {
                authViewModel.changePassword(oldPass, newPass);
            }
        });
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
            Window window = dialog.getWindow();
            if (window != null) {
                int width = (int) (context.getResources()
                        .getDisplayMetrics().widthPixels * 0.9);
                window.setLayout(width, ViewGroup.LayoutParams
                        .WRAP_CONTENT);
            }
        }
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
