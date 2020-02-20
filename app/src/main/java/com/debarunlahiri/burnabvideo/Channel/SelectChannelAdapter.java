package com.debarunlahiri.burnabvideo.Channel;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnabvideo.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectChannelAdapter extends RecyclerView.Adapter<SelectChannelAdapter.ViewHolder> {

    private Context mContext;
    private List<Channel> channelList;
    private Dialog dialog;
    private boolean isChannelSelected = false;

    private static String PREF_SELECT_CHANNEL = "select_channel";

    public SelectChannelAdapter(Context mContext, List<Channel> channelList, Dialog dialog) {
        this.mContext = mContext;
        this.channelList = channelList;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_select_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectChannelAdapter.ViewHolder holder, int position) {
        final Channel channel = channelList.get(position);


        setChannelDetails(channel, holder);

        holder.cvSelectChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cvSelectChannel.setBackgroundResource(R.drawable.selected_blue_border_bg);
                isChannelSelected = true;
                Intent openRepliesIntent = new Intent("selectChannelBroadcast");
                openRepliesIntent.putExtra("selectChannelClick", isChannelSelected);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(openRepliesIntent);
                SharedPreferences.Editor prefEditor = mContext.getSharedPreferences(PREF_SELECT_CHANNEL, mContext.MODE_PRIVATE).edit();
                prefEditor.putString("user_id", channel.getUser_id());
                prefEditor.putString("channel_id", channel.getChannel_id());
                prefEditor.apply();
            }
        });
    }

    private void setChannelDetails(Channel channel, SelectChannelAdapter.ViewHolder holder) {
        Glide.with(mContext).load(channel.getChannel_profile_pic()).into(holder.civSelectChannelProfilePic);
        holder.tvSelectChannelName.setText(channel.getChannel_name());
        holder.tvSelectChannelFollowers.setText("0 Followers");
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSelectChannelName, tvSelectChannelFollowers;
        private CircleImageView civSelectChannelProfilePic;
        private CardView cvSelectChannel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSelectChannelName = itemView.findViewById(R.id.tvSelectChannelName);
            tvSelectChannelFollowers = itemView.findViewById(R.id.tvSelectChannelFollowers);
            civSelectChannelProfilePic = itemView.findViewById(R.id.civSelectChannelProfilePic);
            cvSelectChannel = itemView.findViewById(R.id.cvSelectChannel);
        }
    }
}