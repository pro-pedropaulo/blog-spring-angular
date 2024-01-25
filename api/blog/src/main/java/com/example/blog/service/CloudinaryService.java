package com.example.blog.service;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private Cloudinary cloudinary;

    public CloudinaryService() {
//        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
//                "cloud_name", "dq8fo12xu",
//                "api_key", "377558495654854",
//                "api_secret", "Hs-Fd3xcDfL2S8rczQGyDZFQejE"
//        ));
    }

    public String uploadImage(MultipartFile file) throws IOException {
        File uploadedFile = convertMultiPartToFile(file);
        Map uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
        uploadedFile.delete();
        return (String) uploadResult.get("url");
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
