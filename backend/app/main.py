import os
from fastapi import FastAPI, Request, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from mangum import Mangum

app = FastAPI()

# CORS configuration
origins = [
    "http://localhost:3000",
    "https://coursegenius.free.nf",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=False, # Changed to False (No cookies)
    allow_methods=["*"],
    allow_headers=["*"],
)

# This replaces AWS_IAM. It checks for a secret header you'll set in CloudFront.
@app.middleware("http")
async def verify_cloudfront_secret(request: Request, call_next):
    # Only enforce this on the deployed version (Lambda)
    # Locally, this env var won't exist, so it will skip the check.
    expected_secret = os.getenv("CLOUDFRONT_SECRET")
    
    if expected_secret:
        client_secret = request.headers.get("x-cloudfront-secret")
        if client_secret != expected_secret:
            return JSONResponse(
                status_code=403,
                content={"detail": "Access denied. Origin must be CloudFront."}
            )
            
    return await call_next(request)

# Root endpoint for testing
@app.get("/")
def root():
    return {"route_name": "API Root"}

# Include the router
from app.api import user
app.include_router(user.router)
from app.api import test
app.include_router(test.router)
from app.api import courses
app.include_router(courses.router)

# Mangum handler for AWS Lambda
handler = Mangum(app, lifespan="off")

# For local testing
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)