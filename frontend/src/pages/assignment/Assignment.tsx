import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import "./Assignment.css";

interface Assignment {
  id: number;
  name: string;
  weight: number;
  grade: number | null;
}

const Assignment: React.FC = () => {
  const { assignmentId } = useParams<{ assignmentId: string }>();
  const [assignment, setAssignment] = useState<Assignment | null>(null);
  const [error, setError] = useState<string | null>(null);

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  useEffect(() => {
    // Get data
    const fetchAssignment = async () => {
      try {
        const response = await fetch(
          `${apiDomain}/assignment/get-assignment?assignmentId=${assignmentId}`,
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
          setAssignment(data.assignment);
        } else {
          // bad response
          setError("Failed to fetch assignment");
        }
      } catch (error) {
        // Handle errors
        setError("Error fetching assignment");
      }
    };
    // Call
    fetchAssignment();
  }, [assignmentId]);

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!assignment) {
    return <div>Loading...</div>;
  }

  return (
    <div className="assignmentContainer">
      <h1>Assignment ID: {assignment.id}</h1>
      <h2>Assignment Name: {assignment.name}</h2>
      <p>Weight: {assignment.weight}%</p>
      <p>Grade: {assignment.grade ?? "N/A"}</p>
    </div>
  );
};

export default Assignment;
