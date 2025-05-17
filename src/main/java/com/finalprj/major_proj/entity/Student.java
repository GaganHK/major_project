package com.finalprj.major_proj.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
@Table(name = "STUDENT")
public class Student {

    @Id
    @Column(name = "USN")
    private String usn;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "MOBILE", nullable = false)
    private long mobile;

    @Column(name = "FATHERNAME", nullable = false)
    private String fathername;

    @Column(name = "GENDER", nullable = false)
    private String gender;

    @Column(name = "PASSWORD", nullable = true)
    private String password;

    @Column(name = "ROLE", nullable = true)
    private String role;

    @Column(name = "BRANCH", nullable = false)
    private String branch;

    // Getters and setters (for safety if Lombok doesn't process)
    public String getUsn() {
        return usn;
    }
    public void setUsn(String usn) {
        this.usn = usn;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public long getMobile() {
        return mobile;
    }
    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    public String getFathername() {
        return fathername;
    }
    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getBranch() {
        return branch;
    }
    public void setBranch(String branch) {
        this.branch = branch;
    }
    @Column(name = "SEMESTER", nullable = false)
    private Integer semester;

    // Getter and Setter
    public Integer getSemester() {
        return semester;
    }
    public void setSemester(Integer semester) {
        this.semester = semester;
    }

}
