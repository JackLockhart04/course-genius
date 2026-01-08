from pydantic import BaseModel

class UserResponse(BaseModel):
    username: str
    status: str = "active"
    
from pydantic import BaseModel, EmailStr

class UserSignup(BaseModel):
    email: EmailStr
    password: str
    full_name: str
    
from pydantic import BaseModel, EmailStr

class UserLogin(BaseModel):
    email: EmailStr
    password: str
    
    
from pydantic import BaseModel
from typing import Optional

class CourseCreate(BaseModel):
    name: str
    credits: float = 3.0
    semester: Optional[str] = None
    color_code: Optional[str] = None

class CourseResponse(CourseCreate):
    id: str
    user_id: str
    created_at: str
    
from pydantic import BaseModel
from typing import Optional
from datetime import date
from uuid import UUID

class AssignmentCreate(BaseModel):
    course_id: UUID
    title: str
    weight: float
    max_score: Optional[float] = 100.0
    due_date: Optional[date] = None

class AssignmentResponse(AssignmentCreate):
    id: UUID
    user_id: UUID
    score_achieved: Optional[float] = None
    created_at: str

    class Config:
        from_attributes = True