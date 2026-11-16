package dev.portfolio.content;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UploadedImageController {
    private static final Pattern SAFE_FILENAME = Pattern.compile("[A-Za-z0-9._-]+");

    private final UploadedImageRepository repository;
    private final ImageStorageService imageStorage;

    public UploadedImageController(UploadedImageRepository repository, ImageStorageService imageStorage) {
        this.repository = repository;
        this.imageStorage = imageStorage;
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getUpload(@PathVariable String filename) {
        if (!SAFE_FILENAME.matcher(filename).matches()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        UploadedImage image = repository.findById(filename).orElse(null);
        if (image != null) {
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .cacheControl(CacheControl.noCache())
                .body(new ByteArrayResource(image.getData()));
        }

        Path file = imageStorage.getUploadDirectory().resolve(filename).normalize();
        if (file.startsWith(imageStorage.getUploadDirectory()) && Files.isRegularFile(file)) {
            return response(new FileSystemResource(file), filename);
        }

        Resource packagedFile = new ClassPathResource("uploads/" + filename);
        if (packagedFile.exists()) {
            return response(packagedFile, filename);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Resource> response(Resource resource, String filename) {
        MediaType contentType = MediaTypeFactory.getMediaType(filename)
            .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
            .contentType(contentType)
            .cacheControl(CacheControl.noCache())
            .body(resource);
    }
}
