import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";

import "./Assignment.css";

interface Assignment {
  id: number;
  name: string;
  pointsEarned: number;
  pointsPossible: number;
  percentageGrade: number;
  assignmentGroupId: number;
}

const Assignment: React.FC = () => {
  const { assignmentId } = useParams<{ assignmentId: string }>();
  const [assignment, setAssignment] = useState<Assignment | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [pointsEarned, setPointsEarned] = useState("");
  const [pointsPossible, setPointsPossible] = useState("");

  const apiDomain = process.env.REACT_APP_API_DOMAIN;

  useEffect(() => {
    const fetchAssignment = async () => {
      try {
        const response = await fetch(
          `${apiDomain}/assignment/get-assignment?assignmentId=${assignmentId}`,
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
          setAssignment(data.assignment);
          setPointsEarned(data.assignment.pointsEarned.toString());
          setPointsPossible(data.assignment.pointsPossible.toString());
        } else {
          const errorData = await response.json();
          setError(errorData.message || "Failed to fetch assignment");
        }
      } catch (error) {
        setError("Error fetching assignment");
      }
    };
    fetchAssignment();
  }, [assignmentId]);

  const updateAssignment = async () => {
    if (!pointsEarned || !pointsPossible) {
      setError("Please fill in all fields");
      return;
    }

    const earned = Number(pointsEarned);
    const possible = Number(pointsPossible);

    if (isNaN(earned) || earned < 0) {
      setError("Points earned must be a non-negative number");
      return;
    }

    if (isNaN(possible) || possible <= 0) {
      setError("Points possible must be a positive number");
      return;
    }

    try {
      const response = await fetch(`${apiDomain}/assignment/update-assignment`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          assignmentId,
          pointsEarned: earned,
          pointsPossible: possible
        }),
      });

      if (response.ok) {
        const data = await response.json();
        setAssignment(data.assignment);
        setIsEditing(false);
        setError(null);
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to update assignment");
      }
    } catch (error) {
      setError("Error updating assignment");
    }
  };

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!assignment) {
    return <div>Loading...</div>;
  }

  return (
    <div className="assignmentContainer">
      <div className="assignmentHeader">
        <h1>{assignment.name}</h1>
        {!isEditing && (
          <button onClick={() => setIsEditing(true)} className="editButton">
            Edit Assignment
          </button>
        )}
      </div>

      {isEditing ? (
        <div className="editAssignmentSection">
          <div className="inputGroup">
            <div className="inputField">
              <label>Points Earned:</label>
              <input
                type="number"
                value={pointsEarned}
                onChange={(e) => setPointsEarned(e.target.value)}
                min="0"
                step="0.1"
              />
              <small className="inputHint">Can exceed points possible for extra credit</small>
            </div>
            <div className="inputField">
              <label>Points Possible:</label>
              <input
                type="number"
                value={pointsPossible}
                onChange={(e) => setPointsPossible(e.target.value)}
                min="0.1"
                step="0.1"
              />
            </div>
            <div className="buttonGroup">
              <button onClick={updateAssignment} className="saveButton">
                Save Changes
              </button>
              <button onClick={() => setIsEditing(false)} className="cancelButton">
                Cancel
              </button>
            </div>
          </div>
        </div>
      ) : (
        <div className="assignmentDetails">
          <div className="detailRow">
            <span className="label">Points Earned:</span>
            <span className="value">{assignment.pointsEarned}</span>
          </div>
          <div className="detailRow">
            <span className="label">Points Possible:</span>
            <span className="value">{assignment.pointsPossible}</span>
          </div>
          <div className="detailRow">
            <span className="label">Grade:</span>
            <span className="value">
              {assignment.percentageGrade.toFixed(1)}%
              {assignment.percentageGrade > 100 && " (Extra Credit)"}
            </span>
          </div>
        </div>
      )}

      <Link to={`/assignment-group/${assignment.assignmentGroupId}`} className="backButton">
        Back to Assignment Group
      </Link>
    </div>
  );
};

export default Assignment;
