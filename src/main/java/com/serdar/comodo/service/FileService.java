package com.serdar.comodo.service;

import java.io.*;

public class FileService {

    private static FileService instance = null;

    private FileService() {}

    public static FileService getInstance() {
        if(instance == null) {
            instance = new FileService();
        }
        return instance;
    }

    // save uploaded file to new location
    public void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) throws IOException {
        int read;
        final int BUFFER_LENGTH = 1024;
        final byte[] buffer = new byte[BUFFER_LENGTH];
        OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
        while ((read = uploadedInputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.flush();
        out.close();

    }

    /**
     * Remove files from given path
     * @param path
     * @return string result of operation
     */
    public String removeFileFromPath(String path){

        File file = new File(path);
        if(file.delete()){
            return file.getName() + " is deleted!";
        }else{
            return "Delete operation is failed.";
        }
    }
}
