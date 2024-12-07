package com.example.ProjectAkhir.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ProjectAkhir.R;
import com.squareup.picasso.Picasso;

import com.example.ProjectAkhir.model.Book;

import java.util.ArrayList;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.CustomViewHolder> {

    private Context context;
    private final ArrayList<Book> books;
    private final LayoutInflater inflater;

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String id);
    }

    public BookListAdapter(ArrayList<Book> books, Context context, OnItemClickListener listener) {
        this.context = context;
        this.books = books;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.single_book, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Book book = books.get(position);

        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookAuthor.setText(book.getAuthor());
        holder.tvBookDescription.setText(book.getDescription());

        Picasso.get()
                .load(book.getBanner())
                .into(holder.ivBookCover);

        Picasso.get()
                .load(book.getCover())
                .into(holder.ivBookImage);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(book.getId()));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView tvBookTitle;
        public TextView tvBookAuthor;
        public TextView tvBookDescription;
        public ImageView ivBookCover;
        public ImageView ivBookImage;

        public CustomViewHolder(View itemView) {
            super(itemView);

            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvBookDescription = itemView.findViewById(R.id.tvBookDescription);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);
        }
    }
}
