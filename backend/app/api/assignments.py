from fastapi import APIRouter, Depends, HTTPException, Request, Response, status
from app.schemas import AssignmentCreate, AssignmentResponse
from app.core.auth import get_current_user
from app.core.db import supabase
from typing import List

router = APIRouter(prefix="/assignments", tags=["Assignments"])

# GET: List all assignments for a specific course
@router.get("/course/{course_id}", response_model=List[AssignmentResponse])
async def get_course_assignments(
    course_id: str,
    request: Request,
    user = Depends(get_current_user)
):
    try:
        auth_header = request.headers.get("Authorization")
        token = auth_header.split(" ")[1]
        supabase.postgrest.auth(token)

        response = supabase.table("assignments")\
            .select("*")\
            .eq("course_id", course_id)\
            .eq("user_id", user.id)\
            .execute()
            
        return response.data
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

# POST: Create a new assignment
@router.post("/", response_model=AssignmentResponse)
async def create_assignment(
    request: Request,
    assignment: AssignmentCreate,
    user = Depends(get_current_user)
):
    try:
        auth_header = request.headers.get("Authorization")
        token = auth_header.split(" ")[1]
        supabase.postgrest.auth(token)

        new_assignment = {
            "course_id": str(assignment.course_id),
            "user_id": user.id,
            "title": assignment.title,
            "weight": assignment.weight,
            "max_score": assignment.max_score,
            "due_date": str(assignment.due_date) if assignment.due_date else None
        }

        response = supabase.table("assignments").insert(new_assignment).execute()
        return response.data[0]
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

# DELETE: Remove an assignment
@router.delete("/{assignment_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_assignment(
    assignment_id: str,
    request: Request,
    user = Depends(get_current_user)
):
    try:
        auth_header = request.headers.get("Authorization")
        token = auth_header.split(" ")[1]
        supabase.postgrest.auth(token)

        response = supabase.table("assignments")\
            .delete()\
            .eq("id", assignment_id)\
            .eq("user_id", user.id)\
            .execute()

        if len(response.data) == 0:
            raise HTTPException(status_code=404, detail="Assignment not found")
            
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))