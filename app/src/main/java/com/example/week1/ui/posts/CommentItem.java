package com.example.week1.ui.posts;

import com.example.week1.network.Comment;



public class CommentItem{
    String profile;
    Comment comment;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}

