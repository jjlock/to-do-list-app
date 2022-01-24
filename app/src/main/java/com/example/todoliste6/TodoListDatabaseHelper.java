package com.example.todoliste6;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoListDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todolist";
    private static final int DB_VERSION = 1;

    TodoListDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE LIST (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "LIST_NAME TEXT);");

        db.execSQL("CREATE TABLE ITEM (item_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "_id INTEGER, "
                + "ITEM_TEXT TEXT, "
                + "ITEM_STATUS TEXT, "
                + "FOREIGN KEY (_id) REFERENCES LIST (_id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
