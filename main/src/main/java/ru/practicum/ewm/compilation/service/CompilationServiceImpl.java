package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationCreateDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestStatus;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public CompilationDto create(CompilationCreateDto compilationCreateDto) {
        Set<Event> events = new HashSet<>();
        List<Integer> eventsIds = new ArrayList<>();

        if (compilationCreateDto.getEvents() != null) {
            events = eventRepository.findAllByIdIn(compilationCreateDto.getEvents());
            eventsIds = events.stream()
                    .map(event -> event.getId())
                    .collect(Collectors.toList());
        }
        List<Request> requests = requestRepository.findByEventIdInAndStatus(eventsIds, RequestStatus.CONFIRMED);
        Map<Integer, List<Request>> eventRequests = requests.stream().collect(groupingBy(r -> r.getEvent().getId()));

        Compilation compilation = CompilationMapper.toModel(compilationCreateDto, events);
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), eventRequests);
    }

    @Override
    @Transactional
    public void delete(int compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(UpdateCompilationRequest dto, Integer compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка с id= " + compId + " не найдено"));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }

        List<Integer> eventsIds = new ArrayList<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Set<Integer> eventsId = dto.getEvents();
            Set<Event> events = eventRepository.findAllByIdIn(eventsId);
            compilation.setEvents(events);
            eventsIds = events.stream()
                    .map(event -> event.getId())
                    .collect(Collectors.toList());
        }
        List<Request> requests = requestRepository.findByEventIdInAndStatus(eventsIds, RequestStatus.CONFIRMED);
        Map<Integer, List<Request>> eventRequests = requests.stream().collect(groupingBy(r -> r.getEvent().getId()));

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), eventRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, Pageable page) {
        if (pinned == null) {
            pinned = false;
        }
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, page);

        List<Event> events = new ArrayList<>();
        for (Compilation compilation : compilations) {
            events.addAll(compilation.getEvents());
        }

        List<Integer> eventsIds = events.stream().map(event -> event.getId()).collect(Collectors.toList());
        List<Request> requests = requestRepository.findByEventIdInAndStatus(eventsIds, RequestStatus.CONFIRMED);
        Map<Integer, List<Request>> eventRequests = requests.stream().collect(groupingBy(r -> r.getEvent().getId()));

        return compilations.stream()
                .map(compilation -> CompilationMapper.toCompilationDto(compilation, eventRequests))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(int compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Событие с id= " + compId + " не найдено"));
        List<Integer> eventsIds = compilation.getEvents().stream()
                .map(event -> event.getId())
                .collect(Collectors.toList());
        List<Request> requests = requestRepository.findByEventIdInAndStatus(eventsIds, RequestStatus.CONFIRMED);
        Map<Integer, List<Request>> eventRequests = requests.stream().collect(groupingBy(r -> r.getEvent().getId()));

        return CompilationMapper.toCompilationDto(compilation, eventRequests);
    }
}
