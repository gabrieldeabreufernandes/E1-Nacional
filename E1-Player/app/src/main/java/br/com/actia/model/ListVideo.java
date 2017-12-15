package br.com.actia.model;

import java.util.List;

/**
 *
 * @author Actia
 */
public class ListVideo {
    private int id;
    private int version;
    private String name;
    private List<Video> listVideo;

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

    public List<Video> getListVideo() {
        return listVideo;
    }

    public void setListVideo(List<Video> listVideo) {
        this.listVideo = listVideo;
    }
}