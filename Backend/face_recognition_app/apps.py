from django.apps import AppConfig
import numpy as np
import pickle
from pathlib import Path
import os
os.environ["TF_ENABLE_ONEDNN_OPTS"] = '0'
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

BASE_DIR = Path(__file__).resolve().parent.parent

class FaceRecognitionAppConfig(AppConfig):
    default_auto_field = "django.db.models.BigAutoField"
    name = "face_recognition_app"
    face_embeddings = None
    model = None

    def ready(self):
        embeddings_path = os.path.join(BASE_DIR, "ai_modules", "faces_embeddings_done_4classes.npz")
        model_path = os.path.join(BASE_DIR, "ai_modules", "svm_model_160x160.pkl")
        try:
            self.__class__.face_embeddings = np.load(embeddings_path)
            self.__class__.model = pickle.load(open(model_path, 'rb'))
            print("Load all neccessary model successfully!")
        except Exception as e:
            print(f"Failed to load with error {e}.")
    

