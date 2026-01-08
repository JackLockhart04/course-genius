# app/core/db.py
from supabase import create_client
from app.core.config import settings

# Used for regular user operations (Respects RLS)
supabase = create_client(settings.SUPABASE_URL, settings.SUPABASE_PUBLISHABLE_KEY)

# Used ONLY for admin/background tasks (Bypasses RLS)
supabase_admin = create_client(settings.SUPABASE_URL, settings.SUPABASE_SECRET_KEY)