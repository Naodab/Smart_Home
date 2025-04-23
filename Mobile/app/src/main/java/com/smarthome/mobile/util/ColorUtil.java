package com.smarthome.mobile.util;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.widget.TextView;

public class ColorUtil {
    public static void setTitleMainColor(TextView textView) {
        textView.post(() -> {
            float height = textView.getTextSize();

            Shader textShader = new LinearGradient(
                    0, 0, 0, height,
                    new int[]{
                            Color.parseColor("#FEE117"),
                            Color.parseColor("#F2262A")
                    },
                    null,
                    Shader.TileMode.CLAMP
            );

            textView.getPaint().setShader(textShader);
            textView.invalidate();
        });
    }

    public static void changeFromTitleMainColorToWhite(TextView textView, int duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();

            int blendedStart = blendColors(Color.parseColor("#FEE117"), Color.WHITE, progress);
            int blendedEnd = blendColors(Color.parseColor("#F2262A"), Color.WHITE, progress);

            Shader textShader = new LinearGradient(
                    0, 0, 0, textView.getTextSize(),
                    new int[]{blendedStart, blendedEnd},
                    null,
                    Shader.TileMode.CLAMP
            );
            textView.getPaint().setShader(textShader);
            textView.invalidate();
        });
        animator.start();
    }

    private static int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;
        float r = Color.red(from) * inverseRatio + Color.red(to) * ratio;
        float g = Color.green(from) * inverseRatio + Color.green(to) * ratio;
        float b = Color.blue(from) * inverseRatio + Color.blue(to) * ratio;
        return Color.rgb((int) r, (int) g, (int) b);
    }
}
