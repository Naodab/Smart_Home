package com.smarthome.mobile.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.mobile.R;

public class CustomToast {
    private static void show(Context context, String title, String message, int iconResId, int bgResId)  {
        LayoutInflater  inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);
        layout.setBackgroundResource(bgResId);
        TextView text = layout.findViewById(R.id.toast_text);
        ImageView icon = layout.findViewById(R.id.toast_icon);
        text.setText(title);
        icon.setImageResource(iconResId);

        if (message != null) {
            TextView textView = layout.findViewById(R.id.toast_description);
            textView.setVisibility(View.VISIBLE);
            textView.setText(message);
        }

        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public static void showSuccess(Context context, String message) {
        show(context, context.getString(R.string.success),
                message, R.drawable.ic_success, R.drawable.bg_toast_success);
    }

    public static void showError(Context context, String message) {
        show(context, context.getString(R.string.failure),
                message, R.drawable.ic_error, R.drawable.bg_toast_error);
    }

    public static void showInfo(Context context, String message) {
        show(context, context.getString(R.string.info),
                message, R.drawable.ic_info, R.drawable.bg_toast_info);
    }
}
