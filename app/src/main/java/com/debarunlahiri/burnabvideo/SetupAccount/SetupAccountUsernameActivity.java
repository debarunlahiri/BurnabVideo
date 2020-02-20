package com.debarunlahiri.burnabvideo.SetupAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.debarunlahiri.burnabvideo.MainActivity;
import com.debarunlahiri.burnabvideo.R;
import com.debarunlahiri.burnabvideo.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class SetupAccountUsernameActivity extends AppCompatActivity {

    private EditText etSetupUsername;
    private Button setupUsernamebutton;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String username;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account_username);

        etSetupUsername = findViewById(R.id.etSetupUsername);
        setupUsernamebutton = findViewById(R.id.setupUsernamebutton);

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

        setupUsernamebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupUsername();
            }
        });
    }

    private void setupUsername() {
        username = etSetupUsername.getText().toString();

        if (username.isEmpty()) {
            etSetupUsername.setError("Please enter your username");
        } else {
            HashMap<String, Object> mDataMap = new HashMap<>();
            mDataMap.put("username", username);
            mDataMap.put("hasChannel", false);

            mDatabase.child("users").child(user_id).updateChildren(mDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent mainIntent = new Intent(SetupAccountUsernameActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void sendToStart() {
        Intent loginIntent = new Intent(SetupAccountUsernameActivity.this, StartActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
