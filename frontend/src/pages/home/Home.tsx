import "./Home.css";
import { useUser } from "../../context/UserContext";
import LogoutButton from "../../components/LogoutButton";

const Home: React.FC = () => {
  const { user, loading } = useUser();

  return (
    <div className="homeContainer">
      <header className="homeHeader">
        <h1>Welcome to Course Genius</h1>
        <p>Your personal assistant to track assignments and grades</p>

        {loading ? (
          <p>Loading...</p>
        ) : user.email ? (
          <div>
            <p>Logged in as: {user.email}</p>
            <LogoutButton />
          </div>
        ) : (
          <p>Not logged in</p>
        )}
      </header>
      {/* Signup flow moved to dedicated page */}
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
