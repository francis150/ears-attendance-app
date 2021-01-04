package com.capstone.earsattendanceapp;

import java.io.Serializable;

public class EmployeeBasic implements Serializable {

    String key, fname, lname, designation, image_url, deactivated_by;
    Shift shiftIn;

    public EmployeeBasic() {
    }

    public EmployeeBasic(String key, String fname, String lname, String designation, String image_url, String deactivated_by, Shift shiftIn) {
        this.key = key;
        this.fname = fname;
        this.lname = lname;
        this.designation = designation;
        this.image_url = image_url;
        this.deactivated_by = deactivated_by;
        this.shiftIn = shiftIn;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Boolean hasImage() {
        return image_url != null;
    }

    public String getDeactivated_by() {
        return deactivated_by;
    }

    public Boolean isDeactivated() {
        return deactivated_by != null;
    }

    public void setDeactivated_by(String deactivated_by) {
        this.deactivated_by = deactivated_by;
    }

    public Shift getShiftIn() {
        return shiftIn;
    }

    public void setShiftIn(Shift shiftIn) {
        this.shiftIn = shiftIn;
    }

    public Boolean isIn() {
        return shiftIn != null;
    }
}
