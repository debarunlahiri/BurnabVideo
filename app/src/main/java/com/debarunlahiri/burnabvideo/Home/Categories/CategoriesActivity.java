package com.debarunlahiri.burnabvideo.Home.Categories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.debarunlahiri.burnabvideo.Home.SubscriptionFeeds.SubscriptionFeedsAdapter;
import com.debarunlahiri.burnabvideo.R;
import com.debarunlahiri.burnabvideo.Video.Videos;
import com.debarunlahiri.burnabvideo.Video.VideosAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private Toolbar categoriestoolbar;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private Context mContext;
    private RecyclerView rvCategoriesVideo;
    private List<Videos> videosList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private VideosAdapter videosAdapter;

    private String category_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        mContext = CategoriesActivity.this;

        Intent getIntent = getIntent();
        category_name = getIntent.getStringExtra("category_name");

        categoriestoolbar = findViewById(R.id.categoriestoolbar);
        categoriestoolbar.setTitle(category_name);
        setSupportActionBar(categoriestoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        categoriestoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        categoriestoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        rvCategoriesVideo = findViewById(R.id.rvCategoriesVideo);
        videosAdapter = new VideosAdapter(mContext, videosList);
        linearLayoutManager = new LinearLayoutManager(mContext);
        rvCategoriesVideo.setAdapter(videosAdapter);
        rvCategoriesVideo.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);


        getVideos();

    }

    private void getVideos() {
        mDatabase.child("videos").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Videos videos = dataSnapshot.getValue(Videos.class);
                    if (videos.getVideoCategory().equals(category_name)) {
                        videosList.add(videos);
                    }
                    videosAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
