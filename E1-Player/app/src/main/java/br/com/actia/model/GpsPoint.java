package br.com.actia.model;

import com.google.android.gms.location.Geofence;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Armani
 */
public class GpsPoint implements Serializable {
    private int id;
    private int version;
    private String name;
    private double latitude;
    private double longitude;
    private float radius;
    private Indication indication;
    private String videosPath;
    private List<Poi> pois;

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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Indication getIndication() {
        return indication;
    }

    public void setIndication(Indication indication) {
        this.indication = indication;
    }

    public String getVideoPath() {
        return videosPath;
    }

    public void setVideoPath(String videoListPath) {
        this.videosPath = videoListPath;
    }

    public List<Poi> getPois() {
        return pois;
    }

    public void setPois(List<Poi> pois) {
        this.pois = pois;
    }

    /**
     * Get this GpsPoint in GeoFence format
     * @return Geofence
     */
    public Geofence getGeoFence() {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(this.getName())
                .setCircularRegion(this.getLatitude(), this.getLongitude(), this.getRadius())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        return geofence;
    }
}