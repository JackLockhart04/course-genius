import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useUser } from "../../context/UserContext";
import "./Dashboard.css";

interface Course {
  id: number;
  name: string;
}

const Dashboard: React.FC = () => {
  const { user, loading } = useUser();
  const [courses, setCourses] = useState<Course[]>([]);
  const [loadingCourses, setLoadingCourses] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  useEffect(() => {
    if (!user.loggedIn) {
      return;
    }
    // Fetch data
    const fetchCourses = async () => {
      try {
        const response = await fetch(`${apiDomain}/course/get-all-courses`, {
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

  // Add course function
  const addCourse = async () => {
    // Get course name from input field
    const courseName = (
      document.getElementById("addCourseName") as HTMLInputElement
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

    // Fetch
    try {
      // Send request to add course
      const response = await fetch(`${apiDomain}/course/add-course`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ courseName: courseName }),
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

  return (
    <div className="dashboardContainer">
      <h1>Dashboard</h1>
      {courses.map((course) => (
        <div key={course.id} className="courseRow">
          <Link to={`/course/${course.id}`} className="courseItem">
            {course.name}, id: {course.id}
          </Link>
        </div>
      ))}
      <div className="courseRow" id="addCourse">
        <input id="addCourseName" type="text" placeholder="Course name" />
        <button onClick={addCourse}>Add course</button>
      </div>
      {error && <div className="error">{error}</div>}
    </div>
  );
};

export default Dashboard;
