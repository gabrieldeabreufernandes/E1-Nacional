package br.com.actia.model;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Armani
 */
public class Configuration implements Serializable {
    private String audioMediasFolder;
    private String bannerFilesFolder;
    private String bannerMediasFolder;
    private String gpsFilesFolder;
    private String imageMediasFolder;
    private String logFilesFolder;
    private String routeFilesFolder;
    private String rssFilesFolder;
    private String videoFilesFolder;
    private String videoMediasFolder;
    private long bannerTime;
    private long bannerInterval;
    private long rssTime;

    public String getAudioMediasFolder() {
        return audioMediasFolder;
    }

    public void setAudioMediasFolder(String audioMediasFolder) {
        this.audioMediasFolder = audioMediasFolder;
    }

    public String getBannerFilesFolder() {
        return bannerFilesFolder;
    }

    public void setBannerFilesFolder(String bannerFilesFolder) {
        this.bannerFilesFolder = bannerFilesFolder;
    }

    public String getBannerMediasFolder() {
        return bannerMediasFolder;
    }

    public void setBannerMediasFolder(String bannerMediasFolder) {
        this.bannerMediasFolder = bannerMediasFolder;
    }

    public String getGpsFilesFolder() {
        return gpsFilesFolder;
    }

    public void setGpsFilesFolder(String gpsFilesFolder) {
        this.gpsFilesFolder = gpsFilesFolder;
    }

    public String getImageMediasFolder() {
        return imageMediasFolder;
    }

    public void setImageMediasFolder(String imageMediasFolder) {
        this.imageMediasFolder = imageMediasFolder;
    }

    public String getLogFilesFolder() { return  logFilesFolder; }

    public void setLogFilesFolder(String logFilesFolder) { this.logFilesFolder = logFilesFolder; }

    public String getRouteFilesFolder() {
        return routeFilesFolder;
    }

    public void setRouteFilesFolder(String routeFilesFolder) {
        this.routeFilesFolder = routeFilesFolder;
    }

    public String getRssFilesFolder() {
        return rssFilesFolder;
    }

    public void setRssFilesFolder(String rssFilesFolder) {
        this.rssFilesFolder = rssFilesFolder;
    }

    public String getVideoFilesFolder() {
        return videoFilesFolder;
    }

    public void setVideoFilesFolder(String videoFilesFolder) {
        this.videoFilesFolder = videoFilesFolder;
    }

    public String getVideoMediasFolder() {
        return videoMediasFolder;
    }

    public void setVideoMediasFolder(String videoMediasFolder) {
        this.videoMediasFolder = videoMediasFolder;
    }

    public long getBannerTime() {
        return bannerTime;
    }

    public void setBannerTime(long bannerTime) {
        this.bannerTime = bannerTime;
    }

    public long getBannerInterval() {
        return bannerInterval;
    }

    public void setBannerInterval(long bannerInterval) {
        this.bannerInterval = bannerInterval;
    }

    public long getRssTime() {
        return rssTime;
    }

    public void setRssTime(long rssTime) {
        this.rssTime = rssTime;
    }

    //Convertion methods
    public String getAudioMediasFullPath(String audioFile) {
        return getFullFolderPath(this.audioMediasFolder, audioFile);
    }

    public String getBannerFilesFullPath(String bannerFile) {
        return getFullFolderPath(this.bannerFilesFolder, bannerFile);
    }

    public String getBannerMediasFullPath(String bannerFile) {
        return getFullFolderPath(this.bannerMediasFolder, bannerFile);
    }

    public String getGpsFilesFullPath(String gpsFile) {
        return getFullFolderPath(this.gpsFilesFolder, gpsFile);
    }

    public String getImageMediasFullPath(String imageFile) {
        return getFullFolderPath(this.imageMediasFolder, imageFile);
    }

    public String getLogFilesFullPath(String logFilesFolder) {
        return getFullFolderPath(this.logFilesFolder, logFilesFolder);
    }

    public String getRouteFilesFullPath(String routeFile) {
        return getFullFolderPath(this.routeFilesFolder, routeFile);
    }

    public String getRssFilesFullPath(String rssFile) {
        return getFullFolderPath(this.rssFilesFolder, rssFile);
    }

    public String getVideoFilesFullPath(String videoFile) {
        return getFullFolderPath(this.videoFilesFolder, videoFile);
    }

    public String getVideoMediasFullPath(String videoFile) {
        return getFullFolderPath(this.videoMediasFolder, videoFile);
    }

    private String getFullFolderPath(String folder, String file) {
        String separator = "";

        if(!folder.endsWith(File.separator)) {
            separator = File.separator;
        }

        String result = new StringBuilder()
                .append(folder)
                .append(separator)
                .append(file)
                .toString();
        return result;
    }
}
