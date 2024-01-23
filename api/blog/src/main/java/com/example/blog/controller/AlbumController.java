package com.example.blog.controller;

import com.example.blog.model.Album;
import com.example.blog.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @GetMapping
    public List<Album> getAllAlbums() {
        return albumService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable Long id) {
        return albumService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Album createAlbum(@RequestBody Album album) {
        return albumService.save(album);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable Long id, @RequestBody Album albumDetails) {
        return albumService.update(id, albumDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlbum(@PathVariable Long id) {
        return albumService.delete(id) ?
                ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
