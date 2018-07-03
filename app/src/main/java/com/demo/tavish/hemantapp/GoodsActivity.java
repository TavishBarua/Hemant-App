package com.demo.tavish.hemantapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.tavish.hemantapp.CustomTexts.TextLatoThin;
import com.demo.tavish.hemantapp.DB.DBHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

import static com.demo.tavish.hemantapp.Utils.ExportToXLS.exportToXLS;

public class GoodsActivity extends AppCompatActivity{

    private static final String TAG = "GoodsActivity";
    private int STORAGE_PERMISSION_CODE = 1;
    private int total_sale_today = 0;
    private int total_sale_month = 0;

    ImageButton btn_scan_goods;
    ImageButton btn_buy_goods,btn_sell_goods, btn_export, btn_return;
    EditText et_purch_price_goods,et_barcode_goods, et_sell_price_goods, et_comment, et_comment_return;
    String s_barcode_goods;
    DBHelper dbHelper;

    ImageButton btn_refresh_today_sales, btn_refresh_month_sales;

    LinearLayout ll_purchase, ll_sell, ll_comment, ll_comment_return;
    CoordinatorLayout rr_goods_main;

    Context context;
    RadioButton rb_purchase_sell;
    RadioGroup radioGroup;
    String s_barcode, s_type, s_size, s_comment;
    Float  s_purch_price, s_sell_price;

