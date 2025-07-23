import os
import json
import time
from langchain.vectorstores import FAISS
from langchain_huggingface import HuggingFaceEmbeddings

def create_vector_store(docs, metadata, index_path, batch_size=500):
    try:
        embedding = HuggingFaceEmbeddings(model_name="sentence-transformers/all-MiniLM-L6-v2")
        vector_store = None

        for i in range(0, len(docs), batch_size):
            batch_docs = docs[i:i + batch_size]
            batch_vector_store = FAISS.from_documents(batch_docs, embedding=embedding)

            if vector_store is None:
                vector_store = batch_vector_store
            else:
                vector_store.merge_from(batch_vector_store)

        os.makedirs(index_path, exist_ok=True)
        vector_store.save_local(index_path)

        with open(os.path.join(index_path, "docstore.json"), "w") as f:
            json.dump(metadata, f, indent=4)

        return vector_store

    except Exception as e:
        print(f"[ERROR] Vector store creation failed: {e}")
        return None
