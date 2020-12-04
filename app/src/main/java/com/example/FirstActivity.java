package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.demochatfirebase.ItemListActivity;
import com.example.demochatfirebase.R;
import com.example.filedemo.FileDemoActivity;

import chat.MainChatActivity;
import firebaseauthdemo.MainActivity;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        if (view.getId() == R.id.button1) {
            intent = new Intent(this, MainChatActivity.class);
        } else if (view.getId() == R.id.button2) {
            intent = new Intent(this, MainActivity.class);
        } else if (view.getId() == R.id.button3) {
            intent = new Intent(this, FileDemoActivity.class);
        } else if (view.getId() == R.id.button3) {
            intent = new Intent(this, ItemListActivity.class);
        }
        startActivity(intent);
    }
}