    TextLatoThin textLatoThin;
    TextView tv_total_sale_today, tv_total_sale_month;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);

        btn_scan_goods = findViewById(R.id.btn_scan_goods);
        btn_buy_goods = findViewById(R.id.btn_buy);
        btn_sell_goods = findViewById(R.id.btn_sell);
        btn_export = findViewById(R.id.btn_export);
        btn_return = findViewById(R.id.btn_return);
        et_purch_price_goods = findViewById(R.id.et_purch_price_goods);
        et_sell_price_goods= findViewById(R.id.et_sell_price_goods);
        et_barcode_goods = findViewById(R.id.et_barcode_goods);
        et_comment = findViewById(R.id.et_comment);
        et_comment_return = findViewById(R.id.et_comment_return);
        radioGroup = findViewById(R.id.rb_group);
       /* rb_purchase = (RadioButton) findViewById(R.id.rb_purchase);
        rb_sell = (RadioButton) findViewById(R.id.rb_sell);*/
        ll_purchase = findViewById(R.id.ll_purchase);
        ll_sell= findViewById(R.id.ll_sell);
        ll_comment = findViewById(R.id.ll_comment);
        ll_comment_return = findViewById(R.id.ll_comment_return);
        rr_goods_main = findViewById(R.id.rr_goods_main);

        tv_total_sale_today = findViewById(R.id.tv_total_sale_today);
        tv_total_sale_month = findViewById(R.id.tv_total_sale_month);
        btn_refresh_today_sales = findViewById(R.id.btn_refresh_today_sales);
        btn_refresh_month_sales = findViewById(R.id.btn_refresh_month_sales);
        /*et_date_time_goods = (EditText) findViewById(R.id.et_date_time);
        dt = new DateTime();
*/
        dbHelper = new DBHelper(this);
        purchase_or_sell();
        _getSalesAmount();




        btn_scan_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(GoodsActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Place the code inside the frame");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });
        _addData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Gson g = new Gson();
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                et_barcode_goods.setText(result.getContents());
                s_barcode_goods= et_barcode_goods.getText().toString();
                Log.d(TAG, s_barcode_goods);
                et_barcode_goods.setError(null);
               /* s_barcode_goods_json = g.toJson(s_barcode_goods);
                Log.d(TAG, s_barcode_goods_json);*/
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

      //  isStoragePermissionGranted();
        /*if(rb_purchase.isChecked()){
            ll_purchase.setVisibility(View.VISIBLE);
            ll_sell.setVisibility(View.GONE);
        }else if(rb_sell.isChecked()){
            ll_sell.setVisibility(View.VISIBLE);
            ll_purchase.setVisibility(View.GONE);
        }*/
    }

    public void purchase_or_sell(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.rb_purchase){

                    ll_purchase.setVisibility(View.VISIBLE);
                    ll_sell.setVisibility(View.GONE);
                    ll_comment.setVisibility(View.GONE);
                    ll_comment_return.setVisibility(View.GONE);
                    et_purch_price_goods.setText("");

                }else if (checkedId==R.id.rb_sell){

                    ll_sell.setVisibility(View.VISIBLE);
                    ll_comment.setVisibility(View.VISIBLE);
                    ll_purchase.setVisibility(View.GONE);
                    ll_comment_return.setVisibility(View.GONE);
                    et_comment.setText("");
                    et_sell_price_goods.setText("");

                }else if (checkedId==R.id.rb_return){

                    ll_comment_return.setVisibility(View.VISIBLE);
                    ll_sell.setVisibility(View.GONE);
                    ll_comment.setVisibility(View.GONE);
                    ll_purchase.setVisibility(View.GONE);
                    et_comment_return.setText("");

                }
            }
        });

    }

    public void _addData() {

        final String MNU_BARCODE = "barcode";
        final String MNU_TYPE = "type";
        final String MNU_SIZE = "size";
     //   final String MNU_PURCHASE_PRICE = "purch_price";
        final String MNU_DATE = "purch_date";
//        final int p_price = Integer.parseInt(Et_Purch_price.getText().toString());
        btn_buy_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_barcode_goods.length()==0){
                    et_barcode_goods.requestFocus();
                    et_barcode_goods.setError("FIELD CANNOT BE EMPTY");
                }else if(et_purch_price_goods.length()==0){
                    et_purch_price_goods.requestFocus();
                    et_purch_price_goods.setError("FIELD CANNOT BE EMPTY");

                }else{

                try {
                   // JSONArray menuItemsJSONArray = new JSONArray(s_barcode_goods_json);

                        if (s_barcode_goods== null){
                            snackBarMessage("Barcode format is not correct");
                            return;
                        }else{
                        JSONObject menuItemObject = new JSONObject(s_barcode_goods);

                        s_barcode = menuItemObject.getString(MNU_BARCODE);
                        s_type = menuItemObject.getString(MNU_TYPE);
                        s_size = menuItemObject.getString(MNU_SIZE);
                        //      s_purch_price = menuItemObject.getString(MNU_PURCHASE_PRICE);
                        // s_date = menuItemObject.getString(MNU_DATE);


                        s_purch_price=Float.parseFloat(et_purch_price_goods.getText().toString());

                        }

                   /* s_date=dt._set_DateTime(et_date_time_goods);*/

               /*     Log.d(TAG, s_barcode);
                    Log.d(TAG,s_type);
                    Log.d(TAG,s_size);
                    //     Log.d(TAG,Str_dropdown_product_size);
                    Log.d(TAG, Float.toString(s_purch_price));
                    Log.d(TAG, s_date);*/

                   boolean insertData = dbHelper.addData(s_barcode,s_type,s_size,s_purch_price);


             /*   boolean insertData = dbHelper.addData(Et_barcode.getText().toString(), Str_dropdown_product_type, Str_dropdown_product_size,
                        Integer.parseInt(Et_Purch_price.getText().toString()), Et_date_time.getText().toString());*/
             try {
                 if(insertData){
                     snackBarMessage("Item Successfully purchased");
                    // toastMessage("Item Successfully purchased");
                     Log.d(TAG, s_barcode);
                     Log.d(TAG,s_type);
                     Log.d(TAG,s_size);
                     //     Log.d(TAG,Str_dropdown_product_size);
                     Log.d(TAG, Float.toString(s_purch_price));
                     /* Log.d(TAG, s_date);*/
                     //  backup();
                 }
                 else
                     snackBarMessage("This Barcode is already in Database");
                   //  toastMessage("This Barcode is already in Database");

             }catch (SQLiteConstraintException e){
                snackBarMessage("Something Went Wrong ::SQLite");
             }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                    snackBarMessage("Barcode format is not correct");
                }
                }

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rr_goods_main.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_sell_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_barcode_goods.length()==0){
                    et_barcode_goods.requestFocus();
                    et_barcode_goods.setError("FIELD CANNOT BE EMPTY");
                }else if(et_sell_price_goods.length()==0){
                    et_sell_price_goods.requestFocus();
                    et_sell_price_goods.setError("FIELD CANNOT BE EMPTY");

                }else if(et_comment.length()==0){
                    et_comment.requestFocus();
                    et_comment.setError("FIELD CANNOT BE EMPTY");

                }
                else{

                    try {
                        if (s_barcode_goods== null){
                            snackBarMessage("Barcode format is not correct");
                        }else {
                            JSONObject menuItemObject = new JSONObject(s_barcode_goods);
                            s_barcode = menuItemObject.getString(MNU_BARCODE);
                            s_sell_price = Float.parseFloat(et_sell_price_goods.getText().toString());
                            s_comment = et_comment.getText().toString();
                        }
                        boolean updateData = dbHelper.addSellData(s_barcode,s_sell_price,s_comment);

                        if (updateData){
                            Log.d(TAG, "sell date added");
                           // toastMessage("Item Successfully Sold");
                            snackBarMessage("Item Successfully Sold");

                        }else
                            snackBarMessage("Not Available to Sell");
                          //  toastMessage("Not Available to Sell");

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                        snackBarMessage("Barcode format is not correct");
                    }
                }
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rr_goods_main.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(GoodsActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                boolean b =  exportToExcel(GoodsActivity.this);
                if(b){
                    Log.d(TAG, "Data Exported Successfully in XLS");
                   // toastMessage("Data Exported Successfully in XLS");
                    snackBarMessage("Data Exported Successfully in XLS");
                }


                else {
                    Log.d(TAG, "Failed to Export");
                  //  toastMessage("Failed to Export");
                    snackBarMessage("Failed to Export");
                }
            }else {
                    requestStoragePermission();
                }
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rr_goods_main.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_return.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(et_barcode_goods.length()==0){
                    et_barcode_goods.requestFocus();
                    et_barcode_goods.setError("FIELD CANNOT BE EMPTY");
                }
                /*else if(et_sell_price_goods.length()==0){
                    et_sell_price_goods.requestFocus();
                    et_sell_price_goods.setError("FIELD CANNOT BE EMPTY");

                }*/else if (et_comment_return.length()==0){
                    et_comment_return.requestFocus();
                    et_comment_return.setError("FIELD CANNOT BE EMPTY");
                }else {
                    try{
                        if (s_barcode_goods== null){
                            snackBarMessage("Barcode format is not correct");
                        }else {
                            JSONObject menuItemObject = new JSONObject(s_barcode_goods);
                            s_barcode = menuItemObject.getString(MNU_BARCODE);
                            // s_sell_price=Float.parseFloat(et_sell_price_goods.getText().toString());
                            s_comment = et_comment_return.getText().toString();
                        }

                        Cursor data = dbHelper.check_sell(s_barcode);
                        data.moveToFirst();
                        try{
                        long id = data.getLong(data.getColumnIndex("sell_price"));

                        System.out.println("The current cursor value is"+id);
                        Log.d(TAG, DatabaseUtils.dumpCursorToString(data));

                        if (id!=0){
                        boolean returndata = dbHelper.update_return_data(s_barcode,s_comment);
                        if (returndata){
                            Log.d(TAG, "item returned successfully");
                            snackBarMessage("Item Successfully Returned");

                        }else{
                            Log.d(TAG, "item not even sold");
                            snackBarMessage("Item Not Even Sold, Return Failed");
                        }
                        }else {
                            Log.d(TAG, "item not even sold");
                            snackBarMessage("Item Not Even Sold, Return Failed");
                        }
                        }catch(CursorIndexOutOfBoundsException e){
                            snackBarMessage("item not even sold");
                        }

                    }catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                        snackBarMessage("Barcode format is not correct");
                    }
                }

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rr_goods_main.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }



   /* public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {


                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }*/

    public boolean exportToExcel(Context c){

        String filename = "SoulWings.xls";
        HashMap<String, Cursor> values = new HashMap<String, Cursor>();
        Cursor cur = dbHelper.getAllData();
        values.put("Day_1",cur);
       /* values.put(KEY_TYPE,db.rawQuery(" SELECT "+ KEY_TYPE + " FROM "+ TABLE_NAME ,null));
        values.put(KEY_SIZE,db.rawQuery(" SELECT "+ KEY_SIZE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_PURCHASE_PRICE,db.rawQuery(" SELECT "+ KEY_PURCHASE_PRICE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_SELL_PRICE,db.rawQuery(" SELECT "+ KEY_SELL_PRICE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_PURCHASE_DATE,db.rawQuery(" SELECT "+ KEY_PURCHASE_DATE+ " FROM "+ TABLE_NAME ,null));
        values.put(KEY_SELL_DATE,db.rawQuery(" SELECT "+ KEY_SELL_DATE+ " FROM "+ TABLE_NAME ,null));*/

        System.out.println("HASH MAP DUMP TAVISH: " + values.toString());

        return exportToXLS(c, values, filename);

    }
   private void requestStoragePermission() {
       if (ActivityCompat.shouldShowRequestPermissionRationale(this,
               Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

           new AlertDialog.Builder(this)
                   .setTitle("Permission needed")
                   .setMessage("This permission is needed because of this and that")
                   .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           ActivityCompat.requestPermissions(GoodsActivity.this,
                                   new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                       }
                   })
                   .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   })
                   .create().show();

       } else {
           ActivityCompat.requestPermissions(this,
                   new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
       }
   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              //  Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                snackBarMessage("Permission GRANTED");
            } else {
              //  Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                snackBarMessage("Permission DENIED");
            }
        }
    }
    public int totalSales_Today(){
        int sum = 0;
        Cursor data = dbHelper.totalSales_Today();
       /* Cursor data = db.rawQuery(" SELECT  SUM( "+ KEY_SELL_PRICE + " ) " + " FROM " + TABLE_NAME + " WHERE " + KEY_SELL_DATE +
                " = " + " DATE() ", null);*/
        data.moveToFirst();
        if(data.getCount()>0){
            sum=data.getInt(0);
        }
        Log.d(TAG, DatabaseUtils.dumpCursorToString(data));
        return sum;
    }
    public int totalSales_Month(){
        int sum = 0;
        Cursor data = dbHelper.totalSales_Month();
       /* Cursor data = db.rawQuery(" SELECT  SUM( "+ KEY_SELL_PRICE + " ) " + " FROM " + TABLE_NAME + " WHERE " + KEY_SELL_DATE +
                " = " + " DATE() ", null);*/
        data.moveToFirst();
        if(data.getCount()>0){
            sum=data.getInt(0);
        }
        Log.d(TAG, DatabaseUtils.dumpCursorToString(data));
        return sum;
    }

    private void _getSalesAmount(){
        btn_refresh_today_sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_sale_today = totalSales_Today();
                if(total_sale_today==-1)
                    tv_total_sale_today.setText("0");
                else
                    tv_total_sale_today.setText(String.valueOf(total_sale_today));
            }
        });

        btn_refresh_month_sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_sale_month = totalSales_Month();
                if(total_sale_month==-1)
                    tv_total_sale_month.setText("0");
                else
                    tv_total_sale_month.setText(String.valueOf(total_sale_month));
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void snackBarMessage(String message){
      Snackbar mSnackBar = Snackbar.make(findViewById(android.R.id.content),message, Snackbar.LENGTH_LONG);
        TextView tv = (mSnackBar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        tv.setTypeface(Typeface.createFromAsset(
                getAssets(),
                "fonts/Lato-Regular.ttf"));
        mSnackBar.show();
    }
}
