package br.com.actia.model;

import java.io.Serializable;

public class Indication implements Serializable{
    private String image;
    private String audio;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
