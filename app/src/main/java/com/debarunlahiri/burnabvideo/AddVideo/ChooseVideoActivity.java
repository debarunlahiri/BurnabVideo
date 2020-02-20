package com.debarunlahiri.burnabvideo.AddVideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.debarunlahiri.burnabvideo.R;
import com.debarunlahiri.burnabvideo.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iceteck.silicompressorr.RealPathUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import static com.iceteck.silicompressorr.RealPathUtils.getRealPathFromURI_BelowAPI11;

public class ChooseVideoActivity extends AppCompatActivity {

    private static final String TAG = "ChooseVideo";
    private Toolbar choosevideotoolbar;

    private ImageView ivChooseVideo, ivPlayPauseChooseVideo;
    public VideoView vvChooseVideo;
    private CardView cvChooseVideo;
    private Button buttonChooseVideoReplaceVideo, choosevideonextbutton;
    private CardView cvOptionsChooseVideo;
    private FrameLayout flChooseVideo;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    public Uri videoUri, videoThumbnailUri;
    public Bitmap bitmapVideoThumbnail;
    int height, width, stopPosition = 0;
    long duration;

    private String name;

    private boolean isPlaying = true;

    private static final int SELECT_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_video);

        choosevideotoolbar = findViewById(R.id.choosevideotoolbar);
        choosevideotoolbar.setTitle("Choose Video");
        setSupportActionBar(choosevideotoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        choosevideotoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        choosevideotoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivChooseVideo = findViewById(R.id.ivChooseVideo);
        vvChooseVideo = findViewById(R.id.vvChooseVideo);
        cvChooseVideo = findViewById(R.id.cvChooseVideo);
        flChooseVideo = findViewById(R.id.flChooseVideo);
        cvOptionsChooseVideo = findViewById(R.id.cvOptionsChooseVideo);
        ivPlayPauseChooseVideo = findViewById(R.id.ivPlayPauseChooseVideo);
        buttonChooseVideoReplaceVideo = findViewById(R.id.buttonChooseVideoReplaceVideo);
        choosevideonextbutton = findViewById(R.id.choosevideonextbutton);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        /*
        if (currentUser == null) {
            sendToStart();
        } else {
            user_id = currentUser.getUid();
        }

         */

        user_id = currentUser.getUid();

        getPartialUserDetails();

        ivChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(ChooseVideoActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
                                mediaChooser.setType("video/*");
                                startActivityForResult(Intent.createChooser(mediaChooser, "Select Video"), SELECT_VIDEO);
                            }
                            @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                                PermissionListener dialogPermissionListener =
                                        DialogOnDeniedPermissionListener.Builder
                                                .withContext(ChooseVideoActivity.this)
                                                .withTitle("Read External Permission")
                                                .withMessage("Read External Permission is required in order to choose your video")
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

        buttonChooseVideoReplaceVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(ChooseVideoActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
                                mediaChooser.setType("video/*");
                                startActivityForResult(Intent.createChooser(mediaChooser, "Select Video"), SELECT_VIDEO);
                            }
                            @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                                PermissionListener dialogPermissionListener =
                                        DialogOnDeniedPermissionListener.Builder
                                                .withContext(ChooseVideoActivity.this)
                                                .withTitle("Read External Permission")
                                                .withMessage("Read External Permission is required in order to choose your video")
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




        ivPlayPauseChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vvChooseVideo.isPlaying()) {
                    stopPosition = vvChooseVideo.getCurrentPosition();
                    vvChooseVideo.pause();
                    ivPlayPauseChooseVideo.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    vvChooseVideo.seekTo(stopPosition);
                    vvChooseVideo.start();
                    ivPlayPauseChooseVideo.setImageResource(R.drawable.ic_pause_black_24dp);
                }
            }
        });


        choosevideonextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent addInfoIntent = new Intent(ChooseVideoActivity.this, AddInfoVideoActivity.class);
                addInfoIntent.putExtra("videoUri", videoUri.toString());
                addInfoIntent.putExtra("videoThumbnail", videoThumbnailUri.toString());
                addInfoIntent.putExtra("videoHeight", height);
                addInfoIntent.putExtra("videoWidth", width);
                addInfoIntent.putExtra("videoDuration", duration);
                startActivity(addInfoIntent);


            }
        });

    }

    private void getPartialUserDetails() {
        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                videoUri = data.getData();
                vvChooseVideo.setVideoURI(videoUri);
                vvChooseVideo.start();

                String filePath = getRealPathFromURI_API19(ChooseVideoActivity.this, videoUri);

                cvChooseVideo.setVisibility(View.GONE);
                flChooseVideo.setVisibility(View.VISIBLE);
                cvOptionsChooseVideo.setVisibility(View.VISIBLE);
                choosevideonextbutton.setVisibility(View.VISIBLE);

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(filePath);
                width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                retriever.release();

                bitmapVideoThumbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                videoThumbnailUri = getImageUri(ChooseVideoActivity.this, bitmapVideoThumbnail);

                Toast.makeText(getApplicationContext(), String.valueOf(bitmapVideoThumbnail), Toast.LENGTH_SHORT).show();




            }

        } else {
            Toast.makeText(getApplicationContext(), "Can't select video", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        if (uri.getHost().contains("com.android.providers.media")) {
            // Image pick from recent
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Video.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Video.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        } else {
            // image pick from gallery
            return  getRealPathFromURI_BelowAPI11(context,uri);
        }

    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, String.valueOf(System.currentTimeMillis()), "Name: " + name + "\nUser ID: " + user_id);
        return Uri.parse(path);
    }



    private void sendToStart() {
        Intent loginIntent = new Intent(ChooseVideoActivity.this, StartActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        isPlaying = false;
        stopPosition = vvChooseVideo.getCurrentPosition();
        vvChooseVideo.pause();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

    }
}
