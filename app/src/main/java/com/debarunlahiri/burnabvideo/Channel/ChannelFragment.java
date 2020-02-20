package com.debarunlahiri.burnabvideo.Channel;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnabvideo.LoginActivity;
import com.debarunlahiri.burnabvideo.MyVideos.MyVideosActivity;
import com.debarunlahiri.burnabvideo.R;
import com.debarunlahiri.burnabvideo.RegisterActivity;
import com.debarunlahiri.burnabvideo.Settings.SettingsActivity;
import com.debarunlahiri.burnabvideo.Utils.Variables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelFragment extends Fragment {

    private Toolbar profiletoolbar;

    private CircleImageView civChannelProfilePic;
    private TextView tvChannelPersonName, tvChannelEmail;
    private CardView cvChannel, cvMyChannel;
    private Button bChangeChannel;

    private NestedScrollView nswProfile;
    private FrameLayout flProfileContentHeader;
    private CardView cvProfileMyVideos;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;


    private Dialog dialog;
    private Context mContext;
    private List<Channel> channelList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;



    public ChannelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_channel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        profiletoolbar = view.findViewById(R.id.profiletoolbar);
        profiletoolbar.setTitle("Profile");
        ((AppCompatActivity)getActivity()).setSupportActionBar(profiletoolbar);

        nswProfile = view.findViewById(R.id.nswProfile);
        flProfileContentHeader = view.findViewById(R.id.flProfileContentHeader);

        civChannelProfilePic = view.findViewById(R.id.civChannelProfilePic);
        tvChannelPersonName = view.findViewById(R.id.tvChannelPersonName);
        tvChannelEmail = view.findViewById(R.id.tvChannelEmail);
        bChangeChannel = view.findViewById(R.id.bChangeChannel);

        cvProfileMyVideos = view.findViewById(R.id.cvProfileMyVideos);
        cvChannel = view.findViewById(R.id.cvChannel);
        cvMyChannel = view.findViewById(R.id.cvMyChannel);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        if (currentUser == null) {
            nswProfile.setVisibility(View.INVISIBLE);

            LayoutInflater factory = LayoutInflater.from(getActivity());
            View myView = factory.inflate(R.layout.dialog_account_login_register, null);
            flProfileContentHeader.addView(myView);

            Button dialogregisterbutton = myView.findViewById(R.id.dialogregisterbutton);
            Button dialogloginbutton = myView.findViewById(R.id.dialogloginbutton);

            dialogloginbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                }
            });

            dialogregisterbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                    startActivity(registerIntent);
                }
            });

        } else {
            user_id = currentUser.getUid();
            getChannelDetails();

            bChangeChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_select_channel);
                    dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    dialog.show();

                    RecyclerView rvDialogSelectChannel = dialog.findViewById(R.id.rvDialogSelectChannel);
                    final SelectChannelAdapter selectChannelAdapter = new SelectChannelAdapter(mContext, channelList, dialog);
                    linearLayoutManager = new LinearLayoutManager(mContext);
                    rvDialogSelectChannel.setAdapter(selectChannelAdapter);
                    rvDialogSelectChannel.setLayoutManager(linearLayoutManager);

                    mDatabase.child("users").child(user_id).child("channels").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Channel channel = dataSnapshot.getValue(Channel.class);
                            channelList.add(channel);
                            selectChannelAdapter.notifyDataSetChanged();
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
            });

            cvMyChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent channelIntent = new Intent(getActivity(), ChannelActivity.class);
                    channelIntent.putExtra("channel_id", Variables.selected_channel_id);
                    channelIntent.putExtra("user_id", user_id);
                    startActivity(channelIntent);
                }
            });


        }

        cvProfileMyVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getActivity(), MyVideosActivity.class);
                startActivity(profileIntent);
            }
        });

        cvChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
    }

    private void getChannelDetails() {
        mDatabase.child("channels").child(Variables.selected_channel_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("channel_name").getValue().toString();
                String email = dataSnapshot.child("channel_email").getValue().toString();
//                String profile_image = dataSnapshot.child("profile_image").getValue().toString();

                if (dataSnapshot.child("channel_profile_pic").getValue().toString().equals("")) {
                    Glide.with(getActivity()).load(R.drawable.default_profile_pic).into(civChannelProfilePic);
                } else {
                    String profile_image = dataSnapshot.child("channel_profile_pic").getValue().toString();
                    Glide.with(getActivity()).load(profile_image).into(civChannelProfilePic);
                }

//                Glide.with(getActivity()).load(profile_image).into(civChannelProfilePic);
                tvChannelPersonName.setText(name);
                tvChannelEmail.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {


        }
        return super.onOptionsItemSelected(item);
    }




}
