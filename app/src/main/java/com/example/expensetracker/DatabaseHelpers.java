package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelpers extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseTracker.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "transactions";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CREATED_AT = "created_at";

    public static final String TYPE_EXPENSE = "EXPENSE";
    public static final String TYPE_INCOME = "INCOME";

    public DatabaseHelpers(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT + " REAL NOT NULL, " + // REAL is for decimal numbers
                COLUMN_TYPE + " TEXT NOT NULL, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTransaction(double amount, String type, String category, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_AMOUNT, Math.abs(amount));
        cv.put(COLUMN_TYPE, type);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_DESCRIPTION, description);

        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public ArrayList<String> getAllTransactions() {
        ArrayList<String> transactionList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC",
                null);

        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));

                String displayString;

                if (type.equals(TYPE_EXPENSE)) {
                    displayString = String.format("%s: %s\n-%.2f€", category, description, amount);
                } else {
                    displayString = String.format("Income: %s\n+%.2f€", description, amount);
                }

                transactionList.add(displayString);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return transactionList;
    }

    public double getTotalExpense() {
        return getTotalByType(TYPE_EXPENSE);
    }

    public double getTotalIncome() {
        return getTotalByType(TYPE_INCOME);
    }

    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    private double getTotalByType(String type) {
        double total = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_NAME +
                        " WHERE " + COLUMN_TYPE + " = '" + type + "'",
                null);

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return total;
    }

    public void deleteAllTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}