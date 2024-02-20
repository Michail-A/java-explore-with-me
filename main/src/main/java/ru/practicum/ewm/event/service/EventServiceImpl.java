package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.param.AdminRequestParam;
import ru.practicum.ewm.event.param.PublicRequestParam;
import ru.practicum.ewm.exception.AlreadyAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.dto.RequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.RequestStatusUpdateResult;
import ru.practicum.ewm.stat.client.StatsClient;
import ru.practicum.ewm.stat.dto.HitCreateDto;
import ru.practicum.ewm.stat.dto.HitGetDto;
import ru.practicum.ewm.users.User;
import ru.practicum.ewm.users.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private final RequestRepository requestRepository;

    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto create(EventCreateDto eventCreateDto, int userId) {
        Category category = categoryRepository.findById(eventCreateDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория id=" + eventCreateDto.getCategory() + " не найдена"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));
        if (eventCreateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата события не может быть раньше," +
                    " чем через 2 часа от текущей даты");
        }
        Event event = EventMapper.toModel(eventCreateDto, category, user);

        return EventMapper.tooEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByUser(int userId, Pageable page) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));
        List<Event> events = eventRepository.findByInitiatorIdOrderByIdDesc(userId, page);

        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByIdByUser(int userId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найден"));

        if (event.getInitiator().getId() == userId) {
            return EventMapper.tooEventFullDto(event);
        }
        throw new NotFoundException("Событие с id=" + eventId + " не найден");
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(EventUpdateDto eventUpdateDto, int userId, int eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найден"));

        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("Событие с id=" + eventId + " не найден");
        }

        if (event.getState().equals(Status.PUBLISHED)) {
            throw new AlreadyAvailableException("Нельзя изменить опубликованое событие");
        }

        if (eventUpdateDto.getTitle() != null && !eventUpdateDto.getTitle().isBlank()) {
            event.setTitle(eventUpdateDto.getTitle());
        }
        if (eventUpdateDto.getAnnotation() != null && !eventUpdateDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(eventUpdateDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория id=" +
                            eventUpdateDto.getCategory() + " не найдена")));
        }
        if (eventUpdateDto.getDescription() != null && !eventUpdateDto.getDescription().isBlank()) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getLocation() != null
                && eventUpdateDto.getLocation().getLat() != null
                && eventUpdateDto.getLocation().getLon() != null) {

            event.setLocation(eventUpdateDto.getLocation());
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getEventDate() != null) {
            if (eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))
                    || event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Дата события не может быть раньше," +
                        " чем через 2 часа от текущей даты");
            }
            event.setEventDate(eventUpdateDto.getEventDate());
        }


        if (eventUpdateDto.getStateAction() != null
                && eventUpdateDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(Status.CANCELED);
        }
        if (eventUpdateDto.getStateAction() != null
                && eventUpdateDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(Status.PENDING);
        }

        return EventMapper.tooEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequests(int userId, int eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найден"));

        List<Request> requests = requestRepository.findByEventIdAndEventInitiatorId(eventId, userId);
        List<RequestDto> requestDtos = new ArrayList<>();
        requestDtos.addAll(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
        return requestDtos;
    }

    @Override
    @Transactional
    public RequestStatusUpdateResult updateRequestStatus(RequestStatusUpdateRequest requestStatusUpdateRequest,
                                                         int eventId, int userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найден"));

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new AlreadyAvailableException("Модерация заявок не требуется");
        }
        if (event.getParticipantLimit() - event.getConfirmedRequests() <= 0) {
            throw new AlreadyAvailableException("Достигнут лимит заявок на событие");
        }

        List<Request> requestsByEvent = requestRepository.findByEventId(eventId);
        List<Request> requestsByIds = new ArrayList<>();

        for (Request request : requestsByEvent) {
            if (requestStatusUpdateRequest.getRequestIds().contains(request.getId())) {
                requestsByIds.add(request);
            }
        }

        if (requestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            for (Request request : requestsByIds) {

                if (event.getParticipantLimit() - event.getConfirmedRequests() <= 0) {
                    request.setStatus(RequestStatus.REJECTED);
                }

                request.setStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);

                requestRepository.save(request);
            }
        }

        if (requestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            for (Request request : requestsByIds) {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
            }
        }

        eventRepository.save(event);

        return RequestMapper.toRequestStatusUpdateResult(requestsByIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAll(AdminRequestParam param) {
        List<Event> events = eventRepository.findByAdmin(param.getUsers(), param.getStates(), param.getCategories(),
                param.getStart(), param.getEnd(), param.getPage());

        return events.stream().map(EventMapper::tooEventFullDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(EventUpdateDto eventUpdateDto, int eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id=" + eventId + " не найден"));

        if (eventUpdateDto.getStateAction() != null
                && eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)
                && !event.getState().equals(Status.PENDING)) {
            throw new AlreadyAvailableException("Событие с id=" + eventId + " не находится на рассмотрении");
        }

        if (eventUpdateDto.getStateAction() != null
                && eventUpdateDto.getStateAction().equals(StateAction.REJECT_EVENT)
                && event.getState().equals(Status.PUBLISHED)) {
            throw new AlreadyAvailableException("Событие с id=" + eventId + " уже опубликовано");
        }

        if (eventUpdateDto.getTitle() != null && !eventUpdateDto.getTitle().isBlank()) {
            event.setTitle(eventUpdateDto.getTitle());
        }
        if (eventUpdateDto.getAnnotation() != null && !eventUpdateDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(eventUpdateDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория id=" +
                            eventUpdateDto.getCategory() + " не найдена")));
        }
        if (eventUpdateDto.getDescription() != null && !eventUpdateDto.getDescription().isBlank()) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getLocation() != null
                && eventUpdateDto.getLocation().getLat() != null
                && eventUpdateDto.getLocation().getLon() != null) {
            event.setLocation(eventUpdateDto.getLocation());
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getEventDate() != null) {
            event.setEventDate(eventUpdateDto.getEventDate());
        }

        if (eventUpdateDto.getStateAction() != null
                && eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            event.setState(Status.PUBLISHED);
        }

        if (eventUpdateDto.getStateAction() != null
                && eventUpdateDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
            event.setState(Status.CANCELED);
        }

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Некорректная дата");
        }

        if (event.getPublishedOn() != null && event.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new AlreadyAvailableException("Дата начала измененного события должна быть " +
                    " не ранее, чем через час после даты публикации");
        }

        return EventMapper.tooEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAll(PublicRequestParam param) {
        if (param.getStart() == null) {
            param.setStart(LocalDateTime.now());
        }
        if (param.getEnd() != null && param.getEnd().isBefore(param.getStart())) {
            throw new ValidationException("Дата конца не может быть раньше даты старта");
        }
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        List<Event> events = eventRepository.findAllPublic(param.getText(), param.getCategories(), param.getPaid(),
                param.getStart(), param.getEnd(), param.getOnlyAvailable(), param.getPage());

        if (events.size() > 0 && events != null) {
            eventShortDtos = events.stream()
                    .map(EventMapper::toEventShortDto).collect(Collectors.toList());
        }
        if (!eventShortDtos.isEmpty()) {
            List<Integer> eventIds = events.stream()
                    .map(Event::getId)
                    .collect(Collectors.toList());

            Map<Integer, Long> views = getViews(eventIds);

            eventShortDtos.forEach(dto -> dto.setViews(views.get(dto.getId())));

        }
        return eventShortDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByIdForPublic(int eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id=" + eventId + " не найден"));

        if (event.getState() != Status.PUBLISHED) {
            throw new NotFoundException("Событие id=" + eventId + " еще не опубликовано");
        }


        EventFullDto eventFullDto = EventMapper.tooEventFullDto(event);

        eventFullDto.setViews((long) statsClient.get(event.getCreatedOn(),
                LocalDateTime.now(),
                List.of("/events/" + eventId),
                true).size());

        return eventFullDto;
    }

    private Map<Integer, Long> getViews(List<Integer> eventsId) {
        List<String> uris = eventsId
                .stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());


        Optional<LocalDateTime> start = eventRepository.getCreatedOn(eventsId);

        Map<Integer, Long> views = new HashMap<>();

        if (start.isPresent()) {
            List<HitGetDto> response = statsClient.get(start.get(), LocalDateTime.now(), uris, true);

            response.forEach(dto -> {
                String uri = dto.getUri();
                String[] split = uri.split("/");
                String id = split[2];
                Integer eventId = Integer.parseInt(id);
                views.put(eventId, dto.getHits());
            });
        } else {
            eventsId.forEach(el -> views.put(el, 0L));
        }

        return views;
    }
}
