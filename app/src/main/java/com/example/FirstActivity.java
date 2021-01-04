package com.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.auth.AuthActivity;
import com.example.auth.HomeActivity;
import com.example.chat.ChatActivity;
import com.example.demochatfirebase.CreateSongActivity;
import com.example.demochatfirebase.ItemListActivity;
import com.example.demochatfirebase.R;
import com.example.filedemo.FileDemoActivity;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        if (view.getId() == R.id.button1) {
            intent = new Intent(this, ChatActivity.class);
        } else if (view.getId() == R.id.button2) {
            intent = new Intent(this, AuthActivity.class);
        } else if (view.getId() == R.id.button3) {
            intent = new Intent(this, FileDemoActivity.class);
        } else if (view.getId() == R.id.button4) {
            intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
    }
}