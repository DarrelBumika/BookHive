package com.example.ProjectAkhir.service;

import retrofit2.Call;
import retrofit2.http.*;

import com.example.ProjectAkhir.model.Book;

import java.util.ArrayList;

public interface BookApiService {
    // GET semua buku
    @GET("api/books")
    Call<ArrayList<Book>> getAllBooks();

    // GET buku berdasarkan ID
    @GET("api/books/{id}")
    Call<Book> getBookById(@Path("id") String id);

    // GET cari buku berdasarkan pengarang
    @GET("api/books/search/{author}")
    Call<ArrayList<Book>> searchBooksByAuthor(@Path("author") String author);

    // GET cari buku berdasarkan judul
    @GET("api/books/search/{title}")
    Call<ArrayList<Book>> searchBooksByTitle(@Path("title") String title);

    // Update rating buku
    @PUT("api/score/{book_id}/{score_before}/{score_after}")
    Call<Book> updateRating(@Path("book_id") String book_id, @Path("score_before") int score_before, @Path("score_after") int score_after);
}