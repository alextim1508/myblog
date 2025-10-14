package com.alextim.myblog.service;

import com.alextim.myblog.exception.ClientException;
import com.alextim.myblog.model.Image;
import com.alextim.myblog.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository repository;

    @Override
    public Image save(Image image) {
        log.info("Saving image for postId: {}", image.getPostId());
        log.debug("Image data: {}", image);

        if (image.getId() == null) {
            log.info("Image ID is null, creating new image with filename: {}", image.getFileName());
            Image savedImage = repository.save(image);
            log.info("Successfully created image with ID: {}", savedImage.getId());
            return savedImage;
        } else {
            log.info("Image ID is not null, updating existing image with ID: {}", image.getId());
            int updatedRowCount = repository.update(image);

            if (updatedRowCount != 1) {
                log.error("Failed to update image with ID: {}. Updated row count: {}", image.getId(), updatedRowCount);
                throw new ClientException("Update image exception. updatedRowCount: " + updatedRowCount);
            }

            log.info("Successfully updated image with ID: {}", image.getId());
            return repository.findById(image.getId())
                    .orElseThrow(() -> new ClientException("Image with ID " + image.getId() + " not found after update"));
        }
    }

    @Override
    public Optional<Image> findByPostId(long postId) {
        log.info("Finding image for post ID: {}", postId);
        Optional<Image> image = repository.findByPostId(postId);

        if (image.isPresent()) {
            log.info("Found image with ID: {}", image.get().getId());
        } else {
            log.info("No image found for post ID: {}", postId);
        }

        return image;
    }
}