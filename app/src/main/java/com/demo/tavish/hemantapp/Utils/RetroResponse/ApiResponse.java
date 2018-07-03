package com.demo.tavish.hemantapp.Utils.RetroResponse;

import java.util.ArrayList;

public class ApiResponse<T> {

   private String messsage;
   private boolean status;
   private ArrayList<T> object;

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }

    public boolean getStatus() {
        return status;
    }

    public ArrayList<T> getObject() {
        return object;
    }

    public void setObject(ArrayList<T> object) {
        this.object = object;
    }
}
