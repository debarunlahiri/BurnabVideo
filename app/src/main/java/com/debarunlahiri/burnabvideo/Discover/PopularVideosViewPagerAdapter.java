package com.debarunlahiri.burnabvideo.Discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnabvideo.R;
import com.debarunlahiri.burnabvideo.Video.Videos;
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

public class PopularVideosViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Videos> videosList;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    public PopularVideosViewPagerAdapter(Context mContext, List<Videos> videosList) {
        this.mContext = mContext;
        this.videosList = videosList;
    }

    @Override
    public int getCount() {
        return videosList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_list_popular_videos_view, container, false);

        Videos videos = videosList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        TextView tvPopularViewVideoTitle = container.findViewById(R.id.tvPopularViewVideoTitle);
        TextView tvVideoUploadDate = container.findViewById(R.id.tvVideoUploadDate);
        TextView tvVideoViews = container.findViewById(R.id.tvVideoViews);
        final TextView tvPopularViewName = container.findViewById(R.id.tvPopularViewName);
        ImageView ivPopularViewThumbnail = container.findViewById(R.id.ivPopularViewThumbnail);


        Glide.with(mContext).load(videos.getVideoThumbnail()).into(ivPopularViewThumbnail);
        tvPopularViewVideoTitle.setText(videos.getVideoTitle());

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        java.util.Date currenTimeZone=new java.util.Date((long)1379487711*1000);
        tvVideoUploadDate.setText(sdf.format(videos.getTimestamp()));

        mDatabase.child("videos").child(videos.getChannelId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String channel_name = dataSnapshot.child("channel_name").getValue().toString();
                tvPopularViewName.setText(channel_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
