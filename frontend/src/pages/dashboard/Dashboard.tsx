import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useUser } from "../../context/UserContext";
import "./Dashboard.css";

interface Course {
  id: number;
  userId: number;
  name: string;
  creditHours: number;
  gpa: number | null;
  assignmentGroups: AssignmentGroup[];
}

interface AssignmentGroup {
  id: number;
  name: string;
  weight: number;
  assignments: Assignment[];
}

interface Assignment {
  id: number;
  name: string;
  pointsEarned: number;
  pointsPossible: number;
  percentageGrade: number;
}

const Dashboard: React.FC = () => {
  const { user, loading } = useUser();
  const [courses, setCourses] = useState<Course[]>([]);
  const [loadingCourses, setLoadingCourses] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  // Calculate course grade based on assignment groups and their weights
  const calculateCourseGrade = (course: Course): number => {
    let totalGrade = 0;
    let totalWeight = 0;

    course.assignmentGroups.forEach(group => {
      if (group.assignments.length > 0) {
        // Get the average grade for this group
        const groupGrade = group.assignments.reduce((sum, assignment) => sum + assignment.percentageGrade, 0) / group.assignments.length;
        
        // Add weighted grade (group grade * weight percentage)
        totalGrade += groupGrade * (group.weight / 100);
        totalWeight += group.weight / 100;
      }
    });

    // Return the weighted average
    return totalWeight > 0 ? totalGrade : 0;
  };

  useEffect(() => {
    if (!user.loggedIn) {
      return;
    }
    // Fetch data
    const fetchCourses = async () => {
      try {
        const response = await fetch(`${apiDomain}/course/get-courses`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include", // Include cookies in the request
        });
        // Good response
        if (response.ok) {
          const responseData = await response.json();
          if (responseData) {
            setCourses(responseData.courses);
            setLoadingCourses(false);
          }
        }
      } catch (error) {
        // Handle errors
        setError("Error fetching courses");
      }
    };

    // Call
    fetchCourses();
  }, [user, apiDomain]);

  // Add course function
  const addCourse = async () => {
    // Get course name from input field
    const courseName = (
      document.getElementById("addCourseName") as HTMLInputElement
    ).value;
    const creditHoursInput = (
      document.getElementById("addCourseCreditHours") as HTMLInputElement
    ).value;
    const gpaInput = (
      document.getElementById("addCourseGpa") as HTMLInputElement
    ).value;

    // Validation
    if (courseName === "") {
      setError("Course name cannot be empty");
      return;
    }
    if (courseName.length > 50) {
      setError("Course name cannot be longer than 50 characters");
      return;
    }

    // Convert credit hours to number, default to 0 if empty or invalid
    const creditHours = creditHoursInput ? Number(creditHoursInput) : 0;

    // Fetch
    try {
      // Send request to add course
      const response = await fetch(`${apiDomain}/course/add-course`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ 
          courseName: courseName,
          creditHours: creditHours,
          gpa: gpaInput ? Number(gpaInput) : null
        }),
      });

      // Bad response
      if (!response.ok) {
        setError("Failed to add course");
      }

      // Good response
      const responseData = await response.json();
      // Good response but course not added
      if (response.status !== 200) {
        // Course already exists
        if (response.status === 409) {
          setError("Course already exists");
          return;
        }
        // Other error
        setError("Failed to add course");
        return;
      }

      // Course added successfully
      const courseData = responseData.data;
      setCourses([...courses, courseData]);
      // Reload courses by redirecting to dashboard
      window.location.href = "/dashboard";
    } catch (error) {
      // Fetch error
      setError("Error adding course");
    }
  };

  // Only show logged in users
  if (loading) {
    return (
      <div className="dashboardContainer">
        <div className="message">Loading...</div>
      </div>
    );
  }

  if (!user.loggedIn) {
    return (
      <div className="dashboardContainer">
        <div className="message">You need to log in to view this page</div>
      </div>
    );
  }

  if (loadingCourses) {
    return (
      <div className="dashboardContainer">
        <div className="message">Loading courses...</div>
      </div>
    );
  }

  return (
    <div className="dashboardContainer">
      <h1>Dashboard</h1>
      {courses.map((course) => (
        <div key={course.id} className="courseRow">
          <Link to={`/course/${course.id}`} className="courseItem">
            <div className="courseInfo">
              <h3>{course.name}</h3>
              <p>Grade: {calculateCourseGrade(course).toFixed(1)}%</p>
              <p>GPA: {course.gpa !== null && course.gpa !== undefined ? course.gpa.toFixed(2) : 'N/A'}</p>
              <p>Credit Hours: {course.creditHours}</p>
            </div>
          </Link>
        </div>
      ))}
      <div className="courseRow" id="addCourse">
        <input id="addCourseName" type="text" placeholder="Course name" />
        <input id="addCourseCreditHours" type="number" placeholder="Credit hours" min="0" step="0.5" />
        <input id="addCourseGpa" type="number" placeholder="GPA" min="0" step="0.1" />
        <button onClick={addCourse}>Add course</button>
      </div>
      {error && <div className="error">{error}</div>}
    </div>
  );
};

export default Dashboard;
