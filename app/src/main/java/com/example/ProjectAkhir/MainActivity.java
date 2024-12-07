package com.example.ProjectAkhir;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        Intent intent;
        if (currentUser != null) {
            // Pengguna sudah login, pindah ke HomeActivity
            intent = new Intent(this, HomeActivity.class);
            Log.d("MainActivity", "User is already logged in with email: " + currentUser.getEmail());
        } else {
            // Pengguna belum login, pindah ke LoginActivity
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}