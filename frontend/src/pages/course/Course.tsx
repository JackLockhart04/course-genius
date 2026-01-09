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
  Collapse,
  Container,
  IconButton,
  TextField,
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

interface Assignment {
  id: string;
  title: string;
  weight?: number;
  max_score?: number;
  due_date?: string;
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
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [loadingAssignments, setLoadingAssignments] = useState(false);
  const [showAddAssignment, setShowAddAssignment] = useState(false);
  const [assignmentName, setAssignmentName] = useState("");
  const [pointsEarned, setPointsEarned] = useState("");
  const [pointsPossible, setPointsPossible] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [submittingAssignment, setSubmittingAssignment] = useState(false);

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  const getAssignments = async () => {
    if (!id) return;
    setLoadingAssignments(true);
    try {
      const {
        data: { session },
      } = await supabase.auth.getSession();
      const token = session?.access_token || "";

      const response = await fetch(`${apiDomain}/courses/${id}/assignments`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setAssignments(data);
      }
    } catch (err) {
      console.error("Error fetching assignments:", err);
    } finally {
      setLoadingAssignments(false);
    }
  };

  const createAssignment = async () => {
    if (!assignmentName) return;

    setSubmittingAssignment(true);
    try {
      const {
        data: { session },
      } = await supabase.auth.getSession();
      const token = session?.access_token || "";

      const response = await fetch(`${apiDomain}/courses/${id}/assignments`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          title: assignmentName,
          weight: pointsEarned ? parseFloat(pointsEarned) : undefined,
          max_score: pointsPossible ? parseFloat(pointsPossible) : undefined,
          due_date: dueDate || undefined,
        }),
      });

      if (response.ok) {
        setAssignmentName("");
        setPointsEarned("");
        setPointsPossible("");
        setDueDate("");
        setShowAddAssignment(false);
        await getAssignments();
      }
    } catch (err) {
      console.error("Error creating assignment:", err);
    } finally {
      setSubmittingAssignment(false);
    }
  };

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
          // Fetch assignments after loading course
          setTimeout(() => {
            getAssignments();
          }, 100);
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
              <Typography variant="body2">{course.credits} credits</Typography>
              {course.semester && (
                <Typography variant="body2">{course.semester}</Typography>
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
              Assignments
            </Typography>
            {loadingAssignments ? (
              <Typography variant="body2" color="text.secondary">
                Loading assignments...
              </Typography>
            ) : assignments.length === 0 ? (
              <Typography variant="body2" color="text.secondary">
                No assignments yet. Add one to get started!
              </Typography>
            ) : (
              <Box
                display="grid"
                gap={2}
                gridTemplateColumns={{
                  xs: "1fr",
                  sm: "repeat(2, 1fr)",
                }}
                mt={2}
              >
                {assignments.map((assignment) => (
                  <Card
                    key={assignment.id}
                    sx={{ p: 2, bgcolor: "background.paper", height: "100%" }}
                  >
                    <Typography variant="subtitle2" gutterBottom>
                      {assignment.title}
                    </Typography>
                    {assignment.max_score && (
                      <Typography variant="caption" color="text.secondary">
                        {assignment.weight ?? 0} / {assignment.max_score} points
                      </Typography>
                    )}
                    {assignment.due_date && (
                      <Typography
                        variant="caption"
                        color="text.secondary"
                        display="block"
                      >
                        Due:{" "}
                        {new Date(assignment.due_date).toLocaleDateString()}
                      </Typography>
                    )}
                  </Card>
                ))}
              </Box>
            )}
          </Card>

          <Box mt={3}>
            <Collapse in={showAddAssignment}>
              <Card sx={{ p: 3, mb: 2, bgcolor: "background.paper" }}>
                <Box
                  display="flex"
                  justifyContent="space-between"
                  alignItems="center"
                  mb={2}
                >
                  <Typography variant="h6">Add New Assignment</Typography>
                  <IconButton
                    onClick={() => setShowAddAssignment(false)}
                    size="small"
                  >
                    ✕
                  </IconButton>
                </Box>
                <Stack spacing={2}>
                  <TextField
                    label="Assignment Title"
                    value={assignmentName}
                    onChange={(e) => setAssignmentName(e.target.value)}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Weight (optional)"
                    type="number"
                    value={pointsEarned}
                    onChange={(e) => setPointsEarned(e.target.value)}
                    fullWidth
                    inputProps={{ step: "0.1", min: "0" }}
                  />
                  <TextField
                    label="Max Score (optional)"
                    type="number"
                    value={pointsPossible}
                    onChange={(e) => setPointsPossible(e.target.value)}
                    fullWidth
                    inputProps={{ step: "0.1", min: "0" }}
                  />
                  <TextField
                    label="Due Date (optional)"
                    type="date"
                    value={dueDate}
                    onChange={(e) => setDueDate(e.target.value)}
                    fullWidth
                    InputLabelProps={{ shrink: true }}
                  />
                  <Button
                    variant="contained"
                    onClick={createAssignment}
                    disabled={!assignmentName || submittingAssignment}
                    fullWidth
                  >
                    {submittingAssignment ? "Adding..." : "Add Assignment"}
                  </Button>
                </Stack>
              </Card>
            </Collapse>

            {!showAddAssignment && (
              <Button
                variant="contained"
                onClick={() => setShowAddAssignment(true)}
                fullWidth
              >
                + Add Assignment
              </Button>
            )}
          </Box>

          <Box
            display="flex"
            gap={2}
            pt={3}
            borderTop="1px solid"
            borderColor="divider"
          >
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
