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

class CourseUpdate(BaseModel):
    name: Optional[str] = None
    credits: Optional[float] = None  # Matches DECIMAL in SQL
    semester: Optional[str] = None
    color_code: Optional[str] = None
    
from pydantic import BaseModel, Field
from typing import Optional
from datetime import date
from uuid import UUID

class AssignmentBase(BaseModel):
    title: str
    # Adding '= 0.0' makes it optional in the API and defaults it to 0
    weight: float = 0.0  
    max_score: Optional[float] = 100.0
    due_date: Optional[date] = None

class AssignmentCreate(AssignmentBase):
    # course_id is handled in the URL path, but included here 
    # as optional in case your frontend sends it in the JSON body.
    course_id: Optional[UUID] = None 

class AssignmentUpdate(BaseModel):
    """Schema for PATCH requests - all fields optional."""
    title: Optional[str] = None
    weight: Optional[float] = None
    score_achieved: Optional[float] = None
    max_score: Optional[float] = None
    due_date: Optional[date] = None

class AssignmentResponse(AssignmentBase):
    id: UUID
    course_id: UUID
    user_id: UUID
    score_achieved: Optional[float] = None
    created_at: str

    class Config:
        from_attributes = True