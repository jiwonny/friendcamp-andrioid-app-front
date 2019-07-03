package com.example.week1.ui.contact;

import android.graphics.Bitmap;

import java.io.Serializable;


public class ContactItem implements Serializable {
    private String User_phNumber;
    private String User_Name;
    private int id;
    private Bitmap photo;

    private long photo_id=0, person_id=0; // this for identifying photo


    public ContactItem(){}

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
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


    public void setPhoto_id(long id){
        this.photo_id = id;
    }
    public void setPerson_id(long id){
        this.person_id = id;
    }
    public long getPhoto_id(){
        return photo_id;
    }
    public long getPerson_id(){
        return person_id;
    }

    public void setUser_photo(Bitmap photo){
        this.photo = photo;
    }
    public Bitmap getUser_photo(){
        return  photo;
    }

    // "010-1234-5678" to "010-1234-5678"
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
