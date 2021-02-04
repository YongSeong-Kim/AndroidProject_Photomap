package com.example.workspace1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostResult {

    private String image;
    private String latitude;
    private String longtitude;

    public PostResult(String image, String latitude, String longtitude)
    {
        this.image = image;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public String getImage()
    {
        return image;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public String getLongtitude()
    {
        return longtitude;
    }

    public String setImage(String image)
    {
        return image;
    }

    public String setLatitude(String latitude)
    {
        return latitude;
    }

    public String setLongtitude(String longtitude)
    {
        return longtitude;
    }

}
