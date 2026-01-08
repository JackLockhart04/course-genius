from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from app.core.db import supabase
from app.core.config import settings
from typing import Optional

security = HTTPBearer(auto_error=False)

async def get_current_user(token: Optional[HTTPAuthorizationCredentials] = Depends(security)):
    """
    Verifies the JWT sent by the frontend against Supabase.
    Returns None if no token or invalid token.
    """
    if not token:
        return None
        
    try:
        # We ask Supabase to verify the token it issued
        user_response = supabase.auth.get_user(token.credentials)
        
        if not user_response.user:
            return None
            
        return user_response.user  # Returns user object with ID, email, etc.

    except Exception:
        return None