package com.example.base.recycler;

public interface RecyclerData {

    @RecyclerViewType
    int getViewType();

    default void setViewType(int i) {
    }

    boolean areItemsTheSame(RecyclerData other);

    boolean areContentsTheSame(RecyclerData other);
}
