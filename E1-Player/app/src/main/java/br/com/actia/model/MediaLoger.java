package br.com.actia.model;

import android.location.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import br.com.actia.jsonParser.ParserFileJson;

/**
 * Created by Armani on 26/11/2014.
 */
public class MediaLoger {
    File fileLog = null;
    List<MediaLog> lstMediaLogs = null;

    public MediaLoger(String filePath) throws IOException {
        fileLog = new File(filePath);

        if(filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Invalid path of file Log");
        }

        if(!fileLog.exists() || !fileLog.isFile()) {
            System.out.println("MEDIA LOGER PATH = " + fileLog.getAbsolutePath());
            //Cria arquivo
            fileLog.createNewFile();
        }
    }

    public void log(String mediaName, int mediaType, Location location) {
        MediaLog mediaLog = new MediaLog();

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        mediaLog.setDateTime(currentDateTimeString);
        mediaLog.setMedia(mediaName);
        mediaLog.setType(mediaType);

        if(location != null) {
            mediaLog.setLatitude(location.getLatitude());
            mediaLog.setLongitude(location.getLongitude());
        }
        else {
            mediaLog.setLatitude(0);
            mediaLog.setLongitude(0);
        }

        try {
            ParserFileJson jsonFP = new ParserFileJson(fileLog.getPath());

            MediaListLog mediaListLog = jsonFP.toObject(MediaListLog.class);
            if(mediaListLog == null)
                mediaListLog = new MediaListLog();

            mediaListLog.addMediaLog(mediaLog);

            jsonFP.toFile(mediaListLog);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
