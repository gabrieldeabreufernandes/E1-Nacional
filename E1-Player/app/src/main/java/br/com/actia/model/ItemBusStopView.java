package br.com.actia.model;

public class ItemBusStopView {
    private String busStop;
    private int iconRid;

    public ItemBusStopView(String busStop, int iconRid) {
        this.busStop = busStop;
        this.iconRid = iconRid;
    }

    public String getBusStop() {
        return busStop;
    }

    public void setBusStop(String busStop) {
        this.busStop = busStop;
    }

    public int getIconRid() {
        return iconRid;
    }

    public void setIconRid(int iconRid) {
        this.iconRid = iconRid;
    }
}
