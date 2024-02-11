package ru.practicum.ewm.request;

import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    List<RequestDto> getAll(int userId);

    RequestDto addRequest(int userId, int eventId);

    RequestDto cancelRequest(int userId, int eventId);
}
