package com.debarunlahiri.burnabvideo.Channel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.burnabvideo.R;
import com.debarunlahiri.burnabvideo.Utils.IPAddress;
import com.debarunlahiri.burnabvideo.Utils.Variables;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class CreateChannelActivity extends AppCompatActivity {

    private TextView tvCreateChannel;
    private ImageView ivCreateChannelProfilePic;
    private CircleImageView civCreateChannelProfilePic;
    private EditText etCreateChannelDialogChannelName, etCreateChannelDialogAbout;
    private Button bCreateChannel, bCreateChannelDialog;
    private ProgressBar pbCreateChannelDialogProfileIndicator;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id, channel_profile_image, channel_profile_pic, channel_id, channel_name, channel_description;
    private Uri channelProfilePicuri;
    private Bitmap mCompressedProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        tvCreateChannel = findViewById(R.id.tvCreateChannel);
        bCreateChannel = findViewById(R.id.bCreateChannel);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        if (currentUser != null) {
            user_id = currentUser.getUid();

            mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String email = dataSnapshot.child("email").getValue().toString();

                    tvCreateChannel.setText("Your channel will create under this account " + email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            bCreateChannel.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    channel_id = mDatabase.child("users").child(user_id).child("channels").push().getKey();

                    Dialog dialog = new Dialog(CreateChannelActivity.this);
                    dialog.setContentView(R.layout.dialog_create_channel);
                    dialog.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    dialog.setCanceledOnTouchOutside(false);

                    ivCreateChannelProfilePic = dialog.findViewById(R.id.ivCreateChannelProfilePic);
                    civCreateChannelProfilePic = dialog.findViewById(R.id.civCreateChannelProfilePic);
                    bCreateChannelDialog = dialog.findViewById(R.id.bCreateChannelDialog);
                    etCreateChannelDialogChannelName = dialog.findViewById(R.id.etCreateChannelDialogChannelName);
                    pbCreateChannelDialogProfileIndicator = dialog.findViewById(R.id.pbCreateChannelDialogProfileIndicator);
                    etCreateChannelDialogAbout = dialog.findViewById(R.id.etCreateChannelDialogAbout);

                    pbCreateChannelDialogProfileIndicator.setVisibility(View.GONE);

                    civCreateChannelProfilePic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CropImage.activity()
                                    .setAspectRatio(1, 1)
                                    .setMinCropWindowSize(512, 512)
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setOutputUri(channelProfilePicuri)
                                    .start(CreateChannelActivity.this);
                        }
                    });

                    bCreateChannelDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            channel_name = etCreateChannelDialogChannelName.getText().toString();
                            channel_description = etCreateChannelDialogAbout.getText().toString();

                            if (channel_name.isEmpty()) {
                                etCreateChannelDialogChannelName.setError("Enter your Channel Name");
                            } else {
                                if (channelProfilePicuri == null) {
                                    createChannelWithoutProfilePic();
                                } else {
                                    createChannelWithProfilePic(channelProfilePicuri);
                                }
                            }
                        }
                    });


                    dialog.show();

                }
            });
        }

    }

    private void sendToChannel(String channel_id) {
        Intent channelIntent = new Intent(CreateChannelActivity.this, ChannelActivity.class);
        channelIntent.putExtra("channel_id", channel_id);
        startActivity(channelIntent);
        finish();
        Toast.makeText(getApplicationContext(), "Channel successfully created", Toast.LENGTH_LONG).show();
    }

    private void createChannelWithProfilePic(Uri channelProfilePicuri) {
        if (channelProfilePicuri != null) {
            pbCreateChannelDialogProfileIndicator.setVisibility(View.VISIBLE);
            File mFileProfileImage = new File(channelProfilePicuri.getPath());

            try {
                mCompressedProfileImage = new Compressor(CreateChannelActivity.this).setQuality(15).compressToBitmap(mFileProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
            mCompressedProfileImage.compress(Bitmap.CompressFormat.JPEG, 15, mProfileBAOS);
            byte[] mProfileData = mProfileBAOS.toByteArray();

            final StorageReference mChildRefProfile = storageReference.child("channels/profile_images/" + channel_id + ".jpg");

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
                                pbCreateChannelDialogProfileIndicator.setVisibility(View.GONE);
                                Uri downloadUri = task.getResult();
                                channel_profile_image = downloadUri.toString();

                                final HashMap<String, Object> mChannelDataMap = new HashMap<>();
                                mChannelDataMap.put("channel_id", channel_id);
                                mChannelDataMap.put("timestamp", System.currentTimeMillis());
                                mChannelDataMap.put("user_id", user_id);
                                mChannelDataMap.put("channel_name", channel_name);
                                mChannelDataMap.put("channel_profile_pic", channel_profile_image);
                                mChannelDataMap.put("channel_description", channel_description);
                                mChannelDataMap.put("channel_banner", "");
                                mChannelDataMap.put("channel_email", currentUser.getEmail());
                                mChannelDataMap.put("channel_ip_address_ipv4", Variables.user_ip_address_ipv4);
                                mChannelDataMap.put("channel_ip_address_ipv6", Variables.user_ip_address_ipv6);
                                mChannelDataMap.put("channel_mac_address_eth0", Variables.user_mac_address_eth0);
                                mChannelDataMap.put("channel_mac_address_wlan0", Variables.user_mac_address_wlan0);
                                mDatabase.child("users").child(user_id).child("channels").child(channel_id).setValue(mChannelDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mDatabase.child("users").child(user_id).child("hasChannel").setValue(true);
                                            mDatabase.child("channels").child(channel_id).setValue(mChannelDataMap);
                                            setChannelDeviceDetail(channel_id);
                                            setChannelIPAddressAndMACAddress(channel_id);
                                            sendToChannel(channel_id);
                                        }
                                    }
                                });
                            } else {
                                pbCreateChannelDialogProfileIndicator.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pbCreateChannelDialogProfileIndicator.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "UriTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pbCreateChannelDialogProfileIndicator.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "UploadTask Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    pbCreateChannelDialogProfileIndicator.setIndeterminate(false);
                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    pbCreateChannelDialogProfileIndicator.setProgress(progress);
                }
            });


        }
    }

    private void createChannelWithoutProfilePic() {
        final HashMap<String, Object> mChannelDataMap = new HashMap<>();
        mChannelDataMap.put("channel_id", channel_id);
        mChannelDataMap.put("timestamp", System.currentTimeMillis());
        mChannelDataMap.put("user_id", user_id);
        mChannelDataMap.put("channel_name", channel_name);
        mChannelDataMap.put("channel_profile_pic", "");
        mChannelDataMap.put("channel_description", channel_description);
        mChannelDataMap.put("channel_banner", "");
        mChannelDataMap.put("channel_email", currentUser.getEmail());
        mChannelDataMap.put("channel_ip_address_ipv4", Variables.user_ip_address_ipv4);
        mChannelDataMap.put("channel_ip_address_ipv6", Variables.user_ip_address_ipv6);
        mChannelDataMap.put("channel_mac_address_eth0", Variables.user_mac_address_eth0);
        mChannelDataMap.put("channel_mac_address_wlan0", Variables.user_mac_address_wlan0);
        mDatabase.child("users").child(user_id).child("channels").child(channel_id).setValue(mChannelDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDatabase.child("users").child(user_id).child("hasChannel").setValue(true);
                    mDatabase.child("channels").child(channel_id).setValue(mChannelDataMap);
                    setChannelDeviceDetail(channel_id);
                    setChannelIPAddressAndMACAddress(channel_id);
                    sendToChannel(channel_id);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                channelProfilePicuri = result.getUri();

                Picasso.get().load(channelProfilePicuri).into(civCreateChannelProfilePic);
                ivCreateChannelProfilePic.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void setChannelDeviceDetail(String channel_id) {
        Log.i("TAG", "SERIAL: " + Build.SERIAL);
        Log.i("TAG","MODEL: " + Build.MODEL);
        Log.i("TAG","ID: " + Build.ID);
        Log.i("TAG","Manufacture: " + Build.MANUFACTURER);
        Log.i("TAG","brand: " + Build.BRAND);
        Log.i("TAG","type: " + Build.TYPE);
        Log.i("TAG","user: " + Build.USER);
        Log.i("TAG","BASE: " + Build.VERSION_CODES.BASE);
        Log.i("TAG","INCREMENTAL " + Build.VERSION.INCREMENTAL);
        Log.i("TAG","SDK  " + Build.VERSION.SDK_INT);
        Log.i("TAG","BOARD: " + Build.BOARD);
        Log.i("TAG","BRAND " + Build.BRAND);
        Log.i("TAG","HOST " + Build.HOST);
        Log.i("TAG","FINGERPRINT: "+Build.FINGERPRINT);
        Log.i("TAG","Version Code: " + Build.VERSION.RELEASE);

        HashMap<String, Object> mUserDeviceDetail = new HashMap<>();
        mUserDeviceDetail.put("SERIAL: ", Build.SERIAL);
        mUserDeviceDetail.put("MODEL: ", Build.MODEL);
        mUserDeviceDetail.put("ID: ", Build.ID);
        mUserDeviceDetail.put("MANUFACTURER: ", Build.MANUFACTURER);
        mUserDeviceDetail.put("BRAND: ", Build.BRAND);
        mUserDeviceDetail.put("TYPE: ", Build.TYPE);
        mUserDeviceDetail.put("USER: ", Build.USER);
        mUserDeviceDetail.put("BASE: ", Build.VERSION_CODES.BASE);
        mUserDeviceDetail.put("INCREMENTAL ", Build.VERSION.INCREMENTAL);
        mUserDeviceDetail.put("SDK  ", Build.VERSION.SDK_INT);
        mUserDeviceDetail.put("BOARD: ", Build.BOARD);
        mUserDeviceDetail.put("BRAND ", Build.BRAND);
        mUserDeviceDetail.put("HOST ", Build.HOST);
        mUserDeviceDetail.put("FINGERPRINT: ", Build.FINGERPRINT);
        mUserDeviceDetail.put("VERSION CODE: ", Build.VERSION.RELEASE);

        mDatabase.child("channels").child(channel_id).child("device_detail").updateChildren(mUserDeviceDetail);
    }

    private void setChannelIPAddressAndMACAddress(String channel_id) {
        Variables.user_ip_address_ipv4 = IPAddress.getIPAddress(true);
        Variables.user_ip_address_ipv6 = IPAddress.getIPAddress(false);
        Variables.user_mac_address_eth0 = IPAddress.getMACAddress("eth0");
        Variables.user_mac_address_wlan0 = IPAddress.getMACAddress("wlan0");

        HashMap<String, Object> mUserIPandMACDataMap = new HashMap<>();
        mUserIPandMACDataMap.put("ip_address_ipv4", Variables.user_ip_address_ipv4);
        mUserIPandMACDataMap.put("ip_address_ipv6", Variables.user_ip_address_ipv6);
        mUserIPandMACDataMap.put("user_mac_address_eth0", Variables.user_mac_address_eth0);
        mUserIPandMACDataMap.put("user_mac_address_wlan0", Variables.user_mac_address_wlan0);

        mDatabase.child("channels").child(channel_id).child("ip_and_mac_add").updateChildren(mUserIPandMACDataMap);
    }
}
