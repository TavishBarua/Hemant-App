package com.demo.tavish.hemantapp.Utils.RetroResponse;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

public static final String BASE_URL="http://192.168.0.109:8080/soul_wings/webapi/";

public static Retrofit retrofit = null;

public static Retrofit getApiClient(){

    if(retrofit==null){
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

    }
    return retrofit;
}



}
