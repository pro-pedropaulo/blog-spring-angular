package com.example.blog.service;

import com.example.blog.model.Photo;
import com.example.blog.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    public List<Photo> findAll() {
        return photoRepository.findAll();
    }

    public Optional<Photo> findById(Long id) {
        return photoRepository.findById(id);
    }

    public Photo save(Photo photo) {
        return photoRepository.save(photo);
    }

    public Optional<Photo> update(Long id, Photo photoDetails) {
        return photoRepository.findById(id)
                .map(existingPhoto -> {
                    existingPhoto.setUrl(photoDetails.getUrl());
                    return photoRepository.save(existingPhoto);
                });
    }

    public boolean delete(Long id) {
        return photoRepository.findById(id)
                .map(photo -> {
                    photoRepository.delete(photo);
                    return true;
                }).orElse(false);
    }
}
