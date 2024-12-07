package com.example.ProjectAkhir.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ProjectAkhir.R;
import com.example.ProjectAkhir.model.Review;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.CustomViewHolder> {

    private Context context;
    private final ArrayList<Review> reviews;
    private final LayoutInflater inflater;

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String id);
    }

    public ReviewListAdapter(ArrayList<Review> reviews, Context context, OnItemClickListener listener) {
        this.context = context;
        this.reviews = reviews;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.single_review, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Review review = reviews.get(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("uid", review.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            holder.tvUsername.setText(task.getResult().getDocuments().get(0).getString("username"));
                        }
                    }
                });

        holder.tvReview.setText(review.getContent());
        holder.ratingBar.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private TextView tvReview;
        private RatingBar ratingBar;

        public CustomViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvReview = itemView.findViewById(R.id.tvReview);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
