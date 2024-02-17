package ru.practicum.ewm.compilation.dto;

import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.dto.EventMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toModel(CompilationCreateDto compilationCreateDto,
                                      Set<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setTitle(compilationCreateDto.getTitle());
        compilation.setEvents(events);
        if (compilationCreateDto.getPinned() == null) {
            compilation.setPinned(false);
        } else {
            compilation.setPinned(compilationCreateDto.getPinned());
        }
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setEvents(compilation.getEvents()
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList()));
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        return compilationDto;
    }
}
