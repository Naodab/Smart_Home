# import whisper
# import torch
import sounddevice as sd
import wavio
from pydub import AudioSegment
from pydub.effects import normalize
from transformers import pipeline
import torchvision

# Tắt cảnh báo Beta từ torchvision
torchvision.disable_beta_transforms_warning()

def transfer_audio_to_text():
  # Thiết lập các thông số ghi âm
  # duration = 5  # Thời gian ghi âm (giây)
  # fs = 44100  # Tần số mẫu (Hz)
  # filename = "../media/audio.wav"
  # processed_filename = "../media/processed_audio.wav"

  import os

  media_dir = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), "media")
  os.makedirs(media_dir, exist_ok=True)
    
  filename = os.path.join(media_dir, "audio.wav")
  processed_filename = os.path.join(media_dir, "processed_audio.wav")

  # Tiền xử lý âm thanh
  audio = AudioSegment.from_wav(filename)
  audio = normalize(audio)  # Chuẩn hóa âm lượng
  audio = audio.strip_silence(silence_len=1000, silence_thresh=-40)  # Cắt bỏ khoảng lặng
  audio.export(processed_filename, format="wav")
  print(f"Đã lưu ghi âm đã xử lý vào tệp {processed_filename}")

  # # Kiểm tra xem GPU có sẵn không
  # device = "cuda" if torch.cuda.is_available() else "cpu"

  # # Tải mô hình và chuyển nó sang GPU nếu có
  # model = whisper.load_model("base").to(device)

  # # Chuyển tệp âm thanh đã xử lý sang GPU nếu có và chỉ định ngôn ngữ là tiếng Việt
  # result = model.transcribe(processed_filename, language="vi", fp16=torch.cuda.is_available())
  # print(result["text"])

  transcriber = pipeline("automatic-speech-recognition", model="vinai/PhoWhisper-small")
  output = transcriber(processed_filename)['text']
  print(output)
