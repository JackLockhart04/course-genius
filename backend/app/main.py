from fastapi import FastAPI
from mangum import Mangum
import uvicorn

app = FastAPI()

@app.get("/")
def home():
    return {"status": "Local dev is working!"}

# This is for AWS
handler = Mangum(app, lifespan="off")

# This is for your local machine
if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)