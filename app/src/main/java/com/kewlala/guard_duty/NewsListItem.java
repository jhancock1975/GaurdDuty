package com.kewlala.guard_duty;

import java.util.Date;

/**
 * Created by jhancock2010 on 1/27/18.
 * POJO for list item data
 */

public class NewsListItem {

    private String title;
    private String author;
    private Date date;
    private String url;
    private String section;

    public NewsListItem(String title, String author, Date date, String url, String section) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.url = url;
        this.section = section;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}