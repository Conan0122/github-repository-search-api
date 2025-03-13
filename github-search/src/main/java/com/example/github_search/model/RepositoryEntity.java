package com.example.github_search.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "repositories")
public class RepositoryEntity {

    @Id
    private Long id;  // GitHub Repository ID (unique)

    private String name;
    private String description;
    private String ownerName;
    private String language;
    private int stars;
    private int forks;
    private LocalDateTime lastUpdated;
}
