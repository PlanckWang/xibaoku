package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Literatures")
public class Literature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LiteratureID")
    private Long id;

    @Column(name = "Title", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String title;

    @Column(name = "Authors", columnDefinition = "NVARCHAR(MAX)")
    private String authors; // Comma-separated or a dedicated table for many-to-many

    @Column(name = "JournalOrConference", length = 500)
    private String journalOrConference;

    @Column(name = "PublicationYear")
    private Integer publicationYear;

    @Column(name = "PublicationDate")
    private LocalDate publicationDate;

    @Column(name = "Volume", length = 50)
    private String volume;

    @Column(name = "Issue", length = 50)
    private String issue;

    @Column(name = "Pages", length = 50)
    private String pages;

    @Column(name = "DOI", length = 255, unique = true)
    private String doi;

    @Column(name = "AbstractText", columnDefinition = "NTEXT")
    private String abstractText;

    @Column(name = "Keywords", columnDefinition = "NVARCHAR(MAX)") // Comma-separated or link to Keyword entity
    private String keywords;

    @Column(name = "URL", length = 2048)
    private String url;

    @Column(name = "FilePath", length = 2048) // Path to locally stored PDF or file
    private String filePath;

    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    @Column(name = "Category", length = 100) // e.g., Internal Research, External Publication
    private String category;

    @Column(name = "AddedBy")
    private Integer addedBy; // User ID

    @Column(name = "AddedAt")
    private LocalDateTime addedAt;

    @Column(name = "UpdatedBy")
    private Integer updatedBy; // User ID

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getJournalOrConference() {
        return journalOrConference;
    }

    public void setJournalOrConference(String journalOrConference) {
        this.journalOrConference = journalOrConference;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(Integer addedBy) {
        this.addedBy = addedBy;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

