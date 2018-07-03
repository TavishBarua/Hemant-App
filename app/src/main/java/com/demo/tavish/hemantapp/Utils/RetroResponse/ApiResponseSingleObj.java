package com.demo.tavish.hemantapp.Utils.RetroResponse;

public class ApiResponseSingleObj<T> {

    private T object;
    private String message;
    private boolean status;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getStatus() {
        return status;
    }

}
