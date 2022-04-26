package com.example.base.recycler;

import android.view.View;

import androidx.annotation.NonNull;

public class InvalidViewHolder extends BaseRecyclerViewHolder<RecyclerData> {

    InvalidViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bindViewHolder(RecyclerData data) {
        throw new IllegalArgumentException("You are using an invalid view holder. You need to create a viewType value in CCRecyclerViewType.java, then check CCViewHolderFactory.java and do the same job as TYPE_INVALID.");
    }

    @Override
    public void setupClickableViews(RecyclerActionListener actionListener) {
        throw new IllegalArgumentException("You are using an invalid view holder. You need to create a viewType value in CCRecyclerViewType.java, then check CCViewHolderFactory.java and do the same job as TYPE_INVALID.");
    }
}
