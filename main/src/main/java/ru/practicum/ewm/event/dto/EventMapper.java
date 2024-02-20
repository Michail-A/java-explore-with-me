package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.Status;
import ru.practicum.ewm.users.User;
import ru.practicum.ewm.users.dto.UserMapper;

import java.time.LocalDateTime;

public class EventMapper {

    public static Event toModel(EventCreateDto eventCreateDto, Category category, User user) {
        Event event = new Event();
        event.setTitle(eventCreateDto.getTitle());
        event.setAnnotation(eventCreateDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(eventCreateDto.getDescription());
        event.setCreatedOn(LocalDateTime.now());
        event.setEventDate(eventCreateDto.getEventDate());
        event.setInitiator(user);
        event.setLocation(eventCreateDto.getLocation());
        event.setPaid(eventCreateDto.getPaid());
        event.setParticipantLimit(eventCreateDto.getParticipantLimit());
        event.setState(Status.PENDING);
        event.setRequestModeration(eventCreateDto.getRequestModeration());
        return event;
    }

    public static EventFullDto toEventFullDto(Event event, int confirmedRequests) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(CategoryMapper.toCategoryGetDto(event.getCategory()));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(event.getLocation());
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setConfirmedRequests(confirmedRequests);
        eventFullDto.setState(event.getState().toString());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        if (event.getPublishedOn() != null) {
            eventFullDto.setPublishedOn(event.getPublishedOn());
        }
        return eventFullDto;
    }

    public static EventShortDto toEventShortDto(Event event, int confirmedRequests) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.toCategoryGetDto(event.getCategory()));
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setConfirmedRequests(confirmedRequests);
        eventShortDto.setEventDate(event.getEventDate());
        return eventShortDto;
    }
}
