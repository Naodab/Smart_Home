import torch
import torchaudio
# from speechbrain.inference.speaker import SpeakerRecognition
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
import os
import warnings
from pathlib import Path
# import torchaudio
import torch
_orig_torch_load = torch.load
torch.load = lambda f, **kw: _orig_torch_load(f, weights_only=False, **kw)

# 2) Override hàm mà SpeechBrain thật sự dùng để load state_dict
import speechbrain.utils.checkpoints as _sb_ckpt
_sb_ckpt.torch_patched_state_dict_load = lambda path, device="cpu": torch.load(path, map_location=device)
# _sb_ckpt.torch_patched_state_dict_load = lambda path, device="cpu": _orig_torch_load(path, map_location=device, weights_only=False)
# --- KẾT THÚC PATCH ---

# Bây giờ import các thứ còn lại
import torchaudio
from speechbrain.inference.speaker import SpeakerRecognition

def patched_load(path, device="cpu"):
    print(f"[DEBUG] Trying to load checkpoint: {path}")
    return _orig_torch_load(path, map_location=device, weights_only=False)

_sb_ckpt.torch_patched_state_dict_load = patched_load

from torchaudio import functional as F

import logging

os.environ["SB_DISABLE_QUIRKS"] = "allow_tf32,disable_cudnn_benchmarking,disable_jit_profiling"

# 2) Monkey-patch module quirks để log_applied_quirks và apply_quirks thành no-op
import speechbrain.utils.quirks as _quirks
_quirks.log_applied_quirks = lambda *a, **k: None
_quirks.apply_quirks       = lambda *a, **k: None

# 3) (Tùy chọn) chặn luôn logger con cho chắc
import logging
logging.getLogger("speechbrain.utils.quirks").disabled = True

output_dir = Path("/data/test")

output_dir.mkdir(parents=True, exist_ok=True)

# Tắt các warnings không cần thiết
warnings.filterwarnings("ignore", category=UserWarning)
warnings.filterwarnings("ignore", category=FutureWarning)

# Khởi tạo model
# model2 = SpeakerRecognition.from_hparams(
#     source="/kaggle/working/permanent_checkpoints/CKPT+2025-04-23+05-06-05+00",
#     hparams_file="/kaggle/working/speechbrain/results/finetune/hyperparams.yaml",
#     savedir="/kaggle/working/permanent_checkpoints/CKPT+2025-04-23+05-06-05+00",
#     run_opts={"device": "cpu"}
# )

def verify_checkpoint(checkpoint_path):
    """Verify that checkpoint exists and contains proper weights"""
    # Kiểm tra thư mục checkpoint tồn tại
#     if not os.path.exists(checkpoint_path):
#         print(f"Checkpoint directory not found: {checkpoint_path}")
#         return False
        
#     # Kiểm tra các file checkpoint cần thiết
#     required_files = [
#         "brain.ckpt",
#         "classifier.ckpt",
#         "embedding_model.ckpt",
#         "normalizer.ckpt"
#     ]
    
#     for file in required_files:
#         file_path = os.path.join(checkpoint_path, file)
#         if not os.path.exists(file_path):
#             print(f"Missing required checkpoint file: {file}")
#             return False
    
#     try:
#         # Load và kiểm tra embedding model checkpoint
#         embedding_path = os.path.join(checkpoint_path, "embedding_model.ckpt")
#         embedding_ckpt = torch.load(
#             embedding_path,
#             map_location="cpu",
#             weights_only=True
#         )
        
#         # Load và kiểm tra classifier checkpoint
#         classifier_path = os.path.join(checkpoint_path, "classifier.ckpt")
#         classifier_ckpt = torch.load(
#             classifier_path,
#             map_location="cpu",
#             weights_only=True
#         )
        
#         print("Checkpoint files verified successfully!")
#         return True
        
#     except Exception as e:
#         print(f"Error loading checkpoint files: {str(e)}")
#         return False

# # Sử dụng hàm kiểm tra với đường dẫn chính xác
current_dir = os.path.dirname(__file__)
checkpoint_path = os.path.join(current_dir, "model")
print(f"Checkpoint path: {checkpoint_path}")
# if not verify_checkpoint(checkpoint_path):
#     print(f"Error: Invalid or missing checkpoint at {checkpoint_path}")
#     exit(1)

