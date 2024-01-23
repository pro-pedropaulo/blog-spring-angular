package com.example.blog.service;

import com.example.blog.model.Album;
import com.example.blog.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    public List<Album> findAll() {
        return albumRepository.findAll();
    }

    public Optional<Album> findById(Long id) {
        return albumRepository.findById(id);
    }

    public Album save(Album album) {
        return albumRepository.save(album);
    }

    public Optional<Album> update(Long id, Album albumDetails) {
        return albumRepository.findById(id)
                .map(existingAlbum -> {
                    existingAlbum.setName(albumDetails.getName());
                    return albumRepository.save(existingAlbum);
                });
    }

    public boolean delete(Long id) {
        return albumRepository.findById(id)
                .map(album -> {
                    albumRepository.delete(album);
                    return true;
                }).orElse(false);
    }
}
