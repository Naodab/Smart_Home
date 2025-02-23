package com.smarthome.mobile.util;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.List;

public class AudioRecorderHelper {
    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private AudioDataListener listener;
    private List<byte[]> recordedData;

    public interface AudioDataListener {
        void onAudioDataCaptured(byte[] audioData);
    }

    public AudioRecorderHelper(AudioDataListener listener) {
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    public void startRecording() {
        recordedData.clear();
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        audioRecord.startRecording();
        isRecording = true;
        new Thread(() -> {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (isRecording) {
                int read = audioRecord.read(buffer, 0, buffer.length);
                if (read > 0) {
                    byte[] copy = new byte[read];
                    System.arraycopy(buffer, 0, copy, 0, read);
                    recordedData.add(copy);
                }
            }
        }).start();
    }


    private byte[] mergeBuffers(List<byte[]> buffers) {
        int totalLength = 0;
        for (byte[] buf : buffers) totalLength += buf.length;
        byte[] result = new byte[totalLength];
        int currentIndex = 0;
        for(byte[] buf : buffers) {
            System.arraycopy(buf, 0, result, currentIndex, buf.length);
            currentIndex += buf.length;
        }
        return result;
    }

    public void stopRecording() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }

        if (listener != null) {
            listener.onAudioDataCaptured(mergeBuffers(recordedData));
        }
    }
}