package com.example.demochatfirebase.adapter;

import static com.example.demochatfirebase.model.Constants.FIREBASE_REALTIME_SONG_PATH;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.base.recycler.FirebaseListAdapter;
import com.example.demochatfirebase.R;
import com.example.demochatfirebase.model.Song;
import com.firebase.client.Query;
import com.google.firebase.database.DatabaseReference;

public class SongInAlbumAdapter extends FirebaseListAdapter<Integer> {
    private final DatabaseReference mDatabaseReference;
    private final OnItemChangeListener mItemChangeListener;

    /**
     * @param mRef               The Firebase location to watch for data changes. Can also be a slice of a
     *                           location, using some combination of <code>limit()</code>,
     *                           <code>startAt()</code>, and <code>endAt()</code>,
     * @param mDatabaseReference
     * @param activity           The activity containing the ListView
     * @param listener
     */
    public SongInAlbumAdapter(Query mRef, DatabaseReference mDatabaseReference, Activity activity,
                              OnItemChangeListener listener) {
        super(mRef, Integer.class, R.layout.song_item_in_album_layout, activity);
        this.mDatabaseReference = mDatabaseReference;
        mItemChangeListener = listener;
    }

    @Override
    protected void populateView(int index, View view, Integer model) {
        TextView songName = (TextView) view.findViewById(R.id.songName);
        songName.setText(String.valueOf(model));
        ImageView songImage = (ImageView) view.findViewById(R.id.songImage);

        mDatabaseReference.child(FIREBASE_REALTIME_SONG_PATH).child(String.valueOf(model)).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Song data = task.getResult().getValue(Song.class);
                        if (data == null) return;
                        songName.setText(data.getNameSong());
                        Glide.with(songImage)
                                .load(data.getImageUrl())
                                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                                .apply(RequestOptions.
                                        placeholderOf(R.drawable.placeholder_music))
                                .into(songImage);
                    }

                });
        view.findViewById(R.id.btnDown).setOnClickListener(view13 -> mItemChangeListener.onDown(index));
        view.findViewById(R.id.btnUp).setOnClickListener(view12 -> mItemChangeListener.onUp(index));
        view.findViewById(R.id.btnDelete).setOnClickListener(view1 -> mItemChangeListener.onDelete(index));
    }

    public interface OnItemChangeListener {
        void onUp(int index);

        void onDown(int index);

        void onDelete(int index);
    }
}
