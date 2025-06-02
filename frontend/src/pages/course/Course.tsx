import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";

import "./Course.css";

interface Assignment {
  id: number;
  name: string;
  weight: number;
  grade: number | null;
}

interface AssignmentGroup {
  id: number;
  name: string;
  weight: number;
  assignments: Assignment[];
}

interface Course {
  id: number;
  userId: number;
  name: string;
  creditHours: number;
  gpa: number;
  assignmentGroups: AssignmentGroup[];
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

  // Add assignment group handler
  const addAssignmentGroup = async () => {
    const groupName = (document.getElementById("addGroupName") as HTMLInputElement).value;
    const groupWeight = (document.getElementById("addGroupWeight") as HTMLInputElement).value;

    if (!groupName || !groupWeight) {
      setError("Please fill in all fields");
      return;
    }

    try {
      const response = await fetch(`${apiDomain}/assignment-group/add-group`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          courseId,
          groupName,
          weight: Number(groupWeight)
        }),
      });

      if (response.ok) {
        // Refresh the course data
        window.location.reload();
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to add assignment group");
      }
    } catch (error) {
      setError("Error adding assignment group");
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
        credentials: "include",
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
      <h1>{course.name}</h1>
      <div className="courseInfo">
        <p>Credit Hours: {course.creditHours}</p>
        <p>GPA: {course.gpa ? course.gpa.toFixed(2) : 'N/A'}</p>
      </div>

      <h2>Assignment Groups</h2>
      {course.assignmentGroups.length > 0 ? (
        course.assignmentGroups.map((group) => (
          <div key={group.id} className="assignmentGroup">
            <h3>{group.name} ({group.weight}%)</h3>
            {group.assignments.length > 0 ? (
              group.assignments.map((assignment) => (
                <div key={assignment.id} className="assignmentRow">
                  <Link to={`/assignment/${assignment.id}`} className="assignmentItem">
                    {assignment.name} - Weight: {assignment.weight}%, Grade: {assignment.grade ?? "N/A"}
                  </Link>
                </div>
              ))
            ) : (
              <p>No assignments in this group</p>
            )}
          </div>
        ))
      ) : (
        <p>No assignment groups found</p>
      )}

      <div className="addGroupSection">
        <h3>Add Assignment Group</h3>
        <div className="inputGroup">
          <input
            id="addGroupName"
            type="text"
            placeholder="Group name"
          />
          <input
            id="addGroupWeight"
            type="number"
            placeholder="Weight (%)"
            min="0"
            max="100"
          />
          <button onClick={addAssignmentGroup}>Add Group</button>
        </div>
      </div>

      <button onClick={deleteCourse} className="deleteCourseButton">
        Delete Course
      </button>
      {error && <div className="error">{error}</div>}
    </div>
  );
};

export default Course;
