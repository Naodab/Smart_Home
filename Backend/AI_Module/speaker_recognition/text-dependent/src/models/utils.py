import tensorflow as tf

def compile_model(model, learning_rate=0.001):
    """Compiles the given model with the specified learning rate."""
    optimizer = tf.keras.optimizers.Adam(learning_rate=learning_rate)
    loss = 'sparse_categorical_crossentropy'
    metrics = ['accuracy']
    
    model.compile(optimizer=optimizer, loss=loss, metrics=metrics)
    return model

def create_callbacks():
    """Creates a list of callbacks for model training."""
    early_stopping = tf.keras.callbacks.EarlyStopping(
        monitor='val_loss', patience=5, restore_best_weights=True
    )
    model_checkpoint = tf.keras.callbacks.ModelCheckpoint(
        filepath='best_model.h5', monitor='val_loss', save_best_only=True
    )
    reduce_lr = tf.keras.callbacks.ReduceLROnPlateau(
        monitor='val_loss', factor=0.5, patience=3, min_lr=1e-6
    )
    
    return [early_stopping, model_checkpoint, reduce_lr]