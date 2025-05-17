package com.finalprj.major_proj.dto;

import java.util.List;

public class MarksFormDTO {
    private String email;
    private int semester;
    private double totalScored;
    private double totalMax;
    private double percentage;
    private double cgpa;
    private List<SubjectDTO> subjects;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public double getTotalScored() { return totalScored; }
    public void setTotalScored(double totalScored) { this.totalScored = totalScored; }

    public double getTotalMax() { return totalMax; }
    public void setTotalMax(double totalMax) { this.totalMax = totalMax; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public List<SubjectDTO> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectDTO> subjects) { this.subjects = subjects; }
}
