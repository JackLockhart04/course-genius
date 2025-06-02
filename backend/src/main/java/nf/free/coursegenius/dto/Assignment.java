package nf.free.coursegenius.dto;

import java.math.BigDecimal;

public class Assignment {
    private int id;
    private int assignmentGroupId;
    private String name;
    private BigDecimal pointsEarned;
    private BigDecimal pointsPossible;
    private BigDecimal percentageGrade;

    // Default constructor
    public Assignment() {
    }

    // Parameterized constructor
    public Assignment(int id, int assignmentGroupId, String name, BigDecimal pointsEarned, BigDecimal pointsPossible, BigDecimal percentageGrade) {
        this.id = id;
        this.assignmentGroupId = assignmentGroupId;
        this.name = name;
        this.pointsEarned = pointsEarned;
        this.pointsPossible = pointsPossible;
        this.percentageGrade = percentageGrade;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssignmentGroupId() {
        return assignmentGroupId;
    }

    public void setAssignmentGroupId(int assignmentGroupId) {
        this.assignmentGroupId = assignmentGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(BigDecimal pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public BigDecimal getPointsPossible() {
        return pointsPossible;
    }

    public void setPointsPossible(BigDecimal pointsPossible) {
        this.pointsPossible = pointsPossible;
    }

    public BigDecimal getPercentageGrade() {
        return percentageGrade;
    }

    public void setPercentageGrade(BigDecimal percentageGrade) {
        this.percentageGrade = percentageGrade;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", assignmentGroupId=" + assignmentGroupId +
                ", name='" + name + '\'' +
                ", pointsEarned=" + pointsEarned +
                ", pointsPossible=" + pointsPossible +
                ", percentageGrade=" + percentageGrade +
                '}';
    }
}