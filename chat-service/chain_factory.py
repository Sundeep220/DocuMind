import os
from dotenv import load_dotenv
from langchain.chains import ConversationalRetrievalChain
from langchain.memory import ConversationBufferMemory
from langchain_groq import ChatGroq
from fastapi import  HTTPException
load_dotenv(override=True)

memory = ConversationBufferMemory(memory_key="chat_history", return_messages=True)


def get_llm():
    llm = ChatGroq(
        groq_api_key=os.getenv("GROQ_API_KEY"),
        model_name="gemma2-9b-it"
    )

    return llm


# Create conversational retrieval chain
def create_conversational_chain(vector_store):
    try:
        model = get_llm()

        retriever = vector_store.as_retriever(search_kwargs={"k": 10})
        

        qa_conversational_chain = ConversationalRetrievalChain.from_llm(
            llm=model,
            retriever=retriever,
            memory=memory,
            verbose=True
        )
        return qa_conversational_chain

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))