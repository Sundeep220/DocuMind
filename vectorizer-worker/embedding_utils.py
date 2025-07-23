import os
import json
from langchain_community.vectorstores import FAISS
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_community.document_loaders import TextLoader, Docx2txtLoader, PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from config import INDEX_BASE_PATH, EMBEDDING_MODEL_NAME
from pathlib import Path
import zipfile

embedding_model = HuggingFaceEmbeddings(model_name=EMBEDDING_MODEL_NAME)

def process_and_embed_doc(doc_id, file_path, file_name):
    print("File Path: ", file_path)
    try:
        base_dir = Path(__file__).resolve().parent.parent
        full_path = base_dir / file_path
        print("Resolved Full Path: ", full_path)

        if not full_path.exists():
            raise FileNotFoundError(f"File not found: {full_path}")
        

        if file_name.endswith('.txt'):
            loader = TextLoader(full_path)
        elif file_name.endswith('.docx'):
            loader = Docx2txtLoader(full_path)
        elif file_name.endswith('.pdf'):
                loader = PyPDFLoader(full_path)
        else:
            loader = TextLoader(full_path)
        docs = loader.load()

        splitter = RecursiveCharacterTextSplitter(chunk_size=500, chunk_overlap=50)
        chunks = splitter.split_documents(docs)

        vector_store = FAISS.from_documents(chunks, embedding_model)

        index_path = os.path.join(INDEX_BASE_PATH, f"{doc_id}_{file_name.replace(' ', '_')}")
        os.makedirs(index_path, exist_ok=True)

        vector_store.save_local(index_path)

        # Save metadata
        metadata = {
            "doc_id": doc_id,
            "file_name": file_name,
            "original_path": file_path,
            "vector_count": len(chunks)
        }

        with open(os.path.join(index_path, "metadata.json"), "w") as f:
            json.dump(metadata, f, indent=4)

        print(f"[+] Vector store created at {index_path}")

    except Exception as e:
        print(f"[!] Failed to vectorize document {file_name}: {e}")
