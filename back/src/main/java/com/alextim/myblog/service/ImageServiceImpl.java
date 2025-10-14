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

        Optional<Image> existingImageOpt = repository.findByPostId(image.getPostId());

        if (existingImageOpt.isEmpty()) {
            log.info("No existing image found for postId: {}, creating new image", image.getPostId());

            Image savedImage = repository.save(image);
            log.info("Successfully created image with ID: {}", savedImage.getId());
            return savedImage;
        } else {
            Image existingImage = existingImageOpt.get();
            log.info("Existing image found with ID: {} for postId: {}, updating image", existingImage.getId(), image.getPostId());

            existingImage.setData(image.getData());
            existingImage.setFileName(image.getFileName());
            existingImage.setSize(image.getSize());

            int updatedRowCount = repository.update(existingImage);

            if (updatedRowCount != 1) {
                log.error("Failed to update image with ID: {}. Updated row count: {}", existingImage.getId(), updatedRowCount);
                throw new ClientException("Update image exception. updatedRowCount: " + updatedRowCount);
            }

            log.info("Successfully updated image with ID: {}", existingImage.getId());
            return existingImage;
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

    @Override
    public void deleteByPostId(long postId) {
        log.info("Deleting comments for post ID: {}", postId);
        repository.deleteByPostId(postId);
        log.info("Successfully deleted comments for post ID: {}", postId);
    }
}