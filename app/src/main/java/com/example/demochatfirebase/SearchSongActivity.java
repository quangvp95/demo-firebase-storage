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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.base.recycler.BaseRecyclerAdapter;
import com.example.base.recycler.BaseRecyclerViewHolder;
import com.example.base.recycler.RecyclerActionListener;
import com.example.base.recycler.RecyclerViewType;
import com.example.demochatfirebase.model.Constants;
import com.example.demochatfirebase.model.Song;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchSongActivity extends AppCompatActivity {
    public static final String EXTRA_SONG_ID = "EXTRA_SONG_ID";
    BaseRecyclerAdapter<Song> mViewSearchAdapter;
    private final com.google.firebase.database.ValueEventListener mValueEventListener = new com.google.firebase.database.ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println(snapshot);
            Gson gson = new Gson();

            Object object = snapshot.getValue(Object.class);
            String json = gson.toJson(object);

            try {
                Type listType = new TypeToken<HashMap<String, Song>>() {
                }.getType();
                HashMap<String, Song> data = gson.fromJson(json, listType);
                if (data != null) {
                    mViewSearchAdapter.update(new ArrayList<>(data.values()));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Type listType = new TypeToken<ArrayList<Song>>() {
                }.getType();
                ArrayList<Song> data = gson.fromJson(json, listType);
                if (data != null) {
                    data.remove(null);
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
            final Intent data = new Intent();
            data.putExtra(EXTRA_SONG_ID, mViewSearchAdapter.getData().get(position).getId());
            setResult(Activity.RESULT_OK, data);
            finish();
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

        setTitle("Search Song Activity");

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
                        Constants.FIREBASE_REALTIME_SONG_PATH)
                        .orderByChild("nameSong")
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
        mViewSearchAdapter = new BaseRecyclerAdapter<Song>(new ArrayList<>(), mRecyclerViewAction) {
            @Override
            public int getItemViewType(int position) {
                return RecyclerViewType.TYPE_SONG_SEARCH;
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
