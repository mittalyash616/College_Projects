from fastapi import FastAPI
from pydantic import BaseModel
from fastapi.middleware.cors import CORSMiddleware
from api.predictions import get_crop_prediction,get_fertilizer_prediction

# -----------------------------
# FastAPI App Initialization
# -----------------------------
app = FastAPI()

# Allow all origins (CORS)
origins = ["*"]
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# -----------------------------
# Request Models
# -----------------------------
class CropRequest(BaseModel):
    Nitrogen: float
    Phosphorus: float
    Potassium: float
    Temperature: float
    Humidity: float
    Ph: float
    Rainfall: float

class FertilizerRequest(BaseModel):
    Nitrogen: float
    Phosphorus: float
    Potassium: float
    Temperature: float
    Humidity: float
    Moisture: float
    Crop_type: str
    Soil_type: str

# -----------------------------
# Endpoints
# -----------------------------
@app.post("/predict-crop/")
def predict_crop_endpoint(data: CropRequest):
    """
    Predict recommended crop based on soil and weather conditions.
    """
    return get_crop_prediction(data)

@app.post("/predict-fertilizer/")
def predict_fertilizer_endpoint(data: FertilizerRequest):
    """
    Predict recommended fertilizer based on soil, crop, and weather conditions.
    """
    return get_fertilizer_prediction(data)
