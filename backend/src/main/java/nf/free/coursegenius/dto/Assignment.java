package nf.free.coursegenius.dto;

import java.math.BigDecimal;

public class Assignment {
    private int id;
    private int courseId;
    private String name;
    private BigDecimal weight;
    private BigDecimal grade;

    // Default constructor
    public Assignment() {
    }

    // Parameterized constructor
    public Assignment(int id, int courseId, String name, BigDecimal weight, BigDecimal grade) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.weight = weight;
        this.grade = grade;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", grade=" + grade +
                '}';
    }
}