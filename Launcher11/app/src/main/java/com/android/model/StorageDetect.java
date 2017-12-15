package com.android.model;

import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Actia on 14/04/2015.
 */
public class StorageDetect {
    private String rootPath;

    void StorageDetect () {
    }

    public String getRootPath() {
        Map<String, File> mapFile = new HashMap<String, File>();
        File baseFolder = Environment.getExternalStorageDirectory();

        updateFilesMap(baseFolder, mapFile, null);

        File fileE1P = mapFile.get("E1Player");

        String strRet = "NADA";
        if(fileE1P != null)
            strRet = fileE1P.getAbsolutePath();

        return mapFile.toString();//strRet;
    }

    private void updateFilesMap(final File baseFolder, final Map<String, File> filesMap,
                                final String relativeName) {
        for (final File file : baseFolder.listFiles()) {
            final String fileRelativeName = getFileRelativeName (relativeName, file.getName());

            if (file.isDirectory()) {
                updateFilesMap(file, filesMap, fileRelativeName);
            }
            else {
                final File existingFile = filesMap.get (fileRelativeName);
                if (existingFile == null || file.lastModified() > existingFile.lastModified() ) {
                    filesMap.put (fileRelativeName, file);
                }
            }
        }
    }

    private String getFileRelativeName(final String baseName, final String fileName) {
        return baseName == null ? fileName : baseName + "/" + fileName;
    }
}
