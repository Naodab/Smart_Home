import unittest
from src.models.cnn_model import create_model

class TestCNNModel(unittest.TestCase):

    def setUp(self):
        self.model = create_model()

    def test_model_architecture(self):
        self.assertEqual(len(self.model.layers), 10)  # Example: Check if the model has 10 layers
        self.assertIsNotNone(self.model.get_layer('conv2d'))  # Check if a convolutional layer exists
        self.assertIsNotNone(self.model.get_layer('dense'))  # Check if a dense layer exists

    def test_model_compile(self):
        self.model.compile(optimizer='adam', loss='sparse_categorical_crossentropy', metrics=['accuracy'])
        self.assertIsNotNone(self.model.optimizer)  # Check if the optimizer is set
        self.assertEqual(self.model.loss, 'sparse_categorical_crossentropy')  # Check if the loss function is set

if __name__ == '__main__':
    unittest.main()