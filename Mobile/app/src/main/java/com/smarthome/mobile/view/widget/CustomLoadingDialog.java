package com.smarthome.mobile.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.smarthome.mobile.R;

import java.util.Objects;

public class CustomLoadingDialog {
    private final Dialog dialog;
    private final TextView[] dots;

    public CustomLoadingDialog(Context context) {
        dialog = new Dialog(context);
        View  view = LayoutInflater.from(context).inflate(R.layout.custom_loading, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvDot1 = view.findViewById(R.id.tvDot1);
        TextView tvDot2 = view.findViewById(R.id.tvDot2);
        TextView tvDot3 = view.findViewById(R.id.tvDot3);
        TextView tvDot4 = view.findViewById(R.id.tvDot4);
        dots = new TextView[]{tvDot1, tvDot2, tvDot3, tvDot4};
    }

    public void startLoadingEffect() {
        Handler handler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            int delay = 200;

            for (int i = 0; i < dots.length; i++) {
                final int finalI = i;
                handler.postDelayed(() -> dots[finalI].startAnimation(AnimationUtils
                        .loadAnimation(dialog.getContext(), R.anim.dot_jump)),
                        (long) delay * i);
            }
        }).start();
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
            startLoadingEffect();
        }
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
