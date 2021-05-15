package com.example.listview;

import android.graphics.Bitmap;

public class Model  {
    private String title;
    private String url;
    private Bitmap image;

    private boolean isSelect=false;


    public String getTitle() {
        return title;
    }

    public void setTitle(String text) {
        this.title = text;
    }
    public String getUrl() {
        return url;
    }

    public void setURL(String text) {
        this.url = text;
    }


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

}