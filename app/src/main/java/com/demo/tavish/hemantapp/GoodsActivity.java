package com.demo.tavish.hemantapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.demo.tavish.hemantapp.DB.DBHelper;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.demo.tavish.hemantapp.Utils.ExportToXLS.exportToXLS;

public class GoodsActivity extends AppCompatActivity{

    private static final String TAG = "GoodsActivity";
    private int STORAGE_PERMISSION_CODE = 1;

    ImageButton btn_scan_goods;
    Button btn_buy_goods,btn_sell_goods, btn_export;
    EditText et_purch_price_goods,et_barcode_goods, et_sell_price_goods;
    String s_barcode_goods;
    DBHelper dbHelper;

    LinearLayout ll_purchase, ll_sell;

    RadioButton rb_purchase_sell;
    RadioGroup radioGroup;
    String s_barcode, s_type, s_size;
    Float  s_purch_price, s_sell_price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);

        btn_scan_goods = (ImageButton) findViewById(R.id.btn_scan_goods);
        btn_buy_goods = (Button) findViewById(R.id.btn_buy);
        btn_sell_goods = (Button) findViewById(R.id.btn_sell);
        btn_export = (Button) findViewById(R.id.btn_export);
        et_purch_price_goods = (EditText) findViewById(R.id.et_purch_price_goods);
        et_sell_price_goods=(EditText) findViewById(R.id.et_sell_price_goods);
        et_barcode_goods = (EditText) findViewById(R.id.et_barcode_goods);
        radioGroup = (RadioGroup) findViewById(R.id.rb_group);
       /* rb_purchase = (RadioButton) findViewById(R.id.rb_purchase);
        rb_sell = (RadioButton) findViewById(R.id.rb_sell);*/
        ll_purchase = (LinearLayout) findViewById(R.id.ll_purchase);
        ll_sell=(LinearLayout) findViewById(R.id.ll_sell);
        /*et_date_time_goods = (EditText) findViewById(R.id.et_date_time);
        dt = new DateTime();
*/
        dbHelper = new DBHelper(this);
        purchase_or_sell();

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
        try {
            _addData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

                }else if (checkedId==R.id.rb_sell){

                    ll_sell.setVisibility(View.VISIBLE);
                    ll_purchase.setVisibility(View.GONE);
                }
            }
        });

    }

    public void _addData() throws JSONException{

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


                        JSONObject menuItemObject = new JSONObject(s_barcode_goods);

                        s_barcode = menuItemObject.getString(MNU_BARCODE);
                        s_type = menuItemObject.getString(MNU_TYPE);
                        s_size = menuItemObject.getString(MNU_SIZE);
                        //      s_purch_price = menuItemObject.getString(MNU_PURCHASE_PRICE);
                        // s_date = menuItemObject.getString(MNU_DATE);


                        s_purch_price=Float.parseFloat(et_purch_price_goods.getText().toString());



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
                     toastMessage("Item Successfully purchased");
                     Log.d(TAG, s_barcode);
                     Log.d(TAG,s_type);
                     Log.d(TAG,s_size);
                     //     Log.d(TAG,Str_dropdown_product_size);
                     Log.d(TAG, Float.toString(s_purch_price));
                     /* Log.d(TAG, s_date);*/
                     //  backup();
                 }
                 else

                     toastMessage("Something Went Wrong");

             }catch (SQLiteConstraintException e){
                toastMessage("This Barcode is already in Database");
             }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

                }
                else{

                    try {
                        JSONObject menuItemObject = new JSONObject(s_barcode_goods);
                        s_barcode = menuItemObject.getString(MNU_BARCODE);
                        s_sell_price=Float.parseFloat(et_sell_price_goods.getText().toString());

                        boolean updateData = dbHelper.addSellData(s_barcode,s_sell_price);

                        if (updateData){
                            Log.d(TAG, "sell date added");
                            toastMessage("Item Successfully Sold");

                        }else
                            toastMessage("Not Available to Sell");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



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
                    toastMessage("Data Exported Successfully in XLS");
                }


                else {
                    Log.d(TAG, "Failed to Export");
                    toastMessage("Failed to Export");
                }
            }else {
                    requestStoragePermission();
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
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
