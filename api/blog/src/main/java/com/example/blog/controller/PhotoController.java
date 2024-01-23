package com.example.blog.controller;

import com.example.blog.model.Photo;
import com.example.blog.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @GetMapping
    public List<Photo> getAllPhotos() {
        return photoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Photo> getPhotoById(@PathVariable Long id) {
        return photoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Photo createPhoto(@RequestBody Photo photo) {
        return photoService.save(photo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Photo> updatePhoto(@PathVariable Long id, @RequestBody Photo photoDetails) {
        return photoService.update(id, photoDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long id) {
        return photoService.delete(id) ?
                ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
