import React from "react";
import { useNavigate } from "react-router-dom";
import { supabase } from "../supabaseClient";
import { useUser } from "../context/UserContext";

const LogoutButton: React.FC = () => {
  const navigate = useNavigate();
  const { refreshUser } = useUser();

  const handleLogout = async () => {
    try {
      await supabase.auth.signOut();
      await refreshUser();
      navigate("/");
    } catch (error) {
      console.error("Error logging out:", error);
    }
  };

  return <button onClick={handleLogout}>Logout</button>;
};

export default LogoutButton;
