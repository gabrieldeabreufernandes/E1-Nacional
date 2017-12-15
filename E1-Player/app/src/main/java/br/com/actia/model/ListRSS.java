package br.com.actia.model;

import java.util.List;

public class ListRSS {
    private String name;
    private List<RSS> listRSS;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RSS> getListRSS() {
        return listRSS;
    }

    public void setListRSS(List<RSS> listRSS) {
        this.listRSS = listRSS;
    }
}