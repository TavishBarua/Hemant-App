package com.demo.tavish.hemantapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "soulWings.db";

    private static final String TABLE_NAME = "hemant";
    private static final String KEY_ID = "id";
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SIZE = "size";
    private static final String KEY_PURCHASE_PRICE = "purch_price";
    private static final String KEY_SELL_PRICE = "sell_price";
    private static final String KEY_PURCHASE_DATE="purch_date";
    private static final String KEY_SELL_DATE="sell_date";
    private static final String KEY_COMMENT="comment";
    private static final String KEY_RETURN_DATE="return_date";
   // private static final String KEY_RETURN_PERSON="return_person";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String createTable=" CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_BARCODE + " VARCHAR UNIQUE NOT NULL, "
                + KEY_TYPE + " VARCHAR NOT NULL, "
                + KEY_SIZE + " VARCHAR NOT NULL, "
                + KEY_PURCHASE_PRICE + " FLOAT(10,2), "
                + KEY_SELL_PRICE + " FLOAT(10,2), "
                + KEY_PURCHASE_DATE + " DATE DEFAULT (datetime('now','localtime')),"
                + KEY_SELL_DATE + " DATE ,"
                + KEY_COMMENT + " VARCHAR ,"
                + KEY_RETURN_DATE + " DATE )";

        db.execSQL(createTable);
       /* db.execSQL("CREATE TABLE " + TABLE_NAME +
        " (KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, KEY_BARCODE VARCHAR, KEY_TYPE VARCHAR, KEY_SIZE VARCHAR, KEY_PURCHASE_PRICE INTEGER, KEY_DATE VARCHAR)");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean addData(String barcode, String type, String size, Float purch_price){

      /*  SimpleDateFormat simpleDateFormat;

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = Calendar.getInstance().getTime();
*/

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues  = new ContentValues();
        contentValues.put(KEY_BARCODE,barcode);
        contentValues.put(KEY_TYPE,type);
        contentValues.put(KEY_SIZE,size);
        contentValues.put(KEY_PURCHASE_PRICE,purch_price);
      //  contentValues.put(KEY_DATE, date);

        Log.d(TAG, "Adding Data..." + "to" + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result==-1)
            return false;
        else
            return true;

    }

    public boolean addSellData(String barcode, Float sell_price, String comment){
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SELL_PRICE,sell_price);
        contentValues.put(KEY_SELL_DATE,dateFormat.format(date));
        contentValues.put(KEY_COMMENT,comment);
        contentValues.put(KEY_RETURN_DATE,"");
        /*long result =db.execSQL("UPDATE "+TABLE_NAME+" SET "+ KEY_SELL_DATE + " = "+ " datetime() "+ " WHERE "+ KEY_BARCODE + " = " + barcode );*/
      /* long result =  db.update(TABLE_NAME,contentValues, KEY_BARCODE +" = "+ barcode,null);*/

      Cursor c = _checkData(barcode);
      if(c.moveToFirst() && c.getCount()>0){
          Cursor d = _selectSellStatus(barcode);
          if (d.moveToFirst() && d.getCount()>0){
              String label = d.getString(0);
              if(label==null){
                  long result =  db.update(TABLE_NAME,contentValues, KEY_BARCODE +" = "+ barcode,null);
                  if (result==-1) {
                      Log.d(TAG, DatabaseUtils.dumpCursorToString(d));
                      Log.d(TAG,"print result false");
                      return false;
                  }
                  else {
                      Log.d(TAG, DatabaseUtils.dumpCursorToString(d));
                      Log.d(TAG,"print result true");
                      return true;
                  }

              }else
                  Log.d(TAG, DatabaseUtils.dumpCursorToString(d));
                  Log.d(TAG,"print key sell false");
                  return false;
          }else{
              Log.d(TAG, DatabaseUtils.dumpCursorToString(d));
              Log.d(TAG,"print false");
              return false;
          }
      }else{
          Log.d(TAG, DatabaseUtils.dumpCursorToString(c));
          Log.d(TAG, "return false");
          return false;
      }
        /*if (result==-1)
            return false;
        else {
            Cursor c = _checkData(barcode);
           // Log.d(TAG, DatabaseUtils.dumpCursorToString(c));
            if(c.moveToFirst() && c.getCount()>0){
                Cursor d = _selectSellStatus(barcode);
                if (d.moveToFirst() && d.getCount()>0){
                    Log.d(TAG, DatabaseUtils.dumpCursorToString(d));
                    Log.d(TAG,"print false");
                    return false;
                }else{
                    Log.d(TAG,"print true");
                    return true;
                }
            }
            else {
                Log.d(TAG, DatabaseUtils.dumpCursorToString(c));
                Log.d(TAG,"print false");
                return false;
            }
        }*/
    }

    public Cursor _checkData(String string){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(" SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_BARCODE +
                " = " + string, null);

        return data;
    }

    public Cursor _selectSellStatus(String string){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(" SELECT "+ KEY_SELL_PRICE + ","+ KEY_SELL_DATE +" FROM " + TABLE_NAME + " WHERE " + KEY_BARCODE +
                " = " + string, null);
        return data;
    }

  /*  public boolean update_return_date(String barcode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_RETURN_DATE,"");

        long result = db.update(TABLE_NAME,contentValues,KEY_BARCODE+" = "+barcode,null);

        if(result==-1)
            return false;
        else
            return true;
    }*/

    public boolean update_return_data(String barcode, String comment){
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ContentValues contentValues = new ContentValues();
        contentValues.putNull(KEY_SELL_PRICE);
        contentValues.put(KEY_SELL_DATE,"");
        contentValues.put(KEY_RETURN_DATE,dateFormat.format(date));
        contentValues.put(KEY_COMMENT,comment);
        long result = db.update(TABLE_NAME,contentValues,KEY_BARCODE+" = "+barcode,null);

        if(result==-1)
            return false;
        else
            return true;

    }
    public Cursor check_sell(String barcode){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(" SELECT "+KEY_SELL_PRICE+" FROM " + TABLE_NAME + " WHERE " + KEY_BARCODE +
                " = " + barcode, null);
        return data;
    }

    /*public int totalSales_Today(){
        int sum = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(" SELECT  SUM( "+ KEY_SELL_PRICE + " ) " + " FROM " + TABLE_NAME + " WHERE " + KEY_SELL_DATE +
                " = " + " DATE() ", null);
        data.moveToFirst();
        if(data.getCount()>0){
            sum=data.getInt(0);
        }
        Log.d(TAG, DatabaseUtils.dumpCursorToString(data));
        return sum;
    }*/
    public Cursor totalSales_Today(){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      //  Date date = new Date();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT  SUM( "+ KEY_SELL_PRICE + " ) " + " FROM " + TABLE_NAME + " WHERE " +
                KEY_SELL_DATE + " LIKE " + " '"+dateFormat.format(c)+"%' ";
        Cursor data = db.rawQuery(query,null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor totalSales_Month(){

      /*  Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);*/

       /* System.out.println("This is my new year and month");
        System.out.println(String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1)+String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));*/

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        System.out.println("This is my new year and month");
        System.out.println(dateFormat.format(date));
        //  Date date = new Date();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT  SUM( "+ KEY_SELL_PRICE + " ) " + " FROM " + TABLE_NAME + " WHERE " +
                KEY_SELL_DATE + " LIKE " +"'"+ dateFormat.format(date)+"%'";
        Cursor data = db.rawQuery(query,null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(data));
        return data;
    }

   /* public Cursor totalSales_Month(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT  SUM( "+ KEY_SELL_PRICE + " ) " + " FROM " + TABLE_NAME + " WHERE " +
                KEY_SELL_DATE + " LIKE " + " '"+dateFormat.format(date)+"%' ";
        Cursor data = db.rawQuery(query,null);
        return data;
    }*/


   /* public boolean exportToExcel(Context c,String query){

        String filename = "tavish.xls";
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, Cursor> values = new HashMap<String, Cursor>();
        values.put("Day_1",getAllData());
       *//* values.put(KEY_TYPE,db.rawQuery(" SELECT "+ KEY_TYPE + " FROM "+ TABLE_NAME ,null));
        values.put(KEY_SIZE,db.rawQuery(" SELECT "+ KEY_SIZE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_PURCHASE_PRICE,db.rawQuery(" SELECT "+ KEY_PURCHASE_PRICE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_SELL_PRICE,db.rawQuery(" SELECT "+ KEY_SELL_PRICE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_PURCHASE_DATE,db.rawQuery(" SELECT "+ KEY_PURCHASE_DATE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_SELL_DATE,db.rawQuery(" SELECT "+ KEY_SELL_DATE+ " FROM "+ TABLE_NAME ,null));*//*

        System.out.println("HASH MAP DUMP TAVISH: " + values.toString());

        return exportToXLS(c, values, filename);

    }*/

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query,null);
        return data;
    }
}
