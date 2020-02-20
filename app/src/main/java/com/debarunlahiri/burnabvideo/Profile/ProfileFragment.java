package com.debarunlahiri.burnabvideo.Profile;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnabvideo.Channel.CreateChannelActivity;
import com.debarunlahiri.burnabvideo.LoginActivity;
import com.debarunlahiri.burnabvideo.R;
import com.debarunlahiri.burnabvideo.RegisterActivity;
import com.debarunlahiri.burnabvideo.Settings.SettingsActivity;
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
public class ProfileFragment extends Fragment {

    private Toolbar profiletoolbar;

    private CircleImageView civChannelProfilePic;
    private TextView tvChannelPersonName, tvChannelEmail;

    private NestedScrollView nswProfile;
    private FrameLayout flProfileContentHeader;
    private CardView cvProfileSettings, cvCreateNewChannel;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profiletoolbar = view.findViewById(R.id.profiletoolbar);
        profiletoolbar.setTitle("Profile");
        ((AppCompatActivity)getActivity()).setSupportActionBar(profiletoolbar);

        nswProfile = view.findViewById(R.id.nswProfile);
        flProfileContentHeader = view.findViewById(R.id.flProfileContentHeader);

        civChannelProfilePic = view.findViewById(R.id.civChannelProfilePic);
        tvChannelPersonName = view.findViewById(R.id.tvChannelPersonName);
        tvChannelEmail = view.findViewById(R.id.tvChannelEmail);
        cvCreateNewChannel = view.findViewById(R.id.cvCreateNewChannel);
        cvProfileSettings = view.findViewById(R.id.cvProfileSettings);


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
            getUserDetails();
        }

        cvCreateNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createChannelIntent = new Intent(getActivity(), CreateChannelActivity.class);
                startActivity(createChannelIntent);
            }
        });

        cvProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });



    }

    private void getUserDetails() {
        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();

                if (dataSnapshot.child("profile_image").getValue().toString().equals("")) {
                    Glide.with(getActivity()).load(R.drawable.default_profile_pic).into(civChannelProfilePic);
                } else {
                    String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                    Glide.with(getActivity()).load(profile_image).into(civChannelProfilePic);
                }

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
