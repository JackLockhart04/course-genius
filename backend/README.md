# Venv

### Start

Run `source .venv/Scripts/activate`

### End

Run `deactivate`

# Deployment

Ensure requirements.txt is up to date
Build a zip file by running `python build.py`
Upload to AWS Lambda

# Testing

### Local dev

Run `python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload`

# API

### FastApi

# Pip

### Installed manually

fastapi - for api
mangum - translation to lambda
uvicorn - local api testing

### Pip freeze and loading

Save to requirements.txt run `pip freeze > requirements.txt`
