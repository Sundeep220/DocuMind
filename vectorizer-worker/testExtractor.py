# # # test_extractor.py
# # from app.extractor import extract_text_from_file

# # text = extract_text_from_file("Testing.txt")
# # print(text[:500])
# # from app.splitter import split_text

# # chunks = split_text(text)  # extracted_text is from extractor.py
# # print(f"Got {len(chunks)} chunks!")

# # from app.embedder import Embedder

# # embedder = Embedder()
# # chunk_embeddings = embedder.embed_documents(chunks)  # chunks from splitter

# # print(f"Got {len(chunk_embeddings)} embeddings!")

# from app.vector_store import VectorStoreManager
# from langchain_core.documents import Document

# # Assume you already have embedded Documents
# docs = [Document(page_content="Project A overview", metadata={"source": "project_a.md"})]

# store = VectorStoreManager(index_path="./data/index")
# store.create(docs, metadata={"project": "my_agentic_assistant"})

# results = store.search("overview of project A")
# for doc in results:
#     print(doc.page_content)



# test_pipeline.py

from app.extractor import extract_documents_from_folder
from app.splitter import split_documents
from app.embedder import embed_documents

def test_pipeline(doc_folder: str):
    print(f"[1] Extracting documents from: {doc_folder}")
    raw_docs, errors = extract_documents_from_folder(doc_folder)
    print(f"  → Extracted {len(raw_docs)} documents")

    print(f"[2] Splitting documents into chunks...")
    split_docs, metadata = split_documents(raw_docs)
    print(f"  → Created {len(split_docs)} chunks")
    print("Splitted Docs: ", split_docs[0])

    print(f"[3] Embedding chunks using HuggingFace model...")
    embedded_docs = embed_documents(split_docs)
    print(f"  → Embedded {len(embedded_docs)} chunks")

    # Preview a sample result
    print("\n✅ Sample Embedded Document:")
    print({
        "content": embedded_docs[0]["content"][:200] + "...",
        "metadata": embedded_docs[0]["metadata"],
        "embedding_preview": embedded_docs[0]["embedding"][:5]  # Just first 5 dims
    })


if __name__ == "__main__":
    # Change this to your actual docs folder
    test_pipeline("data/")

    