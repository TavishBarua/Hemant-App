package com.demo.tavish.hemantapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.demo.tavish.hemantapp.DB.DBHelper;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScannerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "ScannerActivity";
   ImageButton Btn_scan;
   Button Btn_save, Btn_display;
   Spinner Dropdown_product_type, Dropdown_product_size;
   String Str_dropdown_product_type,Str_dropdown_product_size, s_barcode_json;

   DBHelper dbHelper;

   String s_barcode, s_type, s_size, s_date;
   int  s_purch_price;


   Double Str_purch_price;

   EditText Et_barcode, Et_Purch_price, Et_date_time;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        dbHelper = new DBHelper(this);

        Btn_scan = (ImageButton) findViewById(R.id.btn_scan);
        Btn_save = (Button) findViewById(R.id.btn_save);
        Btn_display = (Button) findViewById(R.id.btn_display);

        Et_barcode = (EditText) findViewById(R.id.et_barcode);
        Et_Purch_price = (EditText) findViewById(R.id.et_purch_price);

        Et_date_time = (EditText) findViewById(R.id.et_date_time);

        Dropdown_product_type = (Spinner) findViewById(R.id.spin_product_type);
        String[] items = new String[]{"Select Type", "Jeans", "Casual Pant", "Formal Pant", "Regular Shirt", "Slim Fit", "Polo Tees"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ScannerActivity.this,
                android.R.layout.simple_spinner_dropdown_item,items);

        Dropdown_product_size = (Spinner) findViewById(R.id.spin_product_size);
        String[] size = new String[]{"S", "M", "L", "XL", "XXL", "XXXL", "28", "30", "32", "34", "36", "38", "40", "42", "44", "46"};
        ArrayAdapter<String> adapter_size = new ArrayAdapter<String>(ScannerActivity.this,
                android.R.layout.simple_spinner_dropdown_item,size);

        Btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ScannerActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Place the code inside the frame");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });

        Dropdown_product_type.setAdapter(adapter);
        Dropdown_product_type.setOnItemSelectedListener(this);

        Dropdown_product_size.setAdapter(adapter_size);
        Dropdown_product_size.setOnItemSelectedListener(this);

        date_picker();
        _addData();

        Btn_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScannerActivity.this, ListDataActivity.class);
                startActivity(i);
            }
        });

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
                Et_barcode.setText(result.getContents());
                s_barcode= Et_barcode.getText().toString();
                s_barcode_json = g.toJson(s_barcode);
                Log.d(TAG, s_barcode_json);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

     //   Str_purch_price = Double.parseDouble(Et_Purch_price.getText().toString());
    }

   /* @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_save:
                String barcode = s_barcode;
                String type = Str_dropdown_product_type;
                String size = Str_dropdown_product_size;
                int purch_price = Integer.parseInt(Et_Purch_price.getText().toString());
                String date = s_date;
                if(Et_barcode.length()!=0 && Et_Purch_price.length()!=0 && Et_date_time.length()!=0){
                    _addData(barcode,type,size,purch_price,date);
                    Et_barcode.setText("");
                    Et_Purch_price.setText("");
                    Et_date_time.setText("");
                }else {
                    toastMessage("TextField  Cannot Be Empty");
                }
                break;

            default:
                break;
        }


    }*/

    public void _addData(){

//        final int p_price = Integer.parseInt(Et_Purch_price.getText().toString());
        Btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    boolean insertData = dbHelper.addData(Et_barcode.getText().toString(), Str_dropdown_product_type, Str_dropdown_product_size,
                        Float.parseFloat(Et_Purch_price.getText().toString()), Et_date_time.getText().toString());*/
                boolean insertData = dbHelper.addData(Et_barcode.getText().toString(), Str_dropdown_product_type, Str_dropdown_product_size,
                        Float.parseFloat(Et_Purch_price.getText().toString()));
                if(insertData){
                    toastMessage("Data Successfully Inserted");
                    Log.d(TAG, Et_barcode.getText().toString());
                    Log.d(TAG,Str_dropdown_product_type);
                    Log.d(TAG,Et_Purch_price.getText().toString());
               //     Log.d(TAG,Str_dropdown_product_size);
                    Log.d(TAG, Et_date_time.getText().toString());


              //  backup();
                }
                else
                    toastMessage("Something Went Wrong");
            }
        });


    }



    public void date_picker(){

       final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        Et_date_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ScannerActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        Et_date_time.setText(sdf.format(myCalendar.getTime()));
        s_date = Et_date_time.getText().toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        if(spinner.getId()==R.id.spin_product_type){
        switch(position){
            case 0:

                break;
            case 1:
                Str_dropdown_product_type = (String) parent.getItemAtPosition(position);
                Toast.makeText(this, Str_dropdown_product_type, Toast.LENGTH_LONG).show();
                break;
            case 2:
                Str_dropdown_product_type = (String) parent.getItemAtPosition(position);
                Toast.makeText(this, Str_dropdown_product_type, Toast.LENGTH_LONG).show();
                break;
            case 3:
                Str_dropdown_product_type = (String) parent.getItemAtPosition(position);
                Toast.makeText(this, Str_dropdown_product_type, Toast.LENGTH_LONG).show();
                break;
            case 4:
                Str_dropdown_product_type = (String) parent.getItemAtPosition(position);
                Toast.makeText(this, Str_dropdown_product_type, Toast.LENGTH_LONG).show();
                break;

        }
        }else if(spinner.getId()==R.id.spin_product_size){
            switch(position){
                case 0:
                    break;
                case 1:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 6:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 7:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 8:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 9:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 10:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 11:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 12:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 13:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 14:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 15:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;
                case 16:
                    Str_dropdown_product_size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, Str_dropdown_product_size, Toast.LENGTH_LONG).show();
                    break;


            }
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void backup() {
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File outputFile = new File(sdcard,
                    "hemant.bak");

            if (!outputFile.exists())
                outputFile.createNewFile();

            File data = Environment.getDataDirectory();
            File inputFile = new File(data, "data/your.package.name/databases/hemant");
            InputStream input = new FileInputStream(inputFile);
            OutputStream output = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("Copying Failed");
        }
    }


}
