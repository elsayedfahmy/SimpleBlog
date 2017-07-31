package com.example.elsayedfahmy_pc.simpleblog;

/**
 * Created by elsayedfahmy-pc on 19/07/2017.
 */

public class blog {
    private String title;
    private String image;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    private String description;
    public blog()
    {

    }
    public blog(String title, String image, String description,String username) {
        this.title = title;
        this.image = image;
        this.description = description;
        this.username=username;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public void setImage(String image) {
        this.image = image;
    }


    public String getTitle() {
        return title;
    }



    public String getImage() {
        return image;
    }


}
