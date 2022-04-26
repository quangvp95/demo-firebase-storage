package com.example.demochatfirebase.ui.popup;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.example.demochatfirebase.R;
import com.example.demochatfirebase.model.Album;
import com.example.demochatfirebase.model.Constants;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class UpdateAlbumPopup extends AlertDialog {
    EditText etAlbum;

    public UpdateAlbumPopup(@NonNull Context context, Album album) {
        super(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert);

        View view = LayoutInflater.from(context).inflate(R.layout.update_album_popup, null);

        etAlbum = view.findViewById(R.id.etNameAlbum);
        etAlbum.setText(album.getNameAlbum());

        setView(view);

        setButton(BUTTON_POSITIVE, null,
            ResourcesCompat.getDrawable(context.getResources(),
                android.R.drawable.ic_popup_sync, null),
            (dialogInterface, i) -> {
                Firebase mFirebaseRef = new Firebase(
                    Constants.FIREBASE_REALTIME_DATABASE_URL).child(
                    Constants.FIREBASE_REALTIME_ALBUM_PATH + "/" + album.getIdAlbum());
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("nameAlbum", etAlbum.getText().toString());
                mFirebaseRef.updateChildren(hopperUpdates);
            });
        setButton(BUTTON_NEGATIVE, null,
            ResourcesCompat.getDrawable(context.getResources(),
                android.R.drawable.ic_menu_close_clear_cancel, null), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    cancel();
                }
            });
    }
}
