package com.example.demochatfirebase;

import static com.example.demochatfirebase.SearchSongActivity.EXTRA_SONG_ID;
import static com.example.demochatfirebase.model.Constants.FIREBASE_REALTIME_ALBUM_SONG_LIST_PATH;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demochatfirebase.adapter.SongInAlbumAdapter;
import com.example.demochatfirebase.model.Album;
import com.example.demochatfirebase.model.Constants;
import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DetailAlbumActivity extends AppCompatActivity implements SongInAlbumAdapter.OnItemChangeListener {

    private static final int REQUEST_CODE_PICK_MUSIC = 1001;
    public static final String EXTRA_ALBUM = "EXTRA_ALBUM";

    private Firebase mFirebaseAlbumRef;
    private DatabaseReference mDatabase;

    private SongInAlbumAdapter mSongListAdapter;
    private EditText mAlbumName;

    private Album mAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_album);

        mAlbum = (Album) getIntent().getSerializableExtra(EXTRA_ALBUM);

        // Setup our Firebase mFirebaseRef
        mFirebaseAlbumRef = new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(
                Constants.FIREBASE_REALTIME_ALBUM_PATH).child(String.valueOf(mAlbum.getIdAlbum()));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.updateAlbum).setOnClickListener(view -> {
            updateAlbum();
            finish();
        });
        findViewById(R.id.addSong).setOnClickListener(view -> {
            Intent intent = new Intent(DetailAlbumActivity.this, SearchSongActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PICK_MUSIC);
        });
        mAlbumName = findViewById(R.id.etNameAlbum);
        mAlbumName.setText(mAlbum.getNameAlbum());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = findViewById(R.id.list_song_in_album);
        mSongListAdapter = new SongInAlbumAdapter(mFirebaseAlbumRef.child(FIREBASE_REALTIME_ALBUM_SONG_LIST_PATH)
                .orderByKey(), mDatabase, this, this);
        listView.setAdapter(mSongListAdapter);
        mSongListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mSongListAdapter.getCount() - 1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_MUSIC && resultCode == RESULT_OK) {
            int songId = data.getIntExtra(EXTRA_SONG_ID, -1);
            if (songId == -1) return;
            for (Integer i : mAlbum.getSongList())
                if (i == songId) {
                    Toast.makeText(this, "Đã có bài hát trong album", Toast.LENGTH_LONG).show();
                    return;
                }
            mAlbum.getSongList().add(songId);
            updateAlbum();
        }
    }

    public void updateAlbum() {
        String title = String.valueOf(mAlbumName.getText());
        if (TextUtils.isEmpty(title)) {
            return;
        }

        mAlbum = new Album(mAlbum.getIdAlbum(), title, mAlbum.getSongList());

        mFirebaseAlbumRef.setValue(mAlbum);
    }

    @Override
    public void onUp(int index) {
        List<Integer> list = mAlbum.getSongList();
        if (index < 1 || index > list.size() - 1) return;

        int temp = list.get(index);
        list.set(index, list.get(index - 1));
        list.set(index - 1, temp);
        updateAlbum();
    }

    @Override
    public void onDown(int index) {
        List<Integer> list = mAlbum.getSongList();
        if (index < 0 || index > list.size() - 2) return;

        int temp = list.get(index);
        list.set(index, list.get(index + 1));
        list.set(index + 1, temp);
        updateAlbum();
    }

    @Override
    public void onDelete(int index) {
        List<Integer> list = mAlbum.getSongList();
        if (index < 0 || index > list.size() - 1) return;

        list.remove(index);
        updateAlbum();
    }
}
