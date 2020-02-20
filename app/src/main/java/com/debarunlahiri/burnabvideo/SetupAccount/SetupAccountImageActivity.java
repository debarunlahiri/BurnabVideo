package com.debarunlahiri.burnabvideo.SetupAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.debarunlahiri.burnabvideo.R;
import com.debarunlahiri.burnabvideo.StartActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SetupAccountImageActivity extends AppCompatActivity {

    private CircleImageView setupImageCIV;
    private Button setupImageNextbutton, setupImageSkipbutton;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private Uri profileImageUri;
    private Bitmap mCompressedProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account_image);

        setupImageCIV = findViewById(R.id.setupImageCIV);
        setupImageNextbutton = findViewById(R.id.setupImageNextbutton);
        setupImageSkipbutton = findViewById(R.id.setupImageSkipbutton);

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

        setupImageCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(SetupAccountImageActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .setAspectRatio(1, 1)
                                        .start(SetupAccountImageActivity.this);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                                PermissionListener dialogPermissionListener =
                                        DialogOnDeniedPermissionListener.Builder
                                                .withContext(SetupAccountImageActivity.this)
                                                .withTitle("Storage permission")
                                                .withMessage("Storage permission is needed to choose a picture")
                                                .withButtonText(android.R.string.ok)
                                                .withIcon(R.mipmap.ic_launcher)
                                                .build();
                                dialogPermissionListener.onPermissionDenied(response);

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .check();
            }
        });

        setupImageNextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpImage();
            }
        });

        setupImageSkipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("users").child(user_id).child("profile_image").setValue("");
                Intent setupUsernameIntent = new Intent(SetupAccountImageActivity.this, SetupAccountUsernameActivity.class);
                startActivity(setupUsernameIntent);
                finish();
            }
        });
    }

    private void setUpImage() {
        if (profileImageUri == null) {
            Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
        } else {
            final Dialog dialog = new Dialog(SetupAccountImageActivity.this);
            dialog.setContentView(R.layout.loading_dialog_layout);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            File mFileProfileImage = new File(profileImageUri.getPath());

            try {
                mCompressedProfileImage = new Compressor(SetupAccountImageActivity.this).setQuality(15).compressToBitmap(mFileProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
            mCompressedProfileImage.compress(Bitmap.CompressFormat.JPEG, 15, mProfileBAOS);
            byte[] mProfileData = mProfileBAOS.toByteArray();

            final StorageReference mChildRefProfile = storageReference.child("users/profiles/profile_images/" + currentUser.getUid() + ".jpg");

            final UploadTask profile_uploadTask = mChildRefProfile.putBytes(mProfileData);

            profile_uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Task<Uri> uriTask = profile_uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return mChildRefProfile.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                HashMap<String, Object> mDataMap = new HashMap<>();
                                mDataMap.put("profile_image", downloadUri.toString());

                                mDatabase.child("users").child(user_id).updateChildren(mDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent setupUsernameIntent = new Intent(SetupAccountImageActivity.this, SetupAccountUsernameActivity.class);
                                            startActivity(setupUsernameIntent);
                                            finish();
                                            dialog.cancel();
                                        } else {
                                            dialog.cancel();
                                            Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "UriTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "UploadTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sendToStart() {
        Intent loginIntent = new Intent(SetupAccountImageActivity.this, StartActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImageUri = result.getUri();
                Picasso.get().load(profileImageUri).into(setupImageCIV);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        }
    }
}
