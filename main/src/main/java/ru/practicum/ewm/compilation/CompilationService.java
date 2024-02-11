package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationDtoNew;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto create(CompilationDtoNew compilationDtoNew);

    void delete(int compId);

    CompilationDto update(UpdateCompilationRequest dto, Integer compId);

    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(int compId);
}
