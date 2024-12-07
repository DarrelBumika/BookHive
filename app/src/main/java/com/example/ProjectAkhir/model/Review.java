package com.example.ProjectAkhir.model;

public class Review {

    private String documentId;
    private String bookId;
    private String content;
    private String uid;
    private int rating;
    private boolean hideName;

    public Review() {
    }

    public Review(String bookId, String uid, String content, int rating, boolean hideName) {
        this.bookId = bookId;
        if (hideName) {
            this.uid = "Anonymous";
        } else {
            this.uid = uid;
        }
        this.content = content;
        this.rating = rating;
        this.hideName = hideName;
    }

    public Review(String documentId, String bookId, String uid, String content, int rating, boolean hideName) {
        this.documentId = documentId;
        this.bookId = bookId;
        if (hideName) {
            this.uid = "Anonymous";
        } else {
            this.uid = uid;
        }
        this.content = content;
        this.rating = rating;
        this.hideName = hideName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getContent() {
        return content;
    }

    public String getUid() {
        return uid;
    }

    public int getRating() {
        return rating;
    }

    public boolean getHideName() {
        return hideName;
    }
}
