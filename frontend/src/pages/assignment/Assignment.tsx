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

interface Assignment {
  id: string;
  title: string;
  weight?: number;
  max_score?: number;
  due_date?: string;
  created_at: string;
  course_id: string;
}

const Assignment: React.FC = () => {
  const { courseId, assignmentId } = useParams<{
    courseId: string;
    assignmentId: string;
  }>();
  const navigate = useNavigate();
  const { user, loading: userLoading } = useUser();
  const [assignment, setAssignment] = useState<Assignment | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [editing, setEditing] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [editName, setEditName] = useState("");
  const [editPointsEarned, setEditPointsEarned] = useState("");
  const [editPointsPossible, setEditPointsPossible] = useState("");
  const [editDueDate, setEditDueDate] = useState("");

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  const deleteAssignment = async () => {
    if (
      !window.confirm(
        `Are you sure you want to delete "${assignment?.title}"? This cannot be undone.`
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

      const response = await fetch(
        `${apiDomain}/courses/${courseId}/assignments/${assignmentId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        navigate(`/courses/${courseId}`);
      } else {
        setError("Failed to delete assignment");
      }
    } catch (err) {
      console.error("Error deleting assignment:", err);
      setError("Failed to delete assignment");
    } finally {
      setDeleting(false);
    }
  };

  const updateAssignment = async () => {
    setSubmitting(true);
    try {
      const {
        data: { session },
      } = await supabase.auth.getSession();
      const token = session?.access_token || "";

      const response = await fetch(
        `${apiDomain}/courses/${courseId}/assignments/${assignmentId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            title: editName,
            weight: editPointsEarned ? parseFloat(editPointsEarned) : undefined,
            max_score: editPointsPossible
              ? parseFloat(editPointsPossible)
              : undefined,
            due_date: editDueDate || undefined,
          }),
        }
      );

      if (response.ok) {
        const data = await response.json();
        setAssignment(data);
        setEditing(false);
      } else {
        setError("Failed to update assignment");
      }
    } catch (err) {
      console.error("Error updating assignment:", err);
      setError("Failed to update assignment");
    } finally {
      setSubmitting(false);
    }
  };

  useEffect(() => {
    const getAssignment = async () => {
      if (!assignmentId || !courseId || !user.loggedIn) return;

      setLoading(true);
      setError(null);
      try {
        const {
          data: { session },
        } = await supabase.auth.getSession();
        const token = session?.access_token || "";

        const response = await fetch(
          `${apiDomain}/courses/${courseId}/assignments/${assignmentId}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (response.ok) {
          const data = await response.json();
          setAssignment(data);
          setEditName(data.name);
          setEditPointsEarned(data.points_earned?.toString() || "");
          setEditPointsPossible(data.points_possible?.toString() || "");
          setEditDueDate(data.due_date || "");
        } else if (response.status === 404) {
          setError("Assignment not found");
        } else {
          setError("Failed to load assignment");
        }
      } catch (err) {
        console.error("Error fetching assignment:", err);
        setError("Failed to load assignment");
      } finally {
        setLoading(false);
      }
    };

    if (user.loggedIn) {
      getAssignment();
    }
  }, [assignmentId, courseId, user.loggedIn, apiDomain]);

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
          Loading assignment...
        </Typography>
      </Container>
    );
  }

  if (error || !assignment) {
    return (
      <Container maxWidth="lg" sx={{ py: 6 }}>
        <Card sx={{ p: 4, textAlign: "center" }}>
          <Typography variant="h6" color="error" gutterBottom>
            {error || "Assignment not found"}
          </Typography>
          <Button
            variant="contained"
            onClick={() => navigate(`/courses/${courseId}`)}
            sx={{ mt: 2 }}
          >
            Back to Course
          </Button>
        </Card>
      </Container>
    );
  }

  const percentage =
    assignment.max_score && assignment.weight !== undefined
      ? ((assignment.weight / assignment.max_score) * 100).toFixed(1)
      : null;

  return (
    <Container maxWidth="lg" sx={{ py: 6 }}>
      <Button
        variant="outlined"
        onClick={() => navigate(`/courses/${courseId}`)}
        sx={{ mb: 3 }}
      >
        ← Back to Course
      </Button>

      <Card sx={{ p: 4, bgcolor: "background.paper" }}>
        <Stack spacing={3}>
          <Box>
            <Typography variant="h3" component="h1" gutterBottom>
              {assignment.title}
            </Typography>
            {assignment.max_score && assignment.weight !== undefined && (
              <Box display="flex" gap={2} mt={2}>
                <Chip
                  label={`${assignment.weight} / ${assignment.max_score} points`}
                />
                <Chip label={`${percentage}%`} />
              </Box>
            )}
          </Box>

          <Box>
            {assignment.due_date && (
              <Typography variant="body2" color="text.secondary">
                Due: {new Date(assignment.due_date).toLocaleString()}
              </Typography>
            )}
            <Typography variant="body2" color="text.secondary">
              Created: {new Date(assignment.created_at).toLocaleString()}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Assignment ID: {assignment.id}
            </Typography>
          </Box>

          <Card sx={{ p: 3, bgcolor: "background.default" }}>
            <Collapse in={editing}>
              <Stack spacing={2} mb={2}>
                <TextField
                  label="Assignment Title"
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  fullWidth
                />
                <TextField
                  label="Weight"
                  type="number"
                  value={editPointsEarned}
                  onChange={(e) => setEditPointsEarned(e.target.value)}
                  fullWidth
                  inputProps={{ step: "0.1", min: "0" }}
                />
                <TextField
                  label="Max Score"
                  type="number"
                  value={editPointsPossible}
                  onChange={(e) => setEditPointsPossible(e.target.value)}
                  fullWidth
                  inputProps={{ step: "0.1", min: "0" }}
                />
                <TextField
                  label="Due Date"
                  type="date"
                  value={editDueDate}
                  onChange={(e) => setEditDueDate(e.target.value)}
                  fullWidth
                  InputLabelProps={{ shrink: true }}
                />
                <Stack direction="row" gap={2}>
                  <Button
                    variant="contained"
                    onClick={updateAssignment}
                    disabled={submitting}
                    sx={{ flex: 1 }}
                  >
                    {submitting ? "Saving..." : "Save Changes"}
                  </Button>
                  <Button
                    variant="outlined"
                    onClick={() => setEditing(false)}
                    disabled={submitting}
                  >
                    Cancel
                  </Button>
                </Stack>
              </Stack>
            </Collapse>

            {!editing && (
              <Stack spacing={2}>
                <Typography variant="h6" gutterBottom>
                  Assignment Details
                </Typography>
                <Box>
                  {assignment.max_score !== undefined && (
                    <Typography variant="body2">
                      Score: {assignment.weight ?? 0} /{assignment.max_score}
                    </Typography>
                  )}
                  {assignment.due_date && (
                    <Typography variant="body2">
                      Due: {new Date(assignment.due_date).toLocaleDateString()}
                    </Typography>
                  )}
                </Box>
                <Button
                  variant="outlined"
                  onClick={() => setEditing(true)}
                  fullWidth
                >
                  Edit Assignment
                </Button>
              </Stack>
            )}
          </Card>

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
              onClick={deleteAssignment}
              disabled={deleting}
            >
              {deleting ? "Deleting..." : "Delete Assignment"}
            </Button>
          </Box>
        </Stack>
      </Card>
    </Container>
  );
};

export default Assignment;
