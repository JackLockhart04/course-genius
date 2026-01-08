import React from "react";
import "./Account.css";

import { useUser } from "../../context/UserContext";

const Account: React.FC = () => {
  const { user, loading } = useUser();

  // User data is already fetched by UserContext on mount
  // No need to refresh again here

  return (
    <div className="accountContainer">
      <h1 className="accountHeader">Account</h1>
      {loading ? (
        <p>Loading user data...</p>
      ) : user.email ? (
        <div className="userInfo">
          <p>
            <strong>User ID:</strong> {user.id}
          </p>
          <p>
            <strong>Full Name:</strong> {user.fullName}
          </p>
          <p>
            <strong>Email:</strong> {user.email}
          </p>
          <p>
            <strong>Last Sign In:</strong> {user.lastSignIn ?? "N/A"}
          </p>
          <p>
            <strong>Account Created:</strong> {user.createdAt ?? "N/A"}
          </p>
          <p>
            <strong>Last Updated:</strong> {user.updatedAt ?? "N/A"}
          </p>
        </div>
      ) : (
        <p>User not logged in</p>
      )}
    </div>
  );
};

export default Account;
