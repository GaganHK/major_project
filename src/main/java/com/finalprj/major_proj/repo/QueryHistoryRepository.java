package com.finalprj.major_proj.repo;

import com.finalprj.major_proj.entity.QueryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QueryHistoryRepository extends JpaRepository<QueryHistory, Long> {
    List<QueryHistory> findByFavoriteTrue();
}
