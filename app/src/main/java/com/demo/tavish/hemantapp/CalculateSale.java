package com.demo.tavish.hemantapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import android.text.format.DateFormat;

import com.demo.tavish.hemantapp.Interface.ApiInterface;
import com.demo.tavish.hemantapp.Models.SumDto;
import com.demo.tavish.hemantapp.Utils.RetroResponse.ApiClient;
import com.demo.tavish.hemantapp.Utils.RetroResponse.ApiResponse;
import com.demo.tavish.hemantapp.Utils.RetroResponse.ApiResponseSingleObj;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalculateSale extends AppCompatActivity implements View.OnClickListener,DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    LinearLayout sale_ll_one, sale_ll_two;
    private int mDay, mMonth, mYear, mHour, mMinute;
    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    ImageButton btn_calendar;
    String formatted_date;
    Long millisecondsFrom, millisecondsTo;

    TextView tv_from_date, tv_to_date, tv_total_sale;
    String str_simple_date;
    String date, time;
    boolean fromClicked, toClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_sale);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Calculate Sale");

        sale_ll_one = (LinearLayout) findViewById(R.id.sale_ll_one);
        sale_ll_one.setOnClickListener(this);

        sale_ll_two = (LinearLayout) findViewById(R.id.sale_ll_two);
        sale_ll_two.setOnClickListener(this);

        btn_calendar = (ImageButton) findViewById(R.id.btn_calendar);
        btn_calendar.setOnClickListener(this);

        tv_from_date = (TextView) findViewById(R.id.tv_from_date);
        tv_to_date = (TextView) findViewById(R.id.tv_to_date);
        tv_total_sale = (TextView) findViewById(R.id.tv_total_sale);
        fromClicked = false;
        toClicked = true;




    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.sale_ll_one:

                fromClicked = true;
                getCalendar();
                break;

            case R.id.sale_ll_two:
                toClicked = true;
                getCalendar();
                break;

            case R.id.btn_calendar:
                calculate_sale();
                break;

        }


    }

    private void getCalendar(){

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CalculateSale.this, CalculateSale.this,
                mYear, mMonth, mDay);

        datePickerDialog.show();
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        yearFinal = year;
        monthFinal = month+1;
        dayFinal = dayOfMonth;


        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(CalculateSale.this, CalculateSale.this,
                mHour, mMinute, DateFormat.is24HourFormat(this));

        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Date date = null;
        hourFinal = hourOfDay;
        minuteFinal = minute;


        try{
            String dateString = String.valueOf(dayFinal)+"-"+String.valueOf(monthFinal)+"-"+String.valueOf(yearFinal)
                    +" "+String.valueOf(hourFinal)+":"+String.valueOf(minuteFinal);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            date = sdf.parse(dateString);
            formatted_date = sdf.format(date);

        }catch (Exception e){e.printStackTrace();}


        if (fromClicked) {
           // tv_from_date.setText(dayFinal+"-"+"0"+monthFinal+"-"+yearFinal+" "+hourFinal+":"+minuteFinal);
            tv_from_date.setText(formatted_date);
            millisecondsFrom = date.getTime();
            System.out.println(millisecondsFrom);
           // str_simple_date = dayFinal+"-"+"0"+monthFinal+"-"+yearFinal+" "+hourFinal+":"+minuteFinal;
            fromClicked = false;
        }
        else if (toClicked){
            tv_to_date.setText(formatted_date);
            millisecondsTo = date.getTime();
            System.out.println(millisecondsTo);
          //  tv_to_date.setText(dayFinal+"-"+"0"+monthFinal+"-"+yearFinal+" "+hourFinal+":"+minuteFinal);
          //  str_simple_date = dayFinal+"-"+"0"+monthFinal+"-"+yearFinal+" "+hourFinal+":"+minuteFinal;
            toClicked = false;
        }

    }

    private void calculate_sale(){

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        final SumDto sumDto = new SumDto();

        Call<ApiResponseSingleObj<SumDto>> call = apiInterface.calculate_sale(millisecondsFrom, millisecondsTo);
        call.enqueue(new Callback<ApiResponseSingleObj<SumDto>>() {
            @Override
            public void onResponse(Call<ApiResponseSingleObj<SumDto>> call, Response<ApiResponseSingleObj<SumDto>> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus()){
                        if (response.body().getObject().getSum()!=null) {
                            tv_total_sale.setText(response.body().getObject().getSum());
                            System.out.println(response.body().getObject().getSum());
                        }else{
                            snackBarMessage("Something Wrong with Calculation");
                        }

                    }else
                        snackBarMessage("Response Body Error");
                }else{
                    snackBarMessage("Something Went Wrong");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseSingleObj<SumDto>> call, Throwable t) {
                snackBarMessage("Server Error");
            }
        });
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
