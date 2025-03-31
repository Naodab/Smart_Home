from SpeakerRecognition import SpeakerRecognition
from SpeakerVerification import SpeakerVerification
import numpy as np
import tensorflow as tf
import librosa

# root_dir = "models"
# sr = SpeakerRecognition(root_dir)
embedding_model = tf.keras.models.load_model('models/resnet_embedding_model.keras')
# Khởi tạo
verifier = SpeakerVerification(embedding_model)

speaker_files = {
    "hoang": [
        "data/Hoang/Hoang_01.wav",
        "data/Hoang/Hoang_02.wav",
        "data/Hoang/Hoang_03.wav",
        "data/Hoang/Hoang_04.wav",
        "data/Hoang/Hoang_05.wav",
        "data/Hoang/Hoang_06.wav",
        "data/Hoang/Hoang_07.wav",
        "data/Hoang/Hoang_08.wav",
        "data/Hoang/Hoang_09.wav",
        "data/Hoang/Hoang_10.wav",
    ],
    "huy": [
        "data/Huy/Huy_01.wav",
        "data/Huy/Huy_02.wav",
        "data/Huy/Huy_03.wav",
        "data/Huy/Huy_04.wav",
        "data/Huy/Huy_05.wav",
        "data/Huy/Huy_06.wav",
        "data/Huy/Huy_07.wav",
        "data/Huy/Huy_08.wav",
        "data/Huy/Huy_09.wav",
        "data/Huy/Huy_10.wav",
    ],
    "binh": [
        "data/Binh/Binh_01.wav",
        "data/Binh/Binh_02.wav",
        "data/Binh/Binh_03.wav",
        "data/Binh/Binh_04.wav",
        "data/Binh/Binh_05.wav",
        "data/Binh/Binh_06.wav",
        "data/Binh/Binh_07.wav",
        "data/Binh/Binh_08.wav",
        "data/Binh/Binh_09.wav",
        "data/Binh/Binh_10.wav"
    ],
    "doan": [
        "data/Doan/Doan_01.wav",
        "data/Doan/Doan_02.wav",
        "data/Doan/Doan_03.wav",
        "data/Doan/Doan_04.wav",
        "data/Doan/Doan_05.wav",
        "data/Doan/Doan_06.wav",
        "data/Doan/Doan_07.wav",
        "data/Doan/Doan_08.wav",
        "data/Doan/Doan_09.wav",
        "data/Doan/Doan_10.wav",
    ]
}

def preprocess_audio(audio_path, sr=16000, duration=2):
    """Tiền xử lý file âm thanh"""
    # Đọc và resample về 16kHz
    y, _ = librosa.load(audio_path, sr=sr)

    # Tăng cường tín hiệu
    y_harmonic, y_percussive = librosa.effects.hpss(y)

    # Chuẩn hóa âm lượng
    y = librosa.util.normalize(y_harmonic)

    # Lọc nhiễu tần số cao
    y = librosa.effects.preemphasis(y)

    # Loại bỏ khoảng lặng
    y, _ = librosa.effects.trim(y)

    # Cắt hoặc pad để có độ dài cố định
    # target_length = self.sr * self.duration
    # if len(y) > target_length:
    #     y = y[:target_length]
    # else:
    #     y = np.pad(y, (0, max(0, target_length - len(y))))

    # Cắt/pad để có độ dài cố định
    target_length = sr * duration
    if len(y) > target_length:
        # Random crop thay vì cắt đầu
        start = np.random.randint(0, len(y) - target_length)
        y = y[start:start + target_length]
    else:
        # Pad với mirror padding thay vì zero padding
        y = np.pad(y, (0, max(0, target_length - len(y))), mode='reflect')

    return y

# def extract_features(y, sr=16000, n_mels=40):
#     """Trích xuất đặc trưng với độ dài cố định"""
#     # Tính mel spectrogram
#     mel_spec = librosa.feature.melspectrogram(
#         y=y,
#         sr=sr,
#         n_mels=n_mels,
#         hop_length=512,
#         n_fft=2048
#     )
#     mel_spec_db = librosa.power_to_db(mel_spec, ref=np.max)

#     # Chuẩn hóa
#     mel_spec_db = (mel_spec_db - np.mean(mel_spec_db)) / np.std(mel_spec_db)

#     # Đặt độ dài cố định (ví dụ: 128 time steps)
#     target_length = 128

#     if mel_spec_db.shape[1] > target_length:
#         # Cắt bớt nếu dài hơn
#         mel_spec_db = mel_spec_db[:, :target_length]
#     else:
#         # Pad nếu ngắn hơn
#         padding_width = ((0, 0), (0, target_length - mel_spec_db.shape[1]))
#         mel_spec_db = np.pad(mel_spec_db, padding_width, mode='constant')

#     # Thêm chiều kênh
#     mel_spec_db = np.expand_dims(mel_spec_db, axis=-1)

#     return mel_spec_db

def extract_features(y, sr=16000, n_mels=40):
    """Trích xuất đặc trưng MFCC với độ dài cố định"""
    # Tính MFCC
    mfccs = librosa.feature.mfcc(
        y=y,
        sr=sr,
        n_mfcc=n_mels,  # Sử dụng n_mels (40) làm số lượng MFCC
        hop_length=512,
        n_fft=2048
    )

    # Chuẩn hóa MFCC
    mfccs = (mfccs - np.mean(mfccs)) / np.std(mfccs)

    # Đặt độ dài cố định (ví dụ: 128 time steps)
    target_length = 128

    if mfccs.shape[1] > target_length:
        # Cắt bớt nếu dài hơn
        mfccs = mfccs[:, :target_length]
    else:
        # Pad nếu ngắn hơn
        padding_width = ((0, 0), (0, target_length - mfccs.shape[1]))
        mfccs = np.pad(mfccs, padding_width, mode='constant')

    # Thêm chiều kênh
    mfccs = np.expand_dims(mfccs, axis=-1)

    return mfccs

# Hàm helper để lấy features từ audio files
def get_enrollment_samples(speaker_files):
    features = []
    for audio_file in speaker_files:
        # Sử dụng các phương thức từ class SpeakerRecognition
        audio = preprocess_audio(audio_file)
        mel_spec = extract_features(audio)
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

verifier.threshold = 0.55

np.savez('resnet_mfcc_speaker_database.npz',
         centroids=verifier.speaker_centroids,
         threshold=verifier.threshold)

def identify_speaker(audio_path):
    """Nhận dạng speaker từ file audio mới"""
    # Tiền xử lý audio
    audio = preprocess_audio(audio_path)
    mel_spec = extract_features(audio)
    
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
# result = identify_speaker("data/Hoang/normal_1.wav")
# print(result)
# result = identify_speaker("data/Doan/test.wav") 
# result = identify_speaker("data/Huy/8.wav") 
# print(result)

# result = identify_speaker("C:/Users/TechCare\OneDrive - The University of Technology/PBL5/Smart_Home/Backend/media/audio.wav") 
# print(result)
# result = identify_speaker("data/Binh/Recording (10).wav") 
# result = identify_speaker("C:/Users/TechCare/OneDrive - The University of Technology/PBL5/Smart_Home/Backend/media/processed_audio.wav") 