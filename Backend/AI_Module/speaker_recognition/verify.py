import torch
import torchaudio
import pickle
import argparse
import os
import subprocess
import tempfile
from speechbrain.inference.speaker import SpeakerRecognition

import os
os.environ['SB_COPYING_STRATEGY'] = 'copy'
os.environ['HF_HUB_ENABLE_SYMLINKS'] = '0'
os.environ['HF_HUB_DISABLE_SYMLINKS_WARNING'] = '1'

# Tạo thư mục pretrained_models nếu chưa tồn tại
os.makedirs("pretrained_models/spkrec-ecapa-voxceleb", exist_ok=True)

def convert_audio_if_needed(audio_path, target_sample_rate=16000):
    """Chuyển đổi file âm thanh sang định dạng WAV 16kHz nếu cần."""
    try:
        # Thử đọc file bằng torchaudio
        signal, fs = torchaudio.load(audio_path)
        if fs != target_sample_rate:
            signal = torchaudio.functional.resample(signal, fs, target_sample_rate)
        return signal, True
    except Exception as e:
        print(f"Không thể đọc file âm thanh bằng torchaudio: {e}")
        print("Thử chuyển đổi bằng ffmpeg...")
        
        # Kiểm tra ffmpeg đã được cài đặt
        try:
            subprocess.run(["ffmpeg", "-version"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True)
        except (subprocess.SubprocessError, FileNotFoundError):
            return None, False
        
        # Chuyển đổi file
        temp_file = tempfile.NamedTemporaryFile(suffix=".wav", delete=False)
        temp_file.close()
        
        try:
            cmd = [
                "ffmpeg", "-y", "-i", audio_path,
                "-ar", str(target_sample_rate),
                "-ac", "1",
                "-c:a", "pcm_s16le",
                temp_file.name
            ]
            subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True)
            
            # Đọc file đã chuyển đổi
            signal, fs = torchaudio.load(temp_file.name)
            return signal, True
        except Exception as e:
            print(f"Không thể chuyển đổi file âm thanh: {e}")
            return None, False
        finally:
            # Xóa file tạm
            if os.path.exists(temp_file.name):
                os.unlink(temp_file.name)

def verify(audio_path, threshold=0.6, database_path="speaker_database.pkl", debug=False):
    try:
        # Load model
        model = SpeakerRecognition.from_hparams(
            source="pretrained_models/spkrec-ecapa-voxceleb",
            savedir="pretrained_models/spkrec-ecapa-voxceleb",
            run_opts={"device": "cpu"}
        )
        
        # Load database
        with open(database_path, "rb") as f:
            speaker_db = pickle.load(f)
        
        # Load và preprocess audio với xử lý đặc biệt
        signal, success = convert_audio_if_needed(audio_path)
        if not success:
            return {"status": "error", "message": f"Không thể đọc hoặc chuyển đổi file âm thanh: {audio_path}"}
        
        # Trích xuất embedding
        input_emb = model.encode_batch(signal)
        
        # Gỡ lỗi: In ra kích thước embedding đầu vào
        if debug:
            print(f"Embedding đầu vào: shape {input_emb.shape}, dtype {input_emb.dtype}")
        
        # Squeeze embedding và đảm bảo kích thước phù hợp
        input_emb = input_emb.squeeze()
        
        if debug:
            print(f"Sau khi squeeze: shape {input_emb.shape}, dtype {input_emb.dtype}")
        
        best_match, best_score = None, -1
        
        # So sánh với từng speaker
        for name, ref_emb in speaker_db.items():
            # Gỡ lỗi: Kiểm tra kích thước của ref_emb
            if debug:
                print(f"Xử lý {name}: ref_emb shape {ref_emb.shape}")
            
            # Ensure both embeddings have the same dimension for comparison
            if input_emb.dim() == 1 and ref_emb.dim() == 2:
                input_emb_comp = input_emb.unsqueeze(0)
                ref_emb_comp = ref_emb
            elif input_emb.dim() == 2 and ref_emb.dim() == 1:
                input_emb_comp = input_emb
                ref_emb_comp = ref_emb.unsqueeze(0)
            elif input_emb.dim() == 1 and ref_emb.dim() == 1:
                input_emb_comp = input_emb.unsqueeze(0)
                ref_emb_comp = ref_emb.unsqueeze(0)
            else:
                input_emb_comp = input_emb
                ref_emb_comp = ref_emb
            
            # Kiểm tra kích thước sau khi chuẩn hóa
            if debug:
                print(f"  Sau khi chuẩn hóa: input_emb_comp {input_emb_comp.shape}, ref_emb_comp {ref_emb_comp.shape}")
            
            # Kiểm tra xem số chiều cuối cùng có khớp nhau không
            if input_emb_comp.shape[-1] != ref_emb_comp.shape[-1]:
                if debug:
                    print(f"  Cảnh báo: Kích thước không khớp: {input_emb_comp.shape[-1]} vs {ref_emb_comp.shape[-1]}")
                # Nếu kích thước không khớp, bỏ qua speaker này
                continue
            
            # Tính cosine similarity
            try:
                score = torch.nn.functional.cosine_similarity(
                    input_emb_comp, 
                    ref_emb_comp
                ).item()
                
                if debug:
                    print(f"  Score: {score}")
                
                if score > best_score:
                    best_score = score
                    best_match = name
            except Exception as e:
                if debug:
                    print(f"  Lỗi khi tính cosine similarity: {e}")
                continue
        
        if best_match is None:
            return {
                "status": "error",
                "message": "Không tìm thấy speaker nào phù hợp hoặc kích thước embedding không khớp"
            }
        
        return {
            "status": "success" if best_score >= threshold else "fail",
            "speaker": best_match,
            "confidence": float(best_score)
        }
    
    except Exception as e:
        return {"status": "error", "message": str(e)}

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Xác minh giọng nói")
    parser.add_argument("--audio_path", type=str, required=True, help="Đường dẫn đến file âm thanh cần xác minh")
    parser.add_argument("--threshold", type=float, default=0.6, help="Ngưỡng điểm số để xác minh thành công")
    parser.add_argument("--database", type=str, default="speaker_database.pkl", help="Đường dẫn đến file database")
    parser.add_argument("--debug", action="store_true", help="Bật chế độ gỡ lỗi")
    args = parser.parse_args()
    
    # Kiểm tra file audio có tồn tại
    if not os.path.exists(args.audio_path):
        print(f"Lỗi: File âm thanh '{args.audio_path}' không tồn tại")
        exit(1)
    
    # Kiểm tra file database có tồn tại
    if not os.path.exists(args.database):
        print(f"Lỗi: File database '{args.database}' không tồn tại")
        exit(1)
    
    # Gọi hàm verify với tham số từ dòng lệnh
    # result = verify(args.audio_path, args.threshold, args.database, args.debug)
    
    # # In kết quả
    # print("Kết quả xác minh:")
    # print(f"Trạng thái: {result['status']}")
    # if result['status'] == 'success':
    #     print(f"Người dùng: {result['speaker']}")
    #     print(f"Độ tin cậy: {result['confidence']:.2f}")
    # elif result['status'] == 'fail':
    #     print(f"Không xác minh được. Người giống nhất: {result['speaker']}")
    #     print(f"Độ tin cậy: {result['confidence']:.2f}")
    # else:
    #     print(f"Lỗi: {result['message']}")