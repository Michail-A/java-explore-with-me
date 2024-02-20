package ru.practicum.ewm.request;

import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.RequestStatusUpdateResult;

import java.util.List;

public interface RequestService {

    List<RequestDto> getAll(int userId);

    RequestDto create(int userId, int eventId);

    RequestDto cancelRequest(int userId, int eventId);

    List<RequestDto> getRequests(int userId, int eventId);

    RequestStatusUpdateResult updateRequestStatus(RequestStatusUpdateRequest requestStatusUpdateRequest,
                                                  int eventId, int userId);
}
