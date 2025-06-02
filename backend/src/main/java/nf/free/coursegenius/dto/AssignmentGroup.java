package nf.free.coursegenius.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AssignmentGroup {
    private int id;
    private String name;
    private BigDecimal weight;
    private int courseId;
    private List<Assignment> assignments;

    // Default constructor
    public AssignmentGroup() {
        this.assignments = new ArrayList<>();
    }

    // Parameterized constructor
    public AssignmentGroup(int id, String name, BigDecimal weight, int courseId, List<Assignment> assignments) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.courseId = courseId;
        this.assignments = assignments != null ? assignments : new ArrayList<>();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public void addAssignment(Assignment assignment) {
        if (assignments == null) {
            assignments = new ArrayList<>();
        }
        assignments.add(assignment);
    }

    @Override
    public String toString() {
        return "AssignmentGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", courseId=" + courseId +
                ", assignments=" + assignments +
                '}';
    }
} 