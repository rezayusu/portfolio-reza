package dev.portfolio.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
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

    @Test
    void storesUploadedProjectVideosWithDetectedFormats() {
        ImageStorageService service = new ImageStorageService(temporaryDirectory.toString());
        byte[] mp4Bytes = new byte[] { 0, 0, 0, 0, 'f', 't', 'y', 'p', 'i', 's', 'o', 'm' };
        byte[] movBytes = new byte[] { 0, 0, 0, 0, 'f', 't', 'y', 'p', 'q', 't', ' ', ' ' };
        byte[] webmBytes = new byte[] { 0x1a, 0x45, (byte) 0xdf, (byte) 0xa3 };

        assertThat(service.store(new MockMultipartFile("photo", "preview.mp4", "video/mp4", mp4Bytes)))
            .matches("/uploads/[0-9a-f-]{36}\\.mp4");
        assertThat(service.store(new MockMultipartFile("photo", "preview.mov", "video/quicktime", movBytes)))
            .matches("/uploads/[0-9a-f-]{36}\\.mov");
        assertThat(service.store(new MockMultipartFile("photo", "preview.webm", "video/webm", webmBytes)))
            .matches("/uploads/[0-9a-f-]{36}\\.webm");
    }

    @Test
    void streamsVideosLargerThanFiftyMegabytesToDisk() throws Exception {
        ImageStorageService service = new ImageStorageService(temporaryDirectory.toString());
        long videoSize = 50L * 1024 * 1024 + 1;
        byte[] mp4Header = new byte[] { 0, 0, 0, 0, 'f', 't', 'y', 'p', 'i', 's', 'o', 'm' };
        MultipartFile video = mock(MultipartFile.class);
        when(video.isEmpty()).thenReturn(false);
        when(video.getSize()).thenReturn(videoSize);
        when(video.getInputStream()).thenReturn(new InputStream() {
            private long position;

            @Override
            public int read() {
                if (position >= videoSize) {
                    return -1;
                }
                return position < mp4Header.length ? mp4Header[(int) position++] & 0xff : advanceZero();
            }

            @Override
            public int read(byte[] buffer, int offset, int length) {
                if (position >= videoSize) {
                    return -1;
                }
                int count = (int) Math.min(length, videoSize - position);
                int headerLength = (int) Math.min(count, Math.max(0, mp4Header.length - position));
                if (headerLength > 0) {
                    System.arraycopy(mp4Header, (int) position, buffer, offset, headerLength);
                }
                Arrays.fill(buffer, offset + headerLength, offset + count, (byte) 0);
                position += count;
                return count;
            }

            private int advanceZero() {
                position++;
                return 0;
            }
        });

        String videoUrl = service.store(video);

        assertThat(videoUrl).matches("/uploads/[0-9a-f-]{36}\\.mp4");
        assertThat(Files.size(temporaryDirectory.resolve(Path.of(videoUrl).getFileName())))
            .isEqualTo(videoSize);
    }

    @Test
    void rejectsVideosLargerThanOneHundredFiftyMegabytesBeforeReadingContents() {
        ImageStorageService service = new ImageStorageService(temporaryDirectory.toString());
        MockMultipartFile oversized = mock(MockMultipartFile.class);
        when(oversized.isEmpty()).thenReturn(false);
        when(oversized.getSize()).thenReturn(150L * 1024 * 1024 + 1);

        assertThatThrownBy(() -> service.store(oversized))
            .isInstanceOf(ResponseStatusException.class)
            .extracting("statusCode")
            .isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @Test
    void rejectsVideoWhenStoredAsProjectPrimaryImage() {
        ImageStorageService service = new ImageStorageService(temporaryDirectory.toString());
        byte[] mp4Bytes = new byte[] { 0, 0, 0, 0, 'f', 't', 'y', 'p', 'i', 's', 'o', 'm' };

        assertThatThrownBy(() -> service.storeImage(
            new MockMultipartFile("photo", "preview.mp4", "video/mp4", mp4Bytes)))
            .isInstanceOf(ResponseStatusException.class)
            .extracting("statusCode")
            .isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void storesMp3WithGeneratedNameInUploadDirectory() throws Exception {
        byte[] mp3Bytes = new byte[] { 'I', 'D', '3', 4, 0, 0 };
        ImageStorageService service = new ImageStorageService(temporaryDirectory.toString());

        String audioUrl = service.storeAudio(new MockMultipartFile(
            "hoverAudio", "song.mp3", "audio/mpeg", mp3Bytes));

        assertThat(audioUrl).matches("/uploads/[0-9a-f-]{36}\\.mp3");
        assertThat(Files.readAllBytes(temporaryDirectory.resolve(Path.of(audioUrl).getFileName())))
            .containsExactly(mp3Bytes);
    }

    @Test
    void rejectsUnsupportedAudioContentEvenWhenFilenameLooksLikeAudio() {
        ImageStorageService service = new ImageStorageService(temporaryDirectory.toString());
        MockMultipartFile invalidAudio = new MockMultipartFile(
            "hoverAudio", "song.mp3", "audio/mpeg", "not audio".getBytes());

        assertThatThrownBy(() -> service.storeAudio(invalidAudio))
            .isInstanceOf(ResponseStatusException.class)
            .extracting("statusCode")
            .isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
