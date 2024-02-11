package ru.practicum.ewm.compilation.dto;

import ru.practicum.ewm.compilation.CompilationModel;
import ru.practicum.ewm.event.EventModel;
import ru.practicum.ewm.event.dto.EventMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationModel mapToNewCompilationModel(CompilationDtoNew compilationDtoNew,
                                                            Set<EventModel> events) {
        CompilationModel compilationModel = new CompilationModel();
        compilationModel.setTitle(compilationDtoNew.getTitle());
        compilationModel.setEvents(events);
        if (compilationDtoNew.getPinned() == null) {
            compilationModel.setPinned(false);
        } else {
            compilationModel.setPinned(compilationDtoNew.getPinned());
        }
        return compilationModel;
    }

    public static CompilationDto mapToCompilationDto(CompilationModel compilationModel) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilationModel.getId());
        compilationDto.setEvents(compilationModel.getEvents()
                .stream()
                .map(EventMapper::mapToEventShortDto)
                .collect(Collectors.toList()));
        compilationDto.setPinned(compilationModel.getPinned());
        compilationDto.setTitle(compilationModel.getTitle());
        return compilationDto;
    }
}
