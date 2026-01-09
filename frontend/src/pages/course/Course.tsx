import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useUser } from "../../context/UserContext";
import { supabase } from "../../supabaseClient";
import {
  Box,
  Button,
  Card,
  CardContent,
  CardActionArea,
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
  final_letter_grade?: string;
  final_gpa?: number;
}

interface Assignment {
  id: string;
  title: string;
  weight?: number;
  max_score?: number;
  due_date?: string;
  created_at: string;
  score_achieved?: number | null;
}

const Course: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user, loading: userLoading } = useUser();
  const [course, setCourse] = useState<Course | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [editing, setEditing] = useState(false);
  const [submittingEdit, setSubmittingEdit] = useState(false);
  const [editName, setEditName] = useState("");
  const [editCredits, setEditCredits] = useState("");
  const [editSemester, setEditSemester] = useState("");
  const [editColorCode, setEditColorCode] = useState("");
  const [editLetterGrade, setEditLetterGrade] = useState("");
  const [editGpa, setEditGpa] = useState("");
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [loadingAssignments, setLoadingAssignments] = useState(false);
  const [showAddAssignment, setShowAddAssignment] = useState(false);
  const [assignmentName, setAssignmentName] = useState("");
  const [pointsEarned, setPointsEarned] = useState("");
  const [pointsPossible, setPointsPossible] = useState("");
  const [scoreAchieved, setScoreAchieved] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [submittingAssignment, setSubmittingAssignment] = useState(false);
  const [stats, setStats] = useState<{
    current_avg: number;
    weight_completed: number;
  } | null>(null);
  const [loadingStats, setLoadingStats] = useState(false);

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  const handleAssignmentClick = (assignmentId: string) => {
    if (!id) return;
    navigate(`/courses/${id}/assignments/${assignmentId}`);
  };

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

  const getStats = async () => {
    if (!id) return;
    setLoadingStats(true);
    try {
      const {
        data: { session },
      } = await supabase.auth.getSession();
      const token = session?.access_token || "";

      const response = await fetch(`${apiDomain}/courses/${id}/stats`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setStats(data);
      }
    } catch (err) {
      console.error("Error fetching stats:", err);
    } finally {
      setLoadingStats(false);
    }
  };

  const updateCourse = async () => {
    setSubmittingEdit(true);
    try {
      const payload: Record<string, unknown> = {};
      if (editName.trim().length > 0) {
        payload.name = editName.trim();
      }
      if (editCredits !== "") {
        payload.credits = parseFloat(editCredits);
      }
      if (editSemester !== "") {
        payload.semester = editSemester.trim();
      }
      if (editColorCode !== "") {
        payload.color_code = editColorCode.trim();
      }
      if (editLetterGrade !== "") {
        payload.final_letter_grade = editLetterGrade.trim();
      }
      if (editGpa !== "") {
        payload.final_gpa = parseFloat(editGpa);
      }

      if (Object.keys(payload).length === 0) {
        setSubmittingEdit(false);
        setEditing(false);
        return;
      }

      const {
        data: { session },
      } = await supabase.auth.getSession();
      const token = session?.access_token || "";

      const response = await fetch(`${apiDomain}/courses/${id}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        const data = await response.json();
        setCourse(data);
        setEditing(false);
      } else {
        setError("Failed to update course");
      }
    } catch (err) {
      console.error("Error updating course:", err);
      setError("Failed to update course");
    } finally {
      setSubmittingEdit(false);
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
          score_achieved: scoreAchieved ? parseFloat(scoreAchieved) : undefined,
          due_date: dueDate || undefined,
        }),
      });

      if (response.ok) {
        setAssignmentName("");
        setPointsEarned("");
        setPointsPossible("");
        setScoreAchieved("");
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
            getStats();
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
            <Collapse in={!editing}>
              <Box>
                <Typography variant="h3" component="h1" gutterBottom>
                  {course.name}
                </Typography>
                <Box display="flex" gap={1} flexWrap="wrap" mt={2}>
                  <Typography variant="body2">
                    {course.credits} credits
                  </Typography>
                  {course.semester && (
                    <Typography variant="body2">{course.semester}</Typography>
                  )}
                  {course.final_letter_grade && (
                    <Typography variant="body2">
                      Grade: {course.final_letter_grade}
                      {course.final_gpa && ` (${course.final_gpa.toFixed(2)})`}
                    </Typography>
                  )}
                </Box>
              </Box>
            </Collapse>
            <Collapse in={editing}>
              <Stack spacing={2} mb={2}>
                <TextField
                  label="Course Name"
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  fullWidth
                />
                <TextField
                  label="Credits"
                  type="number"
                  value={editCredits}
                  onChange={(e) => setEditCredits(e.target.value)}
                  fullWidth
                  inputProps={{ step: "0.5", min: "0" }}
                />
                <TextField
                  label="Semester"
                  value={editSemester}
                  onChange={(e) => setEditSemester(e.target.value)}
                  fullWidth
                />
                <TextField
                  label="Color Code (hex)"
                  value={editColorCode}
                  onChange={(e) => setEditColorCode(e.target.value)}
                  fullWidth
                  placeholder="#FF5733"
                />
                <TextField
                  label="Final Letter Grade"
                  value={editLetterGrade}
                  onChange={(e) => setEditLetterGrade(e.target.value)}
                  fullWidth
                  placeholder="A, B+, Pass, etc."
                />
                <TextField
                  label="Final GPA"
                  type="number"
                  value={editGpa}
                  onChange={(e) => setEditGpa(e.target.value)}
                  fullWidth
                  inputProps={{ step: "0.1", min: "0", max: "4.0" }}
                />
                <Stack direction="row" gap={2}>
                  <Button
                    variant="contained"
                    onClick={updateCourse}
                    disabled={submittingEdit}
                    sx={{ flex: 1 }}
                  >
                    {submittingEdit ? "Saving..." : "Save Changes"}
                  </Button>
                  <Button
                    variant="outlined"
                    onClick={() => setEditing(false)}
                    disabled={submittingEdit}
                  >
                    Cancel
                  </Button>
                </Stack>
              </Stack>
            </Collapse>
          </Box>

          <Box>
            <Typography variant="body2" color="text.secondary">
              Created: {new Date(course.created_at).toLocaleString()}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Course ID: {course.id}
            </Typography>
          </Box>

          {stats && (
            <Card sx={{ p: 3, bgcolor: "background.default" }}>
              <Typography variant="h6" gutterBottom>
                Course Stats
              </Typography>
              {loadingStats ? (
                <Typography variant="body2" color="text.secondary">
                  Loading stats...
                </Typography>
              ) : (
                <Stack spacing={2}>
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      Current Average
                    </Typography>
                    <Typography variant="h5">
                      {stats.current_avg.toFixed(2)}%
                    </Typography>
                  </Box>
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      Weight Completed
                    </Typography>
                    <Typography variant="h5">
                      {stats.weight_completed.toFixed(2)}%
                    </Typography>
                  </Box>
                </Stack>
              )}
            </Card>
          )}

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
                    sx={{ bgcolor: "background.paper", height: "100%" }}
                  >
                    <CardActionArea
                      onClick={() => handleAssignmentClick(assignment.id)}
                      sx={{ p: 2, height: "100%", textAlign: "left" }}
                    >
                      <Typography variant="subtitle2" gutterBottom>
                        {assignment.title}
                      </Typography>
                      {assignment.max_score !== undefined && (
                        <Typography variant="caption" color="text.secondary">
                          {assignment.score_achieved !== null &&
                          assignment.score_achieved !== undefined
                            ? `${assignment.score_achieved} / ${assignment.max_score}`
                            : "No grade"}{" "}
                          {assignment.max_score > 0 ? "points" : ""}
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
                    </CardActionArea>
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
                    label="Score Achieved (optional)"
                    type="number"
                    value={scoreAchieved}
                    onChange={(e) => setScoreAchieved(e.target.value)}
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
            {!editing && (
              <Button
                variant="outlined"
                onClick={() => {
                  setEditName(course.name);
                  setEditCredits(course.credits.toString());
                  setEditSemester(course.semester || "");
                  setEditColorCode(course.color_code || "");
                  setEditLetterGrade(course.final_letter_grade || "");
                  setEditGpa(course.final_gpa?.toString() || "");
                  setEditing(true);
                }}
              >
                Edit Course
              </Button>
            )}
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
