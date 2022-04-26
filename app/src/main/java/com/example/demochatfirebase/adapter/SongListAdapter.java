package com.example.demochatfirebase.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.base.recycler.FirebaseListAdapter;
import com.example.demochatfirebase.R;
import com.example.demochatfirebase.model.Song;
import com.example.demochatfirebase.ui.popup.UpdateSongPopup;
import com.firebase.client.Query;

public class SongListAdapter extends FirebaseListAdapter<Song> {
    /**
     * @param mRef     The Firebase location to watch for data changes. Can also be a slice of a
     *                 location, using some combination of <code>limit()</code>,
     *                 <code>startAt()</code>, and <code>endAt()</code>,
     * @param activity The activity containing the ListView
     */
    public SongListAdapter(Query mRef, Activity activity) {
        super(mRef, Song.class, R.layout.chat_message, activity);
    }

    @Override
    protected void populateView(View view, Song model) {
        String author = model.getNameSong();
        TextView authorText = (TextView) view.findViewById(R.id.author);
        authorText.setText(String.format("%s: ", author));
        // If the message was sent by this user, color it differently
        authorText.setTextColor(Color.BLUE);
        ((TextView) view.findViewById(R.id.message)).setText(model.getSinger());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateSongPopup(v.getContext(), model).show();
            }
        });
    }
}
