package com.example.scavenger;

import android.graphics.drawable.Drawable;

public class User {

    private String email;

    private int profilePic;

    public User(String email, int profilePic) {
        this.email = email;
        this.profilePic = profilePic;
    }

    public User() {
        this.email = "";
        this.profilePic = 0;
    }

    public void setProfilePic(int profilePic) {this.profilePic=profilePic;}

    public int getProfilePic() {return this.profilePic;}

    public String getEmail() {return this.email;}

}
