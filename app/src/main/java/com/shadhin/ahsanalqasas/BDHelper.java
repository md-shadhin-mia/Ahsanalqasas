package com.shadhin.ahsanalqasas;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BDHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "coursedb";
    private static final String ID_col = "id";
    private static final String Name_col = "fullname";
    private static final String Email_col = "email";
    private static final String Password_col = "password";
    private static final String Table_name = "user";
    public BDHelper(Context context) {
        super(context, "mytest.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+Table_name+" (\n" +
                    ID_col  + "         INTEGER       PRIMARY KEY AUTOINCREMENT,\n" +
                    Name_col  + "  VARCHAR (127),\n" +
                    Email_col  + "     VARCHAR (127) UNIQUE,\n" +
                    Password_col  + "  VARCHAR (127) \n" +
                ");");
    }

    public void insertUser(String fullname, String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Name_col, fullname);
        values.put(Email_col, email);
        values.put(Password_col, password);
        db.insert(Table_name, null, values);
        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }
}
