package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.Literature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiteratureRepository extends JpaRepository<Literature, Long>, JpaSpecificationExecutor<Literature> {
    Optional<Literature> findByDoi(String doi);
    List<Literature> findByTitleContainingIgnoreCase(String title);
    List<Literature> findByAuthorsContainingIgnoreCase(String authors);
    List<Literature> findByKeywordsContainingIgnoreCase(String keywords);
    List<Literature> findByCategory(String category);
    List<Literature> findByPublicationYear(Integer year);
}

