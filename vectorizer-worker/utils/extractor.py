# extractor.py

import os
import zipfile
from langchain_community.document_loaders import TextLoader, Docx2txtLoader, PyPDFLoader

def extract_documents_from_folder(folder_path):
    """
    Loads supported documents from a folder and returns a list of LangChain documents.
    """
    all_documents = []
    errors = []

    for filename in os.listdir(folder_path):
        file_path = os.path.join(folder_path, filename)

        try:
            if filename.endswith('.txt'):
                loader = TextLoader(file_path)
            elif filename.endswith('.docx'):
                try:
                    loader = Docx2txtLoader(file_path)
                except zipfile.BadZipFile:
                    errors.append((file_path, "Invalid DOCX file."))
                    continue
            elif filename.endswith('.pdf'):
                try:
                    loader = PyPDFLoader(file_path)
                except Exception as e:
                    errors.append((file_path, f"PDF load error: {e}"))
                    continue
            else:
                continue  # Unsupported format

            documents = loader.load()
            all_documents.extend(documents)

            # print("Document loaded:", documents)

        except Exception as e:
            errors.append((file_path, str(e)))

    return all_documents, errors
