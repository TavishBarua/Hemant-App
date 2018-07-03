package com.demo.tavish.hemantapp.Models;

import java.util.Date;

public class UserDto {

    private String userName;
    private String password;
    private Date datetime;
    private Integer isflag;



    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Date getDatetime() {
        return datetime;
    }
    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
    public Integer getIsflag() {
        return isflag;
    }
    public void setIsflag(Integer isflag) {
        this.isflag = isflag;
    }


}
