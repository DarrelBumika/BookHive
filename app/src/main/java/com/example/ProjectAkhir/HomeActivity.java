package com.example.ProjectAkhir;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ProjectAkhir.adapter.BookListAdapter;
import com.example.ProjectAkhir.model.Book;
import com.example.ProjectAkhir.service.BookApiService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    ArrayList<Book> bookList;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://keen-chipmunk-quickly.ngrok-free.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    BookApiService bookApiService = retrofit.create(BookApiService.class);

    BookListAdapter bookListAdapter;
    RecyclerView rvBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        bookList = prepareData();

        bookListAdapter = new BookListAdapter(bookList, HomeActivity.this, id -> {
            Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        });
        LinearLayoutManager manager = new LinearLayoutManager(HomeActivity.this);
        rvBooks.setLayoutManager(manager);
        rvBooks.setAdapter(bookListAdapter);
    }

    private void initComponents() {
        rvBooks = findViewById(R.id.rvBooks);
    }

    private ArrayList<Book> prepareData() {

        ArrayList<Book> bookList = new ArrayList<>();

        Call<ArrayList<Book>> call = bookApiService.getAllBooks();
        call.enqueue(new Callback<ArrayList<Book>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Book>> call, @NonNull Response<ArrayList<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Book> books = response.body();
                    Log.d("Success", "Success: " + books);
                    bookList.addAll(books);
                    bookListAdapter.notifyDataSetChanged();
                } else {
                    Log.e("Error", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Book>> call, @NonNull Throwable t) {
                Log.e("Error", "Error: " + t.getMessage());
            }
        });

        return bookList;
    }
}