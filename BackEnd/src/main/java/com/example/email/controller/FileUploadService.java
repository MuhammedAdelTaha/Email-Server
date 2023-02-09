package com.example.email.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileUploadService {
    @Value("${file.upload-dir}")
    private String fileDirectory;

    public void uploadFile(MultipartFile file) throws IllegalStateException, IOException {
        File myFile = new File(fileDirectory + file.getOriginalFilename());
        myFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(myFile);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();
    }
}
