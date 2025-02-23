// ... existing code ...

// Thay vì xử lý như text, cần xử lý như binary
@POST("/upload-audio")
@Multipart
Call<ResponseBody> uploadAudio(
    @Part MultipartBody.Part audio,  // Sử dụng MultipartBody.Part
    @Part("metadata") RequestBody metadata  // Nếu cần thêm metadata
);

// Khi gọi API:
File audioFile = new File(audioPath);
RequestBody audioBody = RequestBody.create(
    MediaType.parse("audio/*"), // Hoặc chỉ định rõ như "audio/wav"
    audioFile
);
MultipartBody.Part audioPart = MultipartBody.Part.createFormData(
    "audio",
    audioFile.getName(),
    audioBody
);

// ... existing code ...