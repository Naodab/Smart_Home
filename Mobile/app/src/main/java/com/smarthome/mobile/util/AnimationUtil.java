package com.smarthome.mobile.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

public class AnimationUtil {
    public static void scaleView(View view, float from, float to) {
        ScaleAnimation animation = new ScaleAnimation(
                from, to,
                from, to,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        animation.setInterpolator(new OvershootInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(300);
        view.startAnimation(animation);
    }
}
