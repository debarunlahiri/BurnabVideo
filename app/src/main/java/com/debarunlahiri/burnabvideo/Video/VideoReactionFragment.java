package com.debarunlahiri.burnabvideo.Video;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.burnabvideo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoReactionFragment extends Fragment {

    private CardView cvVideoShare, cvVideoLike, cvVideoDislike;
    private TextView tvVideoLike, tvVideoDislike;
    private ImageView ivVideoLike, ivVideoDislike;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    
    public String video_id, user_id, video_user_id;
    public int video_like_count, video_dislike_count;
    public boolean userLikedVideo = false;
    public boolean userDislikedVideo = false;


    public VideoReactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            video_id = getArguments().getString("video_id");
            video_user_id = getArguments().getString("video_user_id");
        } else {
            Toast.makeText(getActivity(), "Video Reaction get arguments are null", Toast.LENGTH_SHORT).show();
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_reaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cvVideoDislike = view.findViewById(R.id.cvVideoDislike);
        cvVideoLike = view.findViewById(R.id.cvVideoLike);
        cvVideoShare = view.findViewById(R.id.cvVideoShare);
        tvVideoLike = view.findViewById(R.id.tvVideoLike);
        tvVideoDislike = view.findViewById(R.id.tvVideoDislike);
        ivVideoLike = view.findViewById(R.id.ivVideoLike);
        ivVideoDislike = view.findViewById(R.id.ivVideoDislike);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        cvVideoLike.setBackgroundResource(R.drawable.cardview_border_bg);
        cvVideoDislike.setBackgroundResource(R.drawable.cardview_border_bg);
        cvVideoShare.setBackgroundResource(R.drawable.cardview_border_bg);

        if (currentUser != null) {
            user_id = currentUser.getUid();

            setVideoLikes();
            setVideoDislikes();
            checkUserLikedVideoOrNot();
            checkUserDislikedVideoOrNot();
        }






        //Like Video
        cvVideoLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userLikedVideo == false && userDislikedVideo == true) {
                    setLikeColorBlue();
                    setDislikeColorNormal();
                    removeDislikeFromVideo();
                    likeVideo();
                } else if (userLikedVideo == true && userDislikedVideo == false) {
                    setLikeColorNormal();
                    removeLikeFromVideo();
                } else {
                    setLikeColorBlue();
                    likeVideo();

                }
            }
        });

        //Dislike Video
        cvVideoDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userLikedVideo == true && userDislikedVideo == false) {
                    setLikeColorNormal();
                    setDislikeColorOrange();
                    removeLikeFromVideo();
                    dislikeVideo();
                } else if (userLikedVideo == false && userDislikedVideo == true) {
                    setDislikeColorNormal();
                    removeDislikeFromVideo();
                } else {
                    setDislikeColorOrange();
                    dislikeVideo();
                }
            }
        });
    }

    private void setLikeColorNormal() {
        cvVideoLike.setBackgroundResource(R.drawable.cardview_border_bg);
        tvVideoLike.setTextColor(Color.BLACK);
        ivVideoLike.setImageTintList(ColorStateList.valueOf(Color.BLACK));
    }

    private void setLikeColorBlue() {
        cvVideoLike.setBackgroundResource(R.drawable.cardview_blue_bg);
        tvVideoLike.setTextColor(Color.WHITE);
        ivVideoLike.setImageTintList(ColorStateList.valueOf(Color.WHITE));
    }

    private void setDislikeColorNormal() {
        cvVideoDislike.setBackgroundResource(R.drawable.cardview_border_bg);
        tvVideoDislike.setTextColor(Color.BLACK);
        ivVideoDislike.setImageTintList(ColorStateList.valueOf(Color.BLACK));
    }

    private void setDislikeColorOrange() {
        cvVideoDislike.setBackgroundResource(R.drawable.cardview_orange_bg);
        tvVideoDislike.setTextColor(Color.WHITE);
        ivVideoDislike.setImageTintList(ColorStateList.valueOf(Color.WHITE));
    }

    private void checkUserDislikedVideoOrNot() {
        mDatabase.child("videos").child(video_id).child("dislikes").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userDislikedVideo = true;
                    setDislikeColorOrange();
                } else {
                    userDislikedVideo = false;
                    setDislikeColorNormal();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserLikedVideoOrNot() {
        mDatabase.child("videos").child(video_id).child("likes").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userLikedVideo = true;
                    setLikeColorBlue();
                } else {
                    userLikedVideo = false;
                    setLikeColorNormal();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likeVideo() {
        String like_id = mDatabase.child("videos").push().getKey();
        HashMap<String, Object> mVideoLikeDataMap = new HashMap<>();
        mVideoLikeDataMap.put("video_id", video_id);
        mVideoLikeDataMap.put("timestamp", System.currentTimeMillis());
        mVideoLikeDataMap.put("video_user_id", video_user_id);
        mVideoLikeDataMap.put("liked_user_id", user_id);
        mDatabase.child("videos").child(video_id).child("likes").child(user_id).setValue(mVideoLikeDataMap);
        mDatabase.child("usersLikedVideos").child(user_id).child(video_id).setValue(mVideoLikeDataMap);
    }

    private void removeLikeFromVideo() {
        mDatabase.child("videos").child(video_id).child("likes").child(user_id).removeValue();
    }

    private void removeDislikeFromVideo() {
        mDatabase.child("videos").child(video_id).child("dislikes").child(user_id).removeValue();
    }

    private void dislikeVideo() {
        String like_id = mDatabase.child("videos").push().getKey();
        HashMap<String, Object> mVideoLikeDataMap = new HashMap<>();
        mVideoLikeDataMap.put("video_id", video_id);
        mVideoLikeDataMap.put("timestamp", System.currentTimeMillis());
        mVideoLikeDataMap.put("video_user_id", video_user_id);
        mVideoLikeDataMap.put("liked_user_id", user_id);
        mDatabase.child("videos").child(video_id).child("dislikes").child(user_id).setValue(mVideoLikeDataMap);
    }

    private void setVideoDislikes() {
        mDatabase.child("videos").child(video_id).child("dislikes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    video_dislike_count= (int) dataSnapshot.getChildrenCount();
                    tvVideoDislike.setText(String.valueOf(video_dislike_count));
                } else {
                    tvVideoDislike.setText("Dislike");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void setVideoLikes() {
        mDatabase.child("videos").child(video_id).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    video_like_count = (int) dataSnapshot.getChildrenCount();
                    tvVideoLike.setText(String.valueOf(video_like_count));
                } else {
                    tvVideoLike.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
