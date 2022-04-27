package com.example.demochatfirebase.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.base.recycler.FirebaseListAdapter;
import com.example.demochatfirebase.DetailAlbumActivity;
import com.example.demochatfirebase.R;
import com.example.demochatfirebase.model.Album;
import com.firebase.client.Query;

public class AlbumListAdapter extends FirebaseListAdapter<Album> {
    /**
     * @param mRef     The Firebase location to watch for data changes. Can also be a slice of a
     *                 location, using some combination of <code>limit()</code>,
     *                 <code>startAt()</code>, and <code>endAt()</code>,
     * @param activity The activity containing the ListView
     */
    public AlbumListAdapter(Query mRef, Activity activity) {
        super(mRef, Album.class, R.layout.album_item, activity);
    }

    @Override
    protected void populateView(int index, View view, Album model) {
        String author = model.getNameAlbum();
        TextView authorText = (TextView) view.findViewById(R.id.albumName);
        authorText.setText(String.format("%s: ", author));
        // If the message was sent by this user, color it differently
        authorText.setTextColor(Color.BLUE);
        ((TextView) view.findViewById(R.id.albumInfo)).setText(String.format("%s bài hát", model.getSongList().size()));
        view.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef.getRef().child(String.valueOf(model.getIdAlbum())).removeValue();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), DetailAlbumActivity.class);
                intent.putExtra(DetailAlbumActivity.EXTRA_ALBUM, model);
                view.getContext().startActivity(intent);
            }
        });
    }
}
