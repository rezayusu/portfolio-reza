package dev.portfolio.content;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PackagedImageImporter implements ApplicationRunner {
    private final ApplicationContext applicationContext;
    private final UploadedImageRepository repository;

    public PackagedImageImporter(
            ApplicationContext applicationContext,
            UploadedImageRepository repository) {
        this.applicationContext = applicationContext;
        this.repository = repository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        for (Resource resource : applicationContext.getResources("classpath*:/uploads/*")) {
            String filename = resource.getFilename();
            if (filename == null || repository.existsById(filename)) {
                continue;
            }
            String contentType = ImageStorageService.imageContentTypeForFilename(filename);
            if (contentType == null) {
                continue;
            }
            try (InputStream input = resource.getInputStream()) {
                repository.save(new UploadedImage(filename, contentType, input.readAllBytes()));
            }
        }
    }
}
