import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useUser } from "../../context/UserContext";
import { supabase } from "../../supabaseClient";
import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Container,
  Typography,
  Stack,
} from "@mui/material";

interface Course {
  id: string;
  name: string;
  credits: number;
  semester?: string;
  color_code?: string;
  created_at: string;
}

const Course: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user, loading: userLoading } = useUser();
  const [course, setCourse] = useState<Course | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  const deleteCourse = async () => {
    if (
      !window.confirm(
        `Are you sure you want to delete "${course?.name}"? This cannot be undone.`
      )
    ) {
      return;
    }

    setDeleting(true);
    try {
      const {
        data: { session },
      } = await supabase.auth.getSession();
      const token = session?.access_token || "";

      const response = await fetch(`${apiDomain}/courses/${id}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        navigate("/dashboard");
      } else {
        setError("Failed to delete course");
      }
    } catch (err) {
      console.error("Error deleting course:", err);
      setError("Failed to delete course");
    } finally {
      setDeleting(false);
    }
  };

  useEffect(() => {
    const getCourse = async () => {
      if (!id || !user.loggedIn) return;

      setLoading(true);
      setError(null);
      try {
        const {
          data: { session },
        } = await supabase.auth.getSession();
        const token = session?.access_token || "";

        const response = await fetch(`${apiDomain}/courses/${id}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setCourse(data);
        } else if (response.status === 404) {
          setError("Course not found");
        } else {
          setError("Failed to load course");
        }
      } catch (err) {
        console.error("Error fetching course:", err);
        setError("Failed to load course");
      } finally {
        setLoading(false);
      }
    };

    if (user.loggedIn) {
      getCourse();
    }
  }, [id, user.loggedIn, apiDomain]);

  if (userLoading) {
    return (
      <Container maxWidth="md" sx={{ py: 6 }}>
        <Typography variant="body1" color="text.secondary">
          Loading...
        </Typography>
      </Container>
    );
  }

  if (!user.loggedIn) {
    return (
      <Container maxWidth="md" sx={{ py: 6 }}>
        <Typography variant="body1" color="text.secondary">
          You need to log in to view this page
        </Typography>
      </Container>
    );
  }

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 6 }}>
        <Typography variant="body1" color="text.secondary">
          Loading course...
        </Typography>
      </Container>
    );
  }

  if (error || !course) {
    return (
      <Container maxWidth="lg" sx={{ py: 6 }}>
        <Card sx={{ p: 4, textAlign: "center" }}>
          <Typography variant="h6" color="error" gutterBottom>
            {error || "Course not found"}
          </Typography>
          <Button
            variant="contained"
            onClick={() => navigate("/dashboard")}
            sx={{ mt: 2 }}
          >
            Back to Dashboard
          </Button>
        </Card>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 6 }}>
      <Button
        variant="outlined"
        onClick={() => navigate("/dashboard")}
        sx={{ mb: 3 }}
      >
        ← Back to Dashboard
      </Button>

      <Card
        sx={{
          p: 4,
          bgcolor: "background.paper",
          borderLeft: course.color_code
            ? `6px solid ${course.color_code}`
            : undefined,
        }}
      >
        <Stack spacing={3}>
          <Box>
            <Typography variant="h3" component="h1" gutterBottom>
              {course.name}
            </Typography>
            <Box display="flex" gap={1} flexWrap="wrap" mt={2}>
              <Chip label={`${course.credits} credits`} />
              {course.semester && (
                <Chip label={course.semester} variant="outlined" />
              )}
            </Box>
          </Box>

          <Box>
            <Typography variant="body2" color="text.secondary">
              Created: {new Date(course.created_at).toLocaleString()}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Course ID: {course.id}
            </Typography>
          </Box>

          <Card sx={{ p: 3, bgcolor: "background.default" }}>
            <Typography variant="h6" gutterBottom>
              Course Details
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Additional course content and features coming soon...
            </Typography>
          </Card>

          <Box display="flex" gap={2}>
            <Button
              variant="contained"
              color="error"
              onClick={deleteCourse}
              disabled={deleting}
            >
              {deleting ? "Deleting..." : "Delete Course"}
            </Button>
          </Box>
        </Stack>
      </Card>
    </Container>
  );
};

export default Course;
