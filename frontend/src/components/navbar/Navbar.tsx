import React from "react";
import { Link } from "react-router-dom";
import "./Navbar.css";

import { useUser } from "../../context/UserContext";

const Navbar: React.FC = () => {
  const { user } = useUser();

  return (
    <nav className="navbar">
      <div className="navLeft">
        <Link to="/" className="navLink">
          Home
        </Link>
      </div>
      <div className="navRight">
        <Link to="/dashboard" className="navLink">
          Dashboard
        </Link>
        {user.email ? (
          <Link to="/account" className="navLink">
            Account
          </Link>
        ) : (
          <Link to="/login" className="navLink">
            Login
          </Link>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
