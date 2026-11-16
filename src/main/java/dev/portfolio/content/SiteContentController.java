package dev.portfolio.content;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class SiteContentController {
    private final SiteContentService service;

    public SiteContentController(SiteContentService service) {
        this.service = service;
    }

    @GetMapping("/site-content")
    public SiteContent getContent() {
        return service.getContent();
    }

    @GetMapping("/site-content/photos/{slot}")
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable String slot) {
        if (!ProfilePhoto.PRIMARY_SLOT.equals(slot) && !ProfilePhoto.SECONDARY_SLOT.equals(slot)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        ProfilePhoto photo = service.getProfilePhoto(slot);
        if (photo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(photo.getContentType()))
            .cacheControl(CacheControl.noCache())
            .body(photo.getData());
    }
}
