package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Keyword;
import com.example.guangxishengkejavafx.model.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class KeywordService {

    private final KeywordRepository keywordRepository;

    @Autowired
    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    public List<Keyword> findAll() {
        return keywordRepository.findAll();
    }

    public Optional<Keyword> findById(Long id) {
        return keywordRepository.findById(id);
    }

    public List<Keyword> findByKeywordTextContaining(String keywordText) {
        return keywordRepository.findByKeywordTextContainingIgnoreCase(keywordText);
    }

    public List<Keyword> findByCategory(String category) {
        return keywordRepository.findByCategory(category);
    }

    @Transactional
    public Keyword save(Keyword keyword, Integer userId) throws Exception {
        if (keyword.getKeywordText() == null || keyword.getKeywordText().trim().isEmpty()) {
            throw new Exception("关键词文本不能为空。");
        }
        if (keyword.getCategory() == null || keyword.getCategory().trim().isEmpty()) {
            throw new Exception("关键词分类不能为空。");
        }

        // Check for uniqueness within the category
        Optional<Keyword> existingKeyword = keywordRepository.findByKeywordTextAndCategory(keyword.getKeywordText(), keyword.getCategory());
        if (existingKeyword.isPresent() && (keyword.getId() == null || !existingKeyword.get().getId().equals(keyword.getId()))) {
            throw new Exception("在分类 '" + keyword.getCategory() + "' 中已存在相同的关键词 '" + keyword.getKeywordText() + "'。");
        }

        if (keyword.getId() == null) { // New keyword
            keyword.setCreatedAt(LocalDateTime.now());
            keyword.setCreatedBy(userId); // Assuming userId is passed or obtained from security context
        } else { // Existing keyword
            keywordRepository.findById(keyword.getId()).ifPresent(k -> {
                keyword.setCreatedAt(k.getCreatedAt());
                keyword.setCreatedBy(k.getCreatedBy());
            });
        }
        keyword.setUpdatedAt(LocalDateTime.now());
        keyword.setUpdatedBy(userId); // Assuming userId is passed or obtained from security context

        return keywordRepository.save(keyword);
    }

    @Transactional
    public void deleteById(Long id) throws Exception {
        if (!keywordRepository.existsById(id)) {
            throw new Exception("无法找到ID为 " + id + " 的关键词进行删除。");
        }
        keywordRepository.deleteById(id);
    }

    public List<Keyword> findAll(Specification<Keyword> spec) {
        return keywordRepository.findAll(spec);
    }
}

