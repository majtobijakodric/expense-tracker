package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseStuff extends SQLiteOpenHelper {

    // name of the database file
    private static final String DATABASE_NAME = "expenses.db";
    // version of the database
    private static final int DATABASE_VERSION = 1;

    // table name and column names
    private static final String TABLE_NAME = "expenses";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ITEM = "item";
    private static final String COLUMN_AMOUNT = "amount";

    public DatabaseStuff(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // this is where we create the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM + " TEXT, " +
                COLUMN_AMOUNT + " REAL)";
        db.execSQL(createTable);
    }

    // this runs if we update the database version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // this method adds a new expense to the database
    public void addExpense(String item, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM, item);
        values.put(COLUMN_AMOUNT, amount);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // this method gets all the expenses from the database
    public ArrayList<String> getAllExpenses() {
        ArrayList<String> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                // get the data from the columns
                String item = cursor.getString(1);
                double amount = cursor.getDouble(2);
                // format it for the list
                expenses.add(item + ": " + amount + "€");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenses;
    }

    // this method gets the total sum of all expenses
    public double getTotalExpenses() {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    // this method deletes everything in the database
    public void deleteAllExpenses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }
}
