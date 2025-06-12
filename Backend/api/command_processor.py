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
            "1": ["thấp", "thap", "chậm", "cham", "nhẹ", "nhe", "yếu", "yeu", "nhỏ", "nho", "mức một", "muc mot", "mức 1", "muc 1", "số 1", "số mot", "so 1", "so mot"],
            "2": ["trung bình", "trung binh", "vừa", "vua", "bình thường", "binh thuong", "mức hai", "muc hai", "mức 2", "muc 2", "số 2", "so 2", "so hai", "số hai"],
            "3": ["cao", "mạnh", "manh", "nhanh", "to", "lớn", "lon", "mạnh nhất", "manh nhat", "tối đa", "toi da", "mức ba", "muc ba", "mức 3", "muc 3", "số 3", "so 3", "so ba", "số ba"]
        }

        self.device_keywords = {
            "light": ["đèn", "den"],
            "fan": ["quạt", "quat"],
            "curtain": ["rèm", "rem"],
            "door": ["cửa", "cua"]
        }

        self.status_keywords = {
            "on": ["bật", "mở", "bat", "mo", "bặt", "bậc", "vật", "vat"],
            "off": ["tắt", "đóng", "tat", "dong", "tắc"]
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
        words = normalized_text.split()

        result = {
            "location": None,
            "action": None,
            "is_negative": False,
            "fan_state": None,
            "device": None,
            "status": None
        }

        # Tìm vị trí (location)
        for ngram in ngrams:
            location = self._find_best_match(ngram, self.locations)
            if location:
                result["location"] = location
                break

        # Tìm hành động (action)
        for ngram in ngrams:
            action = self._find_best_match(ngram, self.actions)
            if action:
                result["action"] = action
                break

        # Kiểm tra phủ định (negation)
        for word in words:
            if any(self._is_similar(word, neg) for neg in self.negations):
                result["is_negative"] = True
                break

        # Nhận diện thiết bị độc lập
        for word in words:
            for device, keywords in self.device_keywords.items():
                if any(self._is_similar(word, kw) for kw in keywords):
                    result["device"] = device
                    break
            if result["device"]:
                break

        # Nhận diện trạng thái độc lập
        for word in words:
            for status, keywords in self.status_keywords.items():
                if any(self._is_similar(word, kw) for kw in keywords):
                    result["status"] = status
                    break
            if result["status"]:
                break

        # Nếu thiết bị là quạt, tìm trạng thái quạt (fan_state)
        if result["device"] == "fan":
            for ngram in ngrams:
                fan_state = self._find_best_match(ngram, self.fan_states)
                if fan_state:
                    result["fan_state"] = fan_state
                    break
            # Nếu không tìm thấy fan_state và status là "on", đặt mặc định
            if not result["fan_state"] and result["status"] == "on":
                result["fan_state"] = "1"  # Mặc định mức thấp khi bật quạt

        # Nếu action được tìm thấy, ưu tiên thông tin từ action
        if result["action"]:
            result["status"] = result["action"].split()[0]
            result["device"] = result["action"].split()[1]

        # Xử lý phủ định
        if result["is_negative"]:
            if result["status"] == "on":
                result["status"] = "off"
            elif result["status"] == "off":
                result["status"] = "on"
            elif result["status"] == "open":
                result["status"] = "close"
            elif result["status"] == "close":
                result["status"] = "open"

        # Đối với quạt, cập nhật status dựa trên fan_state
        if result["device"] == "fan":
            if result["status"] == "off":
                result["status"] = "0"
            elif result["status"] == "on" and result["fan_state"]:
                result["status"] = result["fan_state"]

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
        print(f"fan_state: {result['fan_state']}")
        print(f"is_negative: {result['is_negative']}")
        print("-" * 50)