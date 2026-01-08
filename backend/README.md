# Venv

Assume all backend actions are with venv enabled other than git stuff

### Start

Run `source .venv/Scripts/activate`

### End

Run `deactivate`

# Deployment

### Code

Run `python build.py`
Upload to AWS Lambda

### Dependencies

Only when dependencies change
Run `python build_dependencies.py`
Upload to AWS Lambda course-genius-dependencies layer

# Testing

### Local dev

Run `python -m uvicorn app.main:app --host 127.0.0.1 --port 8000 --reload`

# API

### FastApi

# Pip

### Installed manually

fastapi - for api
mangum - translation to lambda
uvicorn - local api testing
supabase - database connection
pydantic-settings - env management
pydantic[email] - email verification

### Pip freeze and loading

Save to requirements.txt run `pip freeze > requirements.txt`

# Database info

Using Supabase for a PostgreSQL database
Handles Authorization
