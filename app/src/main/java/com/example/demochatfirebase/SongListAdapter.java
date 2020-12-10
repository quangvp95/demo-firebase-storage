package com.example.demochatfirebase;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.chat.FirebaseListAdapter;
import com.example.demochatfirebase.model.Song;
import com.firebase.client.Query;

public class SongListAdapter extends FirebaseListAdapter<Song> {
    /**
     * @param mRef        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                    combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param mModelClass Firebase will marshall the data at a location into an instance of a class that you provide
     * @param mLayout     This is the mLayout used to represent a single list item. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of mModelClass.
     * @param activity    The activity containing the ListView
     */
    public SongListAdapter(Query mRef, Activity activity) {
        super(mRef, Song.class, R.layout.chat_message, activity);
    }

    @Override
    protected void populateView(View view, Song model) {
        String author = model.getNameSong();
        TextView authorText = (TextView) view.findViewById(R.id.author);
        authorText.setText(author + ": ");
        // If the message was sent by this user, color it differently
            authorText.setTextColor(Color.BLUE);
        ((TextView) view.findViewById(R.id.message)).setText(model.getLinkUrl());

    }
}
