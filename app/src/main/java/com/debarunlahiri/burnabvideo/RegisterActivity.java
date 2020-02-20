package com.debarunlahiri.burnabvideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.debarunlahiri.burnabvideo.SetupAccount.SetupAccountNameActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterEmail, etRegisterPassword, etRegisterAge;
    private Button registerbutton;

    private String email, password, age;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterAge = findViewById(R.id.etRegisterAge);
        registerbutton = findViewById(R.id.registerbutton);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        if (currentUser != null) {
            sendToMain();
        }

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        email = etRegisterEmail.getText().toString();
        password = etRegisterPassword.getText().toString();
        age = etRegisterAge.getText().toString();

        if (email.isEmpty()) {
            etRegisterEmail.setError("Please enter your email");
        } else if (password.isEmpty()) {
            etRegisterPassword.setError("Please enter your password");
        } else if (Integer.parseInt(age) < 13) {
            Toast.makeText(getApplicationContext(), "Could not register", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        HashMap<String, Object> mDataMap = new HashMap<>();
                        mDataMap.put("email", email);
                        mDataMap.put("age", age);
                        mDataMap.put("user_id", task.getResult().getUser().getUid());
                        mDatabase.child("users").child(task.getResult().getUser().getUid()).setValue(mDataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sendToSetup();
                            }
                        });
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

    private void sendToSetup() {
        Intent mainIntent = new Intent(RegisterActivity.this, SetupAccountNameActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
