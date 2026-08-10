package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.Image;
import com.hotel.repository.ImageRepository;

@Service
@Transactional
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    //Create
    public Image insert(Image image) {
        return imageRepository.save(image);
    }

    //Read All
    @Transactional(readOnly = true)
    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    //Read by ID
    @Transactional(readOnly = true)
    public Optional<Image> findById(Integer id) {
        return imageRepository.findById(id);
    }

    //Update
    public Image update(Integer id, Image updatedImage) {
        return imageRepository.findById(id)
                .map(image -> {
                    if (updatedImage.getPath() != null) {
                        image.setPath(updatedImage.getPath());
                    }
                    if (updatedImage.getImageDesc() != null) {
                        image.setImageDesc(updatedImage.getImageDesc());
                    }
                    return imageRepository.save(image);
                })
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + id));
    }

    //Delete
    public boolean deleteById(Integer id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
