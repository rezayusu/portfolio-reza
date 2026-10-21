package dev.portfolio.certificate;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class CertificateController {
    private final CertificateRepository repository;

    public CertificateController(CertificateRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/certificates")
    public List<Certificate> listPublicCertificates() {
        return repository.findAllByOrderByIssuedYearDescIdDesc();
    }

    @PostMapping("/admin/certificates")
    @ResponseStatus(HttpStatus.CREATED)
    public Certificate create(@Valid @RequestBody Certificate certificate) {
        return repository.save(certificate);
    }

    @PutMapping("/admin/certificates/{id}")
    public Certificate update(@PathVariable Long id, @Valid @RequestBody Certificate input) {
        Certificate existing = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        copyFields(input, existing);
        return repository.save(existing);
    }

    @DeleteMapping("/admin/certificates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    private void copyFields(Certificate input, Certificate target) {
        target.setTitle(input.getTitle());
        target.setIssuer(input.getIssuer());
        target.setIssuedYear(input.getIssuedYear());
        target.setDescription(input.getDescription());
        target.setCredentialUrl(input.getCredentialUrl());
        target.setImageUrl(input.getImageUrl());
    }
}