# Nếu checkpoint hợp lệ, load model
model2 = SpeakerRecognition.from_hparams(
    source=checkpoint_path,
    savedir=checkpoint_path,
    run_opts={"device": "cpu"}
)

# Thư mục lưu embeddings của các speaker đã enroll
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
embeddings_dir = os.path.join(BASE_DIR, "enrolled_embeddings")
os.makedirs(embeddings_dir, exist_ok=True)

def resample_audio(input_path, output_dir, target_sr):
    """Resample audio sang target_sr và lưu vào output_dir. Trả về đường dẫn mới."""
    try:
        # Đọc file gốc
        wav, sr = torchaudio.load(input_path)

        wav = torch.mean(wav, dim=0, keepdim=True)
        
        # Resample nếu sample rate khác target
        if sr != target_sr:
            wav = F.resample(wav, sr, target_sr)
        
        # Tạo tên file mới (giữ nguyên tên gốc)
        output_path = output_dir / Path(input_path).name
        torchaudio.save(output_path, wav, target_sr)
        return str(output_path)
    
    except Exception as e:
        print(f"Lỗi khi resample {input_path}: {str(e)}")
        return None

def enroll_speaker(speaker_id, audio_paths):
    """Lưu trữ embeddings của một speaker từ danh sách file audio."""
    embeddings = []
    
    for audio_path in audio_paths:
        src = Path(audio_path)
        if not src.exists():
            print(f"[WARNING] File gốc không tìm thấy: {src}")
            continue
        # Đọc và xử lý audio
        resampled_path = resample_audio(
            str(src), output_dir, 16000
        )
        signal, fs = torchaudio.load(resampled_path)
        if signal.shape[0] > 1:  # Chuyển về mono nếu cần
            signal = torch.mean(signal, dim=0, keepdim=True)
        
        # Trích xuất embedding bằng hàm encode_batch
        embedding = model2.encode_batch(signal).squeeze().numpy()
        embeddings.append(embedding)
    
    # Lưu embedding trung bình
    avg_embedding = np.mean(embeddings, axis=0)
    np.save(f"{embeddings_dir}/{speaker_id}.npy", avg_embedding)
    print(f"Enrolled speaker {speaker_id} successfully!")

def verify_audio(audio_path, threshold=0.7):
    """Kiểm tra xem audio có thuộc về speaker đã enroll không."""
    # Trích xuất embedding từ audio đầu vào
    src = Path(audio_path)
    if not audio_path or not os.path.exists(audio_path):
            raise ValueError("Invalid audio path")

    resampled_path = resample_audio(
        str(src), output_dir, 16000
    )
    print(resampled_path)
    signal, fs = torchaudio.load(resampled_path)
    if signal.shape[0] > 1:
        signal = torch.mean(signal, dim=0, keepdim=True)
    
    test_embedding = model2.encode_batch(signal).squeeze().numpy()

    # So sánh với tất cả embeddings đã enroll
    max_similarity = -1
    best_speaker = None
    similarities = []

    for speaker_file in os.listdir(embeddings_dir):
        if speaker_file.endswith(".npy"):
            speaker_id = speaker_file[:-4]
            enrolled_embedding = np.load(f"{embeddings_dir}/{speaker_file}")
            
            # Tính cosine similarity
            similarity = cosine_similarity(
                [test_embedding], [enrolled_embedding]
            )[0][0]
            
            similarities.append((speaker_id, similarity))
            
            if similarity > max_similarity:
                max_similarity = similarity
                best_speaker = speaker_id

    # Sắp xếp similarities theo thứ tự giảm dần
    similarities.sort(key=lambda x: x[1], reverse=True)
    
    # In thông tin debug
    print("\nDebug information:")
    for speaker_id, sim in similarities:
        print(f"Speaker {speaker_id}: {sim:.3f}")
    
    # Kiểm tra các điều kiện
    if max_similarity >= threshold:
        if len(similarities) >= 2:
            score_diff = similarities[0][1] - similarities[1][1]
            # print(f"Score difference between top 2: {score_diff:.3f}")
            
            # Nếu độ chênh lệch quá nhỏ và speaker thứ 2 cũng vượt ngưỡng
            if score_diff < 0.03 and similarities[1][1] >= threshold:
                return "Unknown", max_similarity
        return best_speaker, max_similarity
    else:
        return "Unknown", max_similarity

