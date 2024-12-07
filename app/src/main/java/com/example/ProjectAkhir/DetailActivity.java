package com.example.ProjectAkhir;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ProjectAkhir.adapter.ReviewListAdapter;
import com.example.ProjectAkhir.model.Book;
import com.example.ProjectAkhir.model.Review;
import com.example.ProjectAkhir.service.BookApiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {

    // Firebase
    private FirebaseFirestore db;
    private FirebaseUser user;

    // UI Components
    private ImageButton btnBack;
    private ImageView ivBookBanner, ivBookCover;
    private TextView tvBookTitle, tvBookAuthor, tvBookRating, tvBookDescription, tvBookPublisher,
            tvBookISBN, tvBookLanguage, tvBookPublishedDate, tvBookPages, tvBookDimension,
            tvRatingScore, tvReviewCount, tvReadMore;
    private RatingBar rbRating;
    private ProgressBar pbRating5, pbRating4, pbRating3, pbRating2, pbRating1;
    private RecyclerView rvReviews;
    private Button btnWriteReview, btnBuy;

    // Data
    private Book book;
    private ArrayList<Review> reviewList;
    private ReviewListAdapter reviewListAdapter;
    private boolean alreadyReviewed;

    // Network
    private Retrofit retrofit;
    private BookApiService bookApiService;

    // Popup
    LayoutInflater inflater;
    View popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initComponents();
        String bookId = getIntent().getStringExtra("id");

        // Fetch data
        fetchBookData(bookId);
        fetchReviewData(bookId);

        tvReadMore.setOnClickListener(this::showPopupDescription);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Dapatkan kembali ID buku dari Intent
        String bookId = getIntent().getStringExtra("id");

        fetchBookData(bookId);
        fetchReviewData(bookId);

        tvReadMore.setOnClickListener(this::showPopupDescription);
    }

    private void initComponents() {
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://keen-chipmunk-quickly.ngrok-free.app")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        bookApiService = retrofit.create(BookApiService.class);

        // Initialize UI components
        btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(v -> finish());

        ivBookBanner = findViewById(R.id.ivBookBanner);
        ivBookCover = findViewById(R.id.ivBookCover);
        tvBookTitle = findViewById(R.id.tvBookTitle);
        tvBookAuthor = findViewById(R.id.tvBookAuthor);
        rbRating = findViewById(R.id.rbRating);
        tvBookRating = findViewById(R.id.tvBookRating);
        tvBookDescription = findViewById(R.id.tvBookDescription);
        tvBookPublisher = findViewById(R.id.tvBookPublisher);
        tvBookISBN = findViewById(R.id.tvBookISBN);
        tvBookLanguage = findViewById(R.id.tvBookLanguage);
        tvBookPublishedDate = findViewById(R.id.tvBookPublishedDate);
        tvBookPages = findViewById(R.id.tvBookPages);
        tvBookDimension = findViewById(R.id.tvBookDimension);

        tvRatingScore = findViewById(R.id.tvRatingScore);
        tvReviewCount = findViewById(R.id.tvReviewCount);
        pbRating5 = findViewById(R.id.pbRating5);
        pbRating4 = findViewById(R.id.pbRating4);
        pbRating3 = findViewById(R.id.pbRating3);
        pbRating2 = findViewById(R.id.pbRating2);
        pbRating1 = findViewById(R.id.pbRating1);

        tvReadMore = findViewById(R.id.tvReadMore);

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_description, null);

        rvReviews = findViewById(R.id.rvReview);
        reviewList = new ArrayList<>();
        reviewListAdapter = new ReviewListAdapter(reviewList, this, bookId -> {});
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewListAdapter);

        btnWriteReview = findViewById(R.id.btnWrite);
        btnBuy = findViewById(R.id.btnBuy);
    }

    private void fetchBookData(String id) {
        bookApiService.getBookById(id).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(@NonNull Call<Book> call, @NonNull Response<Book> response) {
                if (response.isSuccessful()) {
                    book = response.body();
                    populateBookDetails();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Book> call, @NonNull Throwable t) {
                Log.e("DetailActivity", "Error fetching book data: " + t.getMessage());
            }
        });
    }

    private void fetchReviewData(String id) {
        db.collection("reviews")
                .whereEqualTo("bookId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewList.clear();
                        alreadyReviewed = false;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Review review = document.toObject(Review.class);
                            reviewList.add(review);
                            if (review.getUid().equals(user.getUid())) {
                                alreadyReviewed = true;
                            }
                        }

                        prepareReviewButton(alreadyReviewed);
                        reviewListAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("Firestore", "Error fetching reviews", task.getException());
                    }
                });
    }

    private void populateBookDetails() {
        if (book == null) return;

        Picasso.get().load(book.getBanner()).into(ivBookBanner);
        Picasso.get().load(book.getCover()).into(ivBookCover);
        tvBookTitle.setText(book.getTitle());
        tvBookAuthor.setText(book.getAuthor());
        rbRating.setRating(book.getReviewScore().getAvgScore());
        tvBookRating.setText(String.format("%.1f", book.getReviewScore().getAvgScore()));
        tvBookDescription.setText(book.getDescription());
        tvBookPublisher.setText(book.getPublisher());
        tvBookISBN.setText(book.getIsbn());
        tvBookLanguage.setText(book.getLanguage());
        tvBookPublishedDate.setText(book.getPublicationDate());
        tvBookPages.setText(String.valueOf(book.getNumPages()));
        tvBookDimension.setText(book.getDimension());
        btnBuy.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(book.getStoreLink()));
            startActivity(browserIntent);
        });

        tvRatingScore.setText(String.format("%.1f", book.getReviewScore().getAvgScore()));
        tvReviewCount.setText(String.valueOf(book.getReviewScore().getTotalReview()));
        if (book.getReviewScore().getTotalReview() != 0) {
            pbRating5.setProgress(book.getReviewScore().getStar5() * 100 / book.getReviewScore().getTotalReview());
            pbRating4.setProgress(book.getReviewScore().getStar4() * 100 / book.getReviewScore().getTotalReview());
            pbRating3.setProgress(book.getReviewScore().getStar3() * 100 / book.getReviewScore().getTotalReview());
            pbRating2.setProgress(book.getReviewScore().getStar2() * 100 / book.getReviewScore().getTotalReview());
            pbRating1.setProgress(book.getReviewScore().getStar1() * 100 / book.getReviewScore().getTotalReview());
        }
    }

    private void prepareReviewButton(boolean alreadyReviewed) {
        btnWriteReview.setText(alreadyReviewed ? "Edit" : "Write");
        btnWriteReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("id", book.getId());
            intent.putExtra("alreadyReviewed", alreadyReviewed);
            startActivity(intent);
        });
    }

    private void showPopupDescription(View anchorView) {
        // Inflate ulang popup layout agar dinamis
        View popupView = inflater.inflate(R.layout.popup_description, null);

        // Buat PopupWindow
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );

        // Referensi komponen popup
        TextView popupDescriptionText = popupView.findViewById(R.id.popup_description_text);
        ImageButton closePopupButton = popupView.findViewById(R.id.close_popup_button);

        // Tampilkan deskripsi buku
        if (book != null && book.getDescription() != null && !book.getDescription().isEmpty()) {
            popupDescriptionText.setText(book.getDescription());
        } else {
            popupDescriptionText.setText("Description not available.");
        }

        // Tutup popup ketika tombol "Close" ditekan
        closePopupButton.setOnClickListener(v -> popupWindow.dismiss());

        // Tampilkan popup
        popupWindow.showAtLocation(anchorView, 0, 0, 0);
    }
}
