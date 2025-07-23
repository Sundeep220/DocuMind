# app/sender.py

import requests
from typing import List, Dict

VECTOR_STORE_URL = "http://localhost:8080/api/vectors/upload"  # Change this if needed

def send_to_vector_store(embedded_docs: List[Dict]) -> None:
    success_count = 0

    for i, doc in enumerate(embedded_docs):
        payload = {
            "content": doc["content"],
            "embedding": doc["embedding"],
            "metadata": doc["metadata"]
        }

        try:
            response = requests.post(VECTOR_STORE_URL, json=payload)
            response.raise_for_status()
            success_count += 1
        except requests.RequestException as e:
            print(f"❌ Error sending document #{i + 1}: {e}")

    print(f"\n✅ Successfully sent {success_count}/{len(embedded_docs)} documents to vector store.")
