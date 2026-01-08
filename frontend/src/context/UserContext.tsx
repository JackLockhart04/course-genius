import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";
// Import your existing supabase client
import { supabase } from "../supabaseClient";

interface User {
  id: string | null;
  email: string | null;
  fullName: string | null;
  createdAt: string | null;
  updatedAt: string | null;
  lastSignIn: string | null;
  loggedIn: boolean;
}

interface UserContextProps {
  user: User;
  loading: boolean;
  refreshUser: () => Promise<void>; // Added this so you can trigger a reload after login
}

const UserContext = createContext<UserContextProps | undefined>(undefined);

export const UserProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<User>({
    id: null,
    email: null,
    fullName: null,
    createdAt: null,
    updatedAt: null,
    lastSignIn: null,
    loggedIn: false,
  });
  const [loading, setLoading] = useState<boolean>(true);

  // Make sure your .env variable starts with REACT_APP_ or VITE_
  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  const fetchUserData = React.useCallback(async () => {
    console.log("fetchUserData called"); // Debug log
    setLoading(true);
    try {
      // 1. USE THE SDK to get the session.
      const {
        data: { session },
      } = await supabase.auth.getSession();

      const token = session?.access_token || "";

      // 2. Call FastAPI with the token (or empty if no session)
      const response = await fetch(`${apiDomain}/user/me`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        const { auth_info, profile } = data;

        setUser({
          id: profile.id,
          email: auth_info.email,
          fullName: profile.full_name,
          createdAt: profile.created_at,
          updatedAt: profile.updated_at,
          lastSignIn: auth_info.last_sign_in,
          loggedIn: true,
        });
      } else if (response.status === 401) {
        // Not authenticated - set as guest
        setUser({
          id: null,
          email: null,
          fullName: "Guest",
          createdAt: null,
          updatedAt: null,
          lastSignIn: null,
          loggedIn: false,
        });
      } else {
        // Other errors - clear user
        setUser({
          id: null,
          email: null,
          fullName: null,
          createdAt: null,
          updatedAt: null,
          lastSignIn: null,
          loggedIn: false,
        });
      }
    } catch (error) {
      console.error("Error fetching user data:", error);
      setUser({
        id: null,
        email: null,
        fullName: null,
        createdAt: null,
        updatedAt: null,
        lastSignIn: null,
        loggedIn: false,
      });
    } finally {
      setLoading(false);
    }
  }, [apiDomain]);

  useEffect(() => {
    fetchUserData();
  }, [fetchUserData]);

  return (
    <UserContext.Provider value={{ user, loading, refreshUser: fetchUserData }}>
      {children}
    </UserContext.Provider>
  );
};

export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) throw new Error("useUser must be used within a UserProvider");
  return context;
};
