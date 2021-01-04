package com.capstone.earsattendanceapp;

import java.io.Serializable;

public class Shift implements Serializable {
    String key, branch_key, branch_name, day, from, to;

    public Shift() {
    }

    public Shift(String key, String branch_key, String branch_name, String day, String from, String to) {
        this.key = key;
        this.branch_key = branch_key;
        this.branch_name = branch_name;
        this.day = day;
        this.from = from;
        this.to = to;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBranch_key() {
        return branch_key;
    }

    public void setBranch_key(String branch_key) {
        this.branch_key = branch_key;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
