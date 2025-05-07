import numpy as np
import tensorflow as tf

def add_noise(audio, noise_factor=0.005):
    noise = np.random.randn(len(audio))
    augmented_audio = audio + noise_factor * noise
    return np.clip(augmented_audio, -1.0, 1.0)

def change_speed(audio, speed_factor=1.0):
    if speed_factor <= 0:
        raise ValueError("Speed factor must be greater than 0")
    return tf.signal.frame(audio, frame_length=int(16000 * speed_factor), frame_step=16000, pad_end=True)

def pitch_shift(audio, sampling_rate, n_steps):
    return tf.signal.pitch_shift(audio, sampling_rate, n_steps)

def time_stretch(audio, rate):
    return tf.signal.time_stretch(audio, rate)