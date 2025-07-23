vectorizer-worker/
├── app/
│   ├── main.py               # FastAPI app + manual vectorize trigger
│   ├── consumer.py           # Kafka consumer logic
│   ├── extractor.py          # Extracts text from file
│   ├── splitter.py           # Chunks the text
│   ├── embedder.py           # HuggingFace embedder
│   ├── sender.py             # Sends chunks + vectors to vector-store
│   └── config.py             # Config (Kafka, URLs, etc.)
├── models/
│   └── schemas.py            # Pydantic models for chunk & vector
├── requirements.txt
└── Dockerfile
