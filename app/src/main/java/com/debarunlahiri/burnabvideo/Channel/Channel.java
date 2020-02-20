package com.debarunlahiri.burnabvideo.Channel;

public class Channel {

    private String channel_name = null;
    private String channel_description = null;
    private String channel_profile_pic = null;
    private String channel_banner = null;
    private String user_id = null;
    private String channel_id = null;
    private long timestamp;

    Channel() {

    }

    public Channel(String channel_name, String channel_description, String channel_profile_pic, String channel_banner, long timestamp, String user_id, String channel_id) {
        this.channel_name = channel_name;
        this.channel_description = channel_description;
        this.channel_profile_pic = channel_profile_pic;
        this.channel_banner = channel_banner;
        this.timestamp = timestamp;
        this.user_id = user_id;
        this.channel_id = channel_id;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getChannel_description() {
        return channel_description;
    }

    public void setChannel_description(String channel_description) {
        this.channel_description = channel_description;
    }

    public String getChannel_profile_pic() {
        return channel_profile_pic;
    }

    public void setChannel_profile_pic(String channel_profile_pic) {
        this.channel_profile_pic = channel_profile_pic;
    }

    public String getChannel_banner() {
        return channel_banner;
    }

    public void setChannel_banner(String channel_banner) {
        this.channel_banner = channel_banner;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }
}
