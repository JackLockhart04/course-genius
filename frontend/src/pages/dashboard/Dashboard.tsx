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
          if (responseData.success) {
            const courseData = responseData.data;
            setCourses(courseData);
          } else {
            // Bad response
            setError("Failed to fetch courses");
          }
        } else {
          // Bad response
          setError("Failed to fetch courses");
        }
      } catch (error) {
        // Handle errors
        setError("Error fetching courses");
      }
    };

    // Call
    fetchCourses();
  }, [user]);

  // Only show loged in users
  if (loading) {
    return <div>Loading...</div>;
  }

  if (!user.loggedIn) {
    return <div>You need to log in to view this page</div>;
  }

  // Add course function
  const addCourse = async () => {
    const courseName = (
      document.getElementById("addCourseName") as HTMLInputElement
    ).value;

    if (courseName === "") {
      setError("Course name cannot be empty");
      return;
    }

    if (courseName.length > 50) {
      setError("Course name cannot be longer than 50 characters");
      return;
    }

    try {
      const response = await fetch(`${apiDomain}/course/add-course`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ courseName: courseName }),
      });

      if (response.ok) {
        const responseData = await response.json();
        if (responseData.success) {
          const courseData = responseData.data;
          setCourses([...courses, courseData]);
          console.log(typeof courseData);
        } else {
          setError(responseData.message);
        }
      } else {
        setError("Failed to add course");
      }
    } catch (error) {
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
