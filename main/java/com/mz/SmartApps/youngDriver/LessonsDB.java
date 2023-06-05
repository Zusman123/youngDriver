package com.mz.SmartApps.youngDriver;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.mz.SmartApps.youngDriver.MainActivity.TAG;

public class LessonsDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "LessonsDataBase";
    private static final String TABLE_NAME = "LessonsDB";

    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String KEY_PRICE = "price";
    private static final String KEY_PAID = "paid";
    private static final String KEY_TYPE = "type";
    private static final String KEY_IS_GROUP = "isGroup";

    private static final String[] COLUMNS = {KEY_ID, KEY_DATE, START_TIME,
            END_TIME, KEY_PRICE, KEY_PAID, KEY_TYPE, KEY_IS_GROUP};
    private Context context;
    int time;
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public LessonsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        //  cheakUpdated("kk");
        SharedPreferences preferences = context.getSharedPreferences("file1", 0);
        time = preferences.getInt("time", 0);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE LessonsDB ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "date TEXT, " + "startTime TEXT, " + "endTime TEXT, " + "price INTEGER, " + "paid INTEGER DEFAULT 0, " + "type REAL DEFAULT 1, " + "isGroup INTEGER DEFAULT 0)";
        try {
            db.execSQL(CREATION_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        Log.d(TAG, "onUpgrade: ");

        if (newVersion == 2) {
            db.execSQL("ALTER TABLE "+TABLE_NAME+" ADD COLUMN isGroup INTEGER DEFAULT 0");
        }
        //this.onCreate(db);
    }

    public int getTotalPayment() {
        int total = 0;
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null) {
            Log.d(TAG, "getAllGroups: this null db");
        } else {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    int price = Integer.parseInt(cursor.getString(4));
                    total += price;
                } while (cursor.moveToNext());
            }
        }
        return total;
    }

    public int getTotalNotPaid() {
        int total = 0;
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null) {
            Log.d(TAG, "getAllGroups: this null db");
        } else {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    int price = Integer.parseInt(cursor.getString(4));
                    boolean paid = (Integer.parseInt(cursor.getString(5)) == 1);
                    if (!paid)
                        total += price;
                } while (cursor.moveToNext());
            }
        }
        return total;
    }

    public double getLessonsCount() {
        double count = 0;
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null) {
            Log.d(TAG, "getAllGroups: this null db");
        } else {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    double type = Double.parseDouble(cursor.getString(6));
                    count += type;
                } while (cursor.moveToNext());
            }
        }
        return count;
    }

    public String getTotalLessonsLength() {
        int total = 0;
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null) {
            Log.d(TAG, "getAllGroups: this null db");
        } else {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    boolean isGroup = (Integer.parseInt(cursor.getString(7)) == 1);
                    Log.d(TAG, "is group: " + isGroup);
                    if (!isGroup) {
                        String startTime = cursor.getString(2);
                        String endTime = cursor.getString(3);
                        total += getDiffrenceInMintue(startTime, endTime);
                    } else {
                        double type = Double.parseDouble(cursor.getString(6));
                        total += (int) (type * time);
                    }

                } while (cursor.moveToNext());
            }
        }
        if (total < 60)
            return total + " דקות";
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(total / 60).append(" שעות");
            if (total % 60 != 0)
                sb.append(" ו- ").append(total % 60).append(" דקות");
            return sb.toString();
        }
    }

    public int getDiffrenceInMintue(String startTime, String endTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = simpleDateFormat.parse(startTime);
            endDate = simpleDateFormat.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = endDate.getTime() - startDate.getTime();
        return (int) (difference / (1000 * 60));
    }

    public ArrayList<Lesson> getAllLessons() {
        ArrayList<Lesson> lessons = new ArrayList<Lesson>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null) {
            Log.d(TAG, "getAllGroups: this null db");
        } else {
            Cursor cursor = db.rawQuery(query, null);
            Lesson lesson = null;

            if (cursor.moveToFirst()) {
                do {
                    boolean isGroup = (Integer.parseInt(cursor.getString(7)) == 1);
                    int id = Integer.parseInt(cursor.getString(0));
                    String date = cursor.getString(1);
                    int price = Integer.parseInt(cursor.getString(4));
                    boolean paid = (Integer.parseInt(cursor.getString(5)) == 1);
                    double type = Double.parseDouble(cursor.getString(6));
                    if (!isGroup) {
                        String startTime = cursor.getString(2);
                        String  endTime = cursor.getString(3);
                        lesson = new Lesson(id, Lesson.str2date(date), str2time(startTime), str2time(endTime), price, paid, type, false);
                    } else
                        lesson = new Lesson(id, Lesson.str2date(date), price, paid, type, true);
                    lessons.add(lesson);
                } while (cursor.moveToNext());
            }

        }
        return lessons;
    }

    public ArrayList<Lesson> getSortLessonsByDate() {
        ArrayList<Lesson> sortedGroups = getAllLessons();
        Collections.sort(sortedGroups, new Comparator<Lesson>() {
            public int compare(Lesson o1, Lesson o2) {
                if (o1.getDate() == null || o2.getDate() == null)
                    return 0;
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        Collections.reverse(sortedGroups);
        return sortedGroups;
    }

    public void addLesson(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMNS[1], lesson.getStringDate());
        values.put(COLUMNS[2], lesson.getStringTime(Lesson.TimeS.start));
        values.put(COLUMNS[3], lesson.getStringTime(Lesson.TimeS.end));
        values.put(COLUMNS[4], lesson.getPrice());
        if (lesson.isPaid())
            values.put(COLUMNS[5], 1);
        values.put(COLUMNS[6], lesson.getType());
        // insert
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void addLessonsGroup(Lesson lesson) {
        //public Lesson(int  id, String date, int price, boolean paid, double count, boolean isGroup){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNS[1], lesson.getStringDate());
        values.put(COLUMNS[4], lesson.getPrice());
        if (lesson.isPaid())
            values.put(COLUMNS[5], 1);
        values.put(COLUMNS[6], lesson.getType());
        values.put(COLUMNS[7], 1);
        // insert
        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public int updateGroup(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNS[1], lesson.getStringDate());
        values.put(COLUMNS[2], lesson.getStringTime(Lesson.TimeS.start));
        values.put(COLUMNS[3], lesson.getStringTime(Lesson.TimeS.end));
        values.put(COLUMNS[4], lesson.getPrice());
        if (lesson.isPaid())
            values.put(COLUMNS[5], 1);
        else
            values.put(COLUMNS[5], 0);
        values.put(COLUMNS[6], lesson.getType());
        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[]{String.valueOf(lesson.getId())});

        db.close();
        return i;
    }

    public void deleteOne(int id) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public Date str2time(String str){
        Date d = null;
        try {
            d= timeFormat.parse(str);
        } catch (ParseException e) { e.printStackTrace(); }
        return d;
    }
}
