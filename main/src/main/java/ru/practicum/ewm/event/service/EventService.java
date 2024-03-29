package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.EventCreateDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.param.AdminRequestParam;
import ru.practicum.ewm.event.param.PublicRequestParam;

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
