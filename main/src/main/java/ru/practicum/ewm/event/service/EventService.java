package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.EventSort;
import ru.practicum.ewm.event.Status;
import ru.practicum.ewm.event.dto.EventCreateDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.param.AdminRequestParam;
import ru.practicum.ewm.event.param.PublicRequestParam;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.RequestStatusUpdateResult;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {
    EventFullDto create(EventCreateDto eventCreateDto, int userId);

    List<EventShortDto> getAllByUser(int userId, Pageable page);

    EventFullDto getByIdByUser(int userId, int eventId);

    EventFullDto updateEvent(EventUpdateDto eventDtoNew, int userId, int eventId);

    List<EventFullDto> getAll(AdminRequestParam param);

    EventFullDto updateByAdmin(EventUpdateDto eventUpdateDto, int eventId);

    List<EventShortDto> getAll(PublicRequestParam param);

    EventFullDto getById(int eventId);
}
