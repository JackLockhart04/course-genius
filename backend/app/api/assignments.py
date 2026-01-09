from fastapi import APIRouter, Depends, HTTPException, Request, Response, status
from app.schemas import AssignmentCreate, AssignmentResponse, AssignmentUpdate
from app.core.auth import get_current_user
from app.core.db import supabase
from typing import List

router = APIRouter(prefix="/courses", tags=["Assignments"])

# GET assignments for a specific course
@router.get("/{course_id}/assignments", response_model=List[AssignmentResponse])
async def get_course_assignments(course_id: str, request: Request, user = Depends(get_current_user)):
    try:
        token = request.headers.get("Authorization").split(" ")[1]
        supabase.postgrest.auth(token)

        response = supabase.table("assignments")\
            .select("*")\
            .eq("course_id", course_id)\
            .eq("user_id", user.id)\
            .execute()
        return response.data
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

# GET: Fetch a specific assignment by its ID
@router.get("/{course_id}/assignments/{assignment_id}", response_model=AssignmentResponse)
async def get_assignment(
    course_id: str,
    assignment_id: str,
    request: Request,
    user = Depends(get_current_user)
):
    try:
        # 1. Auth Handoff
        auth_header = request.headers.get("Authorization")
        token = auth_header.split(" ")[1]
        supabase.postgrest.auth(token)

        # 2. Query for the specific assignment
        # We check course_id, assignment_id, and user_id for maximum security
        response = supabase.table("assignments")\
            .select("*")\
            .eq("id", assignment_id)\
            .eq("course_id", course_id)\
            .eq("user_id", user.id)\
            .maybe_single()\
            .execute()
            
        if not response.data:
            raise HTTPException(status_code=404, detail="Assignment not found")
            
        return response.data
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

# POST a new assignment
@router.post("/{course_id}/assignments", response_model=AssignmentResponse)
async def create_assignment(course_id: str, request: Request, assignment: AssignmentCreate, user = Depends(get_current_user)):
    try:
        token = request.headers.get("Authorization").split(" ")[1]
        supabase.postgrest.auth(token)

        new_assignment = {
            "course_id": course_id,
            "user_id": user.id,
            "title": assignment.title, # Using 'title' from schema
            "weight": assignment.weight,
            "max_score": assignment.max_score,
            "score_achieved": assignment.score_achieved,
            "due_date": str(assignment.due_date) if assignment.due_date else None
        }

        response = supabase.table("assignments").insert(new_assignment).execute()
        return response.data[0]
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

# PATCH an assignment
@router.patch("/{course_id}/assignments/{assignment_id}", response_model=AssignmentResponse)
async def update_assignment(course_id: str, assignment_id: str, request: Request, updates: AssignmentUpdate, user = Depends(get_current_user)):
    try:
        token = request.headers.get("Authorization").split(" ")[1]
        supabase.postgrest.auth(token)

        update_data = updates.model_dump(exclude_unset=True)
        if "due_date" in update_data and update_data["due_date"]:
            update_data["due_date"] = str(update_data["due_date"])

        response = supabase.table("assignments")\
            .update(update_data)\
            .eq("id", assignment_id)\
            .eq("course_id", course_id)\
            .eq("user_id", user.id)\
            .execute()
        return response.data[0]
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

# DELETE an assignment
@router.delete("/{course_id}/assignments/{assignment_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_assignment(course_id: str, assignment_id: str, request: Request, user = Depends(get_current_user)):
    try:
        token = request.headers.get("Authorization").split(" ")[1]
        supabase.postgrest.auth(token)

        supabase.table("assignments")\
            .delete()\
            .eq("id", assignment_id)\
            .eq("course_id", course_id)\
            .eq("user_id", user.id)\
            .execute()
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))