import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
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
  final_letter_grade?: string;
  final_gpa?: number;
}

interface CourseStats {
  current_avg: number;
  weight_completed: number;
}

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const { user, loading } = useUser();
  const [courses, setCourses] = useState<Course[]>([]);
  const [loadingCourses, setLoadingCourses] = useState(true);
  const [stats, setStats] = useState<Record<string, CourseStats>>({});
  const [showAddForm, setShowAddForm] = useState(false);
  const [name, setName] = useState("");
  const [credits, setCredits] = useState("3.0");
  const [semester, setSemester] = useState("");
  const [colorCode, setColorCode] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  const getCourses = async () => {
    setLoadingCourses(true);
    try {
      const {
        data: { session },
      } = await supabase.auth.getSession();
      const token = session?.access_token || "";

      const response = await fetch(`${apiDomain}/courses/`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setCourses(data);
        // Fetch stats for each course
        data.forEach((course: Course) => {
          getStats(course.id);
        });
      }
    } catch (error) {
      console.error("Error fetching courses:", error);
    } finally {
      setLoadingCourses(false);
    }
  };

  const getStats = async (courseId: string) => {
    try {
      const {
        data: { session },
      } = await supabase.auth.getSession();
      const token = session?.access_token || "";

      const response = await fetch(`${apiDomain}/courses/${courseId}/stats`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setStats((prev) => ({
          ...prev,
          [courseId]: data,
        }));
      }
    } catch (error) {
      console.error("Error fetching stats:", error);
    }
  };

  const createCourse = async () => {
    setSubmitting(true);
    try {
      const {
        data: { session },
      } = await supabase.auth.getSession();
      const token = session?.access_token || "";

      const response = await fetch(`${apiDomain}/courses/`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          name,
          credits: parseFloat(credits),
          semester: semester || undefined,
          color_code: colorCode || undefined,
        }),
      });

      if (response.ok) {
        // Reset form and refresh courses
        setName("");
        setCredits("3.0");
        setSemester("");
        setColorCode("");
        setShowAddForm(false);
        await getCourses();
      }
    } catch (error) {
      console.error("Error creating course:", error);
    } finally {
      setSubmitting(false);
    }
  };

  useEffect(() => {
    if (user.loggedIn) {
      getCourses();
    }
  }, [user.loggedIn]);

  if (loading) {
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

  return (
    <Container maxWidth="lg" sx={{ py: 6 }}>
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={4}
      >
        <Box>
          <Typography variant="h3" component="h1" gutterBottom>
            My Courses
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Welcome back, {user.fullName || user.email}!
          </Typography>
        </Box>
      </Box>

      {loadingCourses ? (
        <Typography variant="body1" color="text.secondary">
          Loading courses...
        </Typography>
      ) : courses.length === 0 ? (
        <Card sx={{ p: 4, textAlign: "center", bgcolor: "background.paper" }}>
          <Typography variant="h6" gutterBottom>
            No courses yet
          </Typography>
          <Typography variant="body2" color="text.secondary" mb={2}>
            Get started by adding your first course
          </Typography>
        </Card>
      ) : (
        <Box
          display="grid"
          gap={3}
          gridTemplateColumns={{
            xs: "1fr",
            sm: "repeat(2, 1fr)",
            md: "repeat(3, 1fr)",
          }}
        >
          {courses.map((course) => (
            <Card
              key={course.id}
              onClick={() => navigate(`/courses/${course.id}`)}
              sx={{
                height: "100%",
                bgcolor: "background.paper",
                borderLeft: course.color_code
                  ? `4px solid ${course.color_code}`
                  : undefined,
                transition: "transform 0.2s, box-shadow 0.2s",
                cursor: "pointer",
                "&:hover": {
                  transform: "translateY(-4px)",
                  boxShadow: 6,
                },
              }}
            >
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {course.name}
                </Typography>
                <Stack spacing={1}>
                  <Box display="flex" gap={1} flexWrap="wrap">
                    <Typography variant="body2" fontSize="0.875rem">
                      {course.credits} credits
                    </Typography>
                    {course.semester && (
                      <Typography variant="body2" fontSize="0.875rem">
                        {course.semester}
                      </Typography>
                    )}
                    {course.final_letter_grade && (
                      <Typography variant="body2" fontSize="0.875rem">
                        Grade: {course.final_letter_grade}
                        {course.final_gpa &&
                          ` (${course.final_gpa.toFixed(2)})`}
                      </Typography>
                    )}
                  </Box>
                  <Typography variant="caption" color="text.secondary">
                    Added {new Date(course.created_at).toLocaleDateString()}
                  </Typography>
                  {stats[course.id] && (
                    <Box
                      sx={{
                        mt: 1,
                        pt: 1,
                        borderTop: "1px solid",
                        borderColor: "divider",
                      }}
                    >
                      <Typography variant="caption" color="text.secondary">
                        Average: {stats[course.id].current_avg.toFixed(2)}%
                      </Typography>
                      <Typography
                        variant="caption"
                        color="text.secondary"
                        display="block"
                      >
                        Weight Completed:{" "}
                        {stats[course.id].weight_completed.toFixed(2)}%
                      </Typography>
                    </Box>
                  )}
                </Stack>
              </CardContent>
            </Card>
          ))}
        </Box>
      )}

      <Box mt={4}>
        <Collapse in={showAddForm}>
          <Card sx={{ p: 3, mb: 2, bgcolor: "background.paper" }}>
            <Box
              display="flex"
              justifyContent="space-between"
              alignItems="center"
              mb={2}
            >
              <Typography variant="h6">Add New Course</Typography>
              <IconButton onClick={() => setShowAddForm(false)} size="small">
                ✕
              </IconButton>
            </Box>
            <Stack spacing={2}>
              <TextField
                label="Course Name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                fullWidth
                required
              />
              <TextField
                label="Credits"
                type="number"
                value={credits}
                onChange={(e) => setCredits(e.target.value)}
                fullWidth
                inputProps={{ step: "0.5", min: "0" }}
              />
              <TextField
                label="Semester (optional)"
                value={semester}
                onChange={(e) => setSemester(e.target.value)}
                fullWidth
                placeholder="e.g., Spring 2024"
              />
              <TextField
                label="Color Code (optional)"
                value={colorCode}
                onChange={(e) => setColorCode(e.target.value)}
                fullWidth
                placeholder="#3b82f6"
              />
              <Button
                variant="contained"
                onClick={createCourse}
                disabled={!name || submitting}
                fullWidth
                size="large"
              >
                {submitting ? "Adding..." : "Add Course"}
              </Button>
            </Stack>
          </Card>
        </Collapse>

        {!showAddForm && (
          <Button
            variant="contained"
            onClick={() => setShowAddForm(true)}
            fullWidth
            size="large"
          >
            + Add New Course
          </Button>
        )}
      </Box>
    </Container>
  );
};

export default Dashboard;
