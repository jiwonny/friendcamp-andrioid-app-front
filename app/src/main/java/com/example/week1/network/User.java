package com.example.week1.network;

import java.util.ArrayList;

public class User {
    String Login_id;
    String Name;
    String Number;
    String Profile_image_id;
    ArrayList<String> Friends;



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

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getProfile_image_id() {
        return Profile_image_id;
    }

    public void setProfile_image_id(String profile_image_id) {
        Profile_image_id = profile_image_id;
    }

    public ArrayList<String> getFriends() {
        return Friends;
    }

    public void setFriends(ArrayList<String> friends) {
        Friends = friends;
    }
}
