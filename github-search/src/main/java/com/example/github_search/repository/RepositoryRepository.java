package com.example.github_search.repository;

import com.example.github_search.model.RepositoryEntity;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryRepository extends JpaRepository<RepositoryEntity, Long> {
    List<RepositoryEntity> findByLanguage(String language, Sort sort);
    List<RepositoryEntity> findByStarsGreaterThanEqual(int stars, Sort sort);
    List<RepositoryEntity> findByLanguageAndStarsGreaterThanEqual(String language, int stars, Sort sort);
}
