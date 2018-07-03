package com.demo.tavish.hemantapp.Models;

import java.util.Date;

public class ProductDto {

    private String barcodeId;
    private UserDto userName;
    private String productType;
    private Integer productSize;
    private Float puchasePrice;
    private Float sellPrice;
    private Date purchaseDate;
    private Date sellDate;
    private String comment;
    private Date returnDate;
    private Date dateTime;
    private Integer isflag;

    public String getBarcodeId() {
        return barcodeId;
    }
    public void setBarcodeId(String barcodeId) {
        this.barcodeId = barcodeId;
    }
    public UserDto getUserName() {
        return userName;
    }
    public void setUserName(UserDto userName) {
        this.userName = userName;
    }

    public String getProductType() {
        return productType;
    }
    public void setProductType(String productType) {
        this.productType = productType;
    }
    public Integer getProductSize() {
        return productSize;
    }
    public void setProductSize(Integer productSize) {
        this.productSize = productSize;
    }
    public Float getPuchasePrice() {
        return puchasePrice;
    }
    public void setPuchasePrice(Float puchasePrice) {
        this.puchasePrice = puchasePrice;
    }
    public Float getSellPrice() {
        return sellPrice;
    }
    public void setSellPrice(Float sellPrice) {
        this.sellPrice = sellPrice;
    }
    public Date getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    public Date getSellDate() {
        return sellDate;
    }
    public void setSellDate(Date sellDate) {
        this.sellDate = sellDate;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Date getReturnDate() {
        return returnDate;
    }
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
    public Date getDateTime() {
        return dateTime;
    }
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
    public Integer getIsflag() {
        return isflag;
    }
    public void setIsflag(Integer isflag) {
        this.isflag = isflag;
    }



}