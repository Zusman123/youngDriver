package com.mz.SmartApps.youngDriver;

import static com.mz.SmartApps.youngDriver.Lesson.str2date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class PaymentsDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PaymentsDataBase";
    private static final String TABLE_NAME = "PaymentsDB";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_PRICE = "price";

    private static final String[] COLUMNS = {KEY_ID, KEY_NAME,KEY_DATE, KEY_PRICE};
    private Context context;

    LessonsDB lessonsDB;

    public PaymentsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        lessonsDB = new LessonsDB(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE PaymentsDB ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, " + "date TEXT, "  + "price INTEGER )";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    //Calculates the total payments by summing up the prices of all payments in the database.
    public int getTotalPayments() {
        int total = 0;
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null) {
            Log.d(MainActivity.TAG, "getAllGroups: this null db");
        } else {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    int price = Integer.parseInt(cursor.getString(3));
                    total += price;
                } while (cursor.moveToNext());
            }
            Log.d(MainActivity.TAG, "getTotalPayments: "+total);
            total += lessonsDB.getTotalPayment();

        }
        return total;
    }

    //Calculates the total amount of payments that are not paid int the lessons DataBase
    public int getTotalNotPaidAndLessonsNP() {
       return lessonsDB.getTotalNotPaid();
    }


//Retrieves all payments from the database.
    public ArrayList<Payment> getAllPayments() {
        ArrayList<Payment> payments = new ArrayList<Payment>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null) {
            Log.d(MainActivity.TAG, "getAllGroups: this null db");
        } else {
            Cursor cursor = db.rawQuery(query, null);
            Payment payment = null;

            if (cursor.moveToFirst()) {
                do {

                    int id = Integer.parseInt(cursor.getString(0));
                    String name = cursor.getString(1);
                    String  date = cursor.getString(2);
                    int price = Integer.parseInt(cursor.getString(3));

                    payment = new Payment(id,name, str2date(date), price);
                    payments.add(payment);
                } while (cursor.moveToNext());
            }

        }
        return payments;
    }

    //Retrieves all payments from the database and sorts them by date in descending order.
    public ArrayList<Payment> getSortPaymentsByDate() {
            ArrayList<Payment> sortedPayments = getAllPayments();
        Collections.sort(sortedPayments, new Comparator<Payment>() {
            public int compare(Payment o1, Payment o2) {
                if (o1.getDateTime() == null || o2.getDateTime() == null)
                    return 0;
                return o1.getDateTime().compareTo(o2.getDateTime());
            }
        });
        Collections.reverse(sortedPayments);
        return sortedPayments;
    }

    //Adds a new payment to the database.
    public void addPayment(Payment payment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMNS[1], payment.getName());
        values.put(COLUMNS[2], payment.getStringDate());
        values.put(COLUMNS[3], payment.getPrice());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

//Updates an existing payment in the database.
    public int updatePayment(Payment payment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNS[1], payment.getName());
        values.put(COLUMNS[2], payment.getStringDate());
        values.put(COLUMNS[3], payment.getPrice());
        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[]{String.valueOf(payment.getId())});

        db.close();
        return i;
    }

    //delete payment by id
    public void deleteOne(int id) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}
