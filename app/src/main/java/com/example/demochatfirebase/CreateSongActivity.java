package com.example.demochatfirebase;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.auth.HomeActivity;
import com.example.demochatfirebase.adapter.SongListAdapter;
import com.example.demochatfirebase.model.Constants;
import com.example.demochatfirebase.model.Playlist;
import com.example.demochatfirebase.model.Song;
import com.example.filedemo.FileUtil;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreateSongActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_MUSIC = 1001;
    private static final int PERMISSION_READ_WRITE_EXTERNAL_STORAGE = 1002;
    private String mUsername;
    private Firebase mFirebaseSongRef;
    private Firebase mFirebaseSearchRef;
    private ValueEventListener mConnectedListener;
    private SongListAdapter mChatListAdapter;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private StorageReference mStorageReferenceImages;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        // Make sure we have a mUsername
        setupUsername();

        setTitle(" Admin: " + mUsername);

        // Setup our Firebase mFirebaseRef
        mFirebaseSongRef = new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(
                Constants.FIREBASE_REALTIME_SONG_PATH);
        mFirebaseSearchRef = new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(
                Constants.FIREBASE_REALTIME_SEARCH_PATH);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL);
        mStorageReferenceImages = mStorageReference.child(Constants.FIREBASE_SONG_PATH);
        mStorageReferenceImages.listAll().addOnCompleteListener(
                task -> System.out.println(task.getResult().getItems()));

        findViewById(R.id.sendSongButton).setOnClickListener(view -> onUploadFileClick());

        findViewById(R.id.test_send).setOnClickListener(view -> testPush());
        findViewById(R.id.test_receive).setOnClickListener(view -> testSearch());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = findViewById(R.id.list);
        // Tell our list adapter that we only want 50 messages at a time
        mChatListAdapter = new SongListAdapter(mFirebaseSongRef.limitToLast(50), this);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseSongRef.getRoot().child(".info/connected").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean connected = (Boolean) dataSnapshot.getValue();
                        if (connected) {
                            Toast.makeText(CreateSongActivity.this, "Connected to Firebase",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateSongActivity.this, "Disconnected from Firebase",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        // No-op
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_MUSIC) {
                String filePath = FileUtil.getPath(this, data.getData());
                Uri mUri = Uri.fromFile(new File(filePath));
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(this, mUri);
                String isAudio = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
                if (TextUtils.isEmpty(isAudio)) return;

                uploadFile(mUri);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseSongRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_READ_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickMusic();
            }
        }
    }

    private void setupUsername() {
        if (mUsername == null) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                mUsername = user.getEmail();
            } else {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void onUploadFileClick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_READ_WRITE_EXTERNAL_STORAGE);
        } else {
            pickMusic();
        }
    }

    private void pickMusic() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_PICK_MUSIC);
    }

    private void uploadFile(Uri uri) {
        StorageReference uploadStorageReference = mStorageReferenceImages.child(
                uri.getLastPathSegment());
        final UploadTask uploadTask = uploadStorageReference.putFile(uri);
        showHorizontalProgressDialog("Uploading", "Please wait...");
        uploadTask
                .addOnSuccessListener(taskSnapshot -> {
                    hideProgressDialog();
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    sendMessage(uri, uploadStorageReference.toString());
                    Log.d("MainActivity", downloadUrl.toString());
                })
                .addOnFailureListener(exception -> {
                    exception.printStackTrace();
                    // Handle unsuccessful uploads
                    hideProgressDialog();
                })
                .addOnProgressListener(this, taskSnapshot -> {
                    int progress = (int) (100 * (float) taskSnapshot.getBytesTransferred()
                            / taskSnapshot.getTotalByteCount());
                    Log.i("Progress", progress + "");
                    updateProgress(progress);
                });
    }

    private void sendMessage(Uri uri, String link) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this, uri);

        // Create our 'model', a Chat object
        int id = mChatListAdapter.getCount() == 0 ? 0 : mChatListAdapter.getItem(
                mChatListAdapter.getCount() - 1).getId() + 1;
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if (TextUtils.isEmpty(title)) {
            title = uri.getLastPathSegment();
            if (title.contains(".")) {
                title = title.split("\\.")[0];
            }
        }

        Song chat = new Song(id,
                title,
                "https://photo-resize-zmp3.zadn"
                        + ".vn/w480_r1x1_jpeg/cover/f/a/4/b/fa4b429fda0c4d3d2100f64ad3c7a616.jpg",
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION),
                0,
                "https://photo-resize-zmp3.zadn"
                        + ".vn/w480_r1x1_jpeg/covers/f/f"
                        + "/ff44d05771e686143a49b6a73dd844bb_1519265212.jpg",
                link);
        // Create a new, auto-generated child of that chat location, and save our chat data there
        mFirebaseSongRef.child(String.valueOf(chat.getId())).setValue(chat);
        mFirebaseSearchRef.child(title).child(Constants.SONG_ID_NAME).setValue(id);
    }

    private void showProgressDialog(String title, String message) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(message);
        } else {
            mProgressDialog = ProgressDialog.show(this, title, message, true, false);
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showHorizontalProgressDialog(String title, String body) {

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(body);
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(body);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setProgress(0);
            mProgressDialog.setMax(100);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    public void updateProgress(int progress) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setProgress(progress);
        }
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
        List<Song> songs = Arrays.asList(mChatListAdapter.getItem(0),
                mChatListAdapter.getItem(1),
                mChatListAdapter.getItem(2),
                mChatListAdapter.getItem(3),
                mChatListAdapter.getItem(4)
        );
        Playlist playlist = new Playlist(0, "Em không sai, chúng ta sai", songs);
        new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(Constants.FIREBASE_REALTIME_HOME_PATH).child(String.valueOf(0)).setValue(playlist);
        songs = Arrays.asList(mChatListAdapter.getItem(5),
                mChatListAdapter.getItem(6),
                mChatListAdapter.getItem(7),
                mChatListAdapter.getItem(8),
                mChatListAdapter.getItem(10),
                mChatListAdapter.getItem(9)
        );
        playlist = new Playlist(2, "Em không sai, chúng ta sai", songs);
        new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(Constants.FIREBASE_REALTIME_HOME_PATH).child(String.valueOf(1)).setValue(playlist);
        songs = Arrays.asList(mChatListAdapter.getItem(1),
                mChatListAdapter.getItem(6),
                mChatListAdapter.getItem(4),
                mChatListAdapter.getItem(8),
                mChatListAdapter.getItem(0)
        );
        playlist = new Playlist(3, "Em không sai, chúng ta sai", songs);
        new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(Constants.FIREBASE_REALTIME_HOME_PATH).child(String.valueOf(2)).setValue(playlist);
        songs = Arrays.asList(mChatListAdapter.getItem(3),
                mChatListAdapter.getItem(10),
                mChatListAdapter.getItem(7),
                mChatListAdapter.getItem(11),
                mChatListAdapter.getItem(5)
        );
        playlist = new Playlist(1, "Em không sai, chúng ta sai", songs);
        new Firebase(Constants.FIREBASE_REALTIME_DATABASE_URL).child(Constants.FIREBASE_REALTIME_HOME_PATH).child(String.valueOf(3)).setValue(playlist);
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
