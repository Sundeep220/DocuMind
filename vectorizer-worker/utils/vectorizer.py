# app/vectorizer.py

import os, json
from langchain_community.vectorstores.faiss import FAISS
from langchain_huggingface import HuggingFaceEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter

def chunk_and_vectorize(doc_texts: list[str], metadata_list: list[dict], index_path: str):
    embedding = HuggingFaceEmbeddings(model_name="sentence-transformers/all-MiniLM-L6-v2")

    text_splitter = RecursiveCharacterTextSplitter(chunk_size=500, chunk_overlap=50)
    chunks = []
    metadata = []

    for i, text in enumerate(doc_texts):
        chunked = text_splitter.split_text(text)
        chunks.extend(chunked)
        metadata.extend([metadata_list[i]] * len(chunked))

    docs = [{"page_content": c, "metadata": m} for c, m in zip(chunks, metadata)]
    vector_store = FAISS.from_documents(docs, embedding=embedding)

    os.makedirs(index_path, exist_ok=True)
    vector_store.save_local(index_path)

    with open(os.path.join(index_path, "docstore.json"), "w") as f:
        json.dump(metadata_list, f, indent=2)

    return True
