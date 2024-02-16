package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.CategoryModel;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.AlreadyAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.RequestModel;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.dto.RequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.RequestStatusUpdateResult;
import ru.practicum.ewm.stat.client.Client;
import ru.practicum.ewm.stat.dto.HitDtoCreate;
import ru.practicum.ewm.stat.dto.HitDtoGet;
import ru.practicum.ewm.users.UserModel;
import ru.practicum.ewm.users.UserRepository;

import javax.servlet.http.HttpServletRequest;
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

    private final Client client;

    @Override
    public EventFullDto add(EventDtoNew eventDtoNew, int userId) {
        CategoryModel categoryModel = categoryRepository.findById(eventDtoNew.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория id=" + eventDtoNew.getCategory() + " не найдена"));

        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));

        EventModel eventModel = EventMapper.mapToNewEventModel(eventDtoNew, categoryModel, userModel);

        return EventMapper.mapToEventFullDto(eventRepository.save(eventModel));
    }

    @Override
    public List<EventShortDto> getAllByUser(int userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));
        List<EventModel> events = eventRepository.findByInitiatorIdOrderByIdDesc(userId,
                PageRequest.of(from / size, size));

        return events.stream()
                .map(EventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getByIdByUser(int userId, int eventId) {
        EventModel eventModel = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найден"));

        if (eventModel.getInitiator().getId() == userId) {
            return EventMapper.mapToEventFullDto(eventModel);
        }
        throw new NotFoundException("Событие с id=" + eventId + " не найден");
    }

    @Override
    public EventFullDto updateEvent(EventUpdateDto eventUpdateDto, int userId, int eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));

        EventModel eventModel = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найден"));

        if (eventModel.getInitiator().getId() != userId) {
            throw new NotFoundException("Событие с id=" + eventId + " не найден");
        }

        if (eventModel.getState().equals(Status.PUBLISHED)) {
            throw new AlreadyAvailableException("Нельзя изменить опубликованое событие");
        }

        if (eventUpdateDto.getTitle() != null && !eventUpdateDto.getTitle().isBlank()) {
            eventModel.setTitle(eventUpdateDto.getTitle());
        }
        if (eventUpdateDto.getAnnotation() != null && !eventUpdateDto.getAnnotation().isBlank()) {
            eventModel.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getCategory() != null) {
            eventModel.setCategory(categoryRepository.findById(eventUpdateDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория id=" +
                            eventUpdateDto.getCategory() + " не найдена")));
        }
        if (eventUpdateDto.getDescription() != null && !eventUpdateDto.getDescription().isBlank()) {
            eventModel.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getLocation() != null
                && eventUpdateDto.getLocation().getLat() != null
                && eventUpdateDto.getLocation().getLon() != null) {

            eventModel.setLocation(eventUpdateDto.getLocation());
        }
        if (eventUpdateDto.getPaid() != null) {
            eventModel.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getRequestModeration() != null) {
            eventModel.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            eventModel.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getEventDate() != null) {
            eventModel.setEventDate(eventUpdateDto.getEventDate());
        }


        if (eventUpdateDto.getStateAction() != null
                && eventUpdateDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            eventModel.setState(Status.CANCELED);
        }
        if (eventUpdateDto.getStateAction() != null
                && eventUpdateDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            eventModel.setState(Status.PENDING);
        }

        return EventMapper.mapToEventFullDto(eventRepository.save(eventModel));
    }

    @Override
    public List<RequestDto> getRequests(int userId, int eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найден"));

        List<RequestModel> requests = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        List<RequestDto> requestDtos = new ArrayList<>();
        requestDtos.addAll(requests.stream().map(RequestMapper::mapToRequestDto).collect(Collectors.toList()));
        return requestDtos;
    }

    @Override
    public RequestStatusUpdateResult updateRequestStatus(RequestStatusUpdateRequest requestStatusUpdateRequest,
                                                         int eventId, int userId) {
        EventModel eventModel = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найден"));

        if (eventModel.getParticipantLimit() == 0 || !eventModel.getRequestModeration()) {
            throw new AlreadyAvailableException("Модерация заявок не требуется");
        }
        if (eventModel.getParticipantLimit() - eventModel.getConfirmedRequests() <= 0) {
            throw new AlreadyAvailableException("Достигнут лимит заявок на событие");
        }

        List<RequestModel> requestsByEvent = requestRepository.findByEventId(eventId);
        List<RequestModel> requestsByIds = new ArrayList<>();

        for (RequestModel requestModel : requestsByEvent) {
            if (requestStatusUpdateRequest.getRequestIds().contains(requestModel.getId())) {
                requestsByIds.add(requestModel);
            }
        }

        if (requestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            for (RequestModel requestModel : requestsByIds) {

                if (eventModel.getParticipantLimit() - eventModel.getConfirmedRequests() <= 0) {
                    requestModel.setStatus(RequestStatus.REJECTED);
                }

                requestModel.setStatus(RequestStatus.CONFIRMED);
                eventModel.setConfirmedRequests(eventModel.getConfirmedRequests() + 1);

                requestRepository.save(requestModel);
            }
        }

        if (requestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            for (RequestModel requestModel : requestsByIds) {
                requestModel.setStatus(RequestStatus.REJECTED);
                requestRepository.save(requestModel);
            }
        }

        eventRepository.save(eventModel);

        return RequestMapper.mapToRequestStatusUpdateResult(requestsByIds);
    }

    @Override
    public List<EventFullDto> getAllForAdmin(LocalDateTime start, LocalDateTime end,
                                             Collection<Integer> users, Collection<Integer> categories,
                                             Collection<Status> states, int from, int size) {
        List<EventModel> events = eventRepository.findByAdmin(users, states, categories,
                start, end, PageRequest.of(from / size, size));

        return events.stream().map(EventMapper::mapToEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateByAdmin(EventUpdateDto eventUpdateDto, int eventId) {
        EventModel event = eventRepository.findById(eventId).orElseThrow(() ->
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

        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getAllForPublic(String text, List<Integer> categoriesIds,
                                               Boolean paid, LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd, boolean onlyAvailable,
                                               EventSort sort, int from, int size,
                                               HttpServletRequest httpServletRequest) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        List<EventModel> events = eventRepository.findAllPublic(text, categoriesIds, paid,
                rangeStart, rangeEnd, onlyAvailable, PageRequest.of(from / size, size));

        sendHit(httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());

        if(events.size()>0 && events !=null) {
            eventShortDtos = events.stream()
                    .map(EventMapper::mapToEventShortDto).collect(Collectors.toList());
        }
        if (!eventShortDtos.isEmpty()) {
            List<Integer> eventIds = events.stream()
                    .map(EventModel::getId)
                    .collect(Collectors.toList());

            Map<Integer, Long> views = getViews(eventIds);

            eventShortDtos.forEach(dto -> dto.setViews(views.get(dto.getId())));
        }

        return eventShortDtos;
    }

    @Override
    public EventFullDto getByIdForPublic(int eventId, HttpServletRequest httpServletRequest) {
        EventModel event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id=" + eventId + " не найден"));

        if (event.getState() != Status.PUBLISHED) {
            throw new NotFoundException("Событие id=" + eventId + " еще не опубликовано");
        }

        sendHit(httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());

        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);

        eventFullDto.setViews((long) client.get(event.getCreatedOn(),
                LocalDateTime.now(),
                List.of("/events/" + eventId),
                true).size());

        return eventFullDto;
    }

    private void sendHit(String uri, String ip) {
        HitDtoCreate hitDtoCreate = new HitDtoCreate();
        hitDtoCreate.setApp("ewm-main-service");
        hitDtoCreate.setUri(uri);
        hitDtoCreate.setIp(ip);
        hitDtoCreate.setTimestamp(LocalDateTime.now());

        client.create(hitDtoCreate);
    }

    private Map<Integer, Long> getViews(List<Integer> eventsId) {
        List<String> uris = eventsId
                .stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        Optional<LocalDateTime> start = eventRepository.getCreatedOn(eventsId);

        Map<Integer, Long> views = new HashMap<>();

        if (start.isPresent()) {
            List<HitDtoGet> response = client.get(start.get(), LocalDateTime.now(), uris, true);

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
