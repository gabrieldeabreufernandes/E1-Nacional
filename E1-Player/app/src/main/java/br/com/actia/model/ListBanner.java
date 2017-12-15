package br.com.actia.model;

import java.util.List;

/**
 * Implementa lista de Banners para uma determinada Rota
 * @author Armani
 */
public class ListBanner {
    private int id;
    private int version;
    private String name;
    private List<Banner> listBanner;
    // private List<String> listRss;

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

    public List<Banner> getListBanner() {
        return listBanner;
    }

    public void setListBanner(List<Banner> listBanner) {
        this.listBanner = listBanner;
    }

    /*
    public List<String> getListRss() {
        return listRss;
    }

    public void setListRss(List<String> listRss) {
        this.listRss = listRss;
    }
    */
}