package nf.free.coursegenius.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class Course {
    private int id;
    private int userId;
    private String name;
    private BigDecimal creditHours;
    private BigDecimal gpa;
    private List<AssignmentGroup> assignmentGroups;

    // Default constructor
    public Course() {
        this.assignmentGroups = new ArrayList<>();
    }

    // Parameterized constructor
    public Course(int id, int userId, String name, BigDecimal creditHours, BigDecimal gpa, List<AssignmentGroup> assignmentGroups) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.creditHours = creditHours;
        this.gpa = gpa;
        this.assignmentGroups = assignmentGroups != null ? assignmentGroups : new ArrayList<>();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(BigDecimal creditHours) {
        this.creditHours = creditHours;
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }

    public List<AssignmentGroup> getAssignmentGroups() {
        return assignmentGroups;
    }

    public void setAssignmentGroups(List<AssignmentGroup> assignmentGroups) {
        this.assignmentGroups = assignmentGroups;
    }

    public void addAssignmentGroup(AssignmentGroup group) {
        if (assignmentGroups == null) {
            assignmentGroups = new ArrayList<>();
        }
        assignmentGroups.add(group);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", creditHours=" + creditHours +
                ", gpa=" + gpa +
                ", assignmentGroups=" + assignmentGroups +
                '}';
    }
}