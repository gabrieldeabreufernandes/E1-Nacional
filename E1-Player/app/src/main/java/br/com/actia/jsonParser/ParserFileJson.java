package br.com.actia.jsonParser;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;


/**
 *
 * @author Armani
 */
public class ParserFileJson {
    File file = null;

    /**
     * <p>Initialize ParserFileJson with path of file. Convert File.json toObject or Object toFile.json</p>
     * @param path File Path to convert .json to object ou object to .json
     * @throws FileNotFoundException
     */
    public ParserFileJson(String path) throws FileNotFoundException {
        String mediaState = Environment.getExternalStorageState();

        if(!mediaState.equals(Environment.MEDIA_MOUNTED)) {
            throw new FileNotFoundException("File not found, sd in use.");
        }

        file = new File(path);
        if(!file.exists()) {
            throw new FileNotFoundException("Config File not found in " + path);
        }
    }

    /**
     * Reads file of constructor and makes it a object
     * @param <T>
     * @param clss Class of object
     * @return Object
     * @throws IOException
     */
    public <T> T toObject(Class<T> clss) throws IOException {
        Gson gson = new Gson();
        BufferedReader buf = new BufferedReader(new FileReader(file));
        return gson.fromJson(buf, clss);
    }

    /**
     * Transform Objects in Json File
     * @param obj Object to transform in Json file
     * @throws IOException
     */
    public void toFile(Object obj) throws IOException {
        FileWriter fileW;
        String fileJson;

        Gson gson = new Gson();
        fileJson = gson.toJson(obj);

        fileW = new FileWriter(file);
        fileW.write(fileJson);
        fileW.close();
    }

    /**
     * Checks if external storage is available for read and write
     * @return
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if external storage is available to at least read
     * @return
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
