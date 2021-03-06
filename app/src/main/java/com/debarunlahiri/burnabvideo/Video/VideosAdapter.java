package com.debarunlahiri.burnabvideo.Video;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.debarunlahiri.burnabvideo.Home.SubscriptionFeeds.SubscriptionFeeds;
import com.debarunlahiri.burnabvideo.Home.SubscriptionFeeds.SubscriptionFeedsAdapter;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private Context mContext;
    private List<Videos> videosList;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String name, profile_pic;
    private String user_id;

    public VideosAdapter(Context mContext, List<Videos> videosList) {
        this.mContext = mContext;
        this.videosList = videosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videos_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Videos videos = videosList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        setVideoDetails(holder, videos);
        setVideoUserDetails(holder, videos);

        if (currentUser != null) {
            user_id = currentUser.getUid();
            checkUsersVideoStopPosition(holder, videos);
        }




        holder.cvVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoIntent = new Intent(mContext, VideoActivity.class);
                videoIntent.putExtra("video_id", videos.getVideo_id());
                mContext.startActivity(videoIntent);
            }
        });
    }

    private void checkUsersVideoStopPosition(final ViewHolder holder, Videos videos) {
        mDatabase.child("usersVideosStopPosition").child(user_id).child(videos.getVideo_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.pbVideoStopPosition.setVisibility(View.VISIBLE);
                } else {
                    holder.pbVideoStopPosition.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setVideoUserDetails(final ViewHolder holder, Videos videos) {
        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()) {
                    name = dataSnapshot.child("name").getValue().toString();
                    holder.tvVideoPersonName.setText(name);
                }

                if (dataSnapshot.child("profile_image").exists()) {
                    profile_pic = dataSnapshot.child("profile_image").getValue().toString();
                    Glide.with(mContext).load(profile_pic).into(holder.civVideoProfilePic);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setVideoDetails(ViewHolder holder, Videos videos) {
        Glide.with(mContext).load(videos.getVideoThumbnail()).into(holder.ivVideoThumbnail);
        holder.tvVideoTitle.setText(videos.getVideoTitle());
        Spanned sp = Html.fromHtml(videos.getVideoDescription());
        holder.tvVideoDesc.setText(sp);
        holder.tvVideoViews.setText("0 views");

        long millis = videos.getVideoDuration();
        String videoDurationFormat = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        holder.tvVideosVideoDuration.setText(videoDurationFormat);

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        java.util.Date currenTimeZone=new java.util.Date((long)1379487711*1000);
        holder.tvVideoUploadDate.setText(sdf.format(videos.getTimestamp()));

        Glide.with(mContext).load(videos.getVideoThumbnail()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).into(holder.ivVideoBg);
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivVideoThumbnail, ivVideoBg;
        private TextView tvVideoPersonName, tvVideoTitle, tvVideoDesc, tvVideoUploadDate, tvVideoViews, tvVideosVideoDuration;
        private CircleImageView civVideoProfilePic;
        private CardView cvVideosTop, cvVideosBottom, cvVideos;
        private ProgressBar pbVideoStopPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivVideoThumbnail = itemView.findViewById(R.id.ivVideoThumbnail);
            ivVideoBg = itemView.findViewById(R.id.ivVideoBg);
            tvVideoPersonName = itemView.findViewById(R.id.tvVideoPersonName);
            tvVideoTitle = itemView.findViewById(R.id.tvVideoTitle);
            tvVideoDesc = itemView.findViewById(R.id.tvVideoDesc);
            tvVideoUploadDate = itemView.findViewById(R.id.tvVideoUploadDate);
            tvVideoViews = itemView.findViewById(R.id.tvVideoViews);
            tvVideosVideoDuration = itemView.findViewById(R.id.tvVideosVideoDuration);
            civVideoProfilePic = itemView.findViewById(R.id.civVideoProfilePic);
            cvVideosBottom = itemView.findViewById(R.id.cvVideosBottom);
            cvVideosTop = itemView.findViewById(R.id.cvVideosTop);
            cvVideos = itemView.findViewById(R.id.cvVideos);
            pbVideoStopPosition = itemView.findViewById(R.id.pbVideoStopPosition);

        }
    }


}
