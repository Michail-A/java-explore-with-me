package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventModel;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.Status;
import ru.practicum.ewm.exception.AlreadyAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.users.UserModel;
import ru.practicum.ewm.users.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<RequestDto> getAll(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));
        List<RequestModel> requestModels = requestRepository.findByRequesterId(userId);
        return requestModels.stream().map(RequestMapper::mapToRequestDto).collect(Collectors.toList());
    }

    @Override
    public RequestDto addRequest(int userId, int eventId) {
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(" Пользователь с id=" + userId + " не найден"));
        EventModel eventModel = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(" Событие с id=" + eventId + " не найден"));

        if (eventModel.getInitiator().getId() == userId) {
            throw new AlreadyAvailableException("Событие с id= = " + eventId + " принадлежит текущему пользователю");
        }
        if (!eventModel.getState().equals(Status.PUBLISHED)) {
            throw new AlreadyAvailableException("Событие с id= = " + eventId + " не опубликовано");
        }
        if (eventModel.getParticipantLimit() > 0 && eventModel.getParticipantLimit() - eventModel.getConfirmedRequests() <= 0) {
            throw new AlreadyAvailableException("Свободны места в событии кончились");
        }

        if (requestRepository.findByEventIdAndRequesterId(eventId, userId).size() > 0) {
            throw new AlreadyAvailableException("Нельзя отправить повторный запрос");
        }

        RequestModel request = new RequestModel();
        request.setEvent(eventModel);
        request.setRequester(userModel);
        request.setCreateDate(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);

        if (!eventModel.getRequestModeration() || (eventModel.getParticipantLimit() == 0)) {
            request.setStatus(RequestStatus.CONFIRMED);
            eventModel.setConfirmedRequests(eventModel.getConfirmedRequests() + 1);
        }

        eventRepository.save(eventModel);

        return RequestMapper.mapToRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(int userId, int requestId) {
        RequestModel requestModel = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос id=" + requestId + " не найден"));


        requestModel.setStatus(RequestStatus.CANCELED);

        return RequestMapper.mapToRequestDto(requestRepository.save(requestModel));
    }
}
