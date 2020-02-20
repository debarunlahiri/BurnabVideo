package com.debarunlahiri.burnabvideo.SetupAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.debarunlahiri.burnabvideo.R;
import com.debarunlahiri.burnabvideo.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class SetupAccountNameActivity extends AppCompatActivity {

    private Button setupnamebutton;
    private EditText etSetupNameBio, etSetupName, etSetupNameFirstName;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String name, bio;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account_name);

        etSetupNameBio = findViewById(R.id.etSetupNameBio);
        etSetupName = findViewById(R.id.etSetupName);
        setupnamebutton = findViewById(R.id.setupnamebutton);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        if (currentUser == null) {
            sendToStart();
        } else {
            user_id = currentUser.getUid();
        }

        setupnamebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpUserDetails();
            }
        });
    }

    private void sendToStart() {
        Intent loginIntent = new Intent(SetupAccountNameActivity.this, StartActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void setUpUserDetails() {
        name = etSetupName.getText().toString();
        bio = etSetupNameBio.getText().toString();

        if (name.isEmpty()) {
            etSetupName.setError("Please enter your name");
        } else {
            HashMap<String, Object> mDataMap = new HashMap<>();
            mDataMap.put("name", name);
            mDataMap.put("bio", bio);
            mDatabase.child("users").child(user_id).updateChildren(mDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent setupImageIntent = new Intent(SetupAccountNameActivity.this, SetupAccountImageActivity.class);
                        startActivity(setupImageIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
