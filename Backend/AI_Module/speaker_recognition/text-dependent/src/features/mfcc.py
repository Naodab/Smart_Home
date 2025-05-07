import tensorflow as tf

def audio_to_mfcc(audio, sample_rate, num_mfccs=13, frame_length=2048, frame_step=512):
    stfts = tf.signal.stft(tf.squeeze(audio, axis=-1),
                           frame_length=frame_length,
                           frame_step=frame_step,
                           fft_length=frame_length,
                           window_fn=tf.signal.hamming_window)
    spectrograms = tf.abs(stfts)

    num_spectrogram_bins = stfts.shape[-1]
    lower_edge_hertz, upper_edge_hertz, num_mel_bins = 80.0, 7600, 40
    linear_to_mel_weight_matrix = tf.signal.linear_to_mel_weight_matrix(
        num_mel_bins, num_spectrogram_bins, sample_rate, lower_edge_hertz,
        upper_edge_hertz)

    mel_spectrograms = tf.tensordot(
        spectrograms, linear_to_mel_weight_matrix, 1)
    mel_spectrograms.set_shape(spectrograms.shape[:-1].concatenate(
        linear_to_mel_weight_matrix.shape[-1:]))

    log_mel_spectrograms = tf.math.log(mel_spectrograms + 1e-6)

    mfccs = tf.signal.mfccs_from_log_mel_spectrograms(
        log_mel_spectrograms)[..., :num_mfccs]

    return mfccs