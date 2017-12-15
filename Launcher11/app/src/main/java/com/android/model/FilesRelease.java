package com.android.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.io.FileInputStream;
import java.nio.channels.FileChannel;

public class FilesRelease {

    private String USB_FOLDER = "/mnt/usbhost0/E1Player/";
    private final String SDCARD_FOLDER = "/mnt/extsd/E1Player/";

    public boolean Release()
    {
        File usbFile, sdcardFile;
        usbFile = new File(USB_FOLDER);
        sdcardFile = new File(SDCARD_FOLDER);

        if((!usbFile.exists())||(usbFile.length()==0))
        {
            USB_FOLDER = "/mnt/usbhost1/E1Player/";
            usbFile = new File(USB_FOLDER);
        }

        if((!usbFile.exists())||(usbFile.length()==0))
        {
            System.out.print("Não foi possível atualizar os arquivos!");
            return false;
        }

        Map<String,File> usbMap, sdcardMap;
        usbMap =  new HashMap<String, File>();
        sdcardMap =  new HashMap<String, File>();

        updateFilesMap(usbFile, usbMap, null);
        updateFilesMap(sdcardFile, sdcardMap, null);

        //verifica se os arquivos que existem no SDCARD existem na USB, se não existem, deleta.
        for (final Map.Entry<String, File> fileEntry : sdcardMap.entrySet())
        {
            if(!usbMap.containsKey(fileEntry.getKey())){
                System.out.println("SDCARD " + fileEntry.getKey());
                File f = new File(SDCARD_FOLDER + fileEntry.getKey());
                if (f.exists() && f.isFile())
                    f.delete();
            }
        }


        for(final Map.Entry<String, File> fileEntry : usbMap.entrySet())
        {
            //verifica se os arquivos que existem na USB existem no SDCARD, se não existem, copia.
            if (!sdcardMap.containsKey(fileEntry.getKey())) {
                System.out.println("USB " + fileEntry.getKey());
                File src = new File(USB_FOLDER + fileEntry.getKey());
                File dest = new File(SDCARD_FOLDER + fileEntry.getKey());
                if (src.exists() && src.isFile())
                    try{
                        copyFile(src, dest);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            //verifica se os arquivos é um .json se for copia no SDCARD independentemente de qualquer coisa
            if(fileEntry.getKey().endsWith(".json")) {
                File src = new File(USB_FOLDER + fileEntry.getKey());
                File dest = new File(SDCARD_FOLDER + fileEntry.getKey());
                File f = new File(SDCARD_FOLDER + fileEntry.getKey());
                try {
                    f.delete();
                    copyFile(src, dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    private static void copyFile(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            sourceChannel.close();
            destChannel.close();
        }
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
