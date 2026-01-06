import "./Home.css";

import { useUser } from "../../context/UserContext";

const Home: React.FC = () => {
  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  const login = () => {
    window.location.href = apiDomain + "/auth/login";
  };

  const { user, loading } = useUser();

  return (
    <div className="homeContainer">
      <header className="homeHeader">
        <h1>Welcome to Course Genius</h1>
        <p>Your personal assistant to track assignments and grades</p>

        {loading ? (
          <p>Loading...</p>
        ) : user.email ? (
          <p>Logged in as: {user.email}</p>
        ) : (
          <p>Not logged in</p>
        )}

        <button onClick={login}>Login with Microsoft</button>
      </header>
      <section className="homeContent">
        <div className="homeFeature">
          <h2>Track Assignments</h2>
          <p>Keep track of all your assignments and their due dates.</p>
        </div>
        <div className="homeFeature">
          {/* Existing content continues here */}
        </div>
      </section>
    </div>
  );
};

export default Home;
