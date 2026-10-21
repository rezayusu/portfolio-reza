package dev.portfolio.config;

import dev.portfolio.content.ImageStorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadResourceConfig implements WebMvcConfigurer {
    private final ImageStorageService imageStorage;

    public UploadResourceConfig(ImageStorageService imageStorage) {
        this.imageStorage = imageStorage;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = imageStorage.getUploadDirectory().toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(location);
    }
}
