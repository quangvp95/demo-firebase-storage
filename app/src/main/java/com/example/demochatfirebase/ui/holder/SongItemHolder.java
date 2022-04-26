package com.example.demochatfirebase.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.base.recycler.BaseRecyclerViewHolder;
import com.example.base.recycler.RecyclerActionListener;
import com.example.demochatfirebase.R;
import com.example.demochatfirebase.model.Song;

public class SongItemHolder extends BaseRecyclerViewHolder<Song> {
    private final ImageView mImageSong;
    private final TextView mNameSong;

    public SongItemHolder(View itemView) {
        super(itemView);
        mImageSong = itemView.findViewById(R.id.image_item_search);
        mNameSong = itemView.findViewById(R.id.name_item_search);
    }

    @Override
    public void bindViewHolder(Song data) {
        if (data == null) return;
        mNameSong.setText(data.getNameSong());
        Glide.with(mImageSong)
                .load(data.getImageUrl())
                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                .apply(RequestOptions.
                        placeholderOf(R.drawable.placeholder_music))
                .into(mImageSong);
    }

    @Override
    public void setupClickableViews(RecyclerActionListener actionListener) {
        itemView.setOnClickListener(v -> actionListener.onViewClick(getBindingAdapterPosition(), v, SongItemHolder.this));
    }
}
