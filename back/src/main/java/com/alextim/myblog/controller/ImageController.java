package com.alextim.myblog.controller;

import com.alextim.myblog.model.Image;
import com.alextim.myblog.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;


@RestController
@RequestMapping("/api/posts/{postId}/image")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final ImageService imageService;

    private final String DEFAULT_IMAGE = "default.jpg";
    private final String IMAGE_CONTENT_TYPE = "image/jpeg";

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updatePostImage(@PathVariable Long postId, @RequestParam MultipartFile image) {
        log.info("Updating image for post with ID: {}", postId);
        log.debug("Received image: name={}, size={} bytes", image.getOriginalFilename(), image.getSize());

        try {
            imageService.save(new Image(image.getOriginalFilename(), image.getBytes(), image.getSize(), postId));
        } catch (IOException e) {
            log.error("Failed to read image data for post ID: {}", postId, e);
            throw new RuntimeException("Failed to read image data", e);
        }

        log.info("Successfully updated image for post with ID: {}", postId);
    }

    @GetMapping
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long postId) {
        log.info("Getting image for post with ID: {}", postId);

        Optional<Image> imageOptional = imageService.findByPostId(postId);

        byte[] imageData;
        if (imageOptional.isEmpty()) {
            log.warn("Image not found for post with ID: {}, returning default image", postId);
            imageData = loadDefaultImage();
            if (imageData == null) {
                log.error("Default image default.jpg not found in resources");
                return ResponseEntity.notFound().build();
            }
        } else {
            imageData = imageOptional.get().getData();
            log.debug("Retrieved image data for post ID: {}, size: {} bytes", postId, imageData.length);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", IMAGE_CONTENT_TYPE);

        log.info("Successfully retrieved image for post with ID: {}", postId);
        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }

    private byte[] loadDefaultImage() {
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_IMAGE);
            try (InputStream inputStream = resource.getInputStream()) {
                return inputStream.readAllBytes();
            }
        } catch (IOException e) {
            log.error("Failed to load default image default.jpg", e);
            return null;
        }
    }
}
