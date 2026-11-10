package dev.portfolio.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImageStorageService {
    private static final long MAX_IMAGE_SIZE = 50 * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 150 * 1024 * 1024;
    private static final long MAX_AUDIO_SIZE = 20 * 1024 * 1024;

    private final Path uploadDirectory;

    public ImageStorageService(@Value("${portfolio.upload-dir:uploads}") String uploadDirectory) {
        this.uploadDirectory = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }
    
    public String store(MultipartFile image) {
        return store(image, false);
    }

    public String storeImage(MultipartFile image) {
        return store(image, true);
    }

    private String store(MultipartFile image, boolean imageOnly) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        if (image.getSize() > MAX_VIDEO_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File maksimal 150 MB.");
        }
        if (imageOnly && image.getSize() > MAX_IMAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Foto maksimal 50 MB.");
        }

        Path storedFile = null;
        try (InputStream input = image.getInputStream()) {
            byte[] header = input.readNBytes(12);
            String extension = detectImageExtension(header);
            if (extension == null) {
                throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE, imageOnly
                        ? "Gunakan gambar PNG, JPEG, GIF, atau WebP."
                        : "Gunakan gambar (PNG, JPEG, GIF, WebP) atau video (MP4, WEBM, MOV).");
            }
            boolean video = isVideoExtension(extension);
            if (imageOnly && video) {
                throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Media utama project harus berupa gambar.");
            }
            if (!video && image.getSize() > MAX_IMAGE_SIZE) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Foto maksimal 50 MB.");
            }
            Files.createDirectories(uploadDirectory);
            String filename = UUID.randomUUID() + extension;
            storedFile = uploadDirectory.resolve(filename);
            try (OutputStream output = Files.newOutputStream(storedFile)) {
                output.write(header);
                input.transferTo(output);
            }
            return "/uploads/" + filename;
        } catch (IOException exception) {
            if (storedFile != null) {
                try {
                    Files.deleteIfExists(storedFile);
                } catch (IOException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            throw new UncheckedIOException("Tidak dapat menyimpan gambar yang diunggah.", exception);
        }
    }

    private boolean isVideoExtension(String extension) {
        return ".mp4".equals(extension) || ".webm".equals(extension) || ".mov".equals(extension);
    }

    public String storeAudio(MultipartFile audio) {
        if (audio == null || audio.isEmpty()) {
            return null;
        }
        if (audio.getSize() > MAX_AUDIO_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File audio maksimal 20 MB.");
        }

        try {
            byte[] content = audio.getBytes();
            String extension = detectAudioExtension(content);
            if (extension == null) {
                throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Gunakan file audio MP3, WAV, OGG, FLAC, atau M4A.");
            }
            Files.createDirectories(uploadDirectory);
            String filename = UUID.randomUUID() + extension;
            Files.write(uploadDirectory.resolve(filename), content);
            return "/uploads/" + filename;
        } catch (IOException exception) {
            throw new UncheckedIOException("Tidak dapat menyimpan audio yang diunggah.", exception);
        }
    }

    public Path getUploadDirectory() {
        return uploadDirectory;
    }

    private String detectImageExtension(byte[] bytes) {
        if (bytes.length >= 8
                && (bytes[0] & 0xff) == 0x89 && bytes[1] == 0x50
                && bytes[2] == 0x4e && bytes[3] == 0x47
                && bytes[4] == 0x0d && bytes[5] == 0x0a
                && bytes[6] == 0x1a && bytes[7] == 0x0a) {
            return ".png";
        }
        if (bytes.length >= 3
                && (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xd8
                && (bytes[2] & 0xff) == 0xff) {
            return ".jpg";
        }
        if (bytes.length >= 6
                && bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F'
                && bytes[3] == '8' && (bytes[4] == '7' || bytes[4] == '9')
                && bytes[5] == 'a') {
            return ".gif";
        }
        if (bytes.length >= 12
                && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return ".webp";        
        }
        // WebM / Matroska check (EBML header: 0x1A 0x45 0xDF 0xA3)
        if (bytes.length >= 4
                && (bytes[0] & 0xff) == 0x1a && (bytes[1] & 0xff) == 0x45
                && (bytes[2] & 0xff) == 0xdf && (bytes[3] & 0xff) == 0xa3) {
            return ".webm";
        }
        // QuickTime uses the same ftyp box as MP4 but has a "qt" brand.
        if (bytes.length >= 12
                && bytes[4] == 'f' && bytes[5] == 't' && bytes[6] == 'y' && bytes[7] == 'p'
                && bytes[8] == 'q' && bytes[9] == 't') {
            return ".mov";
        }
        // MP4 check (ftyp box starts at offset 4 with 'ftyp')
        if (bytes.length >= 12
                && bytes[4] == 'f' && bytes[5] == 't' && bytes[6] == 'y' && bytes[7] == 'p') {
            return ".mp4";
        }
        return null;
    }

    private String detectAudioExtension(byte[] bytes) {
        if (bytes.length >= 3 && bytes[0] == 'I' && bytes[1] == 'D' && bytes[2] == '3') {
            return ".mp3";
        }
        if (bytes.length >= 2 && (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xe0) == 0xe0) {
            return ".mp3";
        }
        if (bytes.length >= 12
                && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'A' && bytes[10] == 'V' && bytes[11] == 'E') {
            return ".wav";
        }
        if (bytes.length >= 4
                && bytes[0] == 'O' && bytes[1] == 'g' && bytes[2] == 'g' && bytes[3] == 'S') {
            return ".ogg";
        }
        if (bytes.length >= 4
                && bytes[0] == 'f' && bytes[1] == 'L' && bytes[2] == 'a' && bytes[3] == 'C') {
            return ".flac";
        }
        if (bytes.length >= 12
                && bytes[4] == 'f' && bytes[5] == 't' && bytes[6] == 'y' && bytes[7] == 'p'
                && ((bytes[8] == 'M' && bytes[9] == '4' && (bytes[10] == 'A' || bytes[10] == 'B'))
                    || (bytes[8] == 'M' && bytes[9] == '4' && bytes[10] == 'P'))) {
            return ".m4a";
        }
        return null;
    }
}
