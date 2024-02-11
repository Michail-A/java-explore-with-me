package ru.practicum.ewm.event;

import ru.practicum.ewm.event.dto.EventDtoNew;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.RequestStatusUpdateResult;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {
    EventFullDto add(EventDtoNew eventDtoNew, int userId);

    List<EventShortDto> getAllByUser(int userId, int from, int size);

    EventFullDto getByIdByUser(int userId, int eventId);

    EventFullDto updateEvent(EventUpdateDto eventDtoNew, int userId, int eventId);

    List<RequestDto> getRequests(int userId, int eventId);

    RequestStatusUpdateResult updateRequestStatus(RequestStatusUpdateRequest requestStatusUpdateRequest,
                                                  int eventId, int userId);

    List<EventFullDto> getAllForAdmin(LocalDateTime start, LocalDateTime end, Collection<Integer> users,
                                      Collection<Integer> categories, Collection<Status> states, int from, int size);

    EventFullDto updateByAdmin(EventUpdateDto eventUpdateDto, int eventId);

    List<EventShortDto> getAllForPublic(String text, List<Integer> categoriesIds, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        boolean onlyAvailable, EventSort sort, int from, int size,
                                        HttpServletRequest httpServletRequest);

    EventFullDto getByIdForPublic(int eventId, HttpServletRequest httpRequest);
}
