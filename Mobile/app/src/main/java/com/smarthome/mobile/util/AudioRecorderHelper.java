package com.smarthome.mobile.util;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
        this.recordedData = new ArrayList<>();
    }

    @SuppressLint("MissingPermission")
    public void startRecording() {
        if (recordedData == null) {
            recordedData = new ArrayList<>();
        } else {
            recordedData.clear();
        }
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

    public static void writeWavHeader(OutputStream out, int pcmDataLength) throws IOException {
        int totalDataLength = pcmDataLength + 36;
        int byteRate = SAMPLE_RATE * 2;

        byte[] header = new byte[44];

        header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
        header[4] = (byte) (totalDataLength & 0xff);
        header[5] = (byte) ((totalDataLength >> 8) & 0xff);
        header[6] = (byte) ((totalDataLength >> 16) & 0xff);
        header[7] = (byte) ((totalDataLength >> 24) & 0xff);
        header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';

        header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
        header[16] = 16;
        header[17] = 0; header[18] = 0; header[19] = 0;
        header[20] = 1; header[21] = 0;
        header[22] = 1; header[23] = 0;
        header[24] = (byte) (SAMPLE_RATE & 0xff);
        header[25] = (byte) ((SAMPLE_RATE >> 8) & 0xff);
        header[26] = (byte) ((SAMPLE_RATE >> 16) & 0xff);
        header[27] = (byte) ((SAMPLE_RATE >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = 2;
        header[33] = 0;
        header[34] = 16;
        header[35] = 0;

        header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
        header[40] = (byte) (pcmDataLength & 0xff);
        header[41] = (byte) ((pcmDataLength >> 8) & 0xff);
        header[42] = (byte) ((pcmDataLength >> 16) & 0xff);
        header[43] = (byte) ((pcmDataLength >> 24) & 0xff);

        out.write(header, 0, 44);
    }
}