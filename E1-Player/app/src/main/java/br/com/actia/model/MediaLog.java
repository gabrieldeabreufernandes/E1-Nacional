package br.com.actia.model;


import com.google.gson.annotations.SerializedName;

public class MediaLog {
    public final static int TYPE_BANNER = 1;
    public final static int TYPE_VIDEO = 2;
    public final static int TYPE_RSS = 3;

    @SerializedName("m") private String media;
    @SerializedName("tp") private int type;
    @SerializedName("dtt") private String dateTime;
    @SerializedName("lat") private double latitude;
    @SerializedName("lgt") private double longitude;

    public String getMedia() {
        return media;
    }
    public void setMedia(String media) {
        this.media = media;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
