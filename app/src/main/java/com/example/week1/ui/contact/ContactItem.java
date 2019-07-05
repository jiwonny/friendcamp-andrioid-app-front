package com.example.week1.ui.contact;

import android.graphics.Bitmap;

import java.io.Serializable;


public class ContactItem implements Serializable {
    private int id;
    private String User_Login_Id;
    private String User_phNumber;
    private String User_Name;


    public ContactItem(){}

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }


    public String getUser_Login_Id() {
        return User_Login_Id;
    }
    public void setUser_Login_Id(String user_Login_Id) {
        User_Login_Id = user_Login_Id;
    }
    public void setUser_Name(String string){
        this.User_Name = string;
    }
    public void setUser_phNumber(String string){
        this.User_phNumber = string;
    }
    public String getUser_Name(){
        return User_Name;
    }
    public String getUser_phNumber(){
        return User_phNumber;
    }


    // "010-1234-5678" to "01012345678"
    public String getPhNumberChanged(){
        return User_phNumber.replace("-", "");
    }

    @Override
    public String toString() {
        return this.User_phNumber;
    }

    @Override
    public int hashCode() {
        return getPhNumberChanged().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ContactItem)
            return getPhNumberChanged().equals(((ContactItem) o).getPhNumberChanged());
        return false;
    }
}
