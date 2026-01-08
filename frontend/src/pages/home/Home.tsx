import "./Home.css";
import { Link as RouterLink } from "react-router-dom";
import { Box, Button, Container, Stack, Typography } from "@mui/material";
import { useUser } from "../../context/UserContext";
import LogoutButton from "../../components/LogoutButton";

const Home: React.FC = () => {
  const { user, loading } = useUser();

  return (
    <Box className="homeBackground">
      <Container maxWidth="md" sx={{ py: { xs: 6, md: 10 } }}>
        <Stack spacing={3} alignItems="center" textAlign="center">
          <Typography variant="h3" component="h1">
            Welcome to Course Genius
          </Typography>
          <Typography
            variant="body1"
            color="text.secondary"
            sx={{ maxWidth: 640 }}
          >
            Your personal assistant to track assignments and grades.
          </Typography>

          {loading ? (
            <Typography variant="body2" color="text.secondary">
              Checking your session...
            </Typography>
          ) : user.email ? (
            <Stack spacing={2} alignItems="center">
              <Typography variant="body1">
                Hello {user.fullName || user.email}
              </Typography>
              <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                <Button
                  component={RouterLink}
                  to="/dashboard"
                  variant="contained"
                  size="large"
                >
                  Go to Dashboard
                </Button>
                <Button
                  component={RouterLink}
                  to="/account"
                  variant="outlined"
                  size="large"
                >
                  Account
                </Button>
                <LogoutButton />
              </Stack>
            </Stack>
          ) : (
            <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
              <Button
                component={RouterLink}
                to="/signup"
                variant="contained"
                size="large"
              >
                Get Started
              </Button>
              <Button
                component={RouterLink}
                to="/login"
                variant="outlined"
                size="large"
              >
                Log In
              </Button>
            </Stack>
          )}
        </Stack>
      </Container>
    </Box>
  );
};

export default Home;
