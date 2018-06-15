package com.demo.tavish.hemantapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.demo.tavish.hemantapp.Utils.ExportToXLS.exportToXLS;

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
                + KEY_SELL_DATE + " DATE )";

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

    public boolean addSellData(String barcode, Float sell_price){
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SELL_PRICE,sell_price);
        contentValues.put(KEY_SELL_DATE,dateFormat.format(date));
        /*long result =db.execSQL("UPDATE "+TABLE_NAME+" SET "+ KEY_SELL_DATE + " = "+ " datetime() "+ " WHERE "+ KEY_BARCODE + " = " + barcode );*/
      /* long result =  db.update(TABLE_NAME,contentValues, KEY_BARCODE +" = "+ barcode,null);*/

      Cursor c = _checkData(barcode);
      if(c.moveToFirst() && c.getCount()>0){
          Cursor d = _specificData(barcode);
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
                Cursor d = _specificData(barcode);
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

    public Cursor _specificData(String string){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(" SELECT "+ KEY_SELL_PRICE + ","+ KEY_SELL_DATE +" FROM " + TABLE_NAME + " WHERE " + KEY_BARCODE +
                " = " + string, null);
        return data;
    }

    public boolean exportToExcel(Context c,String query){

        String filename = "tavish.xls";
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, Cursor> values = new HashMap<String, Cursor>();
        values.put("Day_1",getAllData());
       /* values.put(KEY_TYPE,db.rawQuery(" SELECT "+ KEY_TYPE + " FROM "+ TABLE_NAME ,null));
        values.put(KEY_SIZE,db.rawQuery(" SELECT "+ KEY_SIZE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_PURCHASE_PRICE,db.rawQuery(" SELECT "+ KEY_PURCHASE_PRICE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_SELL_PRICE,db.rawQuery(" SELECT "+ KEY_SELL_PRICE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_PURCHASE_DATE,db.rawQuery(" SELECT "+ KEY_PURCHASE_DATE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_SELL_DATE,db.rawQuery(" SELECT "+ KEY_SELL_DATE+ " FROM "+ TABLE_NAME ,null));*/

        System.out.println("HASH MAP DUMP TAVISH: " + values.toString());

        return exportToXLS(c, values, filename);

    }




    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query,null);
        return data;
    }
}
