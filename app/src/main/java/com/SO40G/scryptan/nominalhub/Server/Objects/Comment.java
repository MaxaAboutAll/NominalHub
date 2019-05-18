package com.SO40G.scryptan.nominalhub.Server.Objects;

public class Comment {

    public String _id;
    public String nick;
    public String text;
    public String pic;
    public String color;
    public String thread;
    public String date;

    public Comment(String _id, String nick, String text, String pic, String color, String thread, String date) {
        this._id = _id;
        this.nick = nick;
        this.text = text;
        this.pic = pic;
        this.color = color;
        this.thread = thread;
        this.date = date;
    }
}
