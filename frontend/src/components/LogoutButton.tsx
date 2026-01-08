import React from "react";
import { Button } from "@mui/material";
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

  return (
    <Button
      variant="outlined"
      color="secondary"
      onClick={handleLogout}
      size="large"
    >
      Logout
    </Button>
  );
};

export default LogoutButton;
