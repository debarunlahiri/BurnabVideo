package com.debarunlahiri.burnabvideo.Video;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Rational;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.debarunlahiri.burnabvideo.R;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPlayerFragment extends Fragment {

    private Context mContext;

    private VideoView vvVideo;
    private ProgressBar pbVideo;
    private SeekBar sbVideo;
    private TextView tvVideoDuration, tvVideoCurrentVIdeoTIme;
    private ImageButton ibVideoPlayPause, ibVideoFullScreen;
    private FrameLayout flVideoBottomPanel;

    public String videoId, video, videoTitle, videoDesc;
    public Bitmap videoThumbnailBitmap;
    public long video_duration;
    public int stopPosition = 0;
    public int videoProgess;

    MediaPlayer mediaPlayer;
    Handler mHandler;

    MediaSessionCompat mediaSessionCompat;
    NotificationManager notificationManager;
    PendingIntent pausePendingIntent, replay10sPendingIntent, forward10sPendingIntent, likePendingIntent, dislikePendingIntent;


    public VideoPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (getArguments() != null) {
            videoId = getArguments().getString("video_id");
            video = getArguments().getString("video");
            video_duration = Long.parseLong(getArguments().getString("video_duration"));
        } else {
            Toast.makeText(getActivity(), "Video get arguments are null", Toast.LENGTH_SHORT).show();
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vvVideo = view.findViewById(R.id.vvVideo);
        pbVideo = view.findViewById(R.id.pbVideo);
        sbVideo = view.findViewById(R.id.sbVideo);
        tvVideoDuration = view.findViewById(R.id.tvVideoDuration);
        tvVideoCurrentVIdeoTIme = view.findViewById(R.id.tvVideoCurrentVIdeoTIme);
        ibVideoPlayPause = view.findViewById(R.id.ibVideoPlayPause);
        flVideoBottomPanel = view.findViewById(R.id.flVideoBottomPanel);
        ibVideoFullScreen = view.findViewById(R.id.ibVideoFullScreen);



        long millis = video_duration;
        String videoDurationFormat = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        tvVideoDuration.setText(videoDurationFormat);

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
                VideoPlayerFragment.this.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                VideoPlayerFragment.this.onStopTrackingTouch(seekBar);
            }
        });

        ibVideoPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vvVideo.isPlaying()) {
                    ibVideoPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    vvVideo.pause();
                    stopPosition = vvVideo.getCurrentPosition();
                } else {
                    ibVideoPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
                    vvVideo.start();
                    vvVideo.seekTo(stopPosition);
                }
            }
        });

        flVideoBottomPanel.animate().alpha(1f);
        flVideoBottomPanel.setBackgroundResource(R.drawable.gradient_black_bottom_to_top_bg);
        if (flVideoBottomPanel.getVisibility() == View.VISIBLE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    flVideoBottomPanel.setVisibility(View.GONE);
                    flVideoBottomPanel.animate().alpha(0f).setDuration(200);
                }
            }, 1800);
        }
        final Rect rect = new Rect();
        sbVideo.getHitRect(rect);
        vvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flVideoBottomPanel.getVisibility() == View.GONE) {
                    flVideoBottomPanel.animate().alpha(1f).setDuration(200);
                    flVideoBottomPanel.setVisibility(View.VISIBLE);
                } else {
                    flVideoBottomPanel.setVisibility(View.GONE);
                    flVideoBottomPanel.animate().alpha(0f).setDuration(200);
                }
            }
        });

        mediaPlayer = MediaPlayer.create(mContext, Uri.parse(video));
//        mediaPlayer.start();
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

        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, vvVideo.getDuration());
        mediaSessionCompat = new MediaSessionCompat(mContext, "tag");
        mediaSessionCompat.setMetadata(builder.build());

        setPendingIntent();

