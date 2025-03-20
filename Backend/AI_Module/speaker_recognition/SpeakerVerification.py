import numpy as np

class SpeakerVerification:
    def __init__(self, embedding_model, threshold=0.7):
        self.embedding_model = embedding_model
        self.threshold = threshold
        self.speaker_centroids = {}
        
    def compute_centroid(self, X_samples):
        """Tính centroid từ nhiều mẫu audio của cùng 1 speaker"""
        embeddings = self.embedding_model.predict(X_samples)
        # Chuẩn hóa từng embedding trước khi tính mean
        normalized_embeddings = self._normalize_embeddings(embeddings)
        centroid = np.mean(normalized_embeddings, axis=0)
        # Chuẩn hóa centroid
        return self._normalize_embeddings(centroid.reshape(1, -1))[0]
    
    def _normalize_embeddings(self, embeddings):
        """Chuẩn hóa embedding vectors"""
        return embeddings / np.linalg.norm(embeddings, axis=1, keepdims=True)
    
    def enroll_speaker(self, speaker_id, X_enrollment):
        """Đăng ký speaker mới"""
        centroid = self.compute_centroid(X_enrollment)
        self.speaker_centroids[speaker_id] = centroid
        print(f"Enrolled speaker {speaker_id}")
        
    def verify_speaker(self, X_test):
        """Nhận dạng speaker từ audio mới"""
        # Trích xuất và chuẩn hóa embedding
        emb_test = self.embedding_model.predict(X_test[None, ...])
        emb_test = self._normalize_embeddings(emb_test)[0]
        
        best_speaker = None
        best_score = -1
        
        # So sánh với tất cả centroids
        for speaker_id, centroid in self.speaker_centroids.items():
            score = np.dot(emb_test, centroid)
            if score > best_score:
                best_score = score
                best_speaker = speaker_id
                
        if best_score < self.threshold:
            return "unknown", best_score
        return best_speaker, best_score