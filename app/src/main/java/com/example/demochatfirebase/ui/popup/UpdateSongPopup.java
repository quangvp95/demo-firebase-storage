package com.example.demochatfirebase.ui.popup;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.example.demochatfirebase.R;
import com.example.demochatfirebase.model.Song;
import com.example.demochatfirebase.model.Constants;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class UpdateSongPopup extends AlertDialog {
    EditText etSong;
    EditText etSinger;
    EditText etAlbum;

    public UpdateSongPopup(@NonNull Context context, Song song) {
        super(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert);

        View view = LayoutInflater.from(context).inflate(R.layout.update_song_popup, null);

        etSong = view.findViewById(R.id.etNameSong);
        etSinger = view.findViewById(R.id.etNameSinger);
        etAlbum = view.findViewById(R.id.etNameAlbum);
        etSong.setText(song.getNameSong());
        etSinger.setText(song.getSinger());
        etAlbum.setText(song.getAlbumID());

        setView(view);

        setButton(BUTTON_POSITIVE, null,
            ResourcesCompat.getDrawable(context.getResources(),
                android.R.drawable.ic_popup_sync, null),
            (dialogInterface, i) -> {
                Firebase mFirebaseRef = new Firebase(
                    Constants.FIREBASE_REALTIME_DATABASE_URL).child(
                    Constants.FIREBASE_REALTIME_SONG_PATH + "/" + song.getId());
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("nameSong", etSong.getText().toString());
                hopperUpdates.put("singer", etSinger.getText().toString());
                hopperUpdates.put("albumID", etAlbum.getText().toString());
                mFirebaseRef.updateChildren(hopperUpdates);
            });
        setButton(BUTTON_NEGATIVE, null,
            ResourcesCompat.getDrawable(context.getResources(),
                android.R.drawable.ic_menu_close_clear_cancel, null), new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    cancel();
                }
            });
    }
}
