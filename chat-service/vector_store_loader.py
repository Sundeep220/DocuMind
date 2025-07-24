import os
from langchain_community.vectorstores import FAISS
from langchain_huggingface import HuggingFaceEmbeddings
from pathlib import Path
from typing import Dict

# def load_vector_store(user_id: str):
#     # Go up from chat-service/ to project root, then into vector_indexes/
#     base_dir = Path(__file__).resolve().parent.parent
#     vector_base_path = base_dir / "vector_indexes"
#     user_path = vector_base_path / user_id

#     if not user_path.exists():
#         raise FileNotFoundError(f"No vector store found for user {user_id}")


#     embedding_model = HuggingFaceEmbeddings(model_name="sentence-transformers/all-MiniLM-L6-v2")  

#     # Load and merge all vector stores under the user folder
#     vector_stores = []
#     for subdir in os.listdir(user_path):
#         sub_path = os.path.join(user_path, subdir)
#         if os.path.isdir(sub_path):
#             print(f"Loading vector store from: {sub_path}")
#             vs = FAISS.load_local(
#                 folder_path=sub_path,
#                 embeddings=embedding_model,
#                 index_name="index",
#                 allow_dangerous_deserialization=True
#             )
#             vector_stores.append(vs)

#     if not vector_stores:
#         raise Exception(f"No vector indexes found for user {user_id}")

#     # Merge all loaded vector stores
#     merged_store = vector_stores[0]
#     for vs in vector_stores[1:]:
#         merged_store.merge_from(vs)

#     return merged_store


# --- Global in-memory cache ---
vector_cache: Dict[str, Dict] = {}  # { user_id: {"timestamp": float, "vector_store": FAISS} }

def get_latest_timestamp(user_path: Path) -> float:
    """Returns the most recent modification time of any file in the vector store directory."""
    latest = 0.0
    for subdir, _, files in os.walk(user_path):
        for file in files:
            full_path = os.path.join(subdir, file)
            latest = max(latest, os.path.getmtime(full_path))
    return latest


def load_vector_store(user_id: str):
    base_dir = Path(__file__).resolve().parent.parent
    vector_base_path = base_dir / "vector_indexes"
    user_path = vector_base_path / user_id

    if not user_path.exists():
        raise FileNotFoundError(f"No vector store found for user {user_id}")

    # Check if we already have a cached version
    latest_ts = get_latest_timestamp(user_path)
    if user_id in vector_cache:
        cached_ts = vector_cache[user_id]["timestamp"]
        if cached_ts == latest_ts:
            print(f"[Cache] Using cached vector store for {user_id}")
            return vector_cache[user_id]["vector_store"]
        else:
            print(f"[Cache] Updating vector store for {user_id} due to new upload")

    embedding_model = HuggingFaceEmbeddings(model_name="sentence-transformers/all-MiniLM-L6-v2")

    # Load and merge all vector stores under the user folder
    vector_stores = []
    for subdir in os.listdir(user_path):
        sub_path = os.path.join(user_path, subdir)
        if os.path.isdir(sub_path):
            print(f"Loading vector store from: {sub_path}")
            vs = FAISS.load_local(
                folder_path=sub_path,
                embeddings=embedding_model,
                index_name="index",
                allow_dangerous_deserialization=True
            )
            vector_stores.append(vs)

    if not vector_stores:
        raise Exception(f"No vector indexes found for user {user_id}")

    merged_store = vector_stores[0]
    for vs in vector_stores[1:]:
        merged_store.merge_from(vs)

    # Cache it
    vector_cache[user_id] = {
        "timestamp": latest_ts,
        "vector_store": merged_store
    }

    return merged_store