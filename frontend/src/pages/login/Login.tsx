import React from "react";
import "./Login.css";

const Login: React.FC = () => {
  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  const handleLogin = () => {
    window.location.href = `${apiDomain}/auth/login`;
  };

  return (
    <div className="loginContainer">
      <h1 className="loginHeader">Login Page</h1>
      <button className="loginButton" onClick={handleLogin}>
        Log in
      </button>
    </div>
  );
};

export default Login;
