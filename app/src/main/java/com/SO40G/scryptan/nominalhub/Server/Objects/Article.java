package com.SO40G.scryptan.nominalhub.Server.Objects;

public class Article {

    public String _id;
    public String name;
    public String color;
    public String text;
    public String pic;
    public String replies;
    public String date;
    public String theme;
    public String serverDate;

    public Article(String _id, String name, String color, String text, String pic, String replies, String date, String theme) {
        this._id = _id;
        this.name = name;
        this.color = color;
        this.text = text;
        this.pic = pic;
        this.replies = replies;
        this.date = date;
        this.theme = theme;
    }
}
