package com.example.students.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbLoginUser extends SQLiteOpenHelper {
    public static  final String DbName="MyAdmin.dbLoginUser";
    public static final  int Verson=1;
    public static   int ind=1;

    Context context;
    public dbLoginUser(Context context) {
        super(context, DbName, null, Verson);
        this.context=context;
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table if not exists dbLoginUser(id INTEGER primary key,Email TEXT,Count TEXT,date Text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if  exists dbLoginUser");
        onCreate(db);

    }
    public void update(String Email,int Count)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("Email",Email);

        contentValues.put("Count",++Count);
        db.update("dbLoginUser",contentValues, "Email=?",new String[]{Email});

    }
    public  void delete(String Email)
    {
        SQLiteDatabase db=this.getWritableDatabase();

        db.delete("dbLoginUser","Email=?",new String[]{Email});

    }
    public void InsertRowAdmin(String Email, int Count, int date)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",ind++);

        contentValues.put("Email",Email);
        contentValues.put("Count",Count);
        contentValues.put("date",date);

        db.insert("dbLoginUser",null,contentValues);

    }

    public int getRow(String str)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from dbLoginUser",null);
        res.moveToFirst();
        while (res.isAfterLast()==false){
            //   Toast.makeText(this,res.getColumnIndex("name"),Toast.LENGTH_LONG).show();
            if (str.equals(res.getString(res.getColumnIndex("Email"))))
                return Integer.parseInt(res.getString(res.getColumnIndex("Count")));
            res.moveToNext();
        }
        return -1;
    }
    public int getTime(String str)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from dbLoginUser",null);
        res.moveToFirst();
        while (res.isAfterLast()==false){
            //   Toast.makeText(this,res.getColumnIndex("name"),Toast.LENGTH_LONG).show();
            if (str.equals(res.getString(res.getColumnIndex("Email"))))
                return Integer.parseInt(res.getString(res.getColumnIndex("date")));
            res.moveToNext();
        }
        return -1;
    }




}
