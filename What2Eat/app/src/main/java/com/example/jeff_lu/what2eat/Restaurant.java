package com.example.jeff_lu.what2eat;

/**
 * Created by jeff_lu on 2017/5/29.
 */

public class Restaurant {
    private String name;
    private String img;

    public Restaurant(String name, String img){
        this.name = name;
        this.img = img;
    }

    public String getName(){return name;}
    public String getImg(){return img;}
}
