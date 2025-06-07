import numpy as np
import librosa

def normalize_volume(audio, target_db=-20.0):
    """Normalize the volume of an audio signal to a target decibel level."""
    current_db = librosa.amplitude_to_db(np.abs(audio))
    adjustment_db = target_db - current_db.mean()
    normalized_audio = librosa.db_to_amplitude(current_db + adjustment_db)
    return normalized_audio

def remove_silence(audio, threshold=0.01, frame_length=2048, hop_length=512):
    """Remove silence from an audio signal based on a threshold."""
    non_silent_indices = np.where(np.abs(audio) > threshold)[0]
    if len(non_silent_indices) == 0:
        return audio  # Return original audio if all is silent
    start_index = non_silent_indices[0]
    end_index = non_silent_indices[-1] + 1
    return audio[start_index:end_index]