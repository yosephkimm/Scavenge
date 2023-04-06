package com.example.scavenger;

public class Hunt {

    private String name;
    private String desc;

    public Hunt(String name, String description) {
        this.name = name;
        this.desc = description;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public void setDescription(String newDescription) {
        this.desc = newDescription;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }



}
