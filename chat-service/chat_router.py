from fastapi import APIRouter, HTTPException, Header, Request
from schemas import ChatRequest, ChatResponse
from vector_store_loader import load_vector_store
from chain_factory import create_conversational_chain
import requests
import jwt  # PyJWT
import os

from dotenv import load_dotenv
load_dotenv(override=True)

router = APIRouter()
USER_SERVICE_URL = os.getenv("USER_SERVICE_URL", "http://localhost:8081")

def get_email_from_token(token: str) -> str:
    try:
        # No signature verification, we just extract claims
        decoded = jwt.decode(token, options={"verify_signature": False})
        return decoded.get("email")
    except Exception as e:
        raise HTTPException(status_code=401, detail="Invalid JWT token")

def verify_user(user_id: int, token: str) -> None:
    jwt_email = get_email_from_token(token)
    print("Email from JWT:", jwt_email)
    user_id_long = int(user_id)  # Convert to long

    try:
        response = requests.get(
            f"{USER_SERVICE_URL}/user/id/{user_id_long}",
            headers={"Authorization": f"Bearer {token}"}
        )

        print(f"User service url: {USER_SERVICE_URL}/users/{user_id}")
        print(f"User service response: {response}")
        if response.status_code != 200:
            raise HTTPException(status_code=403, detail="User service validation failed")

        user_data = response.json()
        if user_data["email"] != jwt_email:
            raise HTTPException(status_code=403, detail="JWT email does not match user ID")

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"User validation error: {e}")

@router.post("/chat", response_model=ChatResponse)
def chat(
    request: ChatRequest,
    authorization: str = Header(...),
):
    try:
        if not authorization.startswith("Bearer "):
            raise HTTPException(status_code=401, detail="Missing or invalid Authorization header")

        jwt_token = authorization.split(" ")[1]
        verify_user(request.user_id, jwt_token)

        vector_store = load_vector_store(str(request.user_id))
        chain = create_conversational_chain(vector_store)

        response = chain.invoke({"question": request.query})
        return ChatResponse(answer=response["answer"])

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
