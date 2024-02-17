package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationCreateDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto create(CompilationCreateDto compilationCreateDto);

    void delete(int compId);

    CompilationDto update(UpdateCompilationRequest dto, Integer compId);

    List<CompilationDto> getAll(Boolean pinned, Pageable page);

    CompilationDto getById(int compId);
}
