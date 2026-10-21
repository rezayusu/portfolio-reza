package dev.portfolio.config;

import dev.portfolio.project.Project;
import dev.portfolio.project.ProjectRepository;
import dev.portfolio.toolbox.ToolboxItem;
import dev.portfolio.toolbox.ToolboxRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner seedProjects(ProjectRepository repository, ToolboxRepository toolboxRepository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Project(
                    "Zero Wash", "A simple motorbike wash booking app concept with a clean, mobile-first flow.",
                    "Flutter App", "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?auto=format&fit=crop&w=900&q=80",
                    "", "Flutter, Dart", true));
                repository.save(new Project(
                    "Eventku", "An event and organization management concept for registration and event discovery.",
                    "Web App", "https://images.unsplash.com/photo-1505373877841-8d25f7d46678?auto=format&fit=crop&w=900&q=80",
                    "", "HTML, CSS, JavaScript", true));
                repository.save(new Project(
                    "Product API", "A REST API practice project for creating, reading, updating, and deleting products.",
                    "Backend", "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?auto=format&fit=crop&w=900&q=80",
                    "", "Java, Spring Boot, PostgreSQL", false));
            }
            if (toolboxRepository.count() == 0) {
                toolboxRepository.save(new ToolboxItem("HTML", "5", "#ff8a76"));
                toolboxRepository.save(new ToolboxItem("CSS", "✳", "#c7b7f2"));
                toolboxRepository.save(new ToolboxItem("JavaScript", "JS", "#f4d66d"));
                toolboxRepository.save(new ToolboxItem("Java", "☕", "#a9c8a5"));
                toolboxRepository.save(new ToolboxItem("Spring Boot", "↗", "#ff8a76"));
                toolboxRepository.save(new ToolboxItem("PostgreSQL", "▤", "#c7b7f2"));
                toolboxRepository.save(new ToolboxItem("Flutter", "◈", "#f4d66d"));
                toolboxRepository.save(new ToolboxItem("Git", "⑂", "#a9c8a5"));
            }
        };
    }
}
