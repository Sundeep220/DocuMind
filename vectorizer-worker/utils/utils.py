# app/utils.py

import requests

def fetch_document_text(doc_id: str) -> str:
    response = requests.get(f"http://document-service:8080/document/content/{doc_id}")
    response.raise_for_status()
    return response.text
