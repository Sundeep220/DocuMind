from fastapi import APIRouter, HTTPException
from schemas import ChatRequest, ChatResponse
from vector_store_loader import load_vector_store
from chain_factory import create_conversational_chain

router = APIRouter()

@router.post("/chat", response_model=ChatResponse)
def chat(request: ChatRequest):
    try:
        vector_store = load_vector_store(request.user_id)
        chain = create_conversational_chain(vector_store)

        response = chain.invoke({"question": request.query})
        return ChatResponse(answer=response["answer"])

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
