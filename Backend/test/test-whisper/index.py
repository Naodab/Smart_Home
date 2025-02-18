# import whisper

# model = whisper.load_model("turbo")

# # load audio and pad/trim it to fit 30 seconds
# audio = whisper.load_audio("audio.wav")
# audio = whisper.pad_or_trim(audio)

# # make log-Mel spectrogram and move to the same device as the model
# mel = whisper.log_mel_spectrogram(audio, n_mels=model.dims.n_mels).to(model.device)

# # detect the spoken language
# _, probs = model.detect_language(mel)
# print(f"Detected language: {max(probs, key=probs.get)}")

# # decode the audio
# options = whisper.DecodingOptions()
# result = whisper.decode(model, mel, options)

# # print the recognized text
# print(result.text)

# import whisper

# model = whisper.load_model("base")
# result = model.transcribe("audio.wav")
# print(result["text"])

import whisper
import torch
import sounddevice as sd
import wavio

# Thiết lập các thông số ghi âm
duration = 3  # Thời gian ghi âm (giây)
fs = 44100  # Tần số mẫu (Hz)
filename = "audio.wav"

print("Bắt đầu ghi âm...")
# Ghi âm từ micro
recording = sd.rec(int(duration * fs), samplerate=fs, channels=2)
sd.wait()  # Chờ cho đến khi ghi âm xong
print("Ghi âm xong!")

# Lưu ghi âm vào tệp
wavio.write(filename, recording, fs, sampwidth=2)
print(f"Đã lưu ghi âm vào tệp {filename}")



# Kiểm tra xem GPU có sẵn không
device = "cuda" if torch.cuda.is_available() else "cpu"

# Tải mô hình và chuyển nó sang GPU nếu có
model = whisper.load_model("base").to(device)

# Chuyển tệp âm thanh sang GPU nếu có
result = model.transcribe(filename, language="vi", fp16=torch.cuda.is_available())
print(result["text"])