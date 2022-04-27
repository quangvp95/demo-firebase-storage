package com.example.demochatfirebase;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demochatfirebase.adapter.AlbumListAdapter;
import com.example.demochatfirebase.model.Album;
import com.example.demochatfirebase.model.Constants;
import com.firebase.client.Firebase;

public class AlbumActivity extends AppCompatActivity {
    private Firebase mFirebaseAlbumRef;
    private AlbumListAdapter mAlbumListAdapter;

    private EditText mNewAlbumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_album);

        setTitle("Album Management");

        // Setup our Firebase mFirebaseRef
        mFirebaseAlbumRef = new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(
                Constants.FIREBASE_REALTIME_ALBUM_PATH);

        findViewById(R.id.createAlbum).setOnClickListener(view -> createNewAlbum());
        mNewAlbumName = (EditText) findViewById(R.id.newAlbumTitle);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = findViewById(R.id.list);
        // Tell our list adapter that we only want 50 messages at a time
        mAlbumListAdapter = new AlbumListAdapter(mFirebaseAlbumRef.limitToLast(50), this);
        listView.setAdapter(mAlbumListAdapter);
        mAlbumListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mAlbumListAdapter.getCount() - 1);
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        mAlbumListAdapter.cleanup();
    }

    public void createNewAlbum() {

        String title = String.valueOf(mNewAlbumName.getText());
        if (TextUtils.isEmpty(title)) {
            return;
        }

        int id = mAlbumListAdapter.getCount() == 0 ? 0 : mAlbumListAdapter.getItem(
                mAlbumListAdapter.getCount() - 1).getIdAlbum() + 1;
        Album album = new Album(id, title);

        mFirebaseAlbumRef.child(String.valueOf(album.getIdAlbum())).setValue(album);
        mNewAlbumName.setText("");
    }
}
