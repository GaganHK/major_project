package com.finalprj.major_proj.dto;

import lombok.Data;

@Data
public class SubjectMark {
    private String subjectName;
    private int max;
    private int scored;
    private int internal;
    private int external;


    // Getter and Setter for subjectName
    public String getSubjectName() {
        return subjectName;
    }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    // Getter and Setter for scored
    public int getScored() {
        return scored;
    }
    public void setScored(int scored) {
        this.scored = scored;
    }

    // Getter and Setter for max
    public int getMax() {
        return max;
    }
    public void setMax(int max) {
        this.max = max;
    }
}