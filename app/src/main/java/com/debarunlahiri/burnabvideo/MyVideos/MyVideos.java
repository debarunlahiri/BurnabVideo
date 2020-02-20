package com.debarunlahiri.burnabvideo.MyVideos;

public class MyVideos {

    private String video_id = null;
    private String user_id = null;
    private String video = null;
    private String videoThumbnail = null;
    private String videoCategory = null;
    private String videoTitle = null;
    private String videoDesc = null;
    private int videoHeight;
    private int videoWidth;
    private long videoDuration;
    private long timestamp;

    MyVideos() {

    }

    public MyVideos(String video_id, String user_id, String video, String videoThumbnail, String videoCategory, String videoTitle, String videoDesc, int videoHeight, int videoWidth, long videoDuration, long timestamp) {
        this.video_id = video_id;
        this.user_id = user_id;
        this.video = video;
        this.videoThumbnail = videoThumbnail;
        this.videoCategory = videoCategory;
        this.videoTitle = videoTitle;
        this.videoDesc = videoDesc;
        this.videoHeight = videoHeight;
        this.videoWidth = videoWidth;
        this.videoDuration = videoDuration;
        this.timestamp = timestamp;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(String videoCategory) {
        this.videoCategory = videoCategory;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDesc() {
        return videoDesc;
    }

    public void setVideoDesc(String videoDesc) {
        this.videoDesc = videoDesc;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
