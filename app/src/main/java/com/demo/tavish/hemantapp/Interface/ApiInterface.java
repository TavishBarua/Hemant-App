package com.demo.tavish.hemantapp.Interface;

import com.demo.tavish.hemantapp.Models.ProductDto;
import com.demo.tavish.hemantapp.Utils.RetroResponse.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {


    @POST("product_buy")
    Call<ApiResponse<ProductDto>> product_buy(@Body ProductDto productDto);

    @POST("download_excel")
    Call<ApiResponse<ProductDto>> excel_download();

    @POST("product_buy/product_sell")
    Call<ApiResponse<ProductDto>> product_sell(@Header("barcodeId") String barcodeId,
                                               @Header("sellPrice") Float sellPrice,
                                               @Header("comment") String comment,
                                               @Header("userName") String userName);
    @POST("product_buy/product_return")
    Call<ApiResponse<ProductDto>> product_return(@Header("barcodeId") String barcodeId,
                                               //  @Header("sellPrice") String sellPrice,
                                                 @Header("comment") String comment);
}
