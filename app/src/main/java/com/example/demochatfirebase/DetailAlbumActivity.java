package com.example.demochatfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.auth.HomeActivity;
import com.example.demochatfirebase.adapter.AlbumListAdapter;
import com.example.demochatfirebase.adapter.SongListAdapter;
import com.example.demochatfirebase.model.Album;
import com.example.demochatfirebase.model.Constants;
import com.example.demochatfirebase.model.Playlist;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class DetailAlbumActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_MUSIC = 1001;
    private static final String EXTRA_ALBUM = "EXTRA_ALBUM";
    private static final String EXTRA_SONG = "EXTRA_SONG";

    private Firebase mFirebaseSongRef;
    private Firebase mFirebaseAlbumRef;
    private Firebase mFirebaseSearchRef;
    private Firebase mFirebaseSearchAlbumRef;
    private ValueEventListener mConnectedListener;

    private DatabaseReference mDatabase;

    private SongListAdapter mChatListAdapter;
    private AlbumListAdapter mAlbumListAdapter;

    private EditText mAlbumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_album);

        // Setup our Firebase mFirebaseRef
        mFirebaseSongRef = new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(
                Constants.FIREBASE_REALTIME_SONG_PATH);
        mFirebaseSearchRef = new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(
                Constants.FIREBASE_REALTIME_SEARCH_PATH);
        mFirebaseAlbumRef = new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(
                Constants.FIREBASE_REALTIME_ALBUM_PATH);
        mFirebaseSearchAlbumRef = new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(
                Constants.FIREBASE_REALTIME_SEARCH_ALBUM_PATH);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.updateAlbum).setOnClickListener(view -> updateAlbum());
        findViewById(R.id.addSong).setOnClickListener(view -> {
            Intent intent = new Intent(DetailAlbumActivity.this, SearchSongActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PICK_MUSIC);
        });
        mAlbumName = findViewById(R.id.etNameAlbum);

        Album album = (Album) getIntent().getSerializableExtra(EXTRA_ALBUM);
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

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseAlbumRef.getRoot().child(".info/connected").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean connected = (Boolean) dataSnapshot.getValue();
                        if (connected) {
                            Toast.makeText(DetailAlbumActivity.this, "Connected to Firebase",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DetailAlbumActivity.this, "Disconnected from Firebase",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        // No-op
                    }
                });

        mChatListAdapter = new SongListAdapter(mFirebaseSongRef.limitToLast(50), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_MUSIC && resultCode == RESULT_OK) {

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseSongRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mAlbumListAdapter.cleanup();
        mChatListAdapter.cleanup();
    }

    public void updateAlbum() {
        String title = String.valueOf(((EditText)findViewById(R.id.newAlbumTitle)).getText());
        if (TextUtils.isEmpty(title)) {
            return;
        }

        int id = mAlbumListAdapter.getCount() == 0 ? 0 : mAlbumListAdapter.getItem(
                mAlbumListAdapter.getCount() - 1).getIdAlbum() + 1;
        Album album = new Album(id, title);

        mFirebaseAlbumRef.child(String.valueOf(album.getIdAlbum())).setValue(album);
        mFirebaseSearchAlbumRef.child(title).child(Constants.ALBUM_ID_NAME).setValue(id);
    }

    public void testSearch() {
//        Firebase firebase =
        String queryText = "B";
        Query queryRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_REALTIME_SONG_PATH)
                .orderByChild("nameSong")
//                .orderByValue()
                .startAt(queryText)
                .endAt(queryText + "\uf8ff")
                .limitToFirst(10);
        queryRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                System.out.println(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void testPush() {
//        List<Song> songs = Arrays.asList(mChatListAdapter.getItem(0),
//                mChatListAdapter.getItem(1),
//                mChatListAdapter.getItem(2),
//                mChatListAdapter.getItem(3),
//                mChatListAdapter.getItem(4)
//        );
//        Playlist playlist = new Playlist(0, "Em không sai, chúng ta sai", songs);
//        new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(Constants.FIREBASE_REALTIME_HOME_PATH).child(String.valueOf(0)).setValue(playlist);
//        songs = Arrays.asList(mChatListAdapter.getItem(5),
//                mChatListAdapter.getItem(6),
//                mChatListAdapter.getItem(7),
//                mChatListAdapter.getItem(8),
//                mChatListAdapter.getItem(10),
//                mChatListAdapter.getItem(9)
//        );
//        playlist = new Playlist(2, "Em không sai, chúng ta sai", songs);
//        new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(Constants.FIREBASE_REALTIME_HOME_PATH).child(String.valueOf(1)).setValue(playlist);
//        songs = Arrays.asList(mChatListAdapter.getItem(1),
//                mChatListAdapter.getItem(6),
//                mChatListAdapter.getItem(4),
//                mChatListAdapter.getItem(8),
//                mChatListAdapter.getItem(0)
//        );
//        playlist = new Playlist(3, "Em không sai, chúng ta sai", songs);
//        new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(Constants.FIREBASE_REALTIME_HOME_PATH).child(String.valueOf(2)).setValue(playlist);
//        songs = Arrays.asList(mChatListAdapter.getItem(3),
//                mChatListAdapter.getItem(10),
//                mChatListAdapter.getItem(7),
//                mChatListAdapter.getItem(11),
//                mChatListAdapter.getItem(5)
//        );
//        playlist = new Playlist(1, "Em không sai, chúng ta sai", songs);
//        new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(Constants.FIREBASE_REALTIME_HOME_PATH).child(String.valueOf(3)).setValue(playlist);
    }

    public void testReceive() {
        new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(Constants.FIREBASE_REALTIME_HOME_PATH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Playlist> map = (HashMap<String, Playlist>) dataSnapshot.getValue();

//                Playlist playlist = map.get(map.keySet().toArray()[0]);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
