package dev.portfolio.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class UploadedImageControllerTest {
    @Test
    void servesUploadedImageBytesFromDatabase() throws IOException {
        UploadedImageRepository repository = mock(UploadedImageRepository.class);
        ImageStorageService imageStorage = mock(ImageStorageService.class);
        byte[] imageBytes = new byte[] { 1, 2, 3 };
        when(repository.findById("photo.png"))
            .thenReturn(Optional.of(new UploadedImage("photo.png", "image/png", imageBytes)));

        var response = new UploadedImageController(repository, imageStorage).getUpload("photo.png");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
        Resource body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getInputStream().readAllBytes()).containsExactly(imageBytes);
    }
}
