package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long>, JpaSpecificationExecutor<Keyword> {
    List<Keyword> findByKeywordTextContainingIgnoreCase(String keywordText);
    List<Keyword> findByCategory(String category);
    Optional<Keyword> findByKeywordTextAndCategory(String keywordText, String category);
}

