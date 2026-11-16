package dev.portfolio.content;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedImageRepository extends JpaRepository<UploadedImage, String> {}
