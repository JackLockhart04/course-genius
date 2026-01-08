import React from "react";
import "./Account.css";

import { useUser } from "../../context/UserContext";
import LogoutButton from "../../components/LogoutButton";

const Account: React.FC = () => {
  const { user, loading } = useUser();

  // User data is already fetched by UserContext on mount
  // No need to refresh again here

  return (
    <div className="accountContainer">
      <div className="accountCard">
        <h1 className="accountHeader">Account</h1>
        {loading ? (
          <p className="accountStatus">Loading user data...</p>
        ) : user.email ? (
          <>
            <div className="userInfo">
              <div className="infoRow">
                <span className="label">User ID</span>
                <span className="value">{user.id}</span>
              </div>
              <div className="infoRow">
                <span className="label">Full Name</span>
                <span className="value">{user.fullName}</span>
              </div>
              <div className="infoRow">
                <span className="label">Email</span>
                <span className="value">{user.email}</span>
              </div>
              <div className="infoRow">
                <span className="label">Last Sign In</span>
                <span className="value">{user.lastSignIn ?? "N/A"}</span>
              </div>
              <div className="infoRow">
                <span className="label">Account Created</span>
                <span className="value">{user.createdAt ?? "N/A"}</span>
              </div>
              <div className="infoRow">
                <span className="label">Last Updated</span>
                <span className="value">{user.updatedAt ?? "N/A"}</span>
              </div>
            </div>
            <div className="logoutRow">
              <LogoutButton />
            </div>
          </>
        ) : (
          <p className="accountStatus">User not logged in</p>
        )}
      </div>
    </div>
  );
};

export default Account;
