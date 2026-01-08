import { createClient } from "@supabase/supabase-js";

// These are the variables we talked about earlier
const supabaseUrl = process.env.REACT_APP_SUPABASE_URL || "";
const supabaseAnonKey = process.env.REACT_APP_SUPABASE_PUBLISHABLE_KEY || "";

export const supabase = createClient(supabaseUrl, supabaseAnonKey);
