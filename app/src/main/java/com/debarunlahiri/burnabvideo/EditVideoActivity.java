package com.debarunlahiri.burnabvideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnabvideo.AddVideo.AddInfoVideoActivity;
import com.debarunlahiri.burnabvideo.AddVideo.ChooseVideoActivity;
import com.debarunlahiri.burnabvideo.AddVideo.VideoUploadActivity;
import com.debarunlahiri.burnabvideo.MyVideos.MyVideos;
import com.debarunlahiri.burnabvideo.MyVideos.MyVideosActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

public class EditVideoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar editinfovideotoolbar;

    private ImageView ivEditInfoVideoThumbnail;
    private Button editinfovideoreplacethumbbutton, editinfovideouploadbutton;
    private TextView etEditInfoVideoTitle, etEditInfoVideoDesc;
    private Spinner videoEditInfoSpinner;

    private ArrayAdapter<String> categoriesDataAdapter;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    private Context mContext;
    private Uri videoUri, videoThumbnailUri;
    private Bitmap videoThumbnailBitmap;
    public String videoId, videoTitle, videoDesc, videoDate, videoThumbnail, videoCategory;
    private int videoHeight, videoWidth;
    private long videoDuration;
    private String categoryItem;
    private Bitmap mCompressedVideoThumbnail;

    private MyVideos myVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);

        mContext = EditVideoActivity.this;

        Bundle bundle = getIntent().getExtras();
        videoId = bundle.getString("videoId");


        editinfovideotoolbar = findViewById(R.id.editinfovideotoolbar);
        editinfovideotoolbar.setTitle("Edit video info");
        setSupportActionBar(editinfovideotoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        editinfovideotoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        editinfovideotoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivEditInfoVideoThumbnail = findViewById(R.id.ivEditInfoVideoThumbnail);;
        editinfovideoreplacethumbbutton = findViewById(R.id.editinfovideoreplacethumbbutton);
        etEditInfoVideoTitle = findViewById(R.id.etEditInfoVideoTitle);
        etEditInfoVideoDesc = findViewById(R.id.etEditInfoVideoDesc);
        editinfovideouploadbutton = findViewById(R.id.editinfovideouploadbutton);
        videoEditInfoSpinner = findViewById(R.id.videoEditInfoSpinner);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        videoEditInfoSpinner.setOnItemSelectedListener(this);

        user_id = currentUser.getUid();

        getVideoInfo();

        List<String> categoriesList = new ArrayList<>();
        categoriesList.add("Select Category");
        categoriesList.add("Entertainment");
        categoriesList.add("Comedy");
        categoriesList.add("Music");
        categoriesList.add("Gaming");
        categoriesList.add("Sports");
        categoriesList.add("How to & Style");
        categoriesList.add("Technology");
        categoriesList.add("Autos & Vehicles");
        categoriesList.add("Education");
        categoriesList.add("Politics");

        categoriesDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoriesList);
        videoEditInfoSpinner.setAdapter(categoriesDataAdapter);

        editinfovideoreplacethumbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(EditVideoActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setAspectRatio(16, 9)
                                        .start(EditVideoActivity.this);
                            }
                            @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                                PermissionListener dialogPermissionListener =
                                        DialogOnDeniedPermissionListener.Builder
                                                .withContext(EditVideoActivity.this)
                                                .withTitle("Read External Permission")
                                                .withMessage("Read External Permission is required in order to choose your beautiful video thumbnail.")
                                                .withButtonText(android.R.string.ok)
                                                .withIcon(R.mipmap.ic_launcher)
                                                .build();
                                dialogPermissionListener.onPermissionDenied(response);
                            }
                            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });


        editinfovideouploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (videoThumbnail == null) {
                    Toast.makeText(getApplicationContext(), "Video thumbnail is missing", Toast.LENGTH_SHORT).show();
                } else if (videoCategory.equals("Select Category")) {
                    Toast.makeText(getApplicationContext(), "Please select a video category", Toast.LENGTH_LONG).show();
                } else {
                    if (videoThumbnailUri != null) {
                        uploadThumbnail(videoThumbnailUri);
                    } else {
                        saveVideoDetails();
                    }




                }

            }
        });

        
    }

    private void saveVideoDetails() {
        videoTitle = etEditInfoVideoTitle.getText().toString();
        videoDesc = etEditInfoVideoDesc.getText().toString();

        if (videoTitle.isEmpty()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM YYYY");
            videoTitle = simpleDateFormat.format(new Date(System.currentTimeMillis()));

            HashMap<String, Object> mVideoThumbnailDataMap = new HashMap<>();
            mVideoThumbnailDataMap.put("user_id", user_id);
            mVideoThumbnailDataMap.put("videoTitle", videoTitle);
            mVideoThumbnailDataMap.put("videoCategory", videoCategory);
            mVideoThumbnailDataMap.put("videoDescription", videoDesc);
            mVideoThumbnailDataMap.put("videoThumbnail", videoThumbnail);

            mDatabase.child("videos").child(videoId).updateChildren(mVideoThumbnailDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        onBackPressed();
                    }
                }
            });
        } else {
            HashMap<String, Object> mVideoThumbnailDataMap = new HashMap<>();
            mVideoThumbnailDataMap.put("user_id", user_id);
            mVideoThumbnailDataMap.put("videoTitle", videoTitle);
            mVideoThumbnailDataMap.put("videoCategory", videoCategory);
            mVideoThumbnailDataMap.put("videoDescription", videoDesc);
            mVideoThumbnailDataMap.put("videoThumbnail", videoThumbnail);

            mDatabase.child("videos").child(videoId).updateChildren(mVideoThumbnailDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        onBackPressed();
                    }
                }
            });
        }
    }

    private void getVideoInfo() {
        mDatabase.child("videos").child(videoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    videoThumbnail = dataSnapshot.child("videoThumbnail").getValue().toString();
                    videoTitle = dataSnapshot.child("videoTitle").getValue().toString();
                    videoDesc = dataSnapshot.child("videoDescription").getValue().toString();
                    videoCategory = dataSnapshot.child("videoCategory").getValue().toString();

                    Glide.with(getApplicationContext()).load(videoThumbnail).into(ivEditInfoVideoThumbnail);
                    etEditInfoVideoTitle.setText(videoTitle);
                    etEditInfoVideoDesc.setText(videoDesc);
                    int spinnerPosition = categoriesDataAdapter.getPosition(videoCategory);
                    videoEditInfoSpinner.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                videoThumbnailUri = result.getUri();

                Glide.with(getApplicationContext()).load(videoThumbnailUri).into(ivEditInfoVideoThumbnail);

                uploadThumbnail(videoThumbnailUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadThumbnail(Uri videoThumbnailUri) {
        if (videoThumbnailUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            File mFileVideoThumbnail = new File(videoThumbnailUri.getPath());
            Log.d("mFileVideoThumbnail", String.valueOf(mFileVideoThumbnail));
            try {
                mCompressedVideoThumbnail = new Compressor(EditVideoActivity.this).setQuality(15).compressToBitmap(mFileVideoThumbnail);
                Log.d("videoCompressThumbIn", String.valueOf(mCompressedVideoThumbnail));
            } catch (IOException e) {
                Log.d("videoCompressThumbE", e.getMessage());
                e.printStackTrace();
            }

            Log.d("videoCompressThumbOut", String.valueOf(mCompressedVideoThumbnail));

            ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
            mCompressedVideoThumbnail.compress(Bitmap.CompressFormat.JPEG, 15, mProfileBAOS);
            byte[] mProfileData = mProfileBAOS.toByteArray();

            final StorageReference mChildRefVideoThumbnail = storageReference.child("videos/" + videoId + "/videoThumbnail/" + videoId + ".jpg");

            final UploadTask videoThumbnail_uploadTask = mChildRefVideoThumbnail.putBytes(mProfileData);

            videoThumbnail_uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Task<Uri> uriTask = videoThumbnail_uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return mChildRefVideoThumbnail.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                videoThumbnail = downloadUri.toString();

                                mDatabase.child("videos").child(videoId).child("videoThumbnail").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            saveVideoDetails();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                                progressDialog.dismiss();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "UriTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "UploadTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        videoCategory = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
