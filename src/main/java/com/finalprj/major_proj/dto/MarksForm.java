package com.finalprj.major_proj.dto;

import lombok.Data;

import java.util.List;

@Data
public class MarksForm {
    private String email;
    private int semester;
    private int totalMax;
    private int totalScored;
    private double percentage;
    private double cgpa;
    private List<SubjectMark> subjects;

    // other fields...

    public String getEmail() {
        return email;
    }

    public int getSemester() {
        return semester;
    }

    public int getTotalMax() {
        return totalMax;
    }

    public int getTotalScored() {
        return totalScored;
    }

    public float getPercentage() {
        return (float) percentage;
    }

    public float getCgpa() {
        return (float) cgpa;
    }

    public List<SubjectMark> getSubjects() {
        return subjects;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public void setTotalMax(int totalMax) {
        this.totalMax = totalMax;
    }

    public void setTotalScored(int totalScored) {
        this.totalScored = totalScored;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public void setSubjects(List<SubjectMark> subjects) {
        this.subjects = subjects;
    }

}