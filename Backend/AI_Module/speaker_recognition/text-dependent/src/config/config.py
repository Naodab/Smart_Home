# Configuration settings for the speaker recognition project

# Hyperparameters
LEARNING_RATE = 0.001
BATCH_SIZE = 32
EPOCHS = 50
VALIDATION_SPLIT = 0.15

# File paths
DATASET_AUDIO_PATH = 'path/to/dataset'
MODEL_SAVE_PATH = 'path/to/save/model'
LOGS_PATH = 'path/to/save/logs'

# Augmentation settings
NOISE_SCALE = 0.5
SHUFFLE_SEED = 42

# MFCC settings
SAMPLING_RATE = 16000
NUM_MFCCS = 13