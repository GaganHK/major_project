package com.finalprj.major_proj.entity;

import jakarta.persistence.*;
import lombok.Data;

import javax.security.auth.Subject;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data

@Table(name = "RESULT", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "semester"}))
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private int semester;

    @Column(name = "total_scored")
    private int totalScored;

    @Column(name = "total_max")
    private int totalMax;

    private double percentage;
    private double cgpa;

    @Column(name = "created_on")
    private LocalDate createdOn = LocalDate.now();



    // Getter
    public String getEmail() {
        return email;
    }

    // Setter
    public void setEmail(String email) {
        this.email = email;
    }

    public int getTotalScored() {
        return totalScored;
    }

    public void setTotalScored(int totalScored) {
        this.totalScored = totalScored;
    }

    public int getTotalMax() {
        return totalMax;
    }

    public void setTotalMax(int totalMax) {
        this.totalMax = totalMax;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(float cgpa) {
        this.cgpa = cgpa;
    }

    public void setSemester(int semester) {
        this.semester=semester;
    }

    public void setSubjects(List<Subject> subjects) {
    }
    // Getter


}