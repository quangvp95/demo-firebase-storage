package com.example.demochatfirebase;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.base.recycler.BaseRecyclerAdapter;
import com.example.base.recycler.BaseRecyclerViewHolder;
import com.example.base.recycler.RecyclerActionListener;
import com.example.base.recycler.RecyclerViewType;
import com.example.demochatfirebase.model.Album;
import com.example.demochatfirebase.model.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SearchAlbumActivity extends AppCompatActivity {
    public static final String EXTRA_ALBUM_ID = "EXTRA_ALBUM_ID";
    BaseRecyclerAdapter<Album> mViewSearchAdapter;
    private final com.google.firebase.database.ValueEventListener mValueEventListener = new com.google.firebase.database.ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println(snapshot);
            Gson gson = new Gson();

            Object object = snapshot.getValue(Object.class);
            String json = gson.toJson(object);

            try {
                Type listType = new TypeToken<HashMap<String, Album>>() {
                }.getType();
                HashMap<String, Album> data = gson.fromJson(json, listType);
                if (data != null) {
                    mViewSearchAdapter.update(new ArrayList<>(data.values()));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Type listType = new TypeToken<ArrayList<Album>>() {
                }.getType();
                ArrayList<Album> data = gson.fromJson(json, listType);
                if (data != null) {
                    data.removeAll(Collections.singletonList(null));
                    mViewSearchAdapter.update(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    RecyclerActionListener mRecyclerViewAction = new RecyclerActionListener() {
        @Override
        public void onViewClick(int position, View view, BaseRecyclerViewHolder viewHolder) {
            Intent intent = new Intent(view.getContext(), DetailAlbumActivity.class);
            intent.putExtra(DetailAlbumActivity.EXTRA_ALBUM, mViewSearchAdapter.getData().get(position));
            view.getContext().startActivity(intent);
        }

        @Override
        public void onViewLongClick(int position, View view, BaseRecyclerViewHolder viewHolder) {
        }
    };
    private RecyclerView mRecyclerViewSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle("Search Album Activity");

        SearchView mSearchView = findViewById(R.id.search_view);

        SearchManager searchManager = (SearchManager) getSystemService(
                Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
//                mSearchString = newText;
                doFilterAsync(newText);
//                Toast.makeText(getContext(), "Test1 " + newText, Toast.LENGTH_LONG).show();
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
//                mSearchString = query;
                doFilterAsync(query);
//                Toast.makeText(getContext(), "Test2 query: " + query, Toast.LENGTH_LONG).show();

                return true;
            }

            void doFilterAsync(String queryText) {
                boolean isSearchViewVisible = !TextUtils.isEmpty(queryText);
                mRecyclerViewSearch.setVisibility(
                        isSearchViewVisible ? View.VISIBLE : View.INVISIBLE);

                if (!isSearchViewVisible) return;

                Query queryRef = FirebaseDatabase.getInstance().getReference(
                        Constants.FIREBASE_REALTIME_ALBUM_PATH)
                        .orderByChild("nameAlbum")
//                .orderByValue()
                        .startAt(queryText)
                        .endAt(queryText + "\uf8ff")
                        .limitToFirst(10);
                queryRef.addValueEventListener(mValueEventListener);
            }
        });

        mRecyclerViewSearch = findViewById(R.id.recycler_view_search);
        mRecyclerViewSearch.setHasFixedSize(true);
        mRecyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerViewSearch.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerViewSearch.addItemDecoration(dividerItemDecoration);

        mViewSearchAdapter = new BaseRecyclerAdapter<Album>(new ArrayList<>(), mRecyclerViewAction) {
            @Override
            public int getItemViewType(int position) {
                return RecyclerViewType.TYPE_ALBUM_SEARCH;
            }
        };
        mRecyclerViewSearch.setAdapter(mViewSearchAdapter);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
