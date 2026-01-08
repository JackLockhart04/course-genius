from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from mangum import Mangum


# Disable redirect_slashes globally
app = FastAPI(redirect_slashes=False)

# --- 1. FIXED CORS FOR COOKIES ---
# When allow_credentials=True, you CANNOT use ["*"]. 
# You must list your frontend URLs exactly.
origins = [
    "http://localhost:3000",    # Your local frontend (React/Next.js)
    "https://coursegenius.free.nf",  # Your production domain
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,  # Necessary for cookies
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def root():
    return {"route_name": "API Root"}

# Include the router
from app.api import user
app.include_router(user.router)
from app.api import test
app.include_router(test.router)

handler = Mangum(app, lifespan="off")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)

