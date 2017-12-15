package com.android.Json;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@TargetApi(Build.VERSION_CODES.KITKAT) public class ParserFileJson {
    File file = null;

    /**
     * <p>Initialize ParserFileJson with path of file. Convert File.json toObject or Object toFile.json</p>
     * @param path File Path to convert .json to object ou object to .json
     * @throws java.io.FileNotFoundException
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
     * @throws java.io.IOException
     */
    public <T> T toObject(Class<T> clss) throws com.google.gson.JsonSyntaxException, com.google.gson.JsonIOException ,IOException {
        Gson gson = new Gson();

        BufferedReader buf = new BufferedReader(new FileReader(file));
        Log.d("to Object", "111");
        T t = null;
        try {
            t =gson.fromJson(buf, clss);
        }
        catch(Exception e){
        }
        return t;
    }

    /**
     * Transform Objects in Json File
     * @param obj Object to transform in Json file
     * @throws java.io.IOException
     */
    public void toFile(Object obj) throws IOException {
        FileWriter fileW;
        String fileJson;

        /*if(isExternalStorageWritable()) {
        	
        }*/

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
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if external storage is available to at least read
     * @return
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
