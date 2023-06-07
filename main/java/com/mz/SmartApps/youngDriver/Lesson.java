package com.mz.SmartApps.youngDriver;

import static com.mz.SmartApps.youngDriver.MainActivity.TAG;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Lesson {
   private int id;
    private Date date;
    private Date startTime;
    private Date endTime;
    private int price;
    private boolean paid;
    private double type;
    private boolean isGroup;
    private static SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
     private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public Lesson(int id, Date date, Date startTime, Date endTime, int price, boolean paid, double type, boolean isGroup){
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.paid = paid;
        this.type = type;
        this.isGroup = isGroup;
    }
    //lessons group
    public Lesson(int  id, Date date, int price, boolean paid, double count, boolean isGroup){
        this.id = id;
        this.date = date;
        this.price = price;
        this.paid = paid;
        this.type = count;
        this.isGroup = isGroup;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getPrice() {
        return price;
    }

    public boolean isPaid() {
        return paid;
    }

    public double getType() {
        return type;
    }

   //The duration in the format "start time - end time"
    public String getDuration(){
        return getStringTime(TimeS.start)+" - "+getStringTime(TimeS.end);
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    enum TimeS{start,end};
   //Retrieves the string representation of a specific time.
    public String getStringTime(TimeS timeS){
        if (timeS == TimeS.start )
            return timeFormat.format(startTime);
        return timeFormat.format(endTime);

    }
   
   //Retrieves the string representation of the date.
    public String getStringDate(){
      return dateFormat.format(date);
    }

   //Converts a string representation of a date to a Date object.
    public static final Date str2date(String date){
        Date d = null;
        try {
            d= dateFormat.parse(date);
        } catch (ParseException e) { e.printStackTrace(); }
        return d;
    }
}
