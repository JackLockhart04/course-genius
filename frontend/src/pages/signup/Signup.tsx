import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { supabase } from "../../supabaseClient";
import { useUser } from "../../context/UserContext";
import "./Signup.css";

const Signup: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [fullName, setFullName] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { refreshUser } = useUser();

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const { data, error } = await supabase.auth.signUp({
        email,
        password,
        options: {
          data: {
            full_name: fullName,
          },
        },
      });

      if (error) {
        setError(error.message);
      } else {
        // Refresh user context after successful signup
        await refreshUser();
        // Redirect to dashboard
        navigate("/dashboard");
      }
    } catch (error) {
      setError(String(error));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="signupContainer">
      <div className="authCard">
        <h1 className="authTitle">Sign Up</h1>
        <form className="authForm" onSubmit={handleSignup}>
          <input
            className="authInput"
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            className="authInput"
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <input
            className="authInput"
            type="text"
            placeholder="Full Name"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            required
          />
          <button className="authButton" type="submit" disabled={loading}>
            {loading ? "Signing up..." : "Sign Up"}
          </button>
        </form>
        {error && <p className="authError">{error}</p>}
        <p className="authFooter">
          Already have an account? <Link to="/login">Log in</Link>
        </p>
      </div>
    </div>
  );
};

export default Signup;
