import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

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

  useEffect(() => {
    // Get data
    const fetchCourse = async () => {
      try {
        const response = await fetch(
          `${apiDomain}/course/get-course/${courseId}`,
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
          // good backend response
          if (data.success) {
            setCourse(data.data);
          } else {
            // bad backend response
            setError(data.message);
          }
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
            <span className="assignmentItem">
              {assignment.name}, Weight: {assignment.weight}%, Grade:{" "}
              {assignment.grade ?? "N/A"}
            </span>
          </div>
        ))
      ) : (
        <p>No assignments found</p>
      )}
    </div>
  );
};

export default Course;
