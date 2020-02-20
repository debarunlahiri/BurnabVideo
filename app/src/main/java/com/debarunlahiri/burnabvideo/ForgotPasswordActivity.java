package com.debarunlahiri.burnabvideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Toolbar forgotpasswordtoolbar;

    private CardView cvForgotPassword;
    private TextView tvForgotPasswordMessage;
    private EditText etForgotPassword;
    private Button bForgotPassword;
    private ProgressBar pbForgotPassword;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotpasswordtoolbar = findViewById(R.id.forgotpasswordtoolbar);
        forgotpasswordtoolbar.setTitle("Forgot Password");
        setSupportActionBar(forgotpasswordtoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        forgotpasswordtoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        forgotpasswordtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cvForgotPassword = findViewById(R.id.cvForgotPassword);
        tvForgotPasswordMessage = findViewById(R.id.tvForgotPasswordMessage);
        etForgotPassword = findViewById(R.id.etForgotPassword);
        bForgotPassword = findViewById(R.id.bForgotPassword);
        pbForgotPassword = findViewById(R.id.pbForgotPassword);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        cvForgotPassword.setVisibility(View.GONE);
        pbForgotPassword.setVisibility(View.GONE);

        bForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbForgotPassword.setVisibility(View.VISIBLE);
                String email = etForgotPassword.getText().toString();

                if (email.isEmpty()) {
                    pbForgotPassword.setVisibility(View.GONE);
                    etForgotPassword.setError("Please enter your registered email");
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pbForgotPassword.setVisibility(View.GONE);
                                cvForgotPassword.setVisibility(View.VISIBLE);
                                tvForgotPasswordMessage.setText("Password reset link has been sent to your registered email address. Please check your mail.");
                            } else {
                                pbForgotPassword.setVisibility(View.GONE);
                                cvForgotPassword.setVisibility(View.VISIBLE);
                                tvForgotPasswordMessage.setText("Error: " + task.getResult());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pbForgotPassword.setVisibility(View.GONE);
                            cvForgotPassword.setVisibility(View.VISIBLE);
                            tvForgotPasswordMessage.setText("Failure: " + e.getMessage());
                        }
                    });
                }
            }
        });

    }
}
