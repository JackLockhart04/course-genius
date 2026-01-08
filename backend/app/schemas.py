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