//        showNotification(getActivity(), videoTitle, videoDesc, videoThumbnailBitmap);

        ibVideoFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoId != null) {
                    Intent videoFullScreenIntent = new Intent(getActivity(), VideoFullscreenActivity.class);
                    videoFullScreenIntent.putExtra("video_id", videoId);
                    videoFullScreenIntent.putExtra("video", video);
                    videoFullScreenIntent.putExtra("stopPosition", vvVideo.getCurrentPosition());
                    getActivity().startActivityForResult(videoFullScreenIntent, 1);
                }

            }
        });

    }

    private void setPendingIntent() {
        //Creating a regular intent
        Intent pauseVideoIntent = new Intent(getActivity(), NotificationVideoReceiver.class);
        pauseVideoIntent.putExtra("notificationVideoPause", "pause");
        // Creating a pendingIntent and wrapping our intent
        pausePendingIntent = PendingIntent.getBroadcast(getActivity(), 0, pauseVideoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Creating a regular intent
        Intent fordward10sVideoIntent = new Intent(getActivity(), NotificationVideoReceiver.class);
        fordward10sVideoIntent.putExtra("notificationVideoForward10s", "forward_10s");
        // Creating a pendingIntent and wrapping our intent
        forward10sPendingIntent = PendingIntent.getBroadcast(getActivity(), 1, fordward10sVideoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Creating a regular intent
        Intent replayVideoIntent = new Intent(getActivity(), NotificationVideoReceiver.class);
        replayVideoIntent.putExtra("notificationVideoReplay10s", "replay_10s");
        // Creating a pendingIntent and wrapping our intent
        replay10sPendingIntent = PendingIntent.getBroadcast(getActivity(), 2, replayVideoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Creating a regular intent
        Intent likeVideoIntent = new Intent(getActivity(), NotificationVideoReceiver.class);
        likeVideoIntent.putExtra("notificationVideoLike", "like");
        // Creating a pendingIntent and wrapping our intent
        likePendingIntent = PendingIntent.getBroadcast(getActivity(), 3, likeVideoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Creating a regular intent
        Intent dislikeVideoIntent = new Intent(getActivity(), NotificationVideoReceiver.class);
        dislikeVideoIntent.putExtra("notificationVideoDislike", "dislike");
        // Creating a pendingIntent and wrapping our intent
        dislikePendingIntent = PendingIntent.getBroadcast(getActivity(), 4, dislikeVideoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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

    String getVideoTime(long ms)
    {
        ms/=1000;
        return (String.format("%2d:%02d",((ms%3600)/60), ((ms%3600)%60)));
    }

    public void showNotification(Context context, String title, String messageBody, Bitmap videoThumbnailBitmap) {

        //Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_app_notification_icon);

        String channel_id = createNotificationChannel(context);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channel_id)
                .setContentTitle(title)
                .setContentText(messageBody)
                /*.setLargeIcon(largeIcon)*/
                .setSmallIcon(R.drawable.ic_launcher_foreground) //needs white icon with transparent BG (For all platforms)
                // Add media control buttons that invoke intents in your media service
                .addAction(R.drawable.ic_thumb_down_black_24dp, "Dislike", dislikePendingIntent) // #0
                .addAction(R.drawable.ic_replay_10_black_24dp, "Replay 10s", replay10sPendingIntent) // #1
                .addAction(R.drawable.ic_pause_black_24dp, "Pause", pausePendingIntent) // #2
                .addAction(R.drawable.ic_forward_10_black_24dp, "Forward 10s", forward10sPendingIntent)  // #3
                .addAction(R.drawable.ic_thumb_up_black_24dp, "Like", likePendingIntent)     // #4
                // Apply the media style template
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(2 /* #1: pause button */)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setLargeIcon(videoThumbnailBitmap);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) ((new Date(System.currentTimeMillis()).getTime() / 1000L) % Integer.MAX_VALUE) /* ID of notification */, notificationBuilder.build());
    }

    public static String createNotificationChannel(Context context) {

        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.
            String channelId = "Channel_id";

            // The user-visible name of the channel.
            CharSequence channelName = "Application_name";
            // The user-visible description of the channel.
            String channelDescription = "Application_name Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            boolean channelEnableVibrate = true;
//            int channelLockscreenVisibility = Notification.;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
//            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            stopPosition = data.getIntExtra("stopPosition", 0);
            Toast.makeText(getActivity(), String.valueOf(stopPosition), Toast.LENGTH_SHORT).show();
            vvVideo.seekTo(stopPosition);
            vvVideo.resume();

        }
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
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
