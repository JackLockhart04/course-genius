package nf.free.coursegenius.dto;

import java.util.List;
import java.util.ArrayList;

public class Course {
    private int id;
    private int userId;
    private String name;
    private List<Assignment> assignments;

    // Default constructor
    public Course() {
    }

    // Parameterized constructor
    public Course(int id, int userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.assignments = new ArrayList<>();
    }
    public Course(int id, int userId, String name, List<Assignment> assignments) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.assignments = assignments;
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

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public void addAssignment(Assignment assignment) {
        this.assignments.add(assignment);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", assignments=" + assignments +
                '}';
    }
}