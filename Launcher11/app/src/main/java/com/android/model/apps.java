package com.android.model;

public class apps {
    private String image;
    private String name;
    private String intent;
    private boolean haspassword;

    public apps(String image, String name, String intent, boolean haspassword){
        this.image = image;
        this.name = name;
        this.intent = intent;
        this.haspassword = haspassword;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public boolean getHaspassword() {
        return haspassword;
    }

    public void sethaspassword(boolean haspassword) {
        this.haspassword = haspassword;
    }

}
