import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";

interface User {
  id: string | null;
  username: string | null;
  email: string | null;
  loggedIn: boolean;
}

interface UserContextProps {
  user: User;
  loading: boolean;
}

const UserContext = createContext<UserContextProps | undefined>(undefined);

export const UserProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<User>({
    id: null,
    username: null,
    email: null,
    loggedIn: false,
  });
  const [loading, setLoading] = useState<boolean>(true);
  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  useEffect(() => {
    // Fetch data
    const fetchUserData = async () => {
      try {
        const response = await fetch(`${apiDomain}/user/get-user-data`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include", // Include credentials (cookies) in the request
        });
        // Handle data
        if (response.ok) {
          const data = await response.json();
          if (data.id === null) {
            setUser({
              id: null,
              username: null,
              email: null,
              loggedIn: false,
            });
            return;
          }
          setUser({
            id: data.id,
            username: data.username,
            email: data.email,
            loggedIn: true,
          });
        } else {
          // Bad response
          console.error("Failed to fetch user data");
        }
        // Handle errors
      } catch (error) {
        console.error("Error fetching user data:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [apiDomain]);

  return (
    <UserContext.Provider value={{ user, loading }}>
      {children}
    </UserContext.Provider>
  );
};

export const useUser = (): UserContextProps => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("useUser must be used within a UserProvider");
  }
  return context;
};
