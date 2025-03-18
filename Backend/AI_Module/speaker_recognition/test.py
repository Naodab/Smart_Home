import numpy as np
import tensorflow as tf
import librosa
from .SpeakerVerification import SpeakerVerification

embedding_model = tf.keras.models.load_model('AI_Module/speaker_recognition/models/mfcc_embedding_model.keras')
verifier = SpeakerVerification(embedding_model)

data = np.load('AI_Module/speaker_recognition/mfcc_speaker_database.npz', allow_pickle=True)
verifier.speaker_centroids = data['centroids'].item()
verifier.threshold = float(data['threshold'])

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

def identify_speaker():

  import os

  media_dir = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), "media")
  os.makedirs(media_dir, exist_ok=True)
    
  audio_path = os.path.join(media_dir, "audio.wav")

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

# result = identify_speaker("data/Hoang/normal_1.wav")