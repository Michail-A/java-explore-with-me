package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.Status;
import ru.practicum.ewm.exception.AlreadyAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.dto.RequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.RequestStatusUpdateResult;
import ru.practicum.ewm.users.User;
import ru.practicum.ewm.users.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getAll(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));
        List<Request> requests = requestRepository.findByRequesterId(userId);
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto create(int userId, int eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(" Событие с id=" + eventId + " не найден"));

        if (event.getInitiator().getId() == userId) {
            throw new AlreadyAvailableException("Событие с id= = " + eventId + " принадлежит текущему пользователю");
        }
        if (!event.getState().equals(Status.PUBLISHED)) {
            throw new AlreadyAvailableException("Событие с id= = " + eventId + " не опубликовано");
        }
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() - event.getConfirmedRequests() <= 0) {
            throw new AlreadyAvailableException("Свободны места в событии кончились");
        }

        if (requestRepository.findByEventIdAndRequesterId(eventId, userId).size() > 0) {
            throw new AlreadyAvailableException("Нельзя отправить повторный запрос");
        }

        Request request = new Request();
        request.setEvent(event);
        request.setRequester(user);
        request.setCreateDate(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);

        if (!event.getRequestModeration() || (event.getParticipantLimit() == 0)) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        eventRepository.save(event);

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(int userId, int requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос id=" + requestId + " не найден"));


        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toRequestDto(requestRepository.save(request));
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
}
