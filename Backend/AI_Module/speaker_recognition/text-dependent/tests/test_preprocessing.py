import unittest
from src.data.preprocessing import normalize_volume, remove_silence

class TestPreprocessing(unittest.TestCase):

    def test_normalize_volume(self):
        # Test case for volume normalization
        audio_data = [0.1, 0.2, 0.3, 0.4, 0.5]
        normalized_audio = normalize_volume(audio_data)
        self.assertTrue(all(-1 <= x <= 1 for x in normalized_audio))

    def test_remove_silence(self):
        # Test case for silence removal
        audio_data = [0, 0, 0, 0.5, 0.6, 0, 0]
        processed_audio = remove_silence(audio_data, threshold=0.1)
        self.assertGreater(len(processed_audio), 0)
        self.assertTrue(any(x > 0 for x in processed_audio))

if __name__ == '__main__':
    unittest.main()