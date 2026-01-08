from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    # Supabase
    SUPABASE_URL: str
    SUPABASE_PUBLISHABLE_KEY: str
    SUPABASE_SECRET_KEY: str
    
    # App
    FRONTEND_URL: str = "http://localhost:3000"
    ENVIRONMENT: str = "development"

    # Tell pydantic to read from the .env file
    model_config = SettingsConfigDict(env_file=".env.development")

settings = Settings()