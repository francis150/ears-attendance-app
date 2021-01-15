package com.capstone.earsattendanceapp;

import java.io.Serializable;

public class Attendance implements Serializable {
    String date, employee_key, employee_fname, employee_lname, employee_designation, employee_img_url, branch_name, branch_description, timed_in, timed_out;
    Shift shift;

    public Attendance() {
    }

    public Attendance(String date, String employee_key, String employee_fname, String employee_lname, String employee_designation, String employee_img_url, String branch_name, String branch_description, String timed_in, String timed_out, Shift shift) {
        this.date = date;
        this.employee_key = employee_key;
        this.employee_fname = employee_fname;
        this.employee_lname = employee_lname;
        this.employee_designation = employee_designation;
        this.employee_img_url = employee_img_url;
        this.branch_name = branch_name;
        this.branch_description = branch_description;
        this.timed_in = timed_in;
        this.timed_out = timed_out;
        this.shift = shift;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmployee_key() {
        return employee_key;
    }

    public void setEmployee_key(String employee_key) {
        this.employee_key = employee_key;
    }

    public String getEmployee_fname() {
        return employee_fname;
    }

    public void setEmployee_fname(String employee_fname) {
        this.employee_fname = employee_fname;
    }

    public String getEmployee_lname() {
        return employee_lname;
    }

    public void setEmployee_lname(String employee_lname) {
        this.employee_lname = employee_lname;
    }

    public String getEmployee_designation() {
        return employee_designation;
    }

    public void setEmployee_designation(String employee_designation) {
        this.employee_designation = employee_designation;
    }

    public String getEmployee_img_url() {
        return employee_img_url;
    }

    public void setEmployee_img_url(String employee_img_url) {
        this.employee_img_url = employee_img_url;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getBranch_description() {
        return branch_description;
    }

    public void setBranch_description(String branch_description) {
        this.branch_description = branch_description;
    }

    public String getTimed_in() {
        return timed_in;
    }

    public void setTimed_in(String timed_in) {
        this.timed_in = timed_in;
    }

    public String getTimed_out() {
        return timed_out;
    }

    public void setTimed_out(String timed_out) {
        this.timed_out = timed_out;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }
}
