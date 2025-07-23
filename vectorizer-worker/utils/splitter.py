# splitter.py

import uuid
from langchain.text_splitter import RecursiveCharacterTextSplitter
from hashlib import sha256

def get_document_hash(text: str):
    return sha256(text.encode('utf-8')).hexdigest()

def split_documents(documents, chunk_size=1000, chunk_overlap=100):
    """
    Splits documents into semantically meaningful chunks.
    Returns chunks and associated metadata.
    """
    splitter = RecursiveCharacterTextSplitter(chunk_size=chunk_size, chunk_overlap=chunk_overlap)
    # print("Input Docs: ", documents[0])
    chunks = splitter.split_documents(documents)

    # print("Chunked documents: ", chunks[0])

    metadata = []
    for chunk in chunks:
        doc_id = str(uuid.uuid4())
        doc_hash = get_document_hash(chunk.page_content)
        metadata.append({
            "id": doc_id,
            "hash": doc_hash,
            "content": chunk.page_content,
            "source": chunk.metadata.get("source", "unknown"),
        })

    return documents, metadata
