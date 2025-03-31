import os
import tensorflow as tf
from tensorflow.keras import layers, models

# Load the model
model = tf.keras.models.load_model(r'C:\Users\TechCare\OneDrive - The University of Technology\PBL5\Smart_Home\Backend\AI_Module\speaker_recognition\models\speaker_recognition_resnet_model.keras')

# Create the embedding model
embedding_model = tf.keras.Model(
  inputs=model.input,
  outputs=model.get_layer('embedding_layer').output
)

# Get the absolute path to the 'models' directory
models_dir = os.path.join(os.path.dirname(__file__), 'models')
os.makedirs(models_dir, exist_ok=True)

# Save the embedding model
embedding_model.save(os.path.join(models_dir, 'resnet_embedding_model_2.keras'))