package com.demo.tavish.hemantapp.DB;

public class DBConstants {

    static final String DATABASE_NAME = "soulWings.db";

    static final String TABLE_NAME = "hemant";
    static final String KEY_ID = "id";
    static final String KEY_BARCODE = "barcode";
    static final String KEY_TYPE = "type";
    static final String KEY_SIZE = "size";
    static final String KEY_PURCHASE_PRICE = "purch_price";
    static final String KEY_SELL_PRICE = "sell_price";
    static final String KEY_PURCHASE_DATE="purch_date";
    static final String KEY_SELL_DATE="sell_date";

    static final String createTable=" CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_BARCODE + " VARCHAR UNIQUE NOT NULL, "
            + KEY_TYPE + " VARCHAR NOT NULL, "
            + KEY_SIZE + " VARCHAR NOT NULL, "
            + KEY_PURCHASE_PRICE + " FLOAT(10,2), "
            + KEY_SELL_PRICE + " FLOAT(10,2), "
            + KEY_PURCHASE_DATE + " DATE DEFAULT (datetime('now','localtime')),"
            + KEY_SELL_DATE + " DATE )";
}
