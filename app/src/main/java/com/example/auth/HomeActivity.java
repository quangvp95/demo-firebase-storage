package com.example.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demochatfirebase.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtras(getIntent().getExtras());
        startActivity(intent);
    }

    public void onLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtras(getIntent().getExtras());
        startActivity(intent);
    }
}
