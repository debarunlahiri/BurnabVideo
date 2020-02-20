package com.debarunlahiri.burnabvideo.Home.SubscriptionFeeds;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.debarunlahiri.burnabvideo.R;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFeedFragment extends Fragment {

    private Context mContext;
    private RecyclerView rvSubscriptionFeeds;
    private SubscriptionFeedsAdapter subscriptionFeedsAdapter;
    private List<SubscriptionFeeds> subscriptionFeedsList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String name, bio;
    private String user_id;




    public SubscriptionFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscription_feed, container, false);

        subscriptionFeedsList.clear();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        rvSubscriptionFeeds = view.findViewById(R.id.rvSubscriptionFeeds);
        subscriptionFeedsAdapter = new SubscriptionFeedsAdapter(mContext, subscriptionFeedsList);
        linearLayoutManager = new LinearLayoutManager(mContext);
        rvSubscriptionFeeds.setAdapter(subscriptionFeedsAdapter);
        rvSubscriptionFeeds.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        if (currentUser != null) {
            user_id = currentUser.getUid();

        }

        getAllVideos();


    }

    private void getAllVideos() {
        mDatabase.child("videos").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    SubscriptionFeeds subscriptionFeeds = dataSnapshot.getValue(SubscriptionFeeds.class);
                    subscriptionFeedsList.add(subscriptionFeeds);
                    subscriptionFeedsAdapter.notifyDataSetChanged();
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
