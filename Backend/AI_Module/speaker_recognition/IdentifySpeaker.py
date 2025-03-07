from SpeakerRecognition import SpeakerRecognition
from SpeakerVerification import SpeakerVerification
import numpy as np
import tensorflow as tf

root_dir = "models"
sr = SpeakerRecognition(root_dir)
embedding_model = tf.keras.models.load_model('models/embedding_model.keras')

# Khởi tạo
verifier = SpeakerVerification(embedding_model)

speaker_files = {
    # "id_050": [
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_050/id_050_0010.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_050/id_050_0020.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_050/id_050_0030.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_050/id_050_0040.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_050/id_050_0050.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_050/id_050_0060.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_050/id_050_0070.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_050/id_050_0080.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_050/id_050_0090.wav"
    # ],
    # "id_060": [
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_060/id_060_0010.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_060/id_060_0020.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_060/id_060_0030.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_060/id_060_0040.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_060/id_060_0050.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_060/id_060_0060.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_060/id_060_0070.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_060/id_060_0080.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_060/id_060_0090.wav"
    # ],
    # "id_070": [
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_070/id_070_0010.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_070/id_070_0020.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_070/id_070_0030.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_070/id_070_0040.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_070/id_070_0050.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_070/id_070_0060.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_070/id_070_0070.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_070/id_070_0080.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_070/id_070_0081.wav"
    # ],
    # "id_080": [
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_080/id_080_0010.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_080/id_080_0020.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_080/id_080_0030.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_080/id_080_0040.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_080/id_080_0050.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_080/id_080_0060.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_080/id_080_0070.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_080/id_080_0080.wav",
    #     "/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_080/id_080_0090.wav"
    # ],
    "hoang": [
        "data/Hoang/normal_1.wav",
        "data/Hoang/normal_2.wav",
        "data/Hoang/normal_3.wav",
        "data/Hoang/normal_4.wav",
        "data/Hoang/normal_5.wav",
        # "data/Hoang/fast_1.wav",
        # "data/Hoang/fast_2.wav",
        # "data/Hoang/fast_3.wav",
        # "data/Hoang/slow_1.wav",
        # "data/Hoang/slow_2.wav",
        # "data/Hoang/slow_3.wav",
        # "data/Hoang/fun.wav",
        # "data/Hoang/angry.wav",
        # "data/Hoang/sad.wav",
        # "data/Hoang/noise_1.wav",
        # "data/Hoang/noise_2.wav",
        # "data/Hoang/noise_3.wav"
    ],
    "huy": [
        "data/Huy/1.wav",
        "data/Huy/2.wav",
        "data/Huy/3.wav",
        "data/Huy/4.wav",
        "data/Huy/5.wav",
        "data/Huy/6.wav",
    ],
    "binh": [
        "data/Binh/Recording (2).wav",
        "data/Binh/Recording (3).wav",
        "data/Binh/Recording (4).wav",
        "data/Binh/Recording (5).wav",
        "data/Binh/Recording (6).wav",
        "data/Binh/Recording (7).wav"
    ]
}

# Hàm helper để lấy features từ audio files
def get_enrollment_samples(speaker_files):
    features = []
    for audio_file in speaker_files:
        # Sử dụng các phương thức từ class SpeakerRecognition
        audio = sr.preprocess_audio(audio_file)
        mel_spec = sr.extract_features(audio)
        features.append(mel_spec)
    return np.array(features)

# Đăng ký speakers
for speaker_id, audio_files in speaker_files.items():
    # Lấy features từ các file audio
    X_enrollment = get_enrollment_samples(audio_files)
    # Đăng ký speaker
    verifier.enroll_speaker(speaker_id, X_enrollment)

X_val_features = []
y_val_true = []

# Tìm threshold tối ưu
# optimal_threshold, best_accuracy = find_optimal_threshold(verifier, X_val_features, y_val_true)
# print(f"Optimal threshold: {optimal_threshold:.3f} (accuracy: {best_accuracy:.3f})")
# verifier.threshold = optimal_threshold

verifier.threshold = 0.65

def identify_speaker(audio_path):
    """Nhận dạng speaker từ file audio mới"""
    # Tiền xử lý audio
    audio = sr.preprocess_audio(audio_path)
    mel_spec = sr.extract_features(audio)
    
    # Nhận dạng speaker
    speaker_id, confidence = verifier.verify_speaker(mel_spec)
    
    result = {
        'predicted_speaker': speaker_id,
        'confidence': confidence,
        'is_known': speaker_id != "unknown"
    }
    
    if result['is_known']:
        print(f"Detected speaker: {speaker_id} with confidence: {confidence:.3f}")
    else:
        print(f"Unknown speaker (confidence: {confidence:.3f})")
    
    return result
# Ví dụ sử dụng
# result = identify_speaker("/kaggle/input/voice-dataset/content/drive/MyDrive/dataset/train/id_001/id_001_0080.wav")
result = identify_speaker("data/Hoang/utt_005.wav") 
# result = identify_speaker("data/Doan/test.wav") 
# result = identify_speaker("data/Huy/8.wav") 
# result = identify_speaker("data/Binh/Recording (10).wav") 
# result = identify_speaker("C:/Users/TechCare/OneDrive - The University of Technology/PBL5/Smart_Home/Backend/media/processed_audio.wav") 