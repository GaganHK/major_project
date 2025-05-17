package com.finalprj.major_proj.repo;

import com.finalprj.major_proj.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByEmailAndSemester(String email, int semester);
    List<Result> findByEmail(String email);

    boolean existsByEmailAndSemester(String email, int semester);


}