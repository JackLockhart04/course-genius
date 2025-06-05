import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";

import "./Course.css";

interface Assignment {
  id: number;
  name: string;
  pointsEarned: number;
  pointsPossible: number;
  percentageGrade: number;
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
  gpa: number | null;
  assignmentGroups: AssignmentGroup[];
}

const Course: React.FC = () => {
  const { courseId } = useParams<{ courseId: string }>();
  const navigate = useNavigate();
  const [course, setCourse] = useState<Course | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editedName, setEditedName] = useState("");
  const [editedCreditHours, setEditedCreditHours] = useState(0);
  const [editedGpa, setEditedGpa] = useState<number | null>(null);
  const [editingGroupId, setEditingGroupId] = useState<number | null>(null);
  const [editingGroupName, setEditingGroupName] = useState("");
  const [editingAssignmentId, setEditingAssignmentId] = useState<number | null>(null);
  const [editingAssignmentName, setEditingAssignmentName] = useState("");

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  const fetchCourse = async () => {
    try {
      const response = await fetch(`${apiDomain}/course/get-course?courseId=${courseId}`, {
        credentials: "include"
      });
      if (!response.ok) {
        throw new Error('Failed to fetch course');
      }
      const data = await response.json();
      setCourse(data.course);
      setEditedName(data.course.name);
      setEditedCreditHours(data.course.creditHours);
      setEditedGpa(data.course.gpa);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    }
  };

  useEffect(() => {
    fetchCourse();
  }, [courseId]);

  const updateCourse = async () => {
    if (!editedName || !editedCreditHours) {
      setError("Please fill in all required fields");
      return;
    }

    if (editedName.length > 50) {
      setError("Course name cannot be longer than 50 characters");
      return;
    }

    const creditHoursNum = Number(editedCreditHours);
    if (isNaN(creditHoursNum) || creditHoursNum <= 0) {
      setError("Credit hours must be a positive number");
      return;
    }

    if (editedGpa !== null) {
      const gpaNum = Number(editedGpa);
      if (isNaN(gpaNum) || gpaNum < 0) {
        setError("GPA cannot be negative");
        return;
      }
    }

    try {
      const response = await fetch(`${apiDomain}/course/update-course`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: "include",
        body: JSON.stringify({
          courseId,
          name: editedName,
          creditHours: creditHoursNum,
          gpa: editedGpa
        }),
      });

      if (response.ok) {
        const data = await response.json();
        setCourse(data.course);
        setIsEditing(false);
        setError(null);
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to update course");
      }
    } catch (error) {
      setError("Error updating course");
    }
  };

  const updateAssignmentGroup = async (groupId: number) => {
    if (!editingGroupName) {
      setError("Group name cannot be empty");
      return;
    }

    try {
      const response = await fetch(`${apiDomain}/assignment/update-assignment-group`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: "include",
        body: JSON.stringify({
          groupId,
          name: editingGroupName
        }),
      });

      if (response.ok) {
        // Refresh the course data
        await fetchCourse();
        setEditingGroupId(null);
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to update assignment group");
      }
    } catch (error) {
      setError("Error updating assignment group");
    }
  };

  const updateAssignment = async (assignmentId: number) => {
    if (!editingAssignmentName) {
      setError("Assignment name cannot be empty");
      return;
    }

    try {
      const response = await fetch(`${apiDomain}/assignment/update-assignment-name`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: "include",
        body: JSON.stringify({
          assignmentId,
          name: editingAssignmentName
        }),
      });

      if (response.ok) {
        // Refresh the course data
        await fetchCourse();
        setEditingAssignmentId(null);
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to update assignment");
      }
    } catch (error) {
      setError("Error updating assignment");
    }
  };

  const deleteAssignmentGroup = async (groupId: number) => {
    if (!window.confirm("Are you sure you want to delete this assignment group? This will also delete all assignments in the group.")) {
      return;
    }

    try {
      const response = await fetch(`${apiDomain}/assignment/delete-assignment-group`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: "include",
        body: JSON.stringify({ groupId: Number(groupId) }),
      });

      if (response.ok) {
        // Refresh the course data
        await fetchCourse();
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to delete assignment group");
      }
    } catch (error) {
      setError("Error deleting assignment group");
    }
  };

  const deleteAssignment = async (assignmentId: number) => {
    if (!window.confirm("Are you sure you want to delete this assignment?")) {
      return;
    }

    try {
      const response = await fetch(`${apiDomain}/assignment/delete-assignment`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: "include",
        body: JSON.stringify({ assignmentId: Number(assignmentId) }),
      });

      if (response.ok) {
        // Refresh the course data
        await fetchCourse();
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to delete assignment");
      }
    } catch (error) {
      setError("Error deleting assignment");
    }
  };

  // Add assignment group handler
  const addAssignmentGroup = async () => {
    const groupName = (document.getElementById("addGroupName") as HTMLInputElement).value;
    const groupWeight = (document.getElementById("addGroupWeight") as HTMLInputElement).value;

    if (!groupName || !groupWeight) {
      setError("Please fill in all fields");
      return;
    }

    try {
      const response = await fetch(`${apiDomain}/assignment/add-assignment-group`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
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
        await fetchCourse();
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
    if (!window.confirm("Are you sure you want to delete this course? This will also delete all assignment groups and assignments.")) {
      return;
    }

    try {
      const response = await fetch(`${apiDomain}/course/delete-course`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: "include",
        body: JSON.stringify({ courseId: Number(courseId) }),
      });

      if (response.ok) {
        // Redirect to dashboard
        navigate("/");
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
      {isEditing ? (
        <div className="editCourseSection">
          <h2>Edit Course</h2>
          <div className="inputGroup">
            <input
              type="text"
              value={editedName}
              onChange={(e) => setEditedName(e.target.value)}
              placeholder="Course name"
            />
            <input
              type="number"
              value={editedCreditHours}
              onChange={(e) => setEditedCreditHours(parseInt(e.target.value))}
              placeholder="Credit hours"
              min="0"
              step="0.5"
            />
            <input
              type="number"
              value={editedGpa === null ? '' : editedGpa}
              onChange={(e) => setEditedGpa(e.target.value ? parseFloat(e.target.value) : null)}
              placeholder="GPA"
              min="0"
              step="0.1"
            />
            <button onClick={updateCourse} className="saveButton">Save Changes</button>
            <button onClick={() => setIsEditing(false)} className="cancelButton">Cancel</button>
          </div>
        </div>
      ) : (
        <div className="courseHeader">
          <h1>{course.name}</h1>
          <button onClick={() => setIsEditing(true)} className="editButton">
            Edit Course
          </button>
        </div>
      )}

      <div className="courseInfo">
        <h2>{course.name}</h2>
        <p>Credit Hours: {course.creditHours}</p>
        <p>GPA: {course.gpa !== null && course.gpa !== undefined ? course.gpa.toFixed(2) : 'N/A'}</p>
      </div>

      <h2>Assignment Groups</h2>
      {course.assignmentGroups.length > 0 ? (
        course.assignmentGroups.map((group) => (
          <div key={group.id} className="assignmentGroup">
            {editingGroupId === group.id ? (
              <div className="editGroupSection">
                <input
                  type="text"
                  value={editingGroupName}
                  onChange={(e) => setEditingGroupName(e.target.value)}
                  placeholder="Group name"
                />
                <button onClick={() => updateAssignmentGroup(group.id)} className="saveButton">
                  Save
                </button>
                <button onClick={() => setEditingGroupId(null)} className="cancelButton">
                  Cancel
                </button>
              </div>
            ) : (
              <div className="groupHeader">
                <div className="groupTitle">
                  <Link to={`/assignment-group/${group.id}`}>
                    <h3>{group.name} ({group.weight.toFixed(1)}%)</h3>
                  </Link>
                  <div className="groupActions">
                    <button onClick={() => {
                      setEditingGroupId(group.id);
                      setEditingGroupName(group.name);
                    }} className="editButton">
                      Edit
                    </button>
                    <button onClick={() => deleteAssignmentGroup(group.id)} className="deleteButton">
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            )}
            {group.assignments.length > 0 ? (
              group.assignments.map((assignment) => (
                <div key={assignment.id} className="assignmentRow">
                  {editingAssignmentId === assignment.id ? (
                    <div className="editAssignmentSection">
                      <input
                        type="text"
                        value={editingAssignmentName}
                        onChange={(e) => setEditingAssignmentName(e.target.value)}
                        placeholder="Assignment name"
                      />
                      <button onClick={() => updateAssignment(assignment.id)} className="saveButton">
                        Save
                      </button>
                      <button onClick={() => setEditingAssignmentId(null)} className="cancelButton">
                        Cancel
                      </button>
                    </div>
                  ) : (
                    <div className="assignmentItem">
                      <Link to={`/assignment/${assignment.id}`}>
                        <span className="assignmentName">{assignment.name}</span>
                        <span className="assignmentGrade">
                          Grade: {assignment.percentageGrade.toFixed(1)}%
                        </span>
                      </Link>
                      <div className="assignmentActions">
                        <button onClick={() => {
                          setEditingAssignmentId(assignment.id);
                          setEditingAssignmentName(assignment.name);
                        }} className="editButton">
                          Edit
                        </button>
                        <button onClick={() => deleteAssignment(assignment.id)} className="deleteButton">
                          Delete
                        </button>
                      </div>
                    </div>
                  )}
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
            step="0.1"
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
