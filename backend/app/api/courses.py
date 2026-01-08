from fastapi import APIRouter, Depends, HTTPException, Request, Response, status
from app.schemas import CourseCreate, CourseResponse
from app.core.auth import get_current_user
from app.core.db import supabase # Your initialized client

router = APIRouter(prefix="/courses", tags=["Courses"])

@router.get("/", response_model=list[CourseResponse])
async def get_my_courses(
    request: Request, # Inject the request object
    user = Depends(get_current_user)
):
    try:
        # 1. Grab the token from the header
        auth_header = request.headers.get("Authorization")
        if not auth_header:
            raise HTTPException(status_code=401, detail="Token missing")
        
        token = auth_header.split(" ")[1]

        # 2. Tell Supabase to act as this specific student
        # This makes the RLS 'auth.uid()' match your 'user_id' column
        supabase.postgrest.auth(token)

        # 3. Query the table
        # We still keep .eq("user_id", user.id) as a double-check
        response = supabase.table("courses")\
            .select("*")\
            .eq("user_id", user.id)\
            .execute()
            
        return response.data
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Failed to fetch courses: {str(e)}")

@router.post("/", response_model=CourseResponse)
async def create_new_course(
    request: Request, # Inject the request object
    course: CourseCreate, 
    user = Depends(get_current_user)
):
    try:
        # 1. Extract the JWT token from the Authorization header
        auth_header = request.headers.get("Authorization")
        if not auth_header:
            raise HTTPException(status_code=401, detail="Token missing")
        
        token = auth_header.split(" ")[1]

        # 2. IMPORTANT: Tell the Supabase client to use this user's token
        # This makes auth.uid() in your RLS policy work correctly
        supabase.postgrest.auth(token)

        # 3. Prepare data
        new_course_data = {
            "name": course.name,
            "credits": course.credits,
            "semester": course.semester,
            "color_code": course.color_code,
            "user_id": user.id  
        }
        
        # 4. Insert into Supabase
        response = supabase.table("courses").insert(new_course_data).execute()
        
        if not response.data:
            raise HTTPException(status_code=400, detail="Failed to create course")
            
        return response.data[0]
        
    except Exception as e:
        # If Supabase returns an RLS error now, it means your user_id 
        # doesn't match the token's UID or the RLS policy is misconfigured.
        raise HTTPException(status_code=400, detail=f"Failed to create course: {str(e)}")

@router.get("/{course_id}", response_model=CourseResponse)
async def get_course(
    course_id: str,  # Changed from int to str to support UUIDs
    request: Request, 
    user = Depends(get_current_user)
):
    try:
        # 1. Auth Handoff
        auth_header = request.headers.get("Authorization")
        if not auth_header:
            raise HTTPException(status_code=401, detail="Missing token")
            
        token = auth_header.split(" ")[1]
        supabase.postgrest.auth(token)

        # 2. Query for the specific course
        # We filter by both the ID and the user_id for security
        response = supabase.table("courses")\
            .select("*")\
            .eq("id", course_id)\
            .eq("user_id", user.id)\
            .maybe_single()\
            .execute()
            
        # 3. Handle Result
        if not response.data:
            raise HTTPException(status_code=404, detail="Course not found")
            
        return response.data

    except HTTPException as he:
        # Re-raise HTTP exceptions so they don't get caught by the general Exception block
        raise he
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))
    
@router.delete("/{course_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_course(
    course_id: str, 
    request: Request, 
    user = Depends(get_current_user)
):
    try:
        # 1. Auth Handoff
        auth_header = request.headers.get("Authorization")
        if not auth_header:
            raise HTTPException(status_code=401, detail="Missing token")
            
        token = auth_header.split(" ")[1]
        supabase.postgrest.auth(token)

        # 2. Execute Delete
        # The .eq("user_id", user.id) is a critical safety check to ensure 
        # a student can't delete someone else's course via a random ID.
        response = supabase.table("courses")\
            .delete()\
            .eq("id", course_id)\
            .eq("user_id", user.id)\
            .execute()
            
        # 3. Check if anything was actually deleted
        if len(response.data) == 0:
            raise HTTPException(status_code=404, detail="Course not found or unauthorized")
            
        # Return nothing (Standard for 204 No Content)
        return Response(status_code=status.HTTP_204_NO_CONTENT)

    except HTTPException as he:
        raise he
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))