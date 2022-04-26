package com.example.demochatfirebase.ui.holder;

import android.view.View;
import android.widget.TextView;

import com.example.base.recycler.BaseRecyclerViewHolder;
import com.example.base.recycler.RecyclerActionListener;
import com.example.demochatfirebase.R;
import com.example.demochatfirebase.model.Album;

public class AlbumItemHolder extends BaseRecyclerViewHolder<Album> {
    private final TextView mNameAlbum;

    public AlbumItemHolder(View itemView) {
        super(itemView);
        itemView.findViewById(R.id.image_item_search).setVisibility(View.INVISIBLE);
        mNameAlbum = itemView.findViewById(R.id.name_item_search);
    }

    @Override
    public void bindViewHolder(Album data) {
        if (data == null) return;
        mNameAlbum.setText(data.getNameAlbum());
    }

    @Override
    public void setupClickableViews(RecyclerActionListener actionListener) {
        itemView.setOnClickListener(v -> actionListener.onViewClick(getBindingAdapterPosition(), v, AlbumItemHolder.this));
    }
}