def test_verification(audio_path, threshold=0.85):
    """Test verification với debug info"""
    print(f"Testing audio: {audio_path}")
    if not os.path.exists(audio_path):
            raise FileNotFoundError(f"Input file không tồn tại: {audio_path}")
    print(f"\nTesting audio: {os.path.basename(audio_path)}")
    
    speaker_id, confidence = verify_audio(audio_path, threshold)
    
    print(f"\nResults:")
    print(f"Predicted Speaker: {speaker_id}")
    print(f"Confidence Score: {confidence:.3f}")
    print(f"Threshold: {threshold}")
    print("-" * 50)
    return speaker_id, confidence
    # return {"speaker_id": speaker_id, "confidence": confidence}

# Enroll speakers
print("Enrolling speakers...")
# enroll_speaker("1", ["/kaggle/input/datasets-td/dataset/Binh/dth-Binh-cn-sang-phong.wav", 
#                     "/kaggle/input/datasets-td/dataset/Binh/dth-Binh-t7-sang-troi.wav"])
# enroll_speaker("2", ["/kaggle/input/datasets-td/dataset/Doan/Doan_Laptop_Sang_T2.wav", 
#                     "/kaggle/input/datasets-td/dataset/Doan/Doan_Đth_Lạnh.wav"])

# enroll_speaker("Binh", ["enrolled_data/Binh/dth-Binh-cn-sang-phong.wav",
#                     "enrolled_data/Binh/dth-Binh-cn-toi-troi.wav",
#                     "enrolled_data/Binh/dth-Binh-t2-sang-phong.wav",
#                     "enrolled_data/Binh/dth-Binh-t2-toi-troi.wav",
#                     "enrolled_data/Binh/dth-Binh-t7-sang-troi.wav",
#                     "enrolled_data/Binh/dth-Binh-t7-toi-phong.wav",
#                     "enrolled_data/Binh/lap-Binh-t3-sang-phong.wav"])
# enroll_speaker("Huy", ["enrolled_data/Huy/Huy_Quán cf_Chậm.wav",
#                     "enrolled_data/Huy/Huy_Nhà_Mới ngủ dậy.wav",
#                     "enrolled_data/Huy/Huy_Nhà_Tone thay đổi.wav",
#                     "enrolled_data/Huy/Huy_Ngoài trời.wav",
#                     "enrolled_data/Huy/Huy_Lap.wav",
#                     "enrolled_data/Huy/Huy_Bình thường.wav"])

# enroll_speaker("Hoang", ["enrolled_data/Hoang/Hoang_CN_Dienthoai_Noinhanh.wav",
#                     "enrolled_data/Hoang/Hoang_T2_Dienthoai_Noicham.wav",
#                     "enrolled_data/Hoang/Hoang_T7_IP_Noibt.wav",
#                     "enrolled_data/Hoang/Hoang_CN_IP_Noibt.wav"])

# enroll_speaker("Doan", ["enrolled_data/Doan/Doan_Đth_Trưa_T2.wav",
#                     "enrolled_data/Doan/Doan_Đth_Trước_Ngủ.wav",
#                     "enrolled_data/Doan/Doan_Đth_Tối_CN.wav",
#                     "enrolled_data/Doan/Doan_Đth_Quán_CaFe.wav"])

# /kaggle/working/speechbrain/results/checkpoints/CKPT+2025-04-23+04-38-19+00

if __name__ == "__main__":
    test_files = [
        # "datasets/test/Binh_01.wav",
        # "datasets/test/unknown/unknown_04.wav",
        # "datasets/test/Doan_01.wav",
        # "datasets/test/unknown/unknown_05.wav",
        # "datasets/test/Huy_01.wav",
        os.path.join(os.path.dirname(__file__), "datasets", "test", "Hoang_01.wav")
        # "datasets/test/unknown/unknown_03.wav",
        # audio_path
    ]
    print(test_files)
    for test_file in test_files:
        speaker_id, confidence = test_verification(test_file, threshold=0.8)
        print(f"Predicted Speaker: {speaker_id}, Confidence: {confidence:.3f}")
        if(speaker_id != "Unknown"):
            print(f"Audio {test_file} is likely to be spoken by {speaker_id}.")