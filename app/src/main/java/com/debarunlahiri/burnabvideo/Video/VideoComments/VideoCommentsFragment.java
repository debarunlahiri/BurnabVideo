package com.debarunlahiri.burnabvideo.Video.VideoComments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoCommentsFragment extends Fragment {

    private ImageButton ibVideoCommentsClose;
    private TextView tvVideoComment, tvVideoCommentPersonName, tvVideoCommenting;
    private CircleImageView civVideoCommentProfilePic, civCommentingProfilePic;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String comment_id, video_id, comment_user_id;
    private String comment, comment_name, comment_profile_pic, name, profile_pic;
    private String user_id;
    private boolean openReplies = false;

    private Context mContext;


    public VideoCommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        LocalBroadcastManager.getInstance(mContext).registerReceiver(openRepliesFragmentBroadcastReceiver,
                new IntentFilter("showReplies"));

        ibVideoCommentsClose = view.findViewById(R.id.ibVideoCommentsClose);
        civVideoCommentProfilePic = view.findViewById(R.id.civVideoCommentProfilePic);
        tvVideoComment = view.findViewById(R.id.tvVideoComment);
        tvVideoCommentPersonName = view.findViewById(R.id.tvVideoCommentPersonName);
        civCommentingProfilePic = view.findViewById(R.id.civCommentingProfilePic);
        tvVideoCommentPersonName = view.findViewById(R.id.tvVideoCommentPersonName);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        if (currentUser != null) {
            user_id = currentUser.getUid();
            setUserDetails();
        }


        ibVideoCommentsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openRepliesIntent = new Intent("openRepliesFragment");
                openRepliesIntent.putExtra("openReplies", false);
                LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(openRepliesIntent);
            }
        });
    }

    private void setUserDetails() {
        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name = dataSnapshot.child("name").getValue().toString();
                    profile_pic = dataSnapshot.child("profile_image").getValue().toString();

                    Glide.with(mContext).load(comment_profile_pic).into(civCommentingProfilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setComment() {
        mDatabase.child("videos").child(video_id).child("comments").child(comment_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comment = dataSnapshot.child("comment").getValue().toString();
                comment_user_id = dataSnapshot.child("user_id").getValue().toString();

                tvVideoComment.setText(comment);

                mDatabase.child("users").child(comment_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        comment_name = dataSnapshot.child("name").getValue().toString();
                        comment_profile_pic = dataSnapshot.child("profile_image").getValue().toString();

                        tvVideoCommentPersonName.setText(comment_name);
                        Glide.with(mContext).load(comment_profile_pic).into(civVideoCommentProfilePic);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public BroadcastReceiver openRepliesFragmentBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            comment_id = intent.getStringExtra("comment_id");
            video_id = intent.getStringExtra("video_id");
            openReplies = intent.getBooleanExtra("openReplies", false);

            if (openReplies) {
                Toast.makeText(getActivity(), comment_id, Toast.LENGTH_SHORT).show();
                setComment();
            }
        }
    };
}
