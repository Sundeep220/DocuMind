from fastapi import Depends, HTTPException, Request
from jose import jwt, JWTError
import os

SECRET_KEY = os.getenv("JWT_SECRET")  # same as in your Spring Boot app
ALGORITHM = "HS256"  # assuming you're using HS256

def get_current_user_email(request: Request) -> str:
    auth_header = request.headers.get("Authorization")
    if not auth_header or not auth_header.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Invalid auth header")

    token = auth_header.split(" ")[1]

    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        email = payload.get("email")
        if email is None:
            raise HTTPException(status_code=401, detail="Missing email in token")
        return email
    except JWTError:
        raise HTTPException(status_code=401, detail="Invalid JWT token")
