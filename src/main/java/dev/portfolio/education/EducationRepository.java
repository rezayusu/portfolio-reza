package dev.portfolio.education;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<EducationEntry, Long> {
    List<EducationEntry> findAllByOrderByStartYearAscIdAsc();
}
