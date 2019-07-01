package com.example.week1.ui.calendar;

import java.io.Serializable;

//id 값은 리스트뷰의 position 값
//연락처의 사진을 가져오기위해선 photo_id, person_id 필요
public class EventItem implements Serializable {
    private String event_Date, event_Time, event_Location, event_Event;
    private int id;

    public EventItem(){}

    public String getEvent_Date(){
        return event_Date;
    }
    public String getEvent_Time(){
        return event_Time;
    }
    public String getEvent_Location(){
        return event_Location;
    }
    public String getEvent_Event(){
        return event_Event;
    }
    public void setEvent_Date(String string){
        this.event_Date = string;
    }
    public void setEvent_Time(String string){
        this.event_Time = string;
    }
    public void setEvent_Location(String string){
        this.event_Location = string;
    }
    public void setEvent_Event(String string){
        this.event_Event = string;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }




}
