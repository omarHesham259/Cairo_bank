// --- DBHelper.java ---
package com.example.cairo_bank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cairo_bank.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "CairoBank.db";
    private static final int DBVERSION = 3;

    public DBHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, balance REAL DEFAULT 0)");
        db.execSQL("CREATE TABLE transactions(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, type TEXT, amount REAL, details TEXT, timestamp LONG)");
        db.execSQL("CREATE TABLE transfers(id INTEGER PRIMARY KEY AUTOINCREMENT, from_user TEXT, to_user TEXT, amount REAL, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS transactions");
        db.execSQL("DROP TABLE IF EXISTS transfers");
        onCreate(db);
    }

    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("username", username);
        v.put("password", password);
        v.put("balance", 0);
        long id = db.insert("users", null, v);
        return id != -1;
    }

    public boolean checkUser(String u, String p) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{ u, p });
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void insertTransaction(String username, String type, double amount, String details) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("username", username);
        v.put("type", type);
        v.put("amount", amount);
        v.put("details", details);
        v.put("timestamp", System.currentTimeMillis());
        db.insert("transactions", null, v);
    }

    public void insertTransfer(String fromUser, String toUser, double amount, String date) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues transfer = new ContentValues();
        transfer.put("from_user", fromUser);
        transfer.put("to_user", toUser);
        transfer.put("amount", amount);
        transfer.put("date", date);
        db.insert("transfers", null, transfer);

        long timestamp = System.currentTimeMillis();

        ContentValues senderTransaction = new ContentValues();
        senderTransaction.put("username", fromUser);
        senderTransaction.put("type", "Transfer Sent");
        senderTransaction.put("amount", amount);
        senderTransaction.put("details", "To: " + toUser);
        senderTransaction.put("timestamp", timestamp);
        db.insert("transactions", null, senderTransaction);

        ContentValues receiverTransaction = new ContentValues();
        receiverTransaction.put("username", toUser);
        receiverTransaction.put("type", "Transfer Received");
        receiverTransaction.put("amount", amount);
        receiverTransaction.put("details", "From: " + fromUser);
        receiverTransaction.put("timestamp", timestamp);
        db.insert("transactions", null, receiverTransaction);
    }

    public List<Transaction> getAllTransactions(String username) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE username = ? ORDER BY timestamp DESC", new String[]{ username });

        if (cursor.moveToFirst()) {
            do {
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String details = cursor.getString(cursor.getColumnIndexOrThrow("details"));
                transactions.add(new Transaction(timestamp, amount, type, details));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return transactions;
    }

    public double getBalance(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT balance FROM users WHERE username = ?", new String[]{ username });
        double bal = 0;
        if (c.moveToFirst()) {
            bal = c.getDouble(0);
        }
        c.close();
        return bal;
    }

    public void updateBalance(String username, double newBalance) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", newBalance);
        db.update("users", values, "username = ?", new String[]{ username });
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{ username });
        int id = -1;
        if (c.moveToFirst()) {
            id = c.getInt(0);
        }
        c.close();
        return id;
    }
}

// [Other activity classes will follow in the next response due to size.]
