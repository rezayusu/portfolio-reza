package dev.portfolio.toolbox;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolboxRepository extends JpaRepository<ToolboxItem, Long> {
    List<ToolboxItem> findAllByOrderByIdAsc();
}
