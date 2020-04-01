package com.example.calendarviewtest;

public class DateItem {
    String date;
    int txtColor, num;

    public DateItem(int n, String date, int color) {
        this.num = n;
        this.date = date;
        this.txtColor = color;
    }

    public String getDate() {
        return date;
    }

    public int getTxtColor() {
        return txtColor;
    }

    public int getNum() {
        return num;
    }
}
