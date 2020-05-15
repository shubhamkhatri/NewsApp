package com.example.android.shubhamnewsapp;

import java.util.ArrayList;
import java.util.Date;

public class News {
    String title;
    ArrayList<String> category;
    Date date;
    String url;
    String section;
    String imageUrl;

    public News(String title, ArrayList<String> category, Date date, String url, String section, String imageUrl) {
        this.title = title;
        this.category = category;
        this.date = date;
        this.url = url;
        this.section = section;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<String> getcategory() {
        return category;
    }

    public String getSection() {
        return section;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getcategoryLine(){
        StringBuilder builder = new StringBuilder();
        if (category.isEmpty())
            return "";
        for (String author : category){
            builder.append(author + ", ");
        }
        builder.delete(builder.length() - 2, builder.length() - 1);
        return builder.toString();
    }
}
