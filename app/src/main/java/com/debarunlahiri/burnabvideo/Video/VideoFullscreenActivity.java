package com.debarunlahiri.burnabvideo.Video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.debarunlahiri.burnabvideo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VideoFullscreenActivity extends AppCompatActivity {

    private Context mContext;

    private VideoView vvVideo;
    private ProgressBar pbVideo;
    private SeekBar sbVideo;
    private TextView tvVideoDuration, tvVideoCurrentVIdeoTIme;
    private ImageButton ibVideoPlayPause;
    private FrameLayout flVideoBottomPanel;

    public String videoId, video, user_id, videoDesc;
    public Bitmap videoThumbnailBitmap;
    public long video_duration;
    public int stopPosition = 0;
    public int videoProgess;
    public boolean isVideoPlaying = false;

    MediaPlayer mediaPlayer;
    Handler mHandler;

    MediaSessionCompat mediaSessionCompat;
    NotificationManager notificationManager;
    PendingIntent pausePendingIntent, replay10sPendingIntent, forward10sPendingIntent, likePendingIntent, dislikePendingIntent;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private int currentApiVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_fullscreen);

        mContext = VideoFullscreenActivity.this;

        Bundle bundle = getIntent().getExtras();
        videoId = bundle.getString("video_id");
        video = bundle.getString("video");
        stopPosition = bundle.getInt("stopPosition");
        isVideoPlaying = bundle.getBoolean("isVideoPlaying");
        video_duration = bundle.getLong("video_duration");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        vvVideo = findViewById(R.id.vvVideo);
        pbVideo = findViewById(R.id.pbVideo);
        sbVideo = findViewById(R.id.sbVideo);
        tvVideoDuration = findViewById(R.id.tvVideoDuration);
        tvVideoCurrentVIdeoTIme = findViewById(R.id.tvVideoCurrentVIdeoTIme);
        ibVideoPlayPause = findViewById(R.id.ibVideoPlayPause);
        flVideoBottomPanel = findViewById(R.id.flVideoBottomPanel);

        setVideo();
        setVideoBufferUpdate();
        setVideoDetails();

        MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                        pbVideo.setVisibility(View.GONE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                        pbVideo.setVisibility(View.VISIBLE);

                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                        pbVideo.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;
            }
        };

        vvVideo.setOnInfoListener(onInfoListener);

        sbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                VideoFullscreenActivity.this.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                VideoFullscreenActivity.this.onStopTrackingTouch(seekBar);
            }
        });

        ibVideoPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (vvVideo.isPlaying()) {
                ibVideoPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                vvVideo.pause();
                stopPosition = vvVideo.getCurrentPosition();
                isVideoPlaying = false;
            } else {
                ibVideoPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
                vvVideo.start();
                vvVideo.seekTo(stopPosition);
                isVideoPlaying = true;
            }
            }
        });

    }

    private void setVideoDetails() {
        long millis = video_duration;
        String videoDurationFormat = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        tvVideoDuration.setText(videoDurationFormat);
    }

    private void setVideo() {
        if (isVideoPlaying) {
            vvVideo.setVideoPath(video);
            vvVideo.seekTo(stopPosition);
            vvVideo.start();
            mHandler = new Handler();
            updateProgressBar();
        } else {
            vvVideo.setVideoPath(video);
            vvVideo.seekTo(stopPosition);
            vvVideo.pause();
            ibVideoPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
//            mHandler = new Handler();
//            updateProgressBar();
        }
    }

    private void setVideoBufferUpdate() {
        mediaPlayer = MediaPlayer.create(VideoFullscreenActivity.this, Uri.parse(video));
        sbVideo.setSecondaryProgressTintList(ColorStateList.valueOf(Color.WHITE));
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
                double per = percent * 1.00;
                per = (per / 100);
                double f = (sbVideo.getMax() * 1.00);
                f = f * per;
                if (percent < sbVideo.getMax()) {
                    sbVideo.setSecondaryProgress((int) f);
                }
            }
        });
    }


    private void updateProgressBar() {
        mHandler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            videoProgess = vvVideo.getCurrentPosition() * 100 / vvVideo.getDuration();
            String tm = getVideoTime(vvVideo.getCurrentPosition());
            tvVideoCurrentVIdeoTIme.setText(tm);
            sbVideo.setProgress(vvVideo.getCurrentPosition());
            sbVideo.setMax(vvVideo.getDuration());
            mHandler.postDelayed(this, 100);

        }
    };

    public void onProgressChanged(SeekBar seekbar, int progress,boolean fromTouch) {

    }
    public void onStartTrackingTouch(SeekBar seekbar) {
        mHandler.removeCallbacks(updateTimeTask);
    }
    public void onStopTrackingTouch(SeekBar seekbar) {
        mHandler.removeCallbacks(updateTimeTask);
        vvVideo.seekTo(sbVideo.getProgress());
        updateProgressBar();
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    String getVideoTime(long ms)
    {
        ms/=1000;
        return (String.format("%2d:%02d",((ms%3600)/60), ((ms%3600)%60)));
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPosition = vvVideo.getCurrentPosition();
        vvVideo.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPosition = vvVideo.getCurrentPosition();
        vvVideo.pause();

        if (currentUser != null) {
            user_id = currentUser.getUid();
            HashMap<String, Object> mUsersVideoStopPositionDataMap = new HashMap<>();
            mUsersVideoStopPositionDataMap.put("videoId", videoId);
            mUsersVideoStopPositionDataMap.put("user_id", user_id);
            mUsersVideoStopPositionDataMap.put("stopPosition", stopPosition);
            mDatabase.child("usersVideoStopPosition").child(user_id).child(videoId).setValue(mUsersVideoStopPositionDataMap);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        vvVideo.pause();
        Intent intent = new Intent();
        intent.putExtra("stopPosition", vvVideo.getCurrentPosition());
        intent.putExtra("isVideoPlaying", isVideoPlaying);
        setResult(1, intent);
        finish();
    }
}
