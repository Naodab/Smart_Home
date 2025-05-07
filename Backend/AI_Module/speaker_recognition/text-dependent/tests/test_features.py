import unittest
import numpy as np
from src.features.mfcc import audio_to_mfcc

class TestMFCCExtraction(unittest.TestCase):
    
    def setUp(self):
        self.sample_rate = 16000
        self.audio_sample = np.random.rand(1, self.sample_rate * 2)  # 2 seconds of random audio

    def test_mfcc_shape(self):
        mfccs = audio_to_mfcc(self.audio_sample, self.sample_rate)
        self.assertEqual(mfccs.shape[2], 13)  # Check if we get 13 MFCCs

    def test_mfcc_values(self):
        mfccs = audio_to_mfcc(self.audio_sample, self.sample_rate)
        self.assertTrue(np.all(mfccs >= 0))  # MFCCs should be non-negative

if __name__ == '__main__':
    unittest.main()