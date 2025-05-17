package com.finalprj.major_proj.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class QueryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String query;

    private boolean favorite;

    private LocalDateTime timestamp;

    // Constructors
    public QueryHistory() {}

    public QueryHistory(String query, boolean favorite) {
        this.query = query;
        this.favorite = favorite;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
