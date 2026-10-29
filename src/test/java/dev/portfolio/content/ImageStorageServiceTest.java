package dev.portfolio.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

class ImageStorageServiceTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void storesPngWithGeneratedNameInUploadDirectory() throws Exception {
        ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", imageBytes);
        ImageStorageService service = new ImageStorageService(temporaryDirectory.toString());

        String imageUrl = service.store(new MockMultipartFile(
            "photo", "my-photo.png", "image/png", imageBytes.toByteArray()));

        assertThat(imageUrl).matches("/uploads/[0-9a-f-]{36}\\.png");
        assertThat(Files.readAllBytes(temporaryDirectory.resolve(Path.of(imageUrl).getFileName())))
            .containsExactly(imageBytes.toByteArray());
    }

    @Test
    void rejectsUnsupportedContentEvenWhenFilenameLooksLikeAnImage() {
        ImageStorageService service = new ImageStorageService(temporaryDirectory.toString());
        MockMultipartFile svg = new MockMultipartFile(
            "photo", "picture.png", "image/png", "<svg/>".getBytes());

        assertThatThrownBy(() -> service.store(svg))
            .isInstanceOf(ResponseStatusException.class)
            .extracting("statusCode")
            .isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void rejectsImagesLargerThanFiveMegabytes() {
        ImageStorageService service = new ImageStorageService(temporaryDirectory.toString());
        // For a valid magic bytes check so it triggers size check first
        byte[] pngHeader = new byte[] { (byte)0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };
        byte[] largeBytes = new byte[50 * 1024 * 1024 + 1];
        System.arraycopy(pngHeader, 0, largeBytes, 0, pngHeader.length);
        MockMultipartFile oversized = new MockMultipartFile(
            "photo", "large.png", "image/png", largeBytes);

        assertThatThrownBy(() -> service.store(oversized))
            .isInstanceOf(ResponseStatusException.class)
            .extracting("statusCode")
            .isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
    }
}
