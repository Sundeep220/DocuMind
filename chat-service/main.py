from fastapi import FastAPI
from chat_router import router as chat_router

app = FastAPI(title="Chat API Service")
app.include_router(chat_router)
