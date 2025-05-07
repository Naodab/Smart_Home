# filepath: /speaker-recognition/speaker-recognition/README.md
# Speaker Recognition Model with CNN and MFCC
This project implements a text-dependent speaker recognition model using Convolutional Neural Networks (CNN) and Mel-frequency cepstral coefficients (MFCC) for feature extraction. The model is designed to recognize speakers from audio samples while incorporating data augmentation techniques to improve robustness.

## Project Structure
```
speaker-recognition
├── src
│   ├── data
│   │   ├── preprocessing.py       # Functions for preprocessing audio data
│   │   ├── augmentation.py        # Functions for data augmentation
│   │   └── dataset.py             # Dataset loading and splitting
│   ├── models
│   │   ├── cnn_model.py           # CNN architecture for speaker recognition
│   │   └── utils.py               # Model compilation utilities
│   ├── features
│   │   └── mfcc.py                # MFCC feature extraction functions
│   ├── config
│   │   └── config.py              # Configuration settings
│   └── train.py                   # Main training script
├── tests
│   ├── test_preprocessing.py       # Unit tests for preprocessing functions
│   ├── test_model.py               # Unit tests for the CNN model
│   └── test_features.py            # Unit tests for MFCC functions
├── requirements.txt                # Project dependencies
├── setup.py                        # Packaging information
└── README.md                       # Project documentation
```

## Setup Instructions
1. Clone the repository:
   ```
   git clone <repository-url>
   cd speaker-recognition
   ```

2. Install the required dependencies:
   ```
   pip install -r requirements.txt
   ```

3. Configure the project settings in `src/config/config.py` as needed.

## Usage
To train the model, run the following command:
```
python src/train.py
```

## Model Overview
The model utilizes a CNN architecture to process MFCC features extracted from audio samples. Data augmentation techniques, such as adding noise, are applied to enhance the training dataset. The training process includes callbacks like EarlyStopping, ModelCheckpoint, and ReduceLROnPlateau to optimize performance.

## Testing
Unit tests are provided to ensure the functionality of preprocessing, model architecture, and feature extraction. To run the tests, use:
```
pytest tests/
```

## License
This project is licensed under the MIT License. See the LICENSE file for more details.