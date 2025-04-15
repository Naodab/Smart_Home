package com.smarthome.mobile.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.smarthome.mobile.R;

public class SoundRecordUtil {
    private static SoundRecordUtil instance;
    private final SoundPool soundPool;
    private final int soundId;

    private SoundRecordUtil(Context context) {
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        soundId = soundPool.load(context, R.raw.start_audio, 1);
    }

    public static SoundRecordUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SoundRecordUtil(context.getApplicationContext());
        }
        return instance;
    }

    public int playSound(Context context) {
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.start_audio);
        int duration = mediaPlayer.getDuration();
        mediaPlayer.release();
        return duration;
    }

    public void release() {
        soundPool.release();
    }
}
