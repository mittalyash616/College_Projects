import joblib
import numpy as np
import os

# Get the absolute path to the current file's directory
current_dir = os.path.dirname(os.path.abspath(__file__))

# Path to the models folder (one level up)
models_dir = os.path.join(current_dir, '..', 'models')

# Load crop prediction model and scaler
crop_prediction_model = joblib.load(os.path.join(models_dir, 'crop_prediction_model.pkl'))
crop_prediction_scaler = joblib.load(os.path.join(models_dir, 'crop_prediction_scaler.pkl'))

# Load fertilizer prediction model, scaler, and encoders
fertilizer_prediction_model = joblib.load(os.path.join(models_dir, 'fertilizer_prediction_model.pkl'))
fertilizer_prediction_scaler = joblib.load(os.path.join(models_dir, 'fertilizer_prediction_scaler.pkl'))
crop_encoder = joblib.load(os.path.join(models_dir, 'crop_encoder.pkl'))
soil_encoder = joblib.load(os.path.join(models_dir, 'soil_encoder.pkl'))
fertilizer_encoder = joblib.load(os.path.join(models_dir, 'fertilizer_encoder.pkl'))

# -----------------------------
# Crop Prediction Function
# -----------------------------
def get_crop_prediction(data):
    features = np.array([[
        data.Nitrogen,
        data.Phosphorus,
        data.Potassium,
        data.Temperature,
        data.Humidity,
        data.Ph,
        data.Rainfall
    ]])
    scaled_features = crop_prediction_scaler.transform(features)
    prediction = crop_prediction_model.predict(scaled_features)
    return {"predicted_crop": prediction[0]}

# -----------------------------
# Fertilizer Prediction Function
# -----------------------------
def get_fertilizer_prediction(data):
    # Encode crop and soil types
    crop_encoded = crop_encoder.transform(np.array([[data.Crop_type]]))
    soil_encoded = soil_encoder.transform(np.array([[data.Soil_type]]))

    # Prepare features
    features = np.array([[
        data.Temperature,
        data.Humidity,
        data.Moisture,
        data.Nitrogen,
        data.Potassium,
        data.Phosphorus
    ]])

    # Scale numerical features
    scaled_features = fertilizer_prediction_scaler.transform(features)

    # Concatenate scaled features with encoded soil & crop
    final_features = np.concatenate(
        [scaled_features, soil_encoded.reshape(1, -1), crop_encoded.reshape(1, -1)],
        axis=1
    )

    # Predict and decode fertilizer
    prediction_encoded = fertilizer_prediction_model.predict(final_features)
    prediction = fertilizer_encoder.inverse_transform(prediction_encoded)

    return {"predicted_fertilizer": prediction[0]}
