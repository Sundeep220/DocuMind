# app/embedder.py

from langchain_huggingface import HuggingFaceEmbeddings
from typing import List, Dict
from langchain_core.documents import Document

def embed_documents(
    documents: List[Document],
    model_name: str = "sentence-transformers/all-MiniLM-L6-v2"
) -> List[Dict]:
    """
    Takes a list of LangChain Documents, generates embeddings,
    and returns a list of dicts containing the text, metadata, and vector.

    :param documents: List of LangChain Documents (each has .page_content and .metadata)
    :param model_name: HuggingFace model to use for embeddings
    :return: List of dicts: { content, metadata, embedding }
    """
    # Initialize the embedder
    embedder = HuggingFaceEmbeddings(model_name=model_name)

    texts = [doc.page_content for doc in documents]
    embeddings = embedder.embed_documents(texts)

    results = []
    for doc, vector in zip(documents, embeddings):
        results.append({
            "content": doc.page_content,
            "metadata": doc.metadata,
            "embedding": vector
        })

    return results
