package com.example.students.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbAcademics extends SQLiteOpenHelper {
    public static  final String DbName="MyAdmin.dbAcademics";
    public static final  int Verson=1;
    public static   int ind=0;

    Context context;
    public dbAcademics(Context context) {
        super(context, DbName, null, Verson);
        this.context=context;
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table if not exists dbAcademics(id INTEGER primary key,Name TEXT,Info TEXT,image TEXT,lan TEXT,lat TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if  exists dbAcademics");
        onCreate(db);

    }

    public void InsertRowAdmin(String Name, String Info,int img,String lan,String lat){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",ind++);

        contentValues.put("Name",Name);
        contentValues.put("Info",Info);
        contentValues.put("image",img);
        contentValues.put("lan",lan);
        contentValues.put("lat",lat);
        db.insert("dbAcademics",null,contentValues);
    }


    public String getAllrows(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from dbAcademics",null);
        res.moveToFirst();
        String str="";

        while (res.isAfterLast()==false){

            //   Product p=new Product();

            str+=res.getString(res.getColumnIndex("Name"))+" ";
            str+=res.getString(res.getColumnIndex("Info"))+" ";

            res.moveToNext();
            str+="\n";
        }
        return str;
    }
    public String getName(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from dbAcademics",null);
        res.moveToFirst();
        while (res.isAfterLast()==false){
            //   Toast.makeText(this,res.getColumnIndex("name"),Toast.LENGTH_LONG).show();
            if (id==res.getInt(res.getColumnIndex("id")))
                return res.getString(res.getColumnIndex("Name"));
            res.moveToNext();
        }
        return "";
    }
    public int getImg(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from dbAcademics",null);
        res.moveToFirst();
        while (res.isAfterLast()==false){
            //   Toast.makeText(this,res.getColumnIndex("name"),Toast.LENGTH_LONG).show();
            if (id==res.getInt(res.getColumnIndex("id")))
                return res.getInt(res.getColumnIndex("image"));
            res.moveToNext();
        }
        return -1;
    }
    public String getInfo(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from dbAcademics",null);
        res.moveToFirst();
        while (res.isAfterLast()==false){
            //   Toast.makeText(this,res.getColumnIndex("name"),Toast.LENGTH_LONG).show();
            if (id==res.getInt(res.getColumnIndex("id")))
                return res.getString(res.getColumnIndex("Info"));
            res.moveToNext();
        }
        return "";
    }
    public int getID(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from dbAcademics",null);
        res.moveToFirst();
        int i=-1;
        while (res.isAfterLast()==false){
            //   Toast.makeText(this,res.getColumnIndex("name"),Toast.LENGTH_LONG).show();
        //    if (str.equals(res.getString(res.getColumnIndex("Email"))))
               // return Integer.parseInt(res.getString(res.getColumnIndex("date")));
           i= res.getInt(res.getColumnIndex("id"));
            res.moveToNext();
        }
        return i;
    }

    public double[] getLan(){
        double arr[]=new double[3];
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from dbAcademics",null);
        res.moveToFirst();
        int i=0;
        while (res.isAfterLast()==false){
            arr[i++]= res.getDouble(res.getColumnIndex("lan"));
            res.moveToNext();
        }
        return arr;

    }
    public double[] getLat(){
        double arr[]=new double[3];
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from dbAcademics",null);
        res.moveToFirst();
        int i=0;
        while (res.isAfterLast()==false){
            arr[i++]= res.getDouble(res.getColumnIndex("lat"));
            res.moveToNext();
        }
        return arr;

    }
}
