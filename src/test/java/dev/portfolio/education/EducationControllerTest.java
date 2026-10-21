package dev.portfolio.education;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EducationControllerTest {
    @Mock
    private EducationRepository repository;

    @Test
    void listsEducationInChronologicalRepositoryOrder() {
        EducationEntry elementary = new EducationEntry();
        elementary.setLevel("SD");
        elementary.setSchoolName("Elementary School");
        elementary.setStartYear(2012);
        EducationEntry vocational = new EducationEntry();
        vocational.setLevel("SMK");
        vocational.setSchoolName("Vocational School");
        vocational.setStartYear(2021);
        List<EducationEntry> entries = List.of(elementary, vocational);
        when(repository.findAllByOrderByStartYearAscIdAsc()).thenReturn(entries);

        List<EducationEntry> result = new EducationController(repository).listEducation();

        assertThat(result).containsExactly(elementary, vocational);
        verify(repository).findAllByOrderByStartYearAscIdAsc();
    }
}
