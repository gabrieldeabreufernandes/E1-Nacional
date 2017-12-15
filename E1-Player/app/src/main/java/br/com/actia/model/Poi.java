package br.com.actia.model;

import java.io.Serializable;

/**
 * Created by Armani on 18/12/2014.
 * Poi is a point of interest
 */
public class Poi implements Serializable {
    public final static int POI_TYPE_DEFAULT = 1;
    public final static int POI_TYPE_SCHOOL = 2;
    public final static int POI_TYPE_HOSPITAL = 3;
    public final static int POI_TYPE_FIREMAN = 4;
    public final static int POI_TYPE_POLICE = 5;
    public final static int POI_TYPE_MONUMENT = 6;
    public final static int POI_TYPE_SHOP = 7;

    private String name;
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
