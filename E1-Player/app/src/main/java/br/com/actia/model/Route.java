package br.com.actia.model;

/**
 *
 * @author Armani
 */
public class Route {
    private int id;
    private int version;
    private String name;
    private String bannerPath;
    private String videoPath;
    private String gpsPointPath;
    private String rssPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBannerPath() {
        return bannerPath;
    }

    public void setBannerPath(String bannerPath) {
        this.bannerPath = bannerPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getGpsPointPath() {
        return gpsPointPath;
    }

    public void setGpsPointPath(String gpsPointPath) {
        this.gpsPointPath = gpsPointPath;
    }

    public String getRssPath() {
        return rssPath;
    }

    public void setRssPath(String rssPath) {
        this.rssPath = rssPath;
    }
}