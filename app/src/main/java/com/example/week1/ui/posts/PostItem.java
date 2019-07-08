package com.example.week1.ui.posts;

import com.example.week1.network.Image_f;

public class PostItem {
    String login_id;
    String profile;
    Long timestamp;
    String time;
    Image_f post;

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Image_f getPost() {
        return post;
    }

    public void setPost(Image_f post) {
        this.post = post;
    }
}
