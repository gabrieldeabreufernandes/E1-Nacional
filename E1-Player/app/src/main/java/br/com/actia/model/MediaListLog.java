package br.com.actia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Armani on 26/11/2014.
 */
public class MediaListLog {
    private List<MediaLog> logs;

    public MediaListLog() {
        logs = new ArrayList<MediaLog>();
    }

    public void addMediaLog(MediaLog mediaLog) {
        logs.add(mediaLog);
    }

    public List<MediaLog> getLogs() {
        return logs;
    }

    public void setLogs(List<MediaLog> logs) {
        this.logs = logs;
    }
}
