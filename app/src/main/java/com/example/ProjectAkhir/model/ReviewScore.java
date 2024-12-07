package com.example.ProjectAkhir.model;

public class ReviewScore {
    private final String book_id;
    private final int total_score;
    private final int total_review;
    private final int star5;
    private final int star4;
    private final int star3;
    private final int star2;
    private final int star1;

    public ReviewScore(
            String book_id,
            int total_score,
            int total_review,
            int star5,
            int star4,
            int star3,
            int star2,
            int star1
    ) {
        this.book_id = book_id;
        this.total_score = total_score;
        this.total_review = total_review;
        this.star5 = star5;
        this.star4 = star4;
        this.star3 = star3;
        this.star2 = star2;
        this.star1 = star1;
    }

    public String getBookId() {
        return book_id;
    }

    public int getTotalScore() {
        return total_score;
    }

    public int getTotalReview() {
        return total_review;
    }

    public int getStar5() {
        return star5;
    }

    public int getStar4() {
        return star4;
    }

    public int getStar3() {
        return star3;
    }

    public int getStar2() {
        return star2;
    }

    public int getStar1() {
        return star1;
    }

    public float getAvgScore() {
        return (float) total_score / total_review;
    }
}
