package ru.practicum.ewm.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompilationRepository extends JpaRepository<CompilationModel, Integer> {

    List<CompilationModel> findByPinned(Boolean pinned, Pageable pageable);
}
