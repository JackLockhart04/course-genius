import React from "react";
import "./Account.css";

import { useUser } from "../../context/UserContext";

const Account: React.FC = () => {
  const { user, loading } = useUser();

  return (
    <div className="accountContainer">
      <h1 className="accountHeader">Account</h1>
      {loading ? (
        <p>Loading user data...</p>
      ) : user.email ? (
        <div className="userInfo">
          <p>
            <strong>Username:</strong> {user.username}
          </p>
          <p>
            <strong>Email:</strong> {user.email}
          </p>
          <p>
            <strong>User ID:</strong> {user.id}
          </p>
        </div>
      ) : (
        <p>User not logged in</p>
      )}
    </div>
  );
};

export default Account;
