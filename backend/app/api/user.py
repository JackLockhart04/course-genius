from fastapi import APIRouter, Depends, HTTPException
from app.core.db import supabase_admin
from app.core.auth import get_current_user
from postgrest.exceptions import APIError

router = APIRouter(prefix="/user", redirect_slashes=False)

@router.get("/me")
async def get_my_profile(user = Depends(get_current_user)):
    # If no user is authenticated, return guest info
    if user is None:
        return {
            "auth_info": {
                "email": None,
                "last_sign_in": None
            },
            "profile": {
                "id": None,
                "full_name": "Guest",
                "email": None,
                "created_at": None,
                "updated_at": None
            },
            "is_guest": True
        }
    
    # User is authenticated - fetch their profile
    try:
        response = supabase_admin.table("profiles").select("*").eq("id", user.id).single().execute()
    except APIError as e:
        # single() throws an error if 0 or multiple rows are returned
        if "0 rows" in str(e):
            raise HTTPException(status_code=404, detail="Profile not found")
        else:
            raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")
    
    if not response.data:
        raise HTTPException(status_code=404, detail="Profile not found")
    
    profile = response.data
    profile["email"] = user.email
    
    return {
        "auth_info": {
            "email": user.email,
            "last_sign_in": user.last_sign_in_at
        },
        "profile": profile,
        "is_guest": False
    }