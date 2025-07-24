chat-service/
│
├── app/
│   ├── main.py                  # FastAPI entry point
│   ├── chat_router.py           # API routes
│   ├── chain_factory.py         # Your create_conversational_chain logic
│   ├── vector_store_loader.py   # Load FAISS for given user/email
│   ├── schemas.py               # Pydantic request/response models
│   └── config.py                # API keys, constants
│
├── requirements.txt
└── Dockerfile (optional)


Run:  `uvicorn main:app --reload`
