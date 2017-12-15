package br.com.actia.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import br.com.actia.jsonParser.ParserFileJson;

/**
 *
 * @author Armani
 */
public class RouteWithList {
    private Route route = null;
    private ListBanner listBanner = null;
    private ListVideo listVideo = null;
    private ListGpsPoint listGpsPoint = null;
    private ListRSS listRSS = null;
    private Configuration config = null;

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public ListBanner getListBanner() {
        return listBanner;
    }

    public void setListBanner(ListBanner listBanner) {
        this.listBanner = listBanner;
    }

    public ListVideo getListVideo() {
        return listVideo;
    }

    public void setListVideo(ListVideo listVideo) {
        this.listVideo = listVideo;
    }

    public ListGpsPoint getListGpsPoint() {
        return listGpsPoint;
    }

    public void setListGpsPoint(ListGpsPoint listGpsPoint) {
        this.listGpsPoint = listGpsPoint;
    }

    public ListRSS getListRSS() {
        return listRSS;
    }

    public void setListRSS(ListRSS listRSS) {
        this.listRSS = listRSS;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    /**
     * Constructor Method
     * @param config Configuration File Object
     */
    public RouteWithList(Configuration config) {
        this.config = config;
    }

    /**
     *
     * Read File from path, creates route objects from .json files
     *
     * @param routePath Path to file route.json with structure of route
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void fromFile(String routePath) throws FileNotFoundException, IOException {
        String bannerPath;
        String gpsPath;
        String videoPath;
        String RSSPath;

        ParserFileJson json = new ParserFileJson(routePath);
        route = json.toObject(Route.class);

        //Get full paths
        bannerPath = config.getBannerFilesFullPath(route.getBannerPath());
        gpsPath = config.getGpsFilesFullPath(route.getGpsPointPath());
        videoPath = config.getVideoFilesFullPath(route.getVideoPath());
        RSSPath = config.getRssFilesFullPath(route.getRssPath());

        ParserFileJson bnJson = new ParserFileJson(bannerPath);
        listBanner = bnJson.toObject(ListBanner.class);

        ParserFileJson gpsJson = new ParserFileJson(gpsPath);
        listGpsPoint = gpsJson.toObject(ListGpsPoint.class);

        ParserFileJson vdJson = new ParserFileJson(videoPath);
        listVideo = vdJson.toObject(ListVideo.class);

        ParserFileJson rssJson = new ParserFileJson(RSSPath);
        listRSS = rssJson.toObject(ListRSS.class);
    }

    /**
     * Write all objects into respectives files
     * @param routePath Path to route file
     * @throws IOException
     */
    public void toFile(String routePath) throws IOException {
        if(routePath == null || routePath.isEmpty()) {
            throw new IllegalArgumentException("Invalid route path!");
        }

        if(route == null) {
            throw new NullPointerException("Invalid NULL route object");
        }

        ParserFileJson parserRoute = new ParserFileJson(config.getRouteFilesFullPath(routePath));
        parserRoute.toFile(route);

        if(listBanner != null) {
            ParserFileJson parserBanner = new ParserFileJson(config.getBannerFilesFullPath(route.getBannerPath()));
            parserBanner.toFile(listBanner);
        }

        if(listGpsPoint != null) {
            ParserFileJson parserGps = new ParserFileJson(config.getGpsFilesFullPath(route.getGpsPointPath()));
            parserGps.toFile(listGpsPoint);
        }

        if(listVideo != null) {
            ParserFileJson parserVideo = new ParserFileJson(config.getVideoFilesFullPath(route.getVideoPath()));
            parserVideo.toFile(listVideo);
        }

        if(listRSS != null) {
            ParserFileJson parserRSS = new ParserFileJson(config.getRssFilesFullPath(route.getRssPath()));
            parserRSS.toFile(listRSS);
        }
    }
}