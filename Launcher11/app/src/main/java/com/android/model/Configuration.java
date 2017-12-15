package com.android.model;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class Configuration implements Serializable {
    private int colorBG;
    private String imageBG;
    private String imageCompany;
    private String imagesFolder;
    private String password;
    private List<apps> listApps;

    public int getColorBG() {
        return colorBG;
    }

    public void setColorBG(int colorBG) {
        this.colorBG = colorBG;
    }

    public String getImageBG() {
        return imageBG;
    }

    public void setImageBG(String imageBG) {
        this.imageBG = imageBG;
    }

    public String getImageCompany() {
        return imageCompany;
    }

    public void setImageCompany(String imageCompany) {
        this.imageCompany = imageCompany;
    }

    public String getImagesFolder() { return imagesFolder; }

    public void setImagesFolder(String imagesFolder) { this.imagesFolder = imagesFolder; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<apps> getListApps() { return listApps; }

    public void setListApps(List<apps> listApps) { this.listApps = listApps; }

    public String getImagesFullPath(String imageFile) {
        return getFullFolderPath(this.imagesFolder, imageFile);
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
