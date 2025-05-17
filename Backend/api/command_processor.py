import re
from typing import Dict, List, Tuple, Set


class CommandProcessor:
    def __init__(self):
        self.locations = {
            "Phòng ngủ": ["phòng ngủ", "phong ngu", "phòng ngủ", "phòng ngủ", "phòng ngũ"],
            "Phòng khách": ["phòng khách", "phong khach", "phòng khác", "phong khach", "phong cach", "phong cách", "phong kach"],
            "Phòng vệ sinh": ["phòng vệ sinh", "phong ve sinh", "nhà vệ sinh", "nha ve sinh", "wc", "toilet"],
            "Nhà bếp": ["nhà bếp", "nha bep", "phòng bếp", "phong bep", "bếp"]
        }
        
        self.actions = {
            "on light": ["bật đèn", "bat den", "mở đèn", "mo den", "bặt đèn", "bậc đèn", "vật đèn", "vat den"],
            "off light": ["tắt đèn", "tat den", "đóng đèn", "dong den", "tắc đèn"],
            "on fan": ["bật quạt", "bat quat", "mở quạt", "mo quat", "bặt quạt", "bậc quạt"],
            "off fan": ["tắt quạt", "tat quat", "đóng quạt", "dong quat", "tắc quạt"],
            "open curtain": ["mở rèm", "mo rem", "kéo rèm", "keo rem", "vén rèm", "ven rem"],
            "close curtain": ["đóng rèm", "dong rem", "kéo rèm lại", "keo rem lai", "che rèm", "che rem"],
            "open door": ["mở cửa", "mo cua", "kéo cửa", "keo cua", "vén cửa", "ven cua", "kéo cửa lại"],
            "close door": ["đóng cửa", "dong cua", "che cửa", "che cua", "đóng cửa lại", "dong cua lai", "khép cửa", "khep cua", "khép cửa lại", "khep cua lai"],
        }
        
        self.negations = ["không", "đừng", "khong", "dung", "đưng", "dừng", "ngừng", "ngung"]
        
        self.fan_states = {
            "1": ["thấp", "thap", "chậm", "cham", "nhẹ", "nhe", "yếu", "yeu", "nhỏ", "nho"],
            "2": ["trung bình", "trung binh", "vừa", "vua", "bình thường", "binh thuong"], 
            "3": ["cao", "mạnh", "manh", "nhanh", "to", "lớn", "lon", "mạnh nhất", "manh nhat", "tối đa", "toi da"]
        }
    
    def _find_best_match(self, word: str, options: Dict[str, List[str]]) -> str:
        for key, variations in options.items():
            for variation in variations:
                if self._is_similar(word, variation):
                    return key
        return None
    
    def _is_similar(self, word1: str, word2: str) -> bool:
        word1 = word1.lower().strip()
        word2 = word2.lower().strip()
        
        if word1 == word2:
            return True
        
        if word1.startswith(word2) or word2.startswith(word1):
            return True
        
        if len(word1) > 4 and len(word2) > 4:
            distance = self._levenshtein_distance(word1, word2)
            max_len = max(len(word1), len(word2))
            if distance <= max_len * 0.3:
                return True
        
        return False
    
    def _levenshtein_distance(self, s1: str, s2: str) -> int:
        if len(s1) < len(s2):
            return self._levenshtein_distance(s2, s1)
        
        if len(s2) == 0:
            return len(s1)
        
        previous_row = range(len(s2) + 1)
        for i, c1 in enumerate(s1):
            current_row = [i + 1]
            for j, c2 in enumerate(s2):
                insertions = previous_row[j + 1] + 1
                deletions = current_row[j] + 1
                substitutions = previous_row[j] + (c1 != c2)
                current_row.append(min(insertions, deletions, substitutions))
            previous_row = current_row
        
        return previous_row[-1]
    
    def _normalize_text(self, text: str) -> str:
        text = text.lower()
        text = re.sub(r'[^\w\s,.?!0-9]', '', text)
        return text
    
    def _extract_ngrams(self, text: str, max_len: int = 4) -> List[str]:
        words = text.split()
        ngrams = []
        
        for n in range(1, min(max_len + 1, len(words) + 1)):
            for i in range(len(words) - n + 1):
                ngram = ' '.join(words[i:i + n])
                ngrams.append(ngram)
        
        ngrams.sort(key=len, reverse=True)
        return ngrams
    
    def process_command(self, text: str) -> Dict:
        """Xử lý lệnh và trả về thông tin phân tích."""
        normalized_text = self._normalize_text(text)
        ngrams = self._extract_ngrams(normalized_text)
        
        result = {
            "location": None,
            "action": None,
            "is_negative": False,
            "fan_state": None,
            "device": None,
            "status": None
        }
        
        for ngram in ngrams:
            location = self._find_best_match(ngram, self.locations)
            if location:
                result["location"] = location
                break
        
        for ngram in ngrams:
            action = self._find_best_match(ngram, self.actions)
            if action:
                result["action"] = action
                break
        
        for word in normalized_text.split():
            if any(self._is_similar(word, neg) for neg in self.negations):
                result["is_negative"] = True
                break
        
        if result["action"] and "quạt" in result["action"]:
            for ngram in ngrams:
                fan_state = self._find_best_match(ngram, self.fan_states)
                if fan_state:
                    result["fan_state"] = fan_state
                    break
        
        if result["action"]:
            result["status"] = result["action"].split()[0]
            result["device"] = result["action"].split()[1]

            if result["is_negative"]:
                if "on" in result["status"]:
                    result["status"] = "off"
                elif "off" in result["status"]:
                    result["status"] = "on"
                elif "open" in result["status"]:
                    result["status"] = "close"
                elif "close" in result["status"]:
                    result["status"] = "open"
                
            if result["device"] == "fan":
                result["status"] = "0" if result["status"] == "off" else result["fan_state"]

        return result
    

if __name__ == "__main__":
    processor = CommandProcessor()

    test_cases = [
        "vật đèn phòng ngủ",
        "không tắt quạt phòng khách",
        "bật quạt trung bình trong phòng vệ sinh",
        "đóng rèm nhà bếp",
        "bạt đèn phòng ngũ",  # Lỗi chính tả
        "phòng khách đừng tắt đèn",
        "mở rèm trong phòng kách",  # Lỗi chính tả
        "tắt quạt cao ở phòng vệ sinh",
        "bật quạt thấp trong phòng bếp",
        "đừng bạt đèn phòng ve sinh",  # Nhiều lỗi chính tả
    ]

    for i, test in enumerate(test_cases):
        print(f"\nTest case {i+1}: '{test}'")
        print("-" * 50)
        result = processor.process_command(test)
        print(f"location: {result['location']}")
        print(f"action: {result['action']}")
        print(f"device: {result['device']}")
        print(f"status: {result['status']}")
        print("-" * 50)