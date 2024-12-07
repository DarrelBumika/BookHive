package com.example.ProjectAkhir;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ProjectAkhir.model.Book;
import com.example.ProjectAkhir.model.Review;
import com.example.ProjectAkhir.service.BookApiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewActivity extends AppCompatActivity {

    private ImageButton btnBack;

    private Retrofit retrofit;
    private BookApiService bookApiService;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    private ImageView ivBookCover;
    private TextView tvBookTitle, tvBookAuthor, tvBookPublisher, tvBookISBN, tvBookPublishedDate, tvBookPages;

    private RatingBar ratingBar;
    private EditText etReview;
    private CheckBox cbHideName;
    private Button btnSubmitReview;
    private Button btnDeleteReview;

    private Book book;
    private int scoreBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initComponent();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Log.e("ReviewActivity", "User not logged in.");
            finish();
            return;
        }

        String userId = user.getUid();
        if (getIntent().getBooleanExtra("alreadyReviewed", false)) {
            btnSubmitReview.setText("Update");
            btnDeleteReview.setVisibility(Button.VISIBLE);
            prepareUpdateReviewData(userId);
        } else {
            prepareAddReviewData(userId);
        }

        prepareDeleteReviewData(userId);
        prepareReviewData(userId);
    }

    private void initComponent() {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://keen-chipmunk-quickly.ngrok-free.app")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        bookApiService = retrofit.create(BookApiService.class);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ivBookCover = findViewById(R.id.ivBookCover);
        tvBookTitle = findViewById(R.id.tvBookTitle);
        tvBookAuthor = findViewById(R.id.tvBookAuthor);
        tvBookPublisher = findViewById(R.id.tvBookPublisher);
        tvBookISBN = findViewById(R.id.tvBookISBN);
        tvBookPublishedDate = findViewById(R.id.tvBookPublishedDate);
        tvBookPages = findViewById(R.id.tvBookPages);

        ratingBar = findViewById(R.id.rbRating);
        etReview = findViewById(R.id.etReview);
        cbHideName = findViewById(R.id.cbHideName);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        btnDeleteReview = findViewById(R.id.btnDeleteReview);

        String bookId = getIntent().getStringExtra("id");
        if (bookId != null) {
            prepareBookData(bookId);
        }

        Log.d("ReviewActivity", "Score before: " + ratingBar.getNumStars());
    }

    private void prepareAddReviewData(String userId) {
        btnSubmitReview.setOnClickListener(v -> {
            String content = etReview.getText().toString().trim();
            int rating = (int) ratingBar.getRating();
            boolean hideName = cbHideName.isChecked();

            if (content.isEmpty()) {
                etReview.setError("Review cannot be empty");
                etReview.requestFocus();
                Toast.makeText(ReviewActivity.this, "Review cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rating == 0) {
                ratingBar.requestFocus();
                Toast.makeText(ReviewActivity.this, "Rating cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Review newReview = new Review(book.getId(), userId, content, rating, hideName);
            db.collection("reviews")
                    .add(newReview)
                    .addOnSuccessListener(docRef -> Log.d("ReviewActivity", "Review added: " + docRef.getId()))
                    .addOnFailureListener(e -> Log.e("ReviewActivity", "Error adding review", e));

            updateBookRating(0, rating);
            finish();
        });
    }

    private void prepareUpdateReviewData(String userId) {
        btnSubmitReview.setOnClickListener(v -> {
            String content = etReview.getText().toString().trim();
            int rating = (int) ratingBar.getRating();
            boolean hideName = cbHideName.isChecked();

            if (content.isEmpty()) {
                etReview.setError("Review cannot be empty");
                etReview.requestFocus();
                Toast.makeText(ReviewActivity.this, "Review cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rating == 0) {
                ratingBar.requestFocus();
                Toast.makeText(ReviewActivity.this, "Rating cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("reviews")
                    .whereEqualTo("uid", userId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            db.collection("reviews").document(document.getId())
                                    .update("content", content, "rating", rating, "hideName", hideName)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Review updated"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating review", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error fetching reviews", e));

            updateBookRating(scoreBefore, rating);
            finish();
        });
    }

    private void prepareDeleteReviewData(String userId) {
        btnDeleteReview.setOnClickListener(v -> {
            db.collection("reviews")
                    .whereEqualTo("uid", userId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            db.collection("reviews").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Review deleted"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error deleting review", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error fetching reviews", e));

            updateBookRating(scoreBefore, 0);
            finish();
        });
    }

    private void prepareBookData(String bookId) {
        bookApiService.getBookById(bookId).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(@NonNull Call<Book> call, @NonNull Response<Book> response) {
                if (response.isSuccessful() && response.body() != null) {
                    book = response.body();
                    setBookData();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Book> call, @NonNull Throwable t) {
                Log.e("DetailActivity", "Failed to fetch book: " + t.getMessage());
            }
        });
    }

    private void prepareReviewData(String userId) {
        db.collection("reviews")
                .whereEqualTo("uid", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (Review review : querySnapshot.toObjects(Review.class)) {
                        setReviewData(review);
                    }
                })
                .addOnFailureListener(e -> Log.e("ReviewActivity", "Error fetching reviews", e));
    }

    private void setBookData() {
        if (book == null) return;

        Picasso.get().load(book.getCover()).into(ivBookCover);
        tvBookTitle.setText(book.getTitle());
        tvBookAuthor.setText(book.getAuthor());
        tvBookPublisher.setText(book.getPublisher());
        tvBookISBN.setText(book.getIsbn());
        tvBookPublishedDate.setText(book.getPublicationDate());
        tvBookPages.setText(String.valueOf(book.getNumPages()));
    }

    private void setReviewData(Review review) {
        etReview.setText(review.getContent());
        ratingBar.setRating(review.getRating());
        cbHideName.setChecked(review.getHideName());

        scoreBefore = review.getRating();
    }

    private void updateBookRating(int oldRating, int newRating) {
        bookApiService.updateRating(book.getId(), oldRating, newRating).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(@NonNull Call<Book> call, @NonNull Response<Book> response) {
                if (response.isSuccessful()) {
                    Log.d("DetailActivity", "Book rating updated successfully");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Book> call, @NonNull Throwable t) {
                Log.e("DetailActivity", "Failed to update rating: " + t.getMessage());
            }
        });
    }
}
