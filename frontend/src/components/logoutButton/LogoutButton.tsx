import React from "react";
import "./LogoutButton.css";

const LogoutButton: React.FC = () => {
  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  const handleLogout = () => {
    window.location.href = `${apiDomain}/auth/logout`;
  };

  return (
    <div className="logoutContainer">
      <button className="logoutButton" onClick={handleLogout}>
        Logout
      </button>
    </div>
  );
};

export default LogoutButton;
