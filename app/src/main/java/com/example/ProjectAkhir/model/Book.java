package com.example.ProjectAkhir.model;

public class Book {

    private String id;
    private String title;
    private String author;
    private String description;
    private String publisher;
    private String publication_date;
    private String isbn;
    private Integer num_pages;
    private String language;
    private String dimension;
    private String cover;
    private String banner;
    private String gramedia_link;
    private ReviewScore review_score;

    public Book() {
    }

    public Book(
            String id,
            String title,
            String author,
            String description,
            String publisher,
            String publication_date,
            String isbn,
            Integer num_pages,
            String language,
            String dimension,
            String cover,
            String banner,
            String gramedia_link,
            ReviewScore review_score
    ) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.publication_date = publication_date;
        this.isbn = isbn;
        this.num_pages = num_pages;
        this.language = language;
        this.dimension = dimension;
        this.cover = cover;
        this.banner = banner;
        this.gramedia_link = gramedia_link;
        this.review_score = review_score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublicationDate() {
        return publication_date;
    }

    public String getIsbn() {
        return isbn;
    }

    public Integer getNumPages() {
        return num_pages;
    }

    public String getLanguage() {
        return language;
    }

    public String getDimension() {
        return dimension;
    }

    public String getCover() {
        return cover;
    }

    public String getBanner() {
        return banner;
    }

    public String getStoreLink() {
        return gramedia_link;
    }

    public ReviewScore getReviewScore() {
        return review_score;
    }
}
