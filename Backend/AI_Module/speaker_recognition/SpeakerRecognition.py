import os
import numpy as np
import librosa
import soundfile as sf
from sklearn.model_selection import train_test_split
import tensorflow as tf
from tensorflow.keras import layers, models
import random
import pickle

class SpeakerRecognition:
    def __init__(self, root_dir, sr=16000, duration=2, n_mels=40, n_frames=128):
        self.root_dir = root_dir
        self.sr = sr
        self.duration = duration
        self.n_mels = n_mels
        self.n_frames = n_frames
        self.speakers = self._get_speakers()
        self.num_speakers = len(self.speakers)

        # Tạo thư mục để lưu dữ liệu đã xử lý
        # self.processed_data_dir = '/kaggle/input/preprocess'
        # os.makedirs(self.processed_data_dir, exist_ok=True)

    def _get_speakers(self):
        """
        Lấy danh sách các speaker từ thư mục, giới hạn số lượng speaker
        max_speakers: số lượng speaker tối đa muốn lấy
        """
        return [d for d in os.listdir(self.root_dir) if os.path.isdir(os.path.join(self.root_dir, d))]
        # Lấy tất cả các thư mục speaker
        # all_speakers = [d for d in os.listdir(self.root_dir)
        #               if os.path.isdir(os.path.join(self.root_dir, d))]

        # # Sắp xếp để đảm bảo lấy cùng một tập speaker mỗi lần chạy
        # all_speakers.sort()

        # # Lấy max_speakers người đầu tiên
        # selected_speakers = all_speakers[:max_speakers]

        # print(f"Tổng số speaker: {len(all_speakers)}")
        # print(f"Số speaker được chọn: {len(selected_speakers)}")

        # return selected_speakers
    # def save_processed_data(self, data_dict, save_dir='/kaggle/working'):
    #     """
    #     Lưu dữ liệu đã xử lý
    #     data_dict: dictionary chứa các cặp X, y cần lưu
    #     """
    #     # Tạo thư mục nếu chưa tồn tại
    #     os.makedirs(save_dir, exist_ok=True)

    #     # Lưu file
    #     save_path = os.path.join(save_dir, 'processed_data.pkl')
    #     print(f"Đang lưu dữ liệu vào {save_path}")

    #     try:
    #         with open(save_path, 'wb') as f:
    #             pickle.dump(data_dict, f)
    #         print("Đã lưu dữ liệu thành công!")

    #         # In ra kích thước của dữ liệu đã lưu
    #         size_bytes = os.path.getsize(save_path)
    #         size_mb = size_bytes / (1024 * 1024)
    #         print(f"Dung lượng file: {size_mb:.2f} MB")
    #     except Exception as e:
    #         print(f"Lỗi khi lưu dữ liệu: {str(e)}")

    # def load_processed_data(self):
    #     """Load dữ liệu đã xử lý"""
    #     load_path = os.path.join(self.processed_data_dir, 'processed_data.pkl')
    #     if os.path.exists(load_path):
    #         print(f"Đang load dữ liệu từ {load_path}")
    #         with open(load_path, 'rb') as f:
    #             data = pickle.load(f)
    #         return (data['X_train'], data['y_train']), \
    #                (data['X_val'], data['y_val']), \
    #                (data['X_test'], data['y_test'])
    #     else:
    #         print("Không tìm thấy dữ liệu đã xử lý!")
    #         return None

    def preprocess_audio(self, audio_path):
        """Tiền xử lý file âm thanh"""
        # Đọc và resample về 16kHz
        y, _ = librosa.load(audio_path, sr=self.sr)

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
        target_length = self.sr * self.duration
        if len(y) > target_length:
            # Random crop thay vì cắt đầu
            start = np.random.randint(0, len(y) - target_length)
            y = y[start:start + target_length]
        else:
            # Pad với mirror padding thay vì zero padding
            y = np.pad(y, (0, max(0, target_length - len(y))), mode='reflect')

        return y

    def extract_features(self, y):
      """Trích xuất đặc trưng với độ dài cố định"""
      # Tính mel spectrogram
      mel_spec = librosa.feature.melspectrogram(
          y=y,
          sr=self.sr,
          n_mels=self.n_mels,
          hop_length=512,
          n_fft=2048
      )
      mel_spec_db = librosa.power_to_db(mel_spec, ref=np.max)

      # Chuẩn hóa
      mel_spec_db = (mel_spec_db - np.mean(mel_spec_db)) / np.std(mel_spec_db)

      # Đặt độ dài cố định (ví dụ: 128 time steps)
      target_length = 128

      if mel_spec_db.shape[1] > target_length:
          # Cắt bớt nếu dài hơn
          mel_spec_db = mel_spec_db[:, :target_length]
      else:
          # Pad nếu ngắn hơn
          padding_width = ((0, 0), (0, target_length - mel_spec_db.shape[1]))
          mel_spec_db = np.pad(mel_spec_db, padding_width, mode='constant')

      # Thêm chiều kênh
      mel_spec_db = np.expand_dims(mel_spec_db, axis=-1)

      return mel_spec_db

    def augment_audio(self, y):
        """Tăng cường dữ liệu"""
        augmented = []

        # Time shifting với nhiều mức độ
        for shift_factor in [-0.2, -0.1, 0.1, 0.2]:
            shift = int(len(y) * shift_factor)
            augmented.append(np.roll(y, shift))

        # Thêm nhiễu với nhiều mức độ khác nhau
        for noise_factor in [0.001, 0.003, 0.005]:
            noise = np.random.normal(0, 1, len(y))
            augmented.append(y + noise_factor * noise)

        # Speed change với nhiều tốc độ
        for speed_factor in [0.9, 0.95, 1.05, 1.1]:
            augmented.append(librosa.effects.time_stretch(y, rate=speed_factor))

        # Pitch shift với nhiều mức độ
        for n_steps in [-2, -1, 1, 2]:
            augmented.append(librosa.effects.pitch_shift(y, sr=self.sr, n_steps=n_steps))

        return augmented

    def prepare_dataset(self):
        """Chuẩn bị dataset cho huấn luyện"""
        X = []
        y = []

        for idx, speaker in enumerate(self.speakers):
            print(idx)
            speaker_dir = os.path.join(self.root_dir, speaker)
            audio_files = [f for f in os.listdir(speaker_dir) if f.endswith('.wav')]

            for audio_file in audio_files:
                audio_path = os.path.join(speaker_dir, audio_file)

                # Tiền xử lý âm thanh
                audio = self.preprocess_audio(audio_path)

                # Trích xuất đặc trưng
                features = self.extract_features(audio)
                X.append(features)
                y.append(idx)

                # Tăng cường dữ liệu
                augmented_audios = self.augment_audio(audio)
                for aug_audio in augmented_audios:
                    aug_features = self.extract_features(aug_audio)
                    X.append(aug_features)
                    y.append(idx)

        X = np.array(X)
        y = np.array(y)

        # Chia dataset
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
        X_train, X_val, y_train, y_val = train_test_split(X_train, y_train, test_size=0.2, random_state=42)

        # self.save_processed_data(X_train, y_train, X_val, y_val, X_test, y_test)
        processed_data = {
            'X_train': X_train,
            'y_train': y_train,
            'X_val': X_val,
            'y_val': y_val,
            'X_test': X_test,
            'y_test': y_test
        }

        self.save_processed_data(processed_data)

        return (X_train, y_train), (X_val, y_val), (X_test, y_test)

    def build_model(self, embedding_dim=128):
        # Xác định input shape
        input_shape = (self.n_mels, 128, 1)

        # Tạo input layer
        inputs = tf.keras.Input(shape=input_shape)

        # CNN layers
        x = tf.keras.layers.Conv2D(32, (3, 3), activation='relu',
                              kernel_regularizer=tf.keras.regularizers.l2(0.001))(inputs)
        x = tf.keras.layers.BatchNormalization()(x)
        x = tf.keras.layers.MaxPooling2D((2, 2))(x)
        x = tf.keras.layers.Dropout(0.3)(x)

        x = tf.keras.layers.Conv2D(64, (3, 3),
                                  activation='relu',
                                  kernel_regularizer=tf.keras.regularizers.l2(0.001))(x)
        x = tf.keras.layers.BatchNormalization()(x)
        x = tf.keras.layers.MaxPooling2D((2, 2))(x)
        x = tf.keras.layers.Dropout(0.3)(x)

        x = tf.keras.layers.Conv2D(128, (3, 3),
                                  activation='relu',
                                  kernel_regularizer=tf.keras.regularizers.l2(0.001))(x)
        x = tf.keras.layers.BatchNormalization()(x)
        x = tf.keras.layers.MaxPooling2D((2, 2))(x)
        x = tf.keras.layers.Dropout(0.3)(x)

        x = tf.keras.layers.GlobalAveragePooling2D()(x)

        # Embedding layer
        embedding = tf.keras.layers.Dense(embedding_dim,
                                        name='embedding_layer',
                                        kernel_regularizer=tf.keras.regularizers.l2(0.001))(x)

        # Output layer
        outputs = tf.keras.layers.Dense(self.num_speakers, activation='softmax')(embedding)

        # Tạo model
        model = tf.keras.Model(inputs=inputs, outputs=outputs)

        model.compile(
            optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
            loss='sparse_categorical_crossentropy',
            metrics=['accuracy']
        )

        return model

    def create_embedding_model(self, trained_model):
        """Tạo model để trích xuất embedding vectors"""
        embedding_model = tf.keras.Model(
            inputs=trained_model.input,
            outputs=trained_model.get_layer('embedding_layer').output
        )
        return embedding_model