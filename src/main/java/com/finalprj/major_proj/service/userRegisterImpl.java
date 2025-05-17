package com.finalprj.major_proj.service;

import com.finalprj.major_proj.entity.Student;
import com.finalprj.major_proj.repo.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class userRegisterImpl extends userService {
@Autowired
    private StudentRepository repo;

@Override
public void registerUser(Student student){
    repo.save(student);
}
}
