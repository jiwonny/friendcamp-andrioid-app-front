package com.example.week1.network;

import java.io.File;
import java.util.ArrayList;

public class Image_f {
    String Login_id;
    String Name;
    String Url;
    String Timestamp;
    ArrayList<Comment> Comments;

    public String getLogin_id() {
        return Login_id;
    }

    public void setLogin_id(String login_id) {
        Login_id = login_id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }


    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public ArrayList<Comment> getComments() {
        return Comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        Comments = comments;
    }


}
