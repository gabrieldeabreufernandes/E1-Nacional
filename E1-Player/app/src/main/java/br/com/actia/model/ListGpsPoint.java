package br.com.actia.model;

import java.util.List;

/**
 *
 * @author Armani
 */
public class ListGpsPoint {
    private int id;
    private int version;
    private String name;
    private List<GpsPoint> listGpsPoint;

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

    public List<GpsPoint> getListGpsPoint() {
        return listGpsPoint;
    }

    public void setListGpsPoint(List<GpsPoint> listGpsPoint) {
        this.listGpsPoint = listGpsPoint;
    }
}