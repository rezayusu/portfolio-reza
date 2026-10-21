package dev.portfolio.certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CertificateControllerTest {
    @Mock
    private CertificateRepository repository;

    @Test
    void listsCertificatesInNewestFirstRepositoryOrder() {
        Certificate latest = new Certificate();
        latest.setTitle("Latest");
        Certificate previous = new Certificate();
        previous.setTitle("Previous");
        List<Certificate> certificates = List.of(latest, previous);
        when(repository.findAllByOrderByIssuedYearDescIdDesc()).thenReturn(certificates);

        List<Certificate> result = new CertificateController(repository).listPublicCertificates();

        assertThat(result).containsExactly(latest, previous);
        verify(repository).findAllByOrderByIssuedYearDescIdDesc();
    }
}
