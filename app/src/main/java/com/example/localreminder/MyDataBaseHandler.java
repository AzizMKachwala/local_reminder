package com.example.localreminder;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDataBaseHandler extends SQLiteOpenHelper {

    // Database information
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SQL_Demo";
    private static final String TABLE_EVENTS = "events";
    private static final String KEY_ID = "Id";
    private static final String KEY_DATE = "Date";
    private static final String KEY_TIME = "Time";
    private static final String KEY_DESCRIPTION = "Description";

    private static final String TABLE_COMPLETED = "completed";
    private static final String KEY_ID_COMPLETED = "Id";
    private static final String KEY_DATE_COMPLETED = "Date";
    private static final String KEY_TIME_COMPLETED = "Time";
    private static final String KEY_DESCRIPTION_COMPLETED = "Description";

    public MyDataBaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);

        String CREATE_TABLE1 = "CREATE TABLE " + TABLE_COMPLETED + "("
                + KEY_ID_COMPLETED + " INTEGER PRIMARY KEY,"
                + KEY_DATE_COMPLETED + " TEXT,"
                + KEY_TIME_COMPLETED + " TEXT,"
                + KEY_DESCRIPTION_COMPLETED + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE1);

    }

    public void moveDataToCompleted(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Retrieve data from Upcoming table based on the ID
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{KEY_ID, KEY_DATE, KEY_TIME, KEY_DESCRIPTION},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));

            // Insert data into Completed table
            ContentValues values = new ContentValues();
            values.put(KEY_DATE_COMPLETED, date);
            values.put(KEY_TIME_COMPLETED, time);
            values.put(KEY_DESCRIPTION_COMPLETED, description);
            db.insert(TABLE_COMPLETED, null, values);

            // Delete data from Upcoming table
            db.delete(TABLE_EVENTS, KEY_ID + "=?", new String[]{String.valueOf(id)});
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
    }


    public ArrayList<MyDbDataModel> getAllData() {
        ArrayList<MyDbDataModel> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));

                MyDbDataModel data = new MyDbDataModel(id, date, time, description);
                dataList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dataList;
    }

    public ArrayList<MyDbDataModel> getAllCompletedData() {
        ArrayList<MyDbDataModel> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COMPLETED, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(KEY_ID_COMPLETED));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(KEY_DATE_COMPLETED));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(KEY_TIME_COMPLETED));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION_COMPLETED));

                MyDbDataModel data = new MyDbDataModel(id, date, time, description);
                dataList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dataList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public int updateData(int id, String date, String time, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);
        values.put(KEY_TIME, time);
        values.put(KEY_DESCRIPTION, description);

        return db.update(TABLE_EVENTS, values, KEY_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void insertData(String date, String time, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);
        values.put(KEY_TIME, time);
        values.put(KEY_DESCRIPTION, description);
        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }

    public void insertDataCompleted(String date, String time, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE_COMPLETED, date);
        values.put(KEY_TIME_COMPLETED, time);
        values.put(KEY_DESCRIPTION_COMPLETED, description);
        db.insert(TABLE_COMPLETED, null, values);
        db.close();
    }

    public void deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteDataCompleted(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COMPLETED, KEY_ID_COMPLETED + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
