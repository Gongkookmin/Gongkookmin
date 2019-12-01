package com.example.gongkookmin;

public class DateHelper {
    String origin;
    int year;
    int month;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    int day;
    int hour;
    int minute;
    public DateHelper(String str){
        origin = str;
    }
    public boolean process(){
        String[] day = origin.split("-");
        if(day.length != 2)
            return false;
        String[] year = day[0].split("/");
        if(year.length != 3)
            return false;
        this.year =  Integer.parseInt(year[0]);
        this.month = Integer.parseInt(year[1]);
        this.day = Integer.parseInt(year[2]);
        String[] time = day[1].split(":");
        if(time.length != 2)
            return false;
        this.hour = Integer.parseInt(time[0]);
        this.minute = Integer.parseInt(time[1]);
        return true;
    }
}
