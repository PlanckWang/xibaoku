package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Literature;
import com.example.guangxishengkejavafx.model.repository.LiteratureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LiteratureService {

    private final LiteratureRepository literatureRepository;

    @Autowired
    public LiteratureService(LiteratureRepository literatureRepository) {
        this.literatureRepository = literatureRepository;
    }

    public List<Literature> findAll() {
        return literatureRepository.findAll();
    }

    public Optional<Literature> findById(Long id) {
        return literatureRepository.findById(id);
    }

    public Optional<Literature> findByDoi(String doi) {
        return literatureRepository.findByDoi(doi);
    }

    @Transactional
    public Literature save(Literature literature, Integer userId) throws Exception {
        if (literature.getTitle() == null || literature.getTitle().trim().isEmpty()) {
            throw new Exception("文献标题不能为空。");
        }

        // Check for DOI uniqueness if DOI is provided
        if (literature.getDoi() != null && !literature.getDoi().trim().isEmpty()) {
            Optional<Literature> existingLiterature = literatureRepository.findByDoi(literature.getDoi());
            if (existingLiterature.isPresent() && (literature.getId() == null || !existingLiterature.get().getId().equals(literature.getId()))) {
                throw new Exception("已存在相同的DOI: " + literature.getDoi());
            }
        }

        LocalDateTime now = LocalDateTime.now();
        if (literature.getId() == null) { // New literature
            literature.setAddedAt(now);
            literature.setAddedBy(userId);
        } else { // Existing literature, preserve original creation info
            literatureRepository.findById(literature.getId()).ifPresent(l -> {
                literature.setAddedAt(l.getAddedAt());
                literature.setAddedBy(l.getAddedBy());
            });
        }
        literature.setUpdatedAt(now);
        literature.setUpdatedBy(userId);

        return literatureRepository.save(literature);
    }

    @Transactional
    public void deleteById(Long id) throws Exception {
        if (!literatureRepository.existsById(id)) {
            throw new Exception("无法找到ID为 " + id + " 的文献进行删除。");
        }
        literatureRepository.deleteById(id);
    }

    public List<Literature> findAll(Specification<Literature> spec) {
        return literatureRepository.findAll(spec);
    }
}

