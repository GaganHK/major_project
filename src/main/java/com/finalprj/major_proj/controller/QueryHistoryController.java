package com.finalprj.major_proj.controller;

import com.finalprj.major_proj.entity.QueryHistory;
import com.finalprj.major_proj.service.QueryHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/query")
@CrossOrigin
public class QueryHistoryController {

    @Autowired
    private QueryHistoryService queryHistoryService;

    // 1️⃣ Save a new query
    @PostMapping("/save")
    public QueryHistory saveQuery(@RequestParam String query,
                                  @RequestParam(defaultValue = "false") boolean favorite) {
        return queryHistoryService.saveQuery(query, favorite);
    }

    // 2️⃣ Get all queries
    @GetMapping("/history")
    public List<QueryHistory> getQueryHistory() {
        return queryHistoryService.getAllQueries();
    }

    // 3️⃣ Get only favorites
    @GetMapping("/favorites")
    public List<QueryHistory> getFavoriteQueries() {
        return queryHistoryService.getFavoriteQueries();
    }

    // 4️⃣ Toggle favorite
    @PostMapping("/toggleFavorite/{id}")
    public void toggleFavorite(@PathVariable Long id) {
        queryHistoryService.toggleFavorite(id);
    }

    // 5️⃣ Delete single query history
    @DeleteMapping("/delete/{id}")
    public void deleteQuery(@PathVariable Long id) {
        queryHistoryService.deleteQuery(id);
    }

    // 6️⃣ Delete all query history
    @DeleteMapping("/deleteAll")
    public void deleteAllQueries() {
        queryHistoryService.deleteAllQueries();
    }
}
