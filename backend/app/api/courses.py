from fastapi import APIRouter, Depends, HTTPException, Request, Response, status
from app.schemas import CourseCreate, CourseResponse, CourseUpdate
from app.core.auth import get_current_user
from app.core.db import supabase

router = APIRouter(prefix="/courses", tags=["Courses"])

@router.get("/", response_model=list[CourseResponse])
async def get_my_courses(request: Request, user = Depends(get_current_user)):
    try:
        auth_header = request.headers.get("Authorization")
        token = auth_header.split(" ")[1]
        supabase.postgrest.auth(token)

        response = supabase.table("courses")\
            .select("*")\
            .eq("user_id", user.id)\
            .execute()
            
        return response.data
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Failed to fetch courses: {str(e)}")

@router.get("/{course_id}", response_model=CourseResponse)
async def get_course(course_id: str, request: Request, user = Depends(get_current_user)):
    try:
        auth_header = request.headers.get("Authorization")
        token = auth_header.split(" ")[1]
        supabase.postgrest.auth(token)

        response = supabase.table("courses")\
            .select("*")\
            .eq("id", course_id)\
            .eq("user_id", user.id)\
            .maybe_single()\
            .execute()
            
        if not response.data:
            raise HTTPException(status_code=404, detail="Course not found")
            
        return response.data
    except HTTPException as he:
        raise he
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.post("/", response_model=CourseResponse)
async def create_new_course(request: Request, course: CourseCreate, user = Depends(get_current_user)):
    try:
        auth_header = request.headers.get("Authorization")
        token = auth_header.split(" ")[1]
        supabase.postgrest.auth(token)

        new_course_data = {
            "name": course.name,
            "credits": course.credits,
            "semester": course.semester,
            "color_code": course.color_code,
            "user_id": user.id,
            "final_letter_grade": course.final_letter_grade, # New column
            "final_gpa": course.final_gpa               # New column
        }
        
        response = supabase.table("courses").insert(new_course_data).execute()
        return response.data[0]
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Failed to create course: {str(e)}")

@router.patch("/{course_id}", response_model=CourseResponse)
async def update_course(course_id: str, request: Request, updates: CourseUpdate, user = Depends(get_current_user)):
    try:
        auth_header = request.headers.get("Authorization")
        token = auth_header.split(" ")[1]
        supabase.postgrest.auth(token)

        update_data = updates.model_dump(exclude_unset=True)

        if not update_data:
            raise HTTPException(status_code=400, detail="No update data provided")

        response = supabase.table("courses")\
            .update(update_data)\
            .eq("id", course_id)\
            .eq("user_id", user.id)\
            .execute()
            
        if not response.data:
            raise HTTPException(status_code=404, detail="Course not found or unauthorized")
            
        return response.data[0]
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.delete("/{course_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_course(course_id: str, request: Request, user = Depends(get_current_user)):
    try:
        auth_header = request.headers.get("Authorization")
        token = auth_header.split(" ")[1]
        supabase.postgrest.auth(token)

        response = supabase.table("courses")\
            .delete()\
            .eq("id", course_id)\
            .eq("user_id", user.id)\
            .execute()
            
        if len(response.data) == 0:
            raise HTTPException(status_code=404, detail="Course not found or unauthorized")
            
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.get("/{course_id}/stats")
async def get_course_stats(course_id: str, request: Request, user = Depends(get_current_user)):
    try:
        token = request.headers.get("Authorization").split(" ")[1]
        supabase.postgrest.auth(token)

        # 1. Fetch Course details (to check for manual overrides)
        course_resp = supabase.table("courses")\
            .select("final_letter_grade, final_gpa")\
            .eq("id", course_id)\
            .maybe_single()\
            .execute()

        # 2. Fetch calculation stats from View
        stats_resp = supabase.table("course_grade_stats")\
            .select("earned_points, weight_completed")\
            .eq("course_id", course_id)\
            .maybe_single()\
            .execute()

        # Default values
        earned = stats_resp.data.get("earned_points") if stats_resp and stats_resp.data else None
        completed = stats_resp.data.get("weight_completed") if stats_resp and stats_resp.data else None
        
        # Ensure numeric values with fallback to 0
        earned = float(earned) if earned is not None else 0.0
        completed = float(completed) if completed is not None else 0.0
        
        current_avg = (earned / completed * 100) if completed > 0 else 0.0

        # Build response
        return {
            "current_avg": round(current_avg, 2),
            "weight_completed": round(completed, 2),
            "final_letter_grade": course_resp.data.get("final_letter_grade") if course_resp and course_resp.data else None,
            "final_gpa": course_resp.data.get("final_gpa") if course_resp and course_resp.data else None
        }
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))