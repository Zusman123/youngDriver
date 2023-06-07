package com.mz.SmartApps.youngDriver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Payment {
    private SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
    private final int id;
    private final String name;
    private final Date date;
    private final int price;

    public Payment(int id, String name, Date date, int price){
        this.id = id;
        this.name = name;
        this.date = date;
        this.price = price;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public Date getDate() {
        return date;
    }

    public int getPrice() {
        return price;
    }


    public Date getDateTime(){
        return date;
    }
    
    //get the Date object in string format
    public String getStringDate(){
        return dateFormat.format(date);
    }



}
