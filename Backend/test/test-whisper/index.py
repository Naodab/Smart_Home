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

# Thiết lập các thông số ghi âm
duration = 5  # Thời gian ghi âm (giây)
fs = 44100  # Tần số mẫu (Hz)
filename = "audio.wav"
processed_filename = "processed_audio.wav"

print("Bắt đầu ghi âm...")
# Ghi âm từ micro
recording = sd.rec(int(duration * fs), samplerate=fs, channels=2)
sd.wait()  # Chờ cho đến khi ghi âm xong
print("Ghi âm xong!")

# Lưu ghi âm vào tệp
wavio.write(filename, recording, fs, sampwidth=2)
print(f"Đã lưu ghi âm vào tệp {filename}")

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

transcriber = pipeline("automatic-speech-recognition", model="vinai/PhoWhisper-base")
output = transcriber(processed_filename)['text']
print(output)
