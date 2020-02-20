package com.debarunlahiri.burnabvideo.Video.VideoComments;

public class VideoComments {

    private String comment = null;
    private String comment_id = null;
    private String video_id = null;
    private String user_id = null;
    private long timestamp;

    VideoComments() {

    }

    public VideoComments(String comment, String comment_id, String video_id, String user_id, long timestamp) {
        this.comment = comment;
        this.comment_id = comment_id;
        this.video_id = video_id;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
