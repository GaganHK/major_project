package com.finalprj.major_proj.service;

import com.finalprj.major_proj.dto.MarksForm;
import com.finalprj.major_proj.entity.Result;
import com.finalprj.major_proj.repo.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;

    public List<Result> getResultsByEmail(String email) {
        return resultRepository.findByEmail(email);
    }

    public String saveResult(MarksForm form) {
        // Check if result already exists for this email and semester
        if (resultRepository.existsByEmailAndSemester(form.getEmail(), form.getSemester())) {
            return "Marks for Semester " + form.getSemester() + " already exist for this student.";
        }

        // Create new Result entity and set fields from form
        Result result = new Result();
        result.setEmail(form.getEmail());
        result.setSemester(form.getSemester());
        result.setTotalScored(form.getTotalScored());
        result.setTotalMax(form.getTotalMax());
        result.setPercentage(form.getPercentage());
        result.setCgpa(form.getCgpa());

        try {
            // Save to DB
            resultRepository.save(result);
            return "Marks stored successfully!";
        } catch (Exception e) {
            // Catch any unexpected DB-level duplicate constraint errors
            return "Error saving marks: " + e.getMessage();
        }
    }
}
