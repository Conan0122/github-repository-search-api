package com.example.github_search.controller;

import com.example.github_search.model.RepositoryEntity;
import com.example.github_search.repository.RepositoryRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/github")
public class GithubController {

    private final String GITHUB_API_URL = "https://api.github.com/search/repositories?q=";

    @Autowired
    private RepositoryRepository repositoryRepository;

    @GetMapping("/search")
    public ResponseEntity<String> searchRepositories(@RequestParam String query) {
        RestTemplate restTemplate = new RestTemplate();
        String url = GITHUB_API_URL + query;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

        if (items != null && !items.isEmpty()) {
            for (Map<String, Object> repo : items) { // Loop through all repositories

                Long id = ((Number) repo.get("id")).longValue();
                String name = (String) repo.get("name");
                String description = (String) repo.get("description");
                String ownerName = (String) ((Map<String, Object>) repo.get("owner")).get("login");
                String language = (String) repo.get("language");
                int stars = ((Number) repo.get("stargazers_count")).intValue();
                int forks = ((Number) repo.get("forks_count")).intValue();

                // Fix for date parsing issue
                String updatedAt = (String) repo.get("updated_at");
                LocalDateTime lastUpdated = (updatedAt != null) ? LocalDateTime.parse(updatedAt.replace("Z", ""))
                        : LocalDateTime.now();

                // Check if repository already exists
                RepositoryEntity existingRepo = repositoryRepository.findById(id).orElse(null);

                if (existingRepo == null) {
                    // Save new repository
                    RepositoryEntity newRepo = new RepositoryEntity();
                    newRepo.setId(id);
                    newRepo.setName(name);
                    newRepo.setDescription(description);
                    newRepo.setOwnerName(ownerName);
                    newRepo.setLanguage(language);
                    newRepo.setStars(stars);
                    newRepo.setForks(forks);
                    newRepo.setLastUpdated(lastUpdated);

                    repositoryRepository.save(newRepo);
                } else {
                    // Update existing repository
                    existingRepo.setStars(stars);
                    existingRepo.setForks(forks);
                    existingRepo.setLastUpdated(lastUpdated);

                    repositoryRepository.save(existingRepo);
                }
            }
        }
        return ResponseEntity.ok("Repository saved successfully!");
    }

    // Retrieve repositories with filters
    @GetMapping("/repositories")
    public ResponseEntity<List<RepositoryEntity>> getRepositories(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(defaultValue = "stars") String sortBy) {

        Sort sort = Sort.by(Sort.Direction.DESC, sortBy); // Sorting logic

        // Fetch repositories with filtering
        List<RepositoryEntity> repositories;
        if (language != null && minStars != null) {
            repositories = repositoryRepository.findByLanguageAndStarsGreaterThanEqual(language, minStars, sort);
        } else if (language != null) {
            repositories = repositoryRepository.findByLanguage(language, sort);
        } else if (minStars != null) {
            repositories = repositoryRepository.findByStarsGreaterThanEqual(minStars, sort);
        } else {
            repositories = repositoryRepository.findAll(sort);
        }
        return ResponseEntity.ok(repositories);
    }
}
