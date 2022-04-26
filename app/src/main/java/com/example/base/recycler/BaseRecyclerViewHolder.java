package com.example.base.recycler;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerViewHolder<T extends RecyclerData> extends RecyclerView.ViewHolder {

    public BaseRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindViewHolder(T data);

    public abstract void setupClickableViews(RecyclerActionListener actionListener);
}
