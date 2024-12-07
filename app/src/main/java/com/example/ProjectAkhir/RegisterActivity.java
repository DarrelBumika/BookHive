package com.example.ProjectAkhir;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Map<String, Object> users = new HashMap<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    EditText etRegisterUsername;
    EditText etRegisterEmail;
    EditText etRegisterPassword;
    EditText etRegisterConfirmPassword;
    Button btnRegister;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        setUnderline(tvLogin);

        btnRegister.setOnClickListener(v -> {
            String username = etRegisterUsername.getText().toString();
            String email = etRegisterEmail.getText().toString();
            String password = etRegisterPassword.getText().toString();

            if (etRegisterUsername.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            } else if (etRegisterEmail.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Email cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            } else if (etRegisterPassword.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            } else if (!etRegisterConfirmPassword.getText().toString().equals(etRegisterConfirmPassword.getText().toString()) || etRegisterConfirmPassword.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Password and confirm password must be the same.", Toast.LENGTH_SHORT).show();
                return;
            }

            signUp(email, password);
            addUser(username, email);
        });

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void setUnderline(TextView textView) {
        SpannableString string = new SpannableString("sign in");
        string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
        textView.setText(string);
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("SIGN UP", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SIGN UP", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void addUser(String username, String email) {
        users.put("username", username);
        users.put("email", email);

        db.collection("users")
                .add(users)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
