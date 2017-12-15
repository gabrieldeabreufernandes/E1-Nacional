package br.com.actia.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import br.com.actia.jsonParser.ParserFileJson;

/**
 *
 * @author Armani
 */
public class GpsPointWithList {
    private GpsPoint gpsPoint = null;
    private ListVideo listVideo = null;
    private Configuration config = null;

    public GpsPoint getGpsPoint() {
        return gpsPoint;
    }

    public void setGpsPoint(GpsPoint gpsPoint) {
        this.gpsPoint = gpsPoint;
    }

    public ListVideo getListVideo() {
        return listVideo;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public void setListVideo(ListVideo listVideo) {
        this.listVideo = listVideo;
    }

    public GpsPointWithList(Configuration config) {
        this.config = config;
    }
    /**
     * Load ListVideo from file gpsPoint.listVideoPath
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void fromFile() throws FileNotFoundException, IOException {
        String videoListPath;

        if(gpsPoint == null) {
            throw new NullPointerException("Invalid NULL GpsPoint");
        }

        videoListPath = config.getVideoFilesFullPath(gpsPoint.getVideoPath());

        ParserFileJson vdJson = new ParserFileJson(videoListPath);
        listVideo = vdJson.toObject(ListVideo.class);
    }
    /**
     * Write listVideo into file gpsPoint.listVideoPath
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void toFile() throws FileNotFoundException, IOException {

        if(gpsPoint == null || gpsPoint.getVideoPath() == null || gpsPoint.getVideoPath().isEmpty()) {
            throw new IllegalArgumentException("Invalid ListVideo path!");
        }

        if(listVideo == null) {
            throw new NullPointerException("Invalid NULL ListVideo object");
        }

        ParserFileJson parserListVideo = new ParserFileJson(config.getVideoFilesFullPath(gpsPoint.getVideoPath()));
        parserListVideo.toFile(listVideo);
    }
}