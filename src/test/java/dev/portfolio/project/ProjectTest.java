package dev.portfolio.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProjectTest {
    @Test
    void identifiesVideoMediaUrls() {
        Project project = new Project();

        project.setImageUrl("/uploads/preview.mp4");
        assertThat(project.isVideo()).isTrue();

        project.setImageUrl("https://example.com/preview.WEBM?token=sample");
        assertThat(project.isVideo()).isTrue();

        project.setImageUrl("/uploads/preview.jpg");
        assertThat(project.isVideo()).isFalse();
    }
}
