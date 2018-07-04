package com.demo.tavish.hemantapp;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.tavish.hemantapp.Interface.ApiInterface;
import com.demo.tavish.hemantapp.Models.ExcelDto;
import com.demo.tavish.hemantapp.Models.ProductDto;
import com.demo.tavish.hemantapp.Models.SumDto;
import com.demo.tavish.hemantapp.Models.UserDto;
import com.demo.tavish.hemantapp.Utils.Constants;
import com.demo.tavish.hemantapp.Utils.RetroResponse.ApiClient;
import com.demo.tavish.hemantapp.Utils.RetroResponse.ApiResponse;
import com.demo.tavish.hemantapp.Utils.RetroResponse.ApiResponseSingleObj;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.poi.util.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "InventoryActivity";

    private String s_barcode_goods;
    String s_barcode, s_type, s_size, s_comment;
    Float  s_purch_price, s_sell_price;

    ImageButton btn_scan_goods;
    ImageButton btn_buy_goods,btn_sell_goods, btn_export, btn_return;
    EditText et_purch_price_goods,et_barcode_goods, et_sell_price_goods, et_comment, et_comment_return;

    LinearLayout ll_purchase, ll_sell, ll_comment, ll_comment_return;

    RadioGroup radioGroup;

    TextView tv_total_sale_today, tv_total_sale_month;

    ImageButton btn_refresh_today_sales, btn_refresh_month_sales;

    final String MNU_BARCODE = "barcode";
    final String MNU_TYPE = "type";
    final String MNU_SIZE = "size";
    final String MNU_DATE = "purch_date";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);


        btn_scan_goods = findViewById(R.id.btn_scan_goods);

        btn_buy_goods = findViewById(R.id.btn_buy);
        btn_buy_goods.setOnClickListener(this);

        btn_sell_goods = findViewById(R.id.btn_sell);
        btn_sell_goods.setOnClickListener(this);

        btn_export = findViewById(R.id.btn_export);
        btn_export.setOnClickListener(this);

        btn_return = findViewById(R.id.btn_return);
        btn_return.setOnClickListener(this);

        et_barcode_goods = findViewById(R.id.et_barcode_goods);
        et_purch_price_goods = findViewById(R.id.et_purch_price_goods);
        et_sell_price_goods= findViewById(R.id.et_sell_price_goods);
        et_comment = findViewById(R.id.et_comment);
        et_comment_return = findViewById(R.id.et_comment_return);

        radioGroup = findViewById(R.id.rb_group);

        ll_purchase = findViewById(R.id.ll_purchase);
        ll_sell= findViewById(R.id.ll_sell);
        ll_comment = findViewById(R.id.ll_comment);
        ll_comment_return = findViewById(R.id.ll_comment_return);


        tv_total_sale_today = findViewById(R.id.tv_total_sale_today);
        tv_total_sale_month = findViewById(R.id.tv_total_sale_month);
        btn_refresh_today_sales = findViewById(R.id.btn_refresh_today_sales);
        btn_refresh_month_sales = findViewById(R.id.btn_refresh_month_sales);

        purchase_or_sell();

        btn_scan_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(InventoryActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Place the code inside the frame");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });
       /* addData();
        addSell();*/


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

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_buy:
                    if(et_barcode_goods.length()==0){
                        et_barcode_goods.requestFocus();
                        et_barcode_goods.setError("FIELD CANNOT BE EMPTY");
                    }else if(et_purch_price_goods.length()==0){
                        et_purch_price_goods.requestFocus();
                        et_purch_price_goods.setError("FIELD CANNOT BE EMPTY");

                    }else{
                        try{
                            if (s_barcode_goods== null){
                                snackBarMessage("Barcode format is not correct");
                                return;
                            }else {
                                JSONObject menuItemObject = new JSONObject(s_barcode_goods);

                                s_barcode = menuItemObject.getString(MNU_BARCODE);
                                s_type = menuItemObject.getString(MNU_TYPE);
                                s_size = menuItemObject.getString(MNU_SIZE);
                                //      s_purch_price = menuItemObject.getString(MNU_PURCHASE_PRICE);
                                // s_date = menuItemObject.getString(MNU_DATE);


                                s_purch_price=Float.parseFloat(et_purch_price_goods.getText().toString());

                                addPurchase();

                                Log.d(TAG,s_barcode);
                                Log.d(TAG,s_type);
                                Log.d(TAG,s_size);
                                Log.d(TAG,String.valueOf(s_purch_price));
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    break;


            case R.id.btn_sell:
                if(et_barcode_goods.length()==0){
                    et_barcode_goods.requestFocus();
                    et_barcode_goods.setError("FIELD CANNOT BE EMPTY");
                }else if(et_sell_price_goods.length()==0){
                    et_sell_price_goods.requestFocus();
                    et_sell_price_goods.setError("FIELD CANNOT BE EMPTY");

                }else{

                    try{
                        if (s_barcode_goods== null){
                            snackBarMessage("Barcode format is not correct");
                            return;
                        }else{
                            JSONObject menuItemObject = new JSONObject(s_barcode_goods);
                            s_barcode = menuItemObject.getString(MNU_BARCODE);
                            addSell();

                        }
                    }catch(JSONException e){e.printStackTrace();}
                }
                break;

            case R.id.btn_return:
                if(et_barcode_goods.length()==0){
                    et_barcode_goods.requestFocus();
                    et_barcode_goods.setError("FIELD CANNOT BE EMPTY");
                }else if(et_comment_return.length()==0){
                    et_comment_return.requestFocus();
                    et_comment_return.setError("FIELD CANNOT BE EMPTY");

                }else{
                    try{
                        if (s_barcode_goods== null){
                            snackBarMessage("Barcode format is not correct");
                            return;
                        }else {
                            JSONObject menuItemObject = new JSONObject(s_barcode_goods);
                            s_barcode = menuItemObject.getString(MNU_BARCODE);
                            addReturn();
                        }
                    }catch (JSONException e){e.printStackTrace();}

                }
                break;

            case R.id.btn_export:
                if(ContextCompat.checkSelfPermission(InventoryActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                    exportToExcel();

                }else {
                    requestStoragePermission();
                }

        }
    }


    private void addPurchase(){

        try{
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            final ProductDto productDto = new ProductDto();
            productDto.setBarcodeId(s_barcode);
            productDto.setProductType(s_type);
            productDto.setProductSize(Integer.parseInt(s_size));
            productDto.setPuchasePrice(s_purch_price);
            //productDto.setUserName();

            Call<ApiResponse<ProductDto>> call = apiInterface.product_buy(productDto);

            call.enqueue(new Callback<ApiResponse<ProductDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<ProductDto>> call, Response<ApiResponse<ProductDto>> response) {

                    try{
                        if (response.isSuccessful()){

                            if(response.body().getStatus()) {

                                snackBarMessage("Product Added");
                                System.out.println(productDto.getBarcodeId());
                                System.out.println(productDto.getProductType());
                                System.out.println(productDto.getProductSize());

                            }else {
                                snackBarMessage("Product already exists in Database");
                            }
                        }else
                            snackBarMessage("Something Went Wrong");

                    }catch(Exception e){

                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<ApiResponse<ProductDto>> call, Throwable t) {
                    snackBarMessage("Server Error");
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void addSell(){

        try{
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            final ProductDto productDto = new ProductDto();

            // to be edited BY TAVISH.. latest edit on== 02/07/2018 23:10
            productDto.setBarcodeId(s_barcode);
            productDto.setSellPrice(Float.parseFloat(et_sell_price_goods.getText().toString()));
            productDto.setComment(""+et_comment.getText());

            UserDto userDto = new UserDto();
            userDto.setUserName("pawan");
          //  final String userName = String.valueOf(userDto.getUserName());

            //productDto.setUserName();
            Call<ApiResponse<ProductDto>> call = apiInterface.product_sell(productDto.getBarcodeId(),
                    productDto.getSellPrice(),productDto.getComment(),userDto.getUserName() );
            call.enqueue(new Callback<ApiResponse<ProductDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<ProductDto>> call, Response<ApiResponse<ProductDto>> response) {
                    try{

                        if (response.isSuccessful()){
                            // Log.d(TAG,"Response Successfull");
                            if (response.body().getStatus()){
                                snackBarMessage("Product Successfully Sold");
                            }else {
                                snackBarMessage("Not Available to Sell");
                            }

                        }else{
                            snackBarMessage("Something Went Wrong");
                        }
                       /* if(response.isSuccessful()){
                            if (response.body().getStatus()){
                                snackBarMessage("Product Successfully Sold");
                                System.out.println("Tavish,Product Successfully Sold ");
                            }else {
                                snackBarMessage("Not Available to Sell");
                                }
                            }else
                            snackBarMessage("Server Error");*/

                     /*   Log.d(TAG,productDto.getBarcodeId()+" "+ productDto.getSellPrice()+" "+productDto.getComment()
                                +" "+userName);*/
                    }catch (Exception e){e.printStackTrace();}
                }

                @Override
                public void onFailure(Call<ApiResponse<ProductDto>> call, Throwable t) {
                    snackBarMessage("Server Error");
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addReturn(){

        try{
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        final ProductDto productDto = new ProductDto();
        productDto.setBarcodeId(s_barcode);
        productDto.setComment(et_comment.getText().toString());

        Call<ApiResponse<ProductDto>> call = apiInterface.product_return(productDto.getBarcodeId(), productDto.getComment());
                call.enqueue(new Callback<ApiResponse<ProductDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ProductDto>> call, Response<ApiResponse<ProductDto>> response) {

                        try{
                        if(response.isSuccessful()){
                            if (response.body().getStatus()){
                                snackBarMessage("Item Successfully Returned");

                            }else{
                                snackBarMessage("Item Not Even Sold, Return Failed");
                            }

                        }else
                            snackBarMessage("Something Went Wrong");

                        }catch(Exception e){e.printStackTrace();}
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ProductDto>> call, Throwable t) {
                        snackBarMessage("Server Error");
                    }
                });

        }catch (Exception e){e.printStackTrace();}


    }

    private void exportToExcel() {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ApiResponseSingleObj<ExcelDto>> call = apiInterface.download_excel();


        call.enqueue(new Callback<ApiResponseSingleObj<ExcelDto>>() {
            @Override
            public void onResponse(Call<ApiResponseSingleObj<ExcelDto>> call, Response<ApiResponseSingleObj<ExcelDto>> response) {

                if (response.isSuccessful()) {

                    if (response.body().getStatus()) {


                        file_download(Constants.DOWNLOAD_URL+"excel.xlsx");
                        snackBarMessage("Successfully Exported to XLS");
                       /* try{
                        File path = Environment.getExternalStorageDirectory();
                        File file = new File(path, "soul_wings.xlsx");
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                       // IOUtils.write(response.body().bytes(), fileOutputStream);

                        snackBarMessage("Successfully Exported to XLS");
                        }catch (FileNotFoundException e){e.printStackTrace();}
*/
                    } else {
                        snackBarMessage("Failed To Export");
                    }

                } else {
                    snackBarMessage("Something Went Wrong");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseSingleObj<ExcelDto>> call, Throwable t) {
                snackBarMessage("Server Error");
            }
        });

    }

        public void file_download(String url) {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/tavish_files");

            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Soul Wings Report File")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDescription("Downloading File")
                    .setDestinationInExternalPublicDir("/tavish_files", "soul_wings.xlsx");

            snackBarMessage("Your File is Downloading...");
            mgr.enqueue(request);


        }

    /*public void sale_calculate(){

        try{
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
           // Call<ApiResponseSingleObj<SumDto>> call = apiInterface.calculate_sum()



        }catch (Exception e){e.printStackTrace();}

    }*/

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(InventoryActivity.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.STORAGE_PERMISSION_CODE);
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
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.STORAGE_PERMISSION_CODE);
        }
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
