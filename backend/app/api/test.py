from fastapi import APIRouter, Request, Response, HTTPException
from app.core.db import supabase
from postgrest.exceptions import APIError

# Disable redirect_slashes for the router specifically
router = APIRouter(prefix="/test")

@router.get("")
def test_root():
    return {"message": "Test Root"}


@router.get("/set-cookie")
def set_cookie(response: Response):
    # Return a response that sets a cookie
    response.set_cookie(
        key="test_cookie",
        value="test_value",
        httponly=True,
        secure=True,  # restrict cookie to HTTPS
        samesite="none",  # allow cross-site usage with secure
    )
    # Add a message to the response body
    return {"message": "Cookie has been set"}

@router.get("/check-cookie")
def check_cookie(response: Response, request: Request):
    test_cookie = request.cookies.get("test_cookie")
    if test_cookie == "test_value":
        return {"message": "Cookie is set correctly"}
    else:
        response.status_code = 400
        return {"message": "Cookie is missing or incorrect"}
    
# Database connection test endpoint
# Root
@router.get("/db")
def db_root():
    return {"message": "Database Test Root"}

@router.get("/db/{item_id}")
def db_test(item_id: int):
    # return {"item_id": item_id}
    try:
        # 1. Use .single() if you only expect one row. 
        # It simplifies the response so you don't have to use [0].
        # 2. Use .execute() (note: if you initialize the client as async, use 'await')
        response = supabase.table("test_data").select("*").eq("id", item_id).single().execute()
        
        return response.data

    except APIError as e:
        # Supabase throws an APIError if .single() finds 0 rows (PGRST116)
        if e.code == "PGRST116":
            raise HTTPException(status_code=404, detail="Item not found")
        
        # Catch other database errors (e.g., column doesn't exist, RLS violations)
        raise HTTPException(status_code=400, detail=f"Database error: {e.message}")
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))