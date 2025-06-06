import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";

import "./AssignmentGroup.css";

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
  courseId: number;
  assignments: Assignment[];
}

const AssignmentGroup: React.FC = () => {
  const { groupId } = useParams<{ groupId: string }>();
  const [group, setGroup] = useState<AssignmentGroup | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isAddingAssignment, setIsAddingAssignment] = useState(false);
  const [assignmentName, setAssignmentName] = useState("");
  const [pointsEarned, setPointsEarned] = useState("");
  const [pointsPossible, setPointsPossible] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [editedName, setEditedName] = useState("");
  const [editedWeight, setEditedWeight] = useState("");
  const navigate = useNavigate();

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  useEffect(() => {
    const fetchGroup = async () => {
      try {
        const response = await fetch(
          `${apiDomain}/assignment/get-assignment-group?groupId=${groupId}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
            },
            credentials: "include",
          }
        );
        if (response.ok) {
          const data = await response.json();
          setGroup(data.assignmentGroup);
          setEditedName(data.assignmentGroup.name);
          setEditedWeight(data.assignmentGroup.weight.toString());
        } else {
          const errorData = await response.json();
          setError(errorData.message || "Failed to fetch assignment group");
        }
      } catch (error) {
        setError("Error fetching assignment group");
      }
    };
    fetchGroup();
  }, [groupId]);

  const updateGroup = async () => {
    if (!editedName || !editedWeight) {
      setError("Please fill in all fields");
      return;
    }

    try {
      const response = await fetch(`${apiDomain}/assignment/update-assignment-group`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          groupId,
          name: editedName,
          weight: Number(editedWeight)
        }),
      });

      if (response.ok) {
        // Refresh the group data
        window.location.reload();
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to update assignment group");
      }
    } catch (error) {
      setError("Error updating assignment group");
    }
  };

  const deleteGroup = async () => {
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
        // Redirect back to course
        navigate(`/course/${group?.courseId}`);
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to delete assignment group");
      }
    } catch (error) {
      setError("Error deleting assignment group");
    }
  };

  const addAssignment = async () => {
    if (!assignmentName || !pointsEarned || !pointsPossible) {
      setError("Please fill in all fields");
      return;
    }

    try {
      const response = await fetch(`${apiDomain}/assignment/add-assignment`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          assignmentGroupId: groupId,
          assignmentName,
          pointsEarned: Number(pointsEarned),
          pointsPossible: Number(pointsPossible)
        }),
      });

      if (response.ok) {
        // Refresh the group data
        window.location.reload();
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to add assignment");
      }
    } catch (error) {
      setError("Error adding assignment");
    }
  };

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!group) {
    return <div>Loading...</div>;
  }

  return (
    <div className="assignmentGroupContainer">
      <div className="groupHeader">
        {isEditing ? (
          <div className="editGroupSection">
            <input
              type="text"
              value={editedName}
              onChange={(e) => setEditedName(e.target.value)}
              placeholder="Group name"
            />
            <input
              type="number"
              value={editedWeight}
              onChange={(e) => setEditedWeight(e.target.value)}
              placeholder="Weight"
              min="0"
              step="0.1"
            />
            <button onClick={updateGroup} className="saveButton">
              Save
            </button>
            <button onClick={() => setIsEditing(false)} className="cancelButton">
              Cancel
            </button>
          </div>
        ) : (
          <>
            <h1>{group.name}</h1>
            <p>Weight: {group.weight.toFixed(1)}%</p>
            <div className="groupActions">
              <button onClick={() => setIsEditing(true)} className="editButton">
                Edit Group
              </button>
              <button onClick={deleteGroup} className="deleteButton">
                Delete Group
              </button>
            </div>
          </>
        )}
      </div>

      <div className="assignmentsList">
        <h2>Assignments</h2>
        {group.assignments.length > 0 ? (
          group.assignments.map((assignment) => (
            <div key={assignment.id} className="assignmentRow">
              <Link to={`/assignment/${assignment.id}`} className="assignmentItem">
                <div className="assignmentName">{assignment.name}</div>
                <div className="assignmentGrade">
                  {assignment.pointsEarned} / {assignment.pointsPossible} points
                  ({assignment.percentageGrade.toFixed(1)}%)
                </div>
              </Link>
            </div>
          ))
        ) : (
          <p>No assignments in this group</p>
        )}
      </div>

      <div className="addAssignmentSection">
        <h3>Add Assignment</h3>
        <div className="inputGroup">
          <input
            type="text"
            value={assignmentName}
            onChange={(e) => setAssignmentName(e.target.value)}
            placeholder="Assignment name"
          />
          <input
            type="number"
            value={pointsEarned}
            onChange={(e) => setPointsEarned(e.target.value)}
            placeholder="Points earned"
            min="0"
            step="0.1"
          />
          <input
            type="number"
            value={pointsPossible}
            onChange={(e) => setPointsPossible(e.target.value)}
            placeholder="Points possible"
            min="0.1"
            step="0.1"
          />
          <button onClick={addAssignment}>Add Assignment</button>
        </div>
      </div>

      <Link to={`/course/${group.courseId}`} className="backButton">
        Back to Course
      </Link>
    </div>
  );
};

export default AssignmentGroup; 