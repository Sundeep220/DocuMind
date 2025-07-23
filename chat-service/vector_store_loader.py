import os
from langchain_community.vectorstores import FAISS

def load_vector_store(user_id: str):
    path = f"./vectorstores/{user_id}"
    if not os.path.exists(path):
        raise FileNotFoundError(f"No vector store found for user {user_id}")

    return FAISS.load_local(folder_path=path, embeddings=None, index_name="index", allow_dangerous_deserialization=True)
