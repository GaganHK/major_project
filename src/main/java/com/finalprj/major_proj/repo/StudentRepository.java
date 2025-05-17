//package com.finalprj.major_proj.repo;
//
//import com.finalprj.major_proj.entity.Student;
//import org.springframework.data.jpa.repository.JpaRepository;
//
// public interface StudentRepository extends JpaRepository<Student, Long> {
//
//     boolean existsByUsn(String usn);
//
//
//    Student findByEmail(String email);
//}
package com.finalprj.major_proj.repo;

import com.finalprj.major_proj.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface StudentRepository extends JpaRepository<Student, String> {

    // Check if a student exists by USN
    boolean existsByUsn(String usn);

    Optional<Student> findByUsn(String usn);



    // Find student by email (used for login)
    Student findByEmail(String email);

         List<Student> findByBranchAndSemester(String branch, int semester);
        @Query("SELECT s FROM Student s WHERE s.name = :name AND s.branch = :branch AND s.semester = :semester")
        List<Student> searchByNameAndContext(@Param("name") String name,
                                             @Param("branch") String branch,
                                             @Param("semester") int semester);
    }

