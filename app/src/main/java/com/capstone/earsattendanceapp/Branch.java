package com.capstone.earsattendanceapp;

public class Branch {

    private String name, description, key;

    public Branch() {
    }

    public Branch(String name, String description, String key) {
        this.name = name;
        this.description = description;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
