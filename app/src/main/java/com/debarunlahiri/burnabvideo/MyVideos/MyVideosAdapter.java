package com.debarunlahiri.burnabvideo.MyVideos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnabvideo.EditVideoActivity;
import com.debarunlahiri.burnabvideo.R;
import com.debarunlahiri.burnabvideo.Video.VideoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MyVideosAdapter extends RecyclerView.Adapter<MyVideosAdapter.ViewHolder> {

    private Context mContext;
    private List<MyVideos> myVideosList;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String name, bio;
    private String user_id;


    public MyVideosAdapter(Context mContext, List<MyVideos> myVideosList) {
        this.mContext = mContext;
        this.myVideosList = myVideosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_video_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final MyVideos myVideos = myVideosList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");
        
        setVideoDetails(holder, myVideos);

        holder.cvMyVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoIntent = new Intent(mContext, VideoActivity.class);
                videoIntent.putExtra("video_id", myVideos.getVideo_id());
                mContext.startActivity(videoIntent);
            }
        });

        holder.ibMyVideosMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.ibMyVideosMoreOptions);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.my_videos_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit_my_videos_menu_item:
                                Intent editVideoIntent = new Intent(mContext, EditVideoActivity.class);
                                editVideoIntent.putExtra("videoId", myVideos.getVideo_id());
                                mContext.startActivity(editVideoIntent);
                                break;
                        }
                        return false;
                    }
                });
            }
        });


    }

    private void setVideoDetails(ViewHolder holder, MyVideos myVideos) {
        long millis = myVideos.getVideoDuration();
        String videoDurationFormat = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy h:mm a", Locale.getDefault());
        java.util.Date currenTimeZone=new java.util.Date((long)1379487711*1000);
        //Toast.makeText(TimeStampChkActivity.this, sdf.format(currenTimeZone), Toast.LENGTH_SHORT).show();


        Glide.with(mContext).load(myVideos.getVideoThumbnail()).into(holder.ivMyVideosVideoThumbnail);
        holder.tvMyVideosVideoTItle.setText(myVideos.getVideoTitle());
        holder.tvMyVideosVideoDesc.setText(myVideos.getVideoDesc());
        holder.tvMyVideosVideoDuration.setText(videoDurationFormat);
        holder.tvMyVideosVideoUploadedDate.setText("Uploaded on: " + sdf.format(myVideos.getTimestamp()));
    }


    @Override
    public int getItemCount() {
        return myVideosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivMyVideosVideoThumbnail;
        private TextView tvMyVideosVideoTItle, tvMyVideosVideoDesc, tvMyVideosVideoUploadedDate, tvMyVideosVideoDuration;
        private ImageButton ibMyVideosMoreOptions;
        private CardView cvMyVideos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivMyVideosVideoThumbnail = itemView.findViewById(R.id.ivMyVideosVideoThumbnail);
            tvMyVideosVideoTItle = itemView.findViewById(R.id.tvMyVideosVideoTItle);
            tvMyVideosVideoDesc = itemView.findViewById(R.id.tvMyVideosVideoDesc);
            tvMyVideosVideoUploadedDate = itemView.findViewById(R.id.tvMyVideosVideoUploadedDate);
            cvMyVideos = itemView.findViewById(R.id.cvMyVideos);
            ibMyVideosMoreOptions = itemView.findViewById(R.id.ibMyVideosMoreOptions);
            tvMyVideosVideoDuration = itemView.findViewById(R.id.tvMyVideosVideoDuration);

        }
    }
}
