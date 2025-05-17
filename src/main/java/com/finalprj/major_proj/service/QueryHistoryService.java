package com.finalprj.major_proj.service;

import com.finalprj.major_proj.entity.QueryHistory;
import com.finalprj.major_proj.repo.QueryHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueryHistoryService {

    @Autowired
    private QueryHistoryRepository repository;

    public QueryHistory saveQuery(String query, boolean favorite) {
        QueryHistory q = new QueryHistory(query, favorite);
        repository.save(q);
        return q;
    }

    public List<QueryHistory> getAllQueries() {
        return repository.findAll();
    }

    public List<QueryHistory> getFavoriteQueries() {
        return repository.findByFavoriteTrue();
    }

    public void toggleFavorite(Long id) {
        QueryHistory q = repository.findById(id).orElse(null);
        if (q != null) {
            q.setFavorite(!q.isFavorite());
            repository.save(q);
        }
    }

    public void deleteQuery(Long id) {
        repository.deleteById(id);
    }

    public void deleteAllQueries() {
        repository.deleteAll();
    }
}
