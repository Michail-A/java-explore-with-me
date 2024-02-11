package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationDtoNew;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.event.EventModel;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto create(CompilationDtoNew compilationDtoNew) {
        Set<EventModel> events = new HashSet<>();

        if (compilationDtoNew.getEvents() != null) {
            events = eventRepository.findAllByIdIn(compilationDtoNew.getEvents());
        }
        CompilationModel compilationModel = CompilationMapper.mapToNewCompilationModel(compilationDtoNew, events);

        return CompilationMapper.mapToCompilationDto(compilationRepository.save(compilationModel));
    }

    @Override
    public void delete(int compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(UpdateCompilationRequest dto, Integer compId) {
        CompilationModel compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка с id= " + compId + " не найдено"));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Set<Integer> eventsId = dto.getEvents();
            Set<EventModel> events = eventRepository.findAllByIdIn(eventsId);
            compilation.setEvents(events);
        }

        return CompilationMapper.mapToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        if (pinned == null) {
            pinned = false;
        }
        List<CompilationModel> compilationModels = compilationRepository.findByPinned(pinned,
                PageRequest.of(from / size, size));
        return compilationModels.stream().map(CompilationMapper::mapToCompilationDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(int compId) {
        CompilationModel compilationModel = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Событие с id= " + compId + " не найдено"));
        return CompilationMapper.mapToCompilationDto(compilationModel);
    }
}
