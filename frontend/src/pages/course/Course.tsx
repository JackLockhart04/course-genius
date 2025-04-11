import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";

import "./Course.css";

interface Assignment {
  id: number;
  name: string;
  weight: number;
  grade: number | null;
}

interface Course {
  id: number;
  name: string;
  assignments: Assignment[];
}

const Course: React.FC = () => {
  const { courseId } = useParams<{ courseId: string }>();
  const [course, setCourse] = useState<Course | null>(null);
  const [error, setError] = useState<string | null>(null);

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  // Fetch courses on load
  useEffect(() => {
    // Get data
    const fetchCourse = async () => {
      try {
        const response = await fetch(
          `${apiDomain}/course/get-course?courseId=${courseId}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
            },
            credentials: "include", // Include cookies in the request
          }
        );
        // If good response
        if (response.ok) {
          const data = await response.json();
          setCourse(data.course);
        } else {
          // bad response
          setError("Failed to fetch course");
        }
      } catch (error) {
        // Handle errors
        setError("Error fetching course");
      }
    };
    // Call
    fetchCourse();
  }, [courseId]);

  // Add course handler
  const addAssignment = async () => {
    try {
      const response = await fetch(`${apiDomain}/assignment/add-assignment`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // Include cookies in the request
        body: JSON.stringify({ courseId }),
      });

      if (response.ok) {
        // Redirect to dashboard
        window.location.href = "/dashboard";
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to add assignment");
      }
    } catch (error) {
      setError("Error adding assignment");
    }
  };

  // Delete course handler
  const deleteCourse = async () => {
    try {
      const response = await fetch(`${apiDomain}/course/delete-course`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // Include cookies in the request
        body: JSON.stringify({ courseId }),
      });

      if (response.ok) {
        // Redirect to dashboard
        window.location.href = "/dashboard";
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to delete course");
      }
    } catch (error) {
      setError("Error deleting course");
    }
  };

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!course) {
    return <div>Loading...</div>;
  }

  return (
    <div className="courseContainer">
      <h1>Course ID: {course.id}</h1>
      <h2>Course Name: {course.name}</h2>
      <h3>Assignments:</h3>
      {course.assignments.length > 0 ? (
        course.assignments.map((assignment) => (
          <div key={assignment.id} className="assignmentRow">
            <Link
              to={`/assignment/${assignment.id}`}
              className="assignmentItem"
            >
              {assignment.name}, Weight: {assignment.weight}%, Grade:{" "}
              {assignment.grade ?? "N/A"}
            </Link>
          </div>
        ))
      ) : (
        <p>No assignments found</p>
      )}
      <div className="assignmentRow" id="addAssignment">
        <input
          id="addAssignmentName"
          type="text"
          placeholder="Assignment name"
        />
        <input
          id="addAssignmentWeight"
          type="number"
          placeholder="Weight (%)"
        />
        <input id="addAssignmentGrade" type="number" placeholder="Grade (%)" />
        <button onClick={addAssignment}>Add Assignment</button>
      </div>
      <button onClick={deleteCourse} className="deleteCourseButton">
        Delete Course
      </button>
      {error && <div className="error">{error}</div>}
    </div>
  );
};

export default Course